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
import it.nextworks.nfvmano.libs.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.common.exceptions.NotPermittedOperationException;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.nfvmano.libs.descriptors.templates.Node;
import it.nextworks.sdk.*;
import it.nextworks.sdk.enums.*;
import it.nextworks.sdk.exceptions.AlreadyPublishedServiceException;
import it.nextworks.sdk.exceptions.MalformedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.NotPublishedServiceException;
import org.aspectj.weaver.ast.Not;
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
    private L3ConnectivityRepository l3ConnectivityRepository;

    @Autowired
    private ServiceActionRepository serviceActionRepository;

    @Autowired
    private ServiceActionRuleRepository serviceActionRuleRepository;

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

    @Override
    public List<SdkService> getServices() {
        log.info("Request for all service stored in database");
        List<SdkService> serviceList = serviceRepository.findAll();
        if (serviceList.size() == 0) {
            log.debug("No services are available");
        } else
            log.debug("Services present in database: " + serviceList.size());
        return serviceList;
    }

    /*
    @Override
    public List<SdkService> getServicesUsingFunction(Long functionId) {
        //TODO usare una join
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
    */

    @Override
    public String createService(SdkService service)
        throws NotExistingEntityException, MalformedElementException, AlreadyExistingEntityException {

        log.info("Storing into database a new service");

        if(service.getId() != null){
            log.error("Service ID cannot be specified in service creation");
            throw new MalformedElementException("Service ID cannot be specified in service creation");
        }
        if (!service.isValid()) {
            log.error("Malformed SdkService");
            throw new MalformedElementException("Malformed SdkService");
        }
        log.debug("Service is valid");

        checkAndResolveService(service);

        final Set<Integer> componentIndexes = new HashSet<Integer>();
        for(SdkServiceComponent component : service.getComponents()){
            if (component.getId() != null) {
                log.error("Component ID cannot be specified in service creation");
                throw new MalformedElementException("Component ID cannot be specified in service creation");
            }
        }
        for(MonitoringParameter mp : service.getExtMonitoringParameters()){
            if (mp.getId() != null) {
                log.error("Monitoring parameter ID cannot be specified in service creation");
                throw new MalformedElementException("Monitoring parameter ID cannot be specified in service creation");
            }
        }
        for(MonitoringParameter mp : service.getIntMonitoringParameters()){
            if (mp.getId() != null) {
                log.error("Monitoring parameter ID cannot be specified in function creation");
                throw new MalformedElementException("Monitoring parameter ID cannot be specified in function creation");
            }
        }
        for(ConnectionPoint cp : service.getConnectionPoint()){
            if (cp.getId() != null) {
                log.error("Connection point ID cannot be specified in service creation");
                throw new MalformedElementException("Connection point ID cannot be specified in service creation");
            }
        }
        for(Link link : service.getLink()){
            if (link.getId() != null) {
                log.error("Link ID cannot be specified in service creation");
                throw new MalformedElementException("Link ID cannot be specified in service creation");
            }
        }
        for(L3Connectivity lc : service.getL3Connectivity()){
            if (lc.getId() != null) {
                log.error("L3 connectivity ID cannot be specified in service creation");
                throw new MalformedElementException("L3 connectivity ID cannot be specified in service creation");
            }
        }
        for(ServiceAction sa : service.getActions()){
            if (sa.getId() != null) {
                log.error("Service action ID cannot be specified in service creation");
                throw new MalformedElementException("Service action ID cannot be specified in service creation");
            }
        }
        for(ServiceActionRule rule : service.getActionRules()){
            if (rule.getId() != null) {
                log.error("Service action rule ID cannot be specified in service creation");
                throw new MalformedElementException("Service action rule ID cannot be specified in service creation");
            }
        }

        log.debug("Storing into database service with name: " + service.getName());
        // Saving the service
        serviceRepository.saveAndFlush(service);
        return service.getId().toString();
    }

    @Override
    public String updateService(SdkService service) throws NotExistingEntityException, MalformedElementException, NotPermittedOperationException {
		log.info("Updating an existing service with ID " + service.getId());
        if(service.getId() == null){
            log.error("Service ID needs to be specified");
            throw new MalformedElementException("Service ID needs to be specified");
        }

		if (!service.isValid()) {
			log.error("Service id " + service.getId() + " is malformed");
			throw new MalformedElementException("Service id " + service.getId() + " is malformed");
		}
		log.debug("Service is valid");

		// Check if service exists
		Optional<SdkService> srv = serviceRepository.findById(service.getId());
		if (!srv.isPresent()) {
			log.error("Service with ID " + service.getId() + " not present in database");
			throw new NotExistingEntityException("Service with ID " + service.getId() + " not present in database");
		}
		log.debug("Service found on db");

        //update not allowed if the service has at least one descriptor
        List<SdkServiceDescriptor> descriptors = serviceDescriptorRepository.findByTemplateId(service.getId());
        if(descriptors.size() != 0){
            log.error("Service with ID " + service.getId() + " has at least one descriptor. Please delete it before updating");
            throw  new NotPermittedOperationException("Service with ID " + service.getId() + " has at least one descriptor. Please delete it before updating");
        }

        //update not allowed if the service is used by other services
        List<SubService> subServices = subServiceRepository.findByComponentId(service.getId());
        if(subServices.size() != 0){
            log.error("Service with ID " + service.getId() + " used by a service");
            throw  new NotPermittedOperationException("Service with ID " + service.getId() + " used by a service");
        }

        for(SdkServiceComponent component : service.getComponents()){
            if (component.getId() != null) {
                Optional<SubFunction> subFunction = subFunctionRepository.findById(component.getId());
                Optional<SubService> subService = subServiceRepository.findById(component.getId());
                if (!subFunction.isPresent() && !subService.isPresent()) {
                    log.error("Component with ID " + component.getId() + " is not present in database");
                    throw new NotExistingEntityException("Component with ID " + component.getId() + " is not present in database");
                }
                if(subFunction.isPresent()){
                    if ((subFunction.get().getOuterService() == null) || (subFunction.get().getOuterService().getId() != service.getId())) {
                        log.error("Component with ID " + component.getId() + " does not belong to service with ID " + service.getId());
                        throw new NotPermittedOperationException("Component with ID " + component.getId() + " does not belong to service with ID " + service.getId());
                    }
                }
                if(subService.isPresent()){
                    if ((subService.get().getOuterService() == null) || (subService.get().getOuterService().getId() != service.getId())) {
                        log.error("Component with ID " + component.getId() + " does not belong to service with ID " + service.getId());
                        throw new NotPermittedOperationException("Component with ID " + component.getId() + " does not belong to service with ID " + service.getId());
                    }
                }
            }
        }
        Set<MonitoringParameter> monitoringParameters = new HashSet<>();
        monitoringParameters.addAll(service.getExtMonitoringParameters());
        monitoringParameters.addAll(service.getIntMonitoringParameters());
        for(MonitoringParameter param : monitoringParameters) {
            if (param.getId() != null) {
                Optional<MonitoringParameter> mp = monitoringParamRepository.findById(param.getId());
                if (!mp.isPresent()) {
                    log.error("Monitoring parameter with ID " + param.getId() + " is not present in database");
                    throw new NotExistingEntityException("Monitoring parameter with ID " + param.getId() + " is not present in database");
                }
                if ((mp.get().getSdkFunction() != null)
                    || ((mp.get().getSdkServiceExt() != null) && (mp.get().getSdkServiceExt().getId() != service.getId()))
                        || ((mp.get().getSdkServiceInt() != null) && (mp.get().getSdkServiceInt().getId() != service.getId()))
                ){
                    log.error("Monitoring parameter with ID " + param.getId() + " does not belong to service with ID " + service.getId());
                    throw new NotPermittedOperationException("Monitoring parameter with ID " + param.getId() + " does not belong to service with ID " + service.getId());
                }
            }
        }
        for(ConnectionPoint param : service.getConnectionPoint()) {
            if (param.getId() != null) {
                Optional<ConnectionPoint> cp = cpRepository.findById(param.getId());
                if (!cp.isPresent()) {
                    log.error("Connection point with ID " + param.getId() + " is not present in database");
                    throw new NotExistingEntityException("Connection point with ID " + param.getId() + " is not present in database");
                }
                if ((cp.get().getSdkService() == null) || (cp.get().getSdkService().getId() != service.getId())) {
                    log.error("Connection point with ID " + param.getId() + " does not belong to service with ID " + service.getId());
                    throw new NotPermittedOperationException("Connection point with ID " + param.getId() + " does not belong to service with ID " + service.getId());
                }
            }
        }
        for(Link link : service.getLink()) {
            if (link.getId() != null) {
                Optional<Link> rp = linkRepository.findById(link.getId());
                if (!rp.isPresent()) {
                    log.error("Link with ID " + link.getId() + " is not present in database");
                    throw new NotExistingEntityException("Link with ID " + link.getId() + " is not present in database");
                }
                if ((rp.get().getService() == null) || (rp.get().getService().getId() != service.getId())) {
                    log.error("Link with ID " + link.getId() + " does not belong to service with ID " + service.getId());
                    throw new NotPermittedOperationException("Link with ID " + link.getId() + " does not belong to service with ID " + service.getId());
                }
            }
        }
        for(L3Connectivity lc : service.getL3Connectivity()) {
            if (lc.getId() != null) {
                Optional<L3Connectivity> rp = l3ConnectivityRepository.findById(lc.getId());
                if (!rp.isPresent()) {
                    log.error("L3 connectivity with ID " + lc.getId() + " is not present in database");
                    throw new NotExistingEntityException("L3 connectivity with ID " + lc.getId() + " is not present in database");
                }
                if ((rp.get().getService() == null) || (rp.get().getService().getId() != service.getId())) {
                    log.error("L3 connectivity with ID " + lc.getId() + " does not belong to service with ID " + service.getId());
                    throw new NotPermittedOperationException("L3 connectivity with ID " + lc.getId() + " does not belong to service with ID " + service.getId());
                }
            }
        }
        for(ServiceAction sa : service.getActions()) {
            if (sa.getId() != null) {
                Optional<ServiceAction> rp = serviceActionRepository.findById(sa.getId());
                if (!rp.isPresent()) {
                    log.error("Service action with ID " + sa.getId() + " is not present in database");
                    throw new NotExistingEntityException("Service action with ID " + sa.getId() + " is not present in database");
                }
                if ((rp.get().getSdkService() == null) || (rp.get().getSdkService().getId() != service.getId())) {
                    log.error("Service action with ID " + sa.getId() + " does not belong to service with ID " + service.getId());
                    throw new NotPermittedOperationException("Service action with ID " + sa.getId() + " does not belong to service with ID " + service.getId());
                }
            }
        }
        for(ServiceActionRule ar : service.getActionRules()) {
            if (ar.getId() != null) {
                Optional<ServiceActionRule> rp = serviceActionRuleRepository.findById(ar.getId());
                if (!rp.isPresent()) {
                    log.error("Service action rule with ID " + ar.getId() + " is not present in database");
                    throw new NotExistingEntityException("Service action rule with ID " + ar.getId() + " is not present in database");
                }
                if ((rp.get().getSdkService() == null) || (rp.get().getSdkService().getId() != service.getId())) {
                    log.error("Service action rule with ID " + ar.getId() + " does not belong to service with ID " + service.getId());
                    throw new NotPermittedOperationException("Service action rule with ID " + ar.getId() + " does not belong to service with ID " + service.getId());
                }
            }
        }

        try{
            checkAndResolveService(service);
        }catch (AlreadyExistingEntityException e){
            //exception cannot be raised in this case
        }

		log.debug("Updating into database service with id: " + service.getId());

		cleanOldRelations(srv.get());
        // Update service on DB
		serviceRepository.saveAndFlush(service);
		return service.getId().toString();
    }

    @Override
    public SdkService getServiceById(Long id) throws NotExistingEntityException {
        log.info("Request for service with ID " + id);
        Optional<SdkService> service = serviceRepository.findById(id);
        if (service.isPresent()) {
            return service.get();
        } else {
            log.error("Service with ID " + id + " not found");
            throw new NotExistingEntityException("Service with ID " + id + " not found");
        }
    }

    @Override
    public void deleteService(Long serviceId) throws NotExistingEntityException, NotPermittedOperationException {
        log.info("Request for deletion of service with ID " + serviceId);
        // No deletion required: all that depends on the service will cascade.
        Optional<SdkService> service = serviceRepository.findById(serviceId);
        SdkService s = service.orElseThrow(() -> {
            log.error("Service with ID " + serviceId + " not found");
            return new NotExistingEntityException("Service with ID " + serviceId + " not found");
        });

        //delete not allowed if the service has at least one descriptor
        List<SdkServiceDescriptor> descriptors = serviceDescriptorRepository.findByTemplateId(serviceId);
        if(descriptors.size() != 0){
            log.error("Service with ID " + serviceId + " has at least one descriptor. Please delete it before deleting");
            throw  new NotPermittedOperationException("Service with ID " + serviceId + " has at least one descriptor. Please delete it before deleting");
        }

        //delete not allowed if the service is used by other services
        List<SubService> subServices = subServiceRepository.findByComponentId(serviceId);
        if(subServices.size() != 0){
            log.error("Service with ID " + serviceId + " used by a service");
            throw  new NotPermittedOperationException("Service with ID " + serviceId + " used by a service");
        }

        serviceRepository.delete(s);
    }

    @Override
    public void updateMonitoringParameters(Long serviceId, Set<MonitoringParameter> extMonitoringParameters, Set<MonitoringParameter> intMonitoringParameters)
        throws NotExistingEntityException, MalformedElementException, NotPermittedOperationException {
        log.info("Request to update list of scalingAspects for a specific SDK Service " + serviceId);
        Set<MonitoringParameter> monitoringParameters = new HashSet<>();
        monitoringParameters.addAll(extMonitoringParameters);
        monitoringParameters.addAll(intMonitoringParameters);
        for(MonitoringParameter param : monitoringParameters) {
            if (!param.isValid()) {
                log.error("Monitoring param list provided cannot be validated");
                throw new MalformedElementException("Monitoring param list provided cannot be validated");
            }
            if (param.getId() != null) {
                Optional<MonitoringParameter> mp = monitoringParamRepository.findById(param.getId());
                if (!mp.isPresent()) {
                    log.error("Monitoring parameter with ID " + param.getId() + " is not present in database");
                    throw new NotExistingEntityException("Monitoring parameter with ID " + param.getId() + " is not present in database");
                }
                if ((mp.get().getSdkFunction() != null)
                    || ((mp.get().getSdkServiceExt() != null) && (mp.get().getSdkServiceExt().getId() != serviceId))
                    || ((mp.get().getSdkServiceInt() != null) && (mp.get().getSdkServiceInt().getId() != serviceId))
                ){
                    log.error("Monitoring parameter with ID " + param.getId() + " does not belong to service with ID " + serviceId);
                    throw new NotPermittedOperationException("Monitoring parameter with ID " + param.getId() + " does not belong to service with ID " + serviceId);
                }
            }
        }

        Optional<SdkService> service = serviceRepository.findById(serviceId);
        if (!service.isPresent()) {
            log.error("Service with ID " + serviceId + " is not present in database");
            throw new NotExistingEntityException("Service with ID " + serviceId + " is not present in database");
        }

        //update not allowed if the service has at least one descriptor
        List<SdkServiceDescriptor> descriptors = serviceDescriptorRepository.findByTemplateId(serviceId);
        if(descriptors.size() != 0){
            log.error("Service with ID " + serviceId + " has at least one descriptor. Please delete it before updating");
            throw  new NotPermittedOperationException("Service with ID " + serviceId + " has at least one descriptor. Please delete it before updating");
        }

        //update not allowed if the service is used by other services
        List<SubService> subServices = subServiceRepository.findByComponentId(serviceId);
        if(subServices.size() != 0){
            log.error("Service with ID " + serviceId + " used by a service");
            throw  new NotPermittedOperationException("Service with ID " + serviceId + " used by a service");
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
        try{
            checkAndResolveService(service.get());
        }catch (AlreadyExistingEntityException e){
            //exception cannot be raised in this case
        }

        if (!service.get().isValid()) {
            log.error("Malformed SdkService");
            throw new MalformedElementException("Malformed SdkService");
        }
        log.debug("Updating list of monitoring parameters on database");
        serviceRepository.saveAndFlush(service.get());
    }

    @Override
    public void deleteMonitoringParameters(Long serviceId, Long monitoringParameterId)
        throws NotExistingEntityException, MalformedElementException, NotPermittedOperationException {

        log.info("Request to delete a monitoring parameter with ID " + monitoringParameterId + " for a specific SDK Service " + serviceId);
        Optional<MonitoringParameter> mp = monitoringParamRepository.findById(monitoringParameterId);
        if (!mp.isPresent()) {
            log.error("Monitoring parameter with ID " + monitoringParameterId + " is not present in database");
            throw new NotExistingEntityException("Monitoring parameter with ID " + monitoringParameterId + " is not present in database");
        }

        Optional<SdkService> service = serviceRepository.findById(serviceId);
        if (!service.isPresent()) {
            log.error("Service with ID " + serviceId + " is not present in database");
            throw new NotExistingEntityException("Service with ID " + serviceId + " is not present in database");
        }

        if ((mp.get().getSdkFunction() != null)
            || ((mp.get().getSdkServiceExt() != null) && (mp.get().getSdkServiceExt().getId() != serviceId))
            || ((mp.get().getSdkServiceInt() != null) && (mp.get().getSdkServiceInt().getId() != serviceId))
        ){
            log.error("Monitoring parameter with ID " + monitoringParameterId + " does not belong to service with ID " + serviceId);
            throw new NotPermittedOperationException("Monitoring parameter with ID " + monitoringParameterId + " does not belong to service with ID " + serviceId);
        }

        //delete not allowed if the service has at least one descriptor
        List<SdkServiceDescriptor> descriptors = serviceDescriptorRepository.findByTemplateId(serviceId);
        if(descriptors.size() != 0){
            log.error("Service with ID " + serviceId + " has at least one descriptor. Please delete it before deleting");
            throw  new NotPermittedOperationException("Service with ID " + serviceId + " has at least one descriptor. Please delete it before deleting");
        }

        //delete not allowed if the service is used by other services
        List<SubService> subServices = subServiceRepository.findByComponentId(serviceId);
        if(subServices.size() != 0){
            log.error("Service with ID " + serviceId + " used by a service");
            throw  new NotPermittedOperationException("Service with ID " + serviceId + " used by a service");
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

        try{
            checkAndResolveService(service.get());
        }catch (AlreadyExistingEntityException e){
            //exception cannot be raised in this case
        }

        if (!service.get().isValid()) {
            log.error("Malformed SdkService");
            throw new MalformedElementException("Malformed SdkService");
        }

        serviceRepository.saveAndFlush(service.get());
        log.debug("Monitoring parameter has been deleted.");
    }

    @Override
    public MonitoringParameterWrapper getMonitoringParameters(Long serviceId) throws NotExistingEntityException {
        log.info("Request to get the list of monitoring parameters for a specific SDK Service " + serviceId);
        Optional<SdkService> service = serviceRepository.findById(serviceId);
        if (!service.isPresent()) {
            log.error("Service with ID " + serviceId + " is not present in database");
            throw new NotExistingEntityException("Service with ID " + serviceId + " is not present in database");
        }
        MonitoringParameterWrapper params = new MonitoringParameterWrapper(service.get().getExtMonitoringParameters(), service.get().getIntMonitoringParameters());

        return params;
    }

    @Override
    public String publishService(Long serviceId, List<BigDecimal> parameterValues)
        throws NotExistingEntityException, MalformedElementException {
        log.info("Request for publication of service with ID " + serviceId);
        // Check if service exists
        Optional<SdkService> optService = serviceRepository.findById(serviceId);

        SdkService service = optService.orElseThrow(() -> {
            log.error("Service with ID " + serviceId + " is not present in database");
            return new NotExistingEntityException("Service with ID " + serviceId + " is not present in database");
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
            nsInfoId -> {
                if (nsInfoId != null) {
                    log.info("Service descriptor with ID {} successfully published", serviceDescriptorId);
                    descriptor.setStatus(SdkServiceStatus.COMMITTED);
                    descriptor.setNsInfoId(nsInfoId);
                    serviceDescriptorRepository.saveAndFlush(descriptor);
                } else {
                    descriptor.setStatus(SdkServiceStatus.SAVED);
                    serviceDescriptorRepository.saveAndFlush(descriptor);
                    log.error("Error while publishing service descriptor with ID {}", serviceDescriptorId);
                }
            }
        );
        return serviceDescriptorId;
    }

    @Override
    public String createServiceDescriptor(Long serviceId, List<BigDecimal> parameterValues)
        throws NotExistingEntityException, MalformedElementException {
        log.info("Request create-descriptor of service with ID " + serviceId);

        // Check if service exists
        Optional<SdkService> optService = serviceRepository.findById(serviceId);

        SdkService service = optService.orElseThrow(() -> {
            log.error("Service with ID " + serviceId + " is not present in database");
            return new NotExistingEntityException("Service with ID " + serviceId + " is not present in database");
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
        log.info("Descriptor for service {} successfully created. Descriptor ID {}.", serviceId, descriptor.getId());
        return descriptor.getId().toString();
    }

    @Override
    public List<SdkServiceDescriptor> getAllDescriptors() {
        log.info("Request for all service descriptors stored in database");
        List<SdkServiceDescriptor> descriptors = serviceDescriptorRepository.findAll();
        if(descriptors.size() == 0){
            log.debug("No service descriptors are available");
        } else
            log.debug("Service descriptors present in database: " + descriptors.size());
        return descriptors;
    }

    @Override
    public SdkServiceDescriptor getServiceDescriptor(Long descriptorId)
        throws NotExistingEntityException {
        log.info("Request for service descriptor with ID {}", descriptorId);
        Optional<SdkServiceDescriptor> byId = serviceDescriptorRepository.findById(descriptorId);
        return byId.orElseThrow(() -> {
            log.error("Descriptor with ID {} not found", descriptorId);
            return new NotExistingEntityException(String.format("Descriptor with ID %d not found", descriptorId));
        });
    }

    @Override
    public void deleteServiceDescriptor(Long descriptorId) throws NotExistingEntityException, NotPermittedOperationException {
        log.info("Request for deletion of service descriptor with ID {}", descriptorId);
        Optional<SdkServiceDescriptor> optDescriptor = serviceDescriptorRepository.findById(descriptorId);
        SdkServiceDescriptor descriptor = optDescriptor.orElseThrow(() -> {
            log.error("Descriptor with ID {} not found", descriptorId);
            return new NotExistingEntityException(String.format("Descriptor with ID %d not found", descriptorId));
        });

        //delete not allowed if the service is published to catalogue
        if(descriptor.getStatus().equals(SdkServiceStatus.COMMITTED)){
            log.error("Service with ID " + descriptor.getTemplate().getId() + " published to the catalogue. Please unpublish it before deleting the descriptor");
            throw  new NotPermittedOperationException("Service with ID " + descriptor.getTemplate().getId() + " published to the catalogue. Please unpublish it before deleting the descriptor");
        }
        serviceDescriptorRepository.delete(descriptor);
    }

    @Override
    public void publishService(Long serviceDescriptorId)
        throws NotExistingEntityException, AlreadyPublishedServiceException {
        Optional<SdkServiceDescriptor> optDescriptor = serviceDescriptorRepository.findById(serviceDescriptorId);

        SdkServiceDescriptor descriptor = optDescriptor.orElseThrow(() -> {
            log.error("Service descriptor with ID {} is not present in database", serviceDescriptorId);
            return new NotExistingEntityException(String.format("Service descriptor with ID %s is not present in database", serviceDescriptorId));
        });

        synchronized (this) { // To avoid multiple simultaneous calls
            if (!descriptor.getStatus().equals(SdkServiceStatus.SAVED)) {
                log.error("Service descriptor with ID {} is not in status SAVED.", serviceDescriptorId);
                throw new AlreadyPublishedServiceException(String.format("Service descriptor with ID %s is not in status SAVED", serviceDescriptorId));
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
            nsInfoId -> {
                if (nsInfoId != null) {
                    log.info("Service descriptor with ID {} successfully published", serviceDescriptorId);
                    descriptor.setStatus(SdkServiceStatus.COMMITTED);
                    descriptor.setNsInfoId(nsInfoId);
                    serviceDescriptorRepository.save(descriptor);
                } else {
                    descriptor.setStatus(SdkServiceStatus.SAVED);
                    serviceDescriptorRepository.save(descriptor);
                    log.error("Error while publishing service descriptor with ID {}", serviceDescriptorId);
                }
            }
        );
    }

    @Override
    public void unPublishService(Long serviceDescriptorId)
        throws NotExistingEntityException, NotPublishedServiceException {
        log.info("Requested deletion of the publication of the service descriptor with ID {}", serviceDescriptorId);

        Optional<SdkServiceDescriptor> optDescriptor = serviceDescriptorRepository.findById(serviceDescriptorId);
        SdkServiceDescriptor descriptor = optDescriptor.orElseThrow(() -> {
            log.error("Service descriptor with ID {} is not present in database", serviceDescriptorId);
            return new NotExistingEntityException(String.format("Service descriptor with ID %s is not present in database", serviceDescriptorId));
        });

        synchronized (this) { // To avoid multiple simultaneous calls
            // Check if is already published
            if (!descriptor.getStatus().equals(SdkServiceStatus.COMMITTED)) {
                log.error("Service descriptor with ID {} is not in status COMMITTED.", serviceDescriptorId);
                throw new NotPublishedServiceException(String.format("Service descriptor with ID %s is not in status COMMITTED", serviceDescriptorId));
            }
            descriptor.setStatus(SdkServiceStatus.CHANGING);
            serviceDescriptorRepository.saveAndFlush(descriptor);
            // After setting the status, no one can operate on this anymore (except us)
        }

        dispatchUnPublishRequest(
            descriptor.getNsInfoId(),
            successful -> {
                if (successful) {
                    descriptor.setStatus(SdkServiceStatus.SAVED);
                    descriptor.setNsInfoId(null);
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
    public DescriptorTemplate generateTemplate(Long serviceDescriptorId)
        throws NotExistingEntityException {
        Optional<SdkServiceDescriptor> optDescriptor = serviceDescriptorRepository.findById(serviceDescriptorId);

        SdkServiceDescriptor descriptor = optDescriptor.orElseThrow(() -> {
            log.error("Service descriptor with ID {} is not present in database", serviceDescriptorId);
            return new NotExistingEntityException(String.format("Service descriptor with ID %s is not present in database", serviceDescriptorId));
        });
        return adapter.generateNetworkServiceDescriptor(descriptor);
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

    private void checkAndResolveService(SdkService service) throws NotExistingEntityException, MalformedElementException, AlreadyExistingEntityException{
        //In case of new service, check if a service with the same name and version is present
        if(service.getId() == null) {
            Optional<SdkService> serviceOptional = serviceRepository.findByNameAndVersion(service.getName(), service.getVersion());
            if (serviceOptional.isPresent()) {
                log.error("Service with name " + service.getName() + " and version " + service.getVersion() + " is already present");
                throw new AlreadyExistingEntityException("Service with name " + service.getName() + " and version " + service.getVersion() + " is already present");
            }
        }
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
            throw new MalformedElementException(String.format("Malformed service: functions %s are not available", requiredFIds));
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
            throw new MalformedElementException(String.format("Malformed service: functions %s are not available", requiredSIds));
        }
        validateImportedMonitoringParameter(service);
        validateInternalConnectionPoint(service);
        log.debug("Resolving service components");
        try {
            service.resolveComponents(new HashSet<>(availableF), new HashSet<>(availableS));
        } catch (Exception e) {
            throw new MalformedElementException(
                String.format("Error while resolving service: %s", e.getMessage()), e);
        }
    }

    private void validateImportedMonitoringParameter(SdkService service) throws NotExistingEntityException, MalformedElementException {
        Set<MonitoringParameter> monitoringParameters = new HashSet<>();
        monitoringParameters.addAll(service.getIntMonitoringParameters());
        monitoringParameters.addAll(service.getExtMonitoringParameters());;
        for (MonitoringParameter mp : monitoringParameters){
            if(mp instanceof MonParamImported){
                Optional<MonitoringParameter> target = monitoringParamRepository.findById(Long.valueOf(((MonParamImported) mp).getImportedParameterId()));
                if(!target.isPresent()){
                    throw new NotExistingEntityException("Monitoring parameter with ID " + (((MonParamImported) mp).getImportedParameterId()) + " not present in database");
                }

                Integer componentIndex = ((MonParamImported) mp).getComponentIndex();
                for(SdkServiceComponent component : service.getComponents()){
                    if(component.getComponentIndex().equals(componentIndex)){
                        if(target.get().getSdkFunction() != null){
                            if (target.get().getSdkFunction().getId() != component.getComponentId()) {
                                log.error("Monitoring parameter with ID " + target.get().getId() + " does not belong to component with ID " + component.getId());
                                throw new MalformedElementException("Monitoring parameter with ID " + target.get().getId() + " does not belong to component with ID " + component.getId());
                            }
                        }else if(target.get().getSdkServiceExt() != null){
                            if (target.get().getSdkServiceExt().getId() != component.getComponentId()) {
                                log.error("Connection point with ID " + target.get().getId() + " does not belong to component with ID " + component.getId());
                                throw new MalformedElementException("Monitoring parameter with ID " + target.get().getId() + " does not belong to component with ID " + component.getId());
                            }
                        }else if(target.get().getSdkServiceInt() != null){
                            if (target.get().getSdkServiceInt().getId() != component.getComponentId()) {
                                log.error("Connection point with ID " + target.get().getId() + " does not belong to component with ID " + component.getId());
                                throw new MalformedElementException("Monitoring parameter with ID " + target.get().getId() + " does not belong to component with ID " + component.getId());
                            }
                        }
                    }
                }
            }
        }
    }

    private void validateInternalConnectionPoint(SdkService service) throws NotExistingEntityException, MalformedElementException{
        for (ConnectionPoint cp : service.getConnectionPoint()){
            if(cp.getType().equals(ConnectionPointType.INTERNAL)){
                Optional<ConnectionPoint> target = cpRepository.findById(cp.getInternalCpId());
                if(!target.isPresent()){
                    throw new NotExistingEntityException("Connection point with ID " + cp.getInternalCpId() + " not present in database");
                }

                Integer componentIndex = cp.getComponentIndex();
                for(SdkServiceComponent component : service.getComponents()){
                    if(component.getComponentIndex().equals(componentIndex)){
                        if(target.get().getSdkFunction() != null){
                            if (target.get().getSdkFunction().getId() != component.getComponentId()) {
                                log.error("Connection point with ID " + target.get().getId() + " does not belong to component with ID " + component.getId());
                                throw new MalformedElementException("Connection point with ID " + target.get().getId() + " does not belong to component with ID " + component.getId());
                            }
                        }else if(target.get().getSdkService() != null){
                            if (target.get().getSdkService().getId() != component.getComponentId()) {
                                log.error("Connection point with ID " + target.get().getId() + " does not belong to component with ID " + component.getId());
                                throw new MalformedElementException("Connection point with ID " + target.get().getId() + " does not belong to component with ID " + component.getId());
                            }
                        }
                    }
                }
            }
        }
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

    private void dispatchPublishRequest(String servicePackagePath, Consumer<String> callback) {
        // TODO: dispatch publish operation to driver, then return immediately
        executor.execute(() -> {
                try {
                    String nsInfoId = cataloguePlugin.uploadNetworkService(servicePackagePath, "multipart/form-data", null);
                    callback.accept(nsInfoId);
                } catch (Exception exc) {
                    log.error(
                        "Could not push service package. Cause: {}",
                        exc.getMessage()
                    );
                    log.debug("Details: ", exc);
                    callback.accept(null);
                }
            }
        );
    }

    private void dispatchUnPublishRequest(String nsInfoId, Consumer<Boolean> callback) {
        // TODO: dispatch unpublish operation to driver, then return immediately
        executor.execute(() -> {
                try {
                    cataloguePlugin.deleteNetworkService(nsInfoId);
                    callback.accept(true);
                } catch (Exception exc) {
                    log.error(
                        "Could not delete service package. Cause: {}",
                        exc.getMessage()
                    );
                    log.debug("Details: ", exc);
                    callback.accept(false);
                }
            }
        );
    }
}

