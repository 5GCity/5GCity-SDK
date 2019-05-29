/*
 * Copyright 2018 Nextworks s.r.l.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.nextworks.composer.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.nextworks.composer.adaptor.interfaces.ServicesAdaptorProviderInterface;
import it.nextworks.composer.executor.interfaces.FunctionManagerProviderInterface;
import it.nextworks.composer.executor.interfaces.ServiceManagerProviderInterface;
import it.nextworks.composer.executor.repositories.*;
import it.nextworks.composer.plugins.catalogue.ArchiveBuilder;
import it.nextworks.composer.plugins.catalogue.FiveGCataloguePlugin;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.nfvmano.libs.descriptors.templates.Node;
import it.nextworks.sdk.*;
import it.nextworks.sdk.enums.MonitoringParameterType;
import it.nextworks.sdk.enums.SdkServiceComponentType;
import it.nextworks.sdk.enums.SdkServiceStatus;
import it.nextworks.sdk.exceptions.AlreadyPublishedServiceException;
import it.nextworks.sdk.exceptions.MalformedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.NotPublishedServiceException;
import org.hibernate.cfg.NotYetImplementedException;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ServiceManager implements ServiceManagerProviderInterface {

    private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);

    @Autowired
    @Qualifier("expressionAdapter")
    private ServicesAdaptorProviderInterface adapter;

    @Autowired
    private SdkServiceRepository serviceRepository;

    @Autowired
    private SdkServiceDescriptorRepository serviceDescriptorRepository;

    @Autowired
    private SdkFunctionRepository functionRepository;

    @Autowired
    private SdkSubFunctionRepository subFunctionRepository;

    @Autowired
    private SdkSubServiceRepository subServiceRepository;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private ConnectionpointRepository cpRepository;

    @Autowired
    private MonitoringParameterRepository monitoringParamRepository;

    @Autowired
    private MetadataRepository metadataRepository;

    @Autowired
    private FunctionManagerProviderInterface functionManager;

    @Value("${catalogue.host}")
    private String hostname;

    @Autowired
    private CatalogueRepository catalogueRepo;

    @Autowired
    private FiveGCataloguePlugin cataloguePlugin;

    @Autowired
    private TaskExecutor executor;

    public ServiceManager() {

    }

    private static Stream<Long> getSubFunctionIds(SdkService service) {
        return service.getComponents().stream()
            .filter(c -> c.getType().equals(SdkServiceComponentType.SDK_FUNCTION))
            .map(SdkServiceComponent::getComponentId);
    }

    private static Stream<Long> getSubServiceIds(SdkService service) {
        return service.getComponents().stream()
            .filter(c -> c.getType().equals(SdkServiceComponentType.SDK_SERVICE))
            .map(SdkServiceComponent::getComponentId);
    }

    @Override
    public List<SdkService> getServices() {
        log.info("Request for all service stored in database");
        return serviceRepository.findAll();
    }

    @Override
    public List<SdkService> getServicesUsingFunction(Long functionId) {
        // TODO usare una join
        log.info("Request for all services which are using function with uuid: " + functionId);
        if (functionId == null) {
            log.warn("Requested NULL function id.");
            return Collections.emptyList();
        }
        List<SdkService> serviceList = new ArrayList<>();
        List<SdkService> services = serviceRepository.findAll();
        for (SdkService service : services) {
            if (getSubFunctionIds(service).anyMatch(functionId::equals)) {
                serviceList.add(service);
            }
        }
        return serviceList;
    }

    private void validateImportedMonitoringParameter(SdkService service) throws NotExistingEntityException{
        Set<MonitoringParameter> monitoringParameters = new HashSet<>();
        monitoringParameters.addAll(service.getIntMonitoringParameters());
        monitoringParameters.addAll(service.getExtMonitoringParameters());;
        for (MonitoringParameter mp : monitoringParameters){
            if(mp instanceof MonParamImported){
                Optional<MonitoringParameter> target = monitoringParamRepository.findById(Long.valueOf(((MonParamImported) mp).getImportedParameterId()));
                if(!target.isPresent()){
                    throw new NotExistingEntityException("Monitoring parameter id " + (((MonParamImported) mp).getImportedParameterId()) + " not present in database");
                }
            }
        }
    }

    private void checkAndResolveService(SdkService service) throws NotExistingEntityException, MalformedElementException {
        log.debug("Checking functions availability");
        List<SdkFunction> availableF = functionManager.getFunctions();
        Set<Long> availableFIds = availableF.stream()
            .map(SdkFunction::getId)
            .collect(Collectors.toSet());
        Set<Long> requiredFIds = getSubFunctionIds(service).collect(Collectors.toSet());
        // TODO: optimize via repo method?
        // Check if the functions are available
        if (!availableFIds.containsAll(requiredFIds)) {
            requiredFIds.removeAll(availableFIds);
            throw new MalformedElementException(String.format(
                "Malformed service: functions %s are not available",
                requiredFIds
            ));
        }
        log.debug("Checking sub-services availability");
        List<SdkService> availableS = this.getServices();
        Set<Long> availableSIds = availableS.stream()
            .map(SdkService::getId)
            .collect(Collectors.toSet());
        Set<Long> requiredSIds = getSubServiceIds(service).collect(Collectors.toSet());
        // Check if the services are available
        if (!availableSIds.containsAll(requiredSIds)) {
            requiredSIds.removeAll(availableSIds);
            throw new MalformedElementException(String.format(
                "Malformed service: functions %s are not available",
                requiredSIds
            ));
        }
        validateImportedMonitoringParameter(service);
        log.debug("Resolving service components");
        try {
            service.resolveComponents(new HashSet<>(availableF), new HashSet<>(availableS));
        } catch (Exception e) {
            throw new MalformedElementException(
                String.format(
                    "Error while resolving service: %s",
                    e.getMessage()
                ),
                e
            );
        }
    }

    @Override
    public String createService(SdkService service)
        throws NotExistingEntityException, MalformedElementException {

        log.info("Storing into database a new service");
        // TODO Find a way to check if service already exists
        // TODO: should we really? We could check the name, but this is the user's responsibility
        if (!service.isValid()) {
            log.error("Malformed SdkService");
            throw new MalformedElementException("Malformed SdkService");
        }

        checkAndResolveService(service);

        log.debug("Storing into database service with name: " + service.getName());
        // Saving the service
        serviceRepository.saveAndFlush(service);
        return service.getId().toString();
    }

    @Override
    public String updateService(SdkService service) throws NotExistingEntityException, MalformedElementException {
		log.info("Updating an existing service with id: " + service.getId());
		if (!service.isValid()) {
			log.error("Service id " + service.getId() + " is malformed");
			throw new MalformedElementException("Service id " + service.getId() + " is malformed");
		}
		log.debug("Service is valid");
		// Check if service exists
		Optional<SdkService> srv = serviceRepository.findById(service.getId());
		if (!srv.isPresent()) {
			log.error("Service id " + service.getId() + " not present in database");
			throw new NotExistingEntityException("Service id " + service.getId() + " not present in database");
		}
		log.debug("Service found on db");

        checkAndResolveService(service);

		log.debug("Updating into database service with id: " + service.getId());

		cleanOldRelations(srv.get());
        // Update service on DB
		serviceRepository.saveAndFlush(service);
		return service.getId().toString();
    }

    private void cleanOldRelations(SdkService service){
        for(MonitoringParameter mp : service.getExtMonitoringParameters()){
            mp.setSdkServiceExt(null);
        }
        for(MonitoringParameter mp : service.getIntMonitoringParameters()){
            mp.setSdkServiceInt(null);
        }
        for(Link mp : service.getLink()){
            mp.setService(null);

            for(ConnectionPoint cp : mp.getConnectionPoints()){
                cp.setLink(null);
            }
        }
        for(L3Connectivity mp : service.getL3Connectivity()){
            mp.setService(null);
        }
        for(Metadata mp : service.getMetadata2()){
            mp.setService(null);
        }
        for(ConnectionPoint mp : service.getConnectionPoint()){
            mp.setSdkService(null);
        }
        for(ServiceAction sa : service.getActions()){
            sa.setSdkService(null);
        }
        for(ServiceActionRule sa : service.getActionRules()){
            sa.setSdkService(null);
            for(RuleCondition rc : sa.getConditions()){
                rc.setServiceActionRule(null);
            }
        }
    }

    @Override
    public SdkService getServiceById(Long id) throws NotExistingEntityException {
        log.info("Request for service with ID: " + id);
        Optional<SdkService> service = serviceRepository.findById(id);
        if (service.isPresent()) {
            return service.get();
        } else {
            log.error("Service with UUID " + id + " not found");
            throw new NotExistingEntityException("Service with ID " + id + " not found");
        }
    }

    @Override
    public void deleteService(Long serviceId) throws NotExistingEntityException {
        log.info("Request for deletion of service with id: " + serviceId);
        // No deletion required: all that depends on the service will cascade.
        Optional<SdkService> service = serviceRepository.findById(serviceId);
        SdkService s = service.orElseThrow(() -> {
            log.error("Service with ID " + serviceId + " not found");
            return new NotExistingEntityException("Service with ID " + serviceId + " not found");
        });

        //TODO check if service is used by other services

        serviceRepository.delete(s);
    }

    @Override
    public String createServiceDescriptor(Long serviceId, List<BigDecimal> parameterValues)
        throws NotExistingEntityException, MalformedElementException {
        log.info("Request create-descriptor of service with uuid: " + serviceId);

        // Check if service exists
        Optional<SdkService> optService = serviceRepository.findById(serviceId);

        SdkService service = optService.orElseThrow(() -> {
            log.error("The Service with UUID: " + serviceId + " is not present in database");
            return new NotExistingEntityException("The Service with UUID: " + serviceId + " is not present in database");
        });

        SdkServiceDescriptor descriptor;
        try {
            descriptor = adapter.createServiceDescriptor(service, parameterValues);
        } catch (IllegalArgumentException exc) {
            log.error("Malformed create-descriptor request: {}", exc.getMessage());
            throw new MalformedElementException(exc.getMessage(), exc);
        }
        descriptor.setStatus(SdkServiceStatus.SAVED);
        serviceDescriptorRepository.saveAndFlush(descriptor);
        log.info(
            "Descriptor for service {} successfully created. Descriptor ID {}.",
            serviceId,
            descriptor.getId()
        );
        return descriptor.getId().toString();
    }

    @Override
    public List<SdkServiceDescriptor> getAllDescriptors() {
        log.info("Request for all service descriptors stored in database");
        return serviceDescriptorRepository.findAll();
    }

    @Override
    public SdkServiceDescriptor getServiceDescriptor(Long descriptorId)
        throws NotExistingEntityException {
        log.info("Request for service descriptor with id: {}", descriptorId);
        Optional<SdkServiceDescriptor> byId = serviceDescriptorRepository.findById(descriptorId);
        return byId.orElseThrow(() -> {
            log.error("Descriptor with id {} not found", descriptorId);
            return new NotExistingEntityException(String.format(
                "Descriptor with id %d not found",
                descriptorId
            ));
        });
    }

    @Override
    public void deleteServiceDescriptor(Long descriptorId) throws NotExistingEntityException {
        log.info("Request for deletion of service descriptor with id: {}", descriptorId);
        Optional<SdkServiceDescriptor> byId = serviceDescriptorRepository.findById(descriptorId);
        SdkServiceDescriptor descriptor = byId.orElseThrow(() -> {
            log.error("Descriptor with id {} not found", descriptorId);
            return new NotExistingEntityException(String.format(
                "Descriptor with id %d not found",
                descriptorId
            ));
        });
        serviceDescriptorRepository.delete(descriptor);
    }

    @Override
    public String publishService(Long serviceId, List<BigDecimal> parameterValues)
        throws NotExistingEntityException, MalformedElementException {
        log.info("Request for publication of service with uuid: " + serviceId);
        // Check if service exists
        Optional<SdkService> optService = serviceRepository.findById(serviceId);

        SdkService service = optService.orElseThrow(() -> {
            log.error("The Service with UUID: " + serviceId + " is not present in database");
            return new NotExistingEntityException("The Service with UUID: " + serviceId + " is not present in database");
        });
        SdkServiceDescriptor descriptor;
        try {
            descriptor = adapter.createServiceDescriptor(service, parameterValues);
        } catch (IllegalArgumentException e) {
            log.error("Malformed create-descriptor request: {}", e.getMessage());
            throw new MalformedElementException(e.getMessage(), e);
        }
        descriptor.setStatus(SdkServiceStatus.CHANGING);
        serviceDescriptorRepository.saveAndFlush(descriptor);
        String serviceDescriptorId = descriptor.getId().toString();
        DescriptorTemplate nsd = adapter.generateNetworkServiceDescriptor(descriptor);

        Set<MonitoringParameter> monitoringParameters = new HashSet<>();
        monitoringParameters.addAll(service.getExtMonitoringParameters());
        monitoringParameters.addAll(service.getIntMonitoringParameters());

        String servicePackagePath = ArchiveBuilder.createNSCSAR(nsd, monitoringParameters, service.getActions(), service.getActionRules());

        // A thread will be created to handle this request in order to perform it
        // asynchronously.
        dispatchPublishRequest(
            servicePackagePath,
            successful -> {
                if (successful) {
                    log.info("Service descriptor {} successfully published", serviceDescriptorId);
                    descriptor.setStatus(SdkServiceStatus.COMMITTED);
                    serviceDescriptorRepository.saveAndFlush(descriptor);
                } else {
                    descriptor.setStatus(SdkServiceStatus.SAVED);
                    serviceDescriptorRepository.saveAndFlush(descriptor);
                    log.error("Error while publishing service descriptor {}", serviceDescriptorId);
                }
            }
        );
        return serviceDescriptorId;
    }

    public DescriptorTemplate generateTemplate(Long serviceDescriptorId)
        throws NotExistingEntityException {
        Optional<SdkServiceDescriptor> optDescriptor = serviceDescriptorRepository.findById(serviceDescriptorId);

        SdkServiceDescriptor descriptor = optDescriptor.orElseThrow(() -> {
            log.error("The Service descriptor with UUID: {} is not present in database", serviceDescriptorId);
            return new NotExistingEntityException(String.format(
                "The Service descriptor with UUID: %s is not present in database",
                serviceDescriptorId
            ));
        });
        return adapter.generateNetworkServiceDescriptor(descriptor);
    }

    @Override
    public void publishService(Long serviceDescriptorId)
        throws NotExistingEntityException, AlreadyPublishedServiceException {
        Optional<SdkServiceDescriptor> optDescriptor = serviceDescriptorRepository.findById(serviceDescriptorId);

        SdkServiceDescriptor descriptor = optDescriptor.orElseThrow(() -> {
            log.error("The Service descriptor with UUID: {} is not present in database", serviceDescriptorId);
            return new NotExistingEntityException(String.format(
                "The Service descriptor with UUID: %s is not present in database",
                serviceDescriptorId
            ));
        });

        synchronized (this) { // To avoid multiple simultaneous calls
            if (!descriptor.getStatus().equals(SdkServiceStatus.SAVED)) {
                log.error("The Service descriptor with UUID: {} is not in status SAVED.", serviceDescriptorId);
                throw new AlreadyPublishedServiceException(String.format(
                    "The Service descriptor with UUID: %s is not in status SAVED",
                    serviceDescriptorId
                ));
            }
            descriptor.setStatus(SdkServiceStatus.CHANGING);
            serviceDescriptorRepository.saveAndFlush(descriptor);
            // After setting the status, no one can operate on this anymore (except us)
        }

        DescriptorTemplate nsd = adapter.generateNetworkServiceDescriptor(descriptor);

        Set<MonitoringParameter> monitoringParameters = new HashSet<>();
        monitoringParameters.addAll(descriptor.getTemplate().getExtMonitoringParameters());
        monitoringParameters.addAll(descriptor.getTemplate().getIntMonitoringParameters());

        String servicePackagePath = ArchiveBuilder.createNSCSAR(nsd, monitoringParameters, descriptor.getTemplate().getActions(), descriptor.getTemplate().getActionRules());

        dispatchPublishRequest(
            servicePackagePath,
            successful -> {
                if (successful) {
                    log.info("Service descriptor {} successfully published", serviceDescriptorId);
                    descriptor.setStatus(SdkServiceStatus.COMMITTED);
                    serviceDescriptorRepository.save(descriptor);
                } else {
                    descriptor.setStatus(SdkServiceStatus.SAVED);
                    serviceDescriptorRepository.save(descriptor);
                    log.error("Error while publishing service descriptor {}", serviceDescriptorId);
                }
            }
        );
    }

    private void dispatchPublishRequest(String servicePackagePath, Consumer<Boolean> callback) {
        // TODO: dispatch publish operation to driver, then return immediately
        executor.execute(() -> {
                try {
                    String s = cataloguePlugin.uploadNetworkService(servicePackagePath, "multipart/form-data", null);
                    callback.accept(true);
                } catch (Exception exc) {
                    log.error(
                        "Could not push service package. Cause: {}",
                        exc.getMessage()
                    );
                    log.debug("Details: ", exc);
                    callback.accept(false);
                }
            }
        );
    }

    private void dispatchUnPublishRequest(Long serviceDescriptorId, Consumer<Boolean> callback) {
        // TODO: dispatch unpublish operation to driver, then return immediately
        throw new NotYetImplementedException();
    }


    @Override
    public void unPublishService(Long serviceDescriptorId)
        throws NotExistingEntityException, NotPublishedServiceException {
        log.info("Requested deletion of the publication of the service descriptor with id: {}", serviceDescriptorId);
        Optional<SdkServiceDescriptor> optDescriptor = serviceDescriptorRepository.findById(serviceDescriptorId);
        SdkServiceDescriptor descriptor = optDescriptor.orElseThrow(() -> {
            log.error("The Service descriptor with UUID: {} is not present in database", serviceDescriptorId);
            return new NotExistingEntityException(String.format(
                "The Service with UUID: %s is not present in database",
                serviceDescriptorId
            ));
        });

        synchronized (this) { // To avoid multiple simultaneous calls
            // Check if is already published
            if (!descriptor.getStatus().equals(SdkServiceStatus.COMMITTED)) {
                log.error("The Service descriptor with UUID: {} is not in status COMMITTED.", serviceDescriptorId);
                throw new NotPublishedServiceException(String.format(
                    "The Service descriptor with UUID: %s is not in status COMMITTED",
                    serviceDescriptorId
                ));
            }
            descriptor.setStatus(SdkServiceStatus.CHANGING);
            serviceDescriptorRepository.saveAndFlush(descriptor);
            // After setting the status, no one can operate on this anymore (except us)
        }

        dispatchUnPublishRequest(
            serviceDescriptorId,
            successful -> {
                if (successful) {
                    descriptor.setStatus(SdkServiceStatus.SAVED);
                    serviceDescriptorRepository.saveAndFlush(descriptor);
                    log.info("Successfully un-published descriptor {}", serviceDescriptorId);
                } else {
                    descriptor.setStatus(SdkServiceStatus.COMMITTED);
                    serviceDescriptorRepository.saveAndFlush(descriptor);
                    log.error("Error while un-publishing descriptor {}", serviceDescriptorId);
                }
            }
        );

    }

    @Override
    public void updateMonitoringParameters(Long serviceId, Set<MonitoringParameter> extMonitoringParameters, Set<MonitoringParameter> intMonitoringParameters)
        throws NotExistingEntityException, MalformedElementException {
        log.info("Request to update list of scalingAspects for a specific SDK Service " + serviceId);
        Optional<SdkService> service = serviceRepository.findById(serviceId);
        if (!service.isPresent()) {
            log.error("The Service with ID: " + serviceId + " is not present in database");
            throw new NotExistingEntityException("The Service with ID: " + serviceId + " is not present in database");
        }

        for(MonitoringParameter mp : service.get().getExtMonitoringParameters()){
                mp.setSdkServiceExt(null);
        }
        for(MonitoringParameter mp : service.get().getIntMonitoringParameters()){
            mp.setSdkServiceInt(null);
        }
        log.debug("Updating list of monitoring parameters on service");
        service.get().setExtMonitoringParameters(extMonitoringParameters);
        service.get().setIntMonitoringParameters(intMonitoringParameters);
        validateImportedMonitoringParameter(service.get());
        log.debug("Updating list of monitoring parameters on database");
        serviceRepository.saveAndFlush(service.get());
    }

    @Override
    public void deleteMonitoringParameters(Long serviceId, Long monitoringParameterId)
        throws NotExistingEntityException, MalformedElementException {

        log.info("Request to delete a monitoring parameter identified by id " + monitoringParameterId + " for a specific SDK Service " + serviceId);
        Optional<SdkService> service = serviceRepository.findById(serviceId);
        if (!service.isPresent()) {
            log.error("The Service with ID: " + serviceId + " is not present in database");
            throw new NotExistingEntityException("The Service with ID: " + serviceId + " is not present in database");
        }
        Set<MonitoringParameter> extMonitoringParameters = new HashSet<>();
        extMonitoringParameters.addAll(service.get().getExtMonitoringParameters());
        Set<MonitoringParameter> intMonitoringParameters = new HashSet<>();
        intMonitoringParameters.addAll(service.get().getIntMonitoringParameters());
        for (MonitoringParameter param : extMonitoringParameters) {
            if(param.getId().compareTo(monitoringParameterId) == 0){
                param.setSdkServiceExt(null);
                extMonitoringParameters.remove(param);
                break;
            }
        }
        for (MonitoringParameter param : intMonitoringParameters) {
            if(param.getId().compareTo(monitoringParameterId) == 0){
                param.setSdkServiceInt(null);
                intMonitoringParameters.remove(param);
                break;
            }
        }
        service.get().setExtMonitoringParameters(extMonitoringParameters);
        service.get().setIntMonitoringParameters(intMonitoringParameters);
        checkAndResolveService(service.get());
        serviceRepository.saveAndFlush(service.get());
        log.debug("Monitoring parameter has been deleted.");
    }

    @Override
    public MonitoringParameterWrapper getMonitoringParameters(Long serviceId) throws NotExistingEntityException {
        log.info("Request to get the list of monitoring parameters for a specific SDK Service " + serviceId);
        Optional<SdkService> service = serviceRepository.findById(serviceId);
        if (!service.isPresent()) {
            log.error("The Service with ID: " + serviceId + " is not present in database");
            throw new NotExistingEntityException("The Service with ID: " + serviceId + " is not present in database");
        }
        MonitoringParameterWrapper params = new MonitoringParameterWrapper(service.get().getExtMonitoringParameters(), service.get().getIntMonitoringParameters());

        return params;
    }
}

