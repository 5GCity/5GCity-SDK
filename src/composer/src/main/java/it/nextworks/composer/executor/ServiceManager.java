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

import it.nextworks.composer.adaptor.interfaces.ServicesAdaptorProviderInterface;
import it.nextworks.composer.executor.interfaces.FunctionManagerProviderInterface;
import it.nextworks.composer.executor.interfaces.ServiceManagerProviderInterface;
import it.nextworks.composer.executor.repositories.CatalogueRepository;
import it.nextworks.composer.executor.repositories.ConnectionpointRepository;
import it.nextworks.composer.executor.repositories.LinkRepository;
import it.nextworks.composer.executor.repositories.MonitoringParameterRepository;
import it.nextworks.composer.executor.repositories.ScalingAspectRepository;
import it.nextworks.composer.executor.repositories.SdkFunctionRepository;
import it.nextworks.composer.executor.repositories.SdkServiceInstanceRepository;
import it.nextworks.composer.executor.repositories.SdkServiceRepository;
import it.nextworks.composer.plugins.catalogue.FiveGCataloguePlugin;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.ScalingAspect;
import it.nextworks.sdk.SdkFunction;
import it.nextworks.sdk.SdkService;
import it.nextworks.sdk.SdkServiceComponent;
import it.nextworks.sdk.SdkServiceInstance;
import it.nextworks.sdk.enums.SdkServiceComponentType;
import it.nextworks.sdk.enums.SdkServiceStatus;
import it.nextworks.sdk.exceptions.AlreadyPublishedServiceException;
import it.nextworks.sdk.exceptions.ExistingEntityException;
import it.nextworks.sdk.exceptions.MalformedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.NotPublishedServiceException;
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ServiceManager implements ServiceManagerProviderInterface {

    private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);

    @Autowired
    @Qualifier("expressionAdapter")
    private ServicesAdaptorProviderInterface adapter;

    @Autowired
    private SdkServiceRepository serviceRepository;

    @Autowired
    private SdkServiceInstanceRepository serviceInstanceRepository;

    @Autowired
    private SdkFunctionRepository functionRepository;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private ConnectionpointRepository cpRepository;

    @Autowired
    private MonitoringParameterRepository monitoringParamRepository;

    @Autowired
    private ScalingAspectRepository scalingRepository;

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

//	@PostConstruct
//	public void init() {
//		Catalogue catalogue = new Catalogue("5g-catalogue", hostname, false, null, null);
//		catalogueRepo.saveAndFlush(catalogue);
//	}

    @Override
    public List<SdkService> getServices() {
        log.info("Request for all service stored in database");
        List<SdkService> services = serviceRepository.findAll();
        log.info("Returned list of services");
        return services;
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

    @Override
    public String createService(SdkService service)
        throws ExistingEntityException, NotExistingEntityException, MalformedElementException {

        log.info("Storing into database a new service");
        // TODO Find a way to check if service already exists
        // TODO: should we really? We could check the name, but this is the user's responsibility
        if (!service.isValid()) {
            log.error("Malformed SdkService");
            throw new MalformedElementException("Malformed SdkService");
        }
        log.debug("Checking component availability");
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

		log.debug("Updating into database service with id: " + service.getId());

		// Update del service su DB
		serviceRepository.saveAndFlush(service);

		return service.getId().toString();
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
        serviceRepository.delete(s);
    }

    @Override
    public String instantiateService(Long serviceId, List<BigDecimal> parameterValues)
        throws NotExistingEntityException, MalformedElementException {
        log.info("Request for instantiation of service with uuid: " + serviceId);

        // Check if service exists
        Optional<SdkService> optService = serviceRepository.findById(serviceId);

        SdkService service = optService.orElseThrow(() -> {
            log.error("The Service with UUID: " + serviceId + " is not present in database");
            return new NotExistingEntityException("The Service with UUID: " + serviceId + " is not present in database");
        });

        SdkServiceInstance instance;
        try {
            instance = adapter.instantiateSdkService(service, parameterValues);
        } catch (IllegalArgumentException exc) {
            log.error("Malformed instantiation request: {}", exc.getMessage());
            throw new MalformedElementException(exc.getMessage(), exc);
        }
        instance.setStatus(SdkServiceStatus.SAVED);
        serviceInstanceRepository.saveAndFlush(instance);
        log.info(
            "Service {} successfully instantiated. Instance ID {}.",
            serviceId,
            instance.getId()
        );
        return instance.getId().toString();
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
        SdkServiceInstance instance;
        try {
            instance = adapter.instantiateSdkService(service, parameterValues);
        } catch (IllegalArgumentException e) {
            log.error("Malformed instantiation request: {}", e.getMessage());
            throw new MalformedElementException(e.getMessage(), e);
        }
        instance.setStatus(SdkServiceStatus.CHANGING);
        serviceInstanceRepository.saveAndFlush(instance);
        String serviceInstanceId = instance.getId().toString();
        DescriptorTemplate nsd = adapter.generateNetworkServiceDescriptor(instance);

        // A thread will be created to handle this request in order to perform it
        // asynchronously.
        dispatchPublishRequest(
            nsd,
            successful -> {
                if (successful) {
                    log.info("Service instance {} successfully published", serviceInstanceId);
                    instance.setStatus(SdkServiceStatus.COMMITTED);
                    serviceInstanceRepository.saveAndFlush(instance);
                } else {
                    instance.setStatus(SdkServiceStatus.SAVED);
                    serviceInstanceRepository.saveAndFlush(instance);
                    log.error("Error while publishing service instance {}", serviceInstanceId);
                }
            }
        );
        return serviceInstanceId;
    }

    @Override
    public void publishService(Long serviceInstanceId)
        throws NotExistingEntityException, AlreadyPublishedServiceException {
        Optional<SdkServiceInstance> optInstance = serviceInstanceRepository.findById(serviceInstanceId);

        SdkServiceInstance instance = optInstance.orElseThrow(() -> {
            log.error("The Service Instance with UUID: {} is not present in database", serviceInstanceId);
            return new NotExistingEntityException(String.format(
                "The Service with UUID: %s is not present in database",
                serviceInstanceId
            ));
        });

        synchronized (this) { // To avoid multiple simultaneous calls
            if (!instance.getStatus().equals(SdkServiceStatus.SAVED)) {
                log.error("The Service Instance with UUID: {} is not in status SAVED.", serviceInstanceId);
                throw new AlreadyPublishedServiceException(String.format(
                    "The Service Instance with UUID: %s is not in status SAVED",
                    serviceInstanceId
                ));
            }
            instance.setStatus(SdkServiceStatus.CHANGING);
            serviceInstanceRepository.saveAndFlush(instance);
            // After setting the status, no one can operate on this anymore (except us)
        }

        DescriptorTemplate nsd = adapter.generateNetworkServiceDescriptor(instance);
        dispatchPublishRequest(
            nsd,
            successful -> {
                if (successful) {
                    log.info("Service instance {} successfully published", serviceInstanceId);
                    instance.setStatus(SdkServiceStatus.COMMITTED);
                    serviceInstanceRepository.save(instance);
                } else {
                    instance.setStatus(SdkServiceStatus.SAVED);
                    serviceInstanceRepository.save(instance);
                    log.error("Error while publishing service instance {}", serviceInstanceId);
                }
            }
        );
    }

    private void dispatchPublishRequest(DescriptorTemplate nsd, Consumer<Boolean> callback) {
        // TODO: dispatch publish operation to driver, then return immediately
        executor.execute(() -> {
                try {
                    String s = cataloguePlugin.uploadNetworkService(nsd, "application/json", null);
                    callback.accept(true);
                } catch (IOException exc) {
                    log.error("Could not push descriptor %s.", nsd.getMetadata().getDescriptorId());
                    callback.accept(false);
                }
            }
        );
    }

    private void dispatchUnPublishRequest(Long serviceInstanceId, Consumer<Boolean> callback) {
        // TODO: dispatch unpublish operation to driver, then return immediately
        throw new NotYetImplementedException();
    }


    @Override
    public void unPublishService(Long serviceInstanceId) throws NotExistingEntityException, NotPublishedServiceException {
        log.info("Requested deletion of the publication of the service instance with id: {}", serviceInstanceId);
        Optional<SdkServiceInstance> optInstance = serviceInstanceRepository.findById(serviceInstanceId);
        SdkServiceInstance instance = optInstance.orElseThrow(() -> {
            log.error("The Service Instance with UUID: {} is not present in database", serviceInstanceId);
            return new NotExistingEntityException(String.format(
                "The Service with UUID: %s is not present in database",
                serviceInstanceId
            ));
        });

        synchronized (this) { // To avoid multiple simultaneous calls
            // Check if is already published
            if (!instance.getStatus().equals(SdkServiceStatus.COMMITTED)) {
                log.error("The Service Instance with UUID: {} is not ins status COMMITTED.", serviceInstanceId);
                throw new NotPublishedServiceException(String.format(
                    "The Service Instance with UUID: %s is not ins status COMMITTED",
                    serviceInstanceId
                ));
            }
            instance.setStatus(SdkServiceStatus.CHANGING);
            serviceInstanceRepository.saveAndFlush(instance);
            // After setting the status, no one can operate on this anymore (except us)
        }

        dispatchUnPublishRequest(
            serviceInstanceId,
            successful -> {
                if (successful) {
                    instance.setStatus(SdkServiceStatus.SAVED);
                    serviceInstanceRepository.saveAndFlush(instance);
                    log.info("Successfully un-published instance {}", serviceInstanceId);
                } else {
                    instance.setStatus(SdkServiceStatus.COMMITTED);
                    serviceInstanceRepository.saveAndFlush(instance);
                    log.error("Error while un-publishing instance {}", serviceInstanceId);
                }
            }
        );

    }

    @Override
    public void updateScalingAspect(Long serviceId, Set<ScalingAspect> scalingAspects)
        throws NotExistingEntityException, MalformedElementException {
        log.info("Request to update list of scalingAspects for a specific SDK Service " + serviceId);
        Optional<SdkService> service = serviceRepository.findById(serviceId);
        if (!service.isPresent()) {
            log.error("The Service with ID: " + serviceId + " is not present in database");
            throw new NotExistingEntityException("The Service with ID: " + serviceId + " is not present in database");
        }
        for (ScalingAspect scale : scalingAspects) {
            if (!scale.isValid()) {
                log.error("Malformed Scaling Aspect");
                throw new MalformedElementException("Malformed Scaling Aspect");
            }
        }
        log.debug("Updating list of scaling aspects on service");
        service.get().setScalingAspect(scalingAspects);
        log.debug("Updating list of scaling aspects on database");
        serviceRepository.saveAndFlush(service.get());
    }

    @Override
    public void deleteScalingAspect(Long serviceId, Set<ScalingAspect> scalingAspects)
        throws NotExistingEntityException, MalformedElementException {
        log.info("Request to delete a list of scalingAspects for a specific SDK Service " + serviceId);
        Optional<SdkService> service = serviceRepository.findById(serviceId);
        if (!service.isPresent()) {
            log.error("The Service with ID: " + serviceId + " is not present in database");
            throw new NotExistingEntityException("The Service with ID: " + serviceId + " is not present in database");
        }
        for (ScalingAspect scale : scalingAspects) {
            if (!scale.isValid()) {
                log.error("Malformed Scaling Aspect");
                throw new MalformedElementException("Malformed Scaling Aspect");
            }
        }
        log.debug("All scaling aspects are valid");
        service.get().getScalingAspect().removeAll(scalingAspects);
        log.debug("All scaling aspects have been deleted. Saving to database");
        serviceRepository.saveAndFlush(service.get());
    }

    @Override
    public List<ScalingAspect> getScalingAspect(Long serviceId) throws NotExistingEntityException {
        log.info("Request to get the list of scalingAspects for a specific SDK Service " + serviceId);
        Optional<SdkService> service = serviceRepository.findById(serviceId);
        if (!service.isPresent()) {
            log.error("The Service with ID: " + serviceId + " is not present in database");
            throw new NotExistingEntityException("The Service with ID: " + serviceId + " is not present in database");
        }
        return new ArrayList<>(service.get().getScalingAspect());
    }

    @Override
    public void updateMonitoringParameters(Long serviceId, Set<MonitoringParameter> monitoringParameters)
        throws NotExistingEntityException, MalformedElementException {
        log.info("Request to update list of scalingAspects for a specific SDK Service " + serviceId);
        Optional<SdkService> service = serviceRepository.findById(serviceId);
        if (!service.isPresent()) {
            log.error("The Service with ID: " + serviceId + " is not present in database");
            throw new NotExistingEntityException("The Service with ID: " + serviceId + " is not present in database");
        }
        for (MonitoringParameter param : monitoringParameters) {
            if (!param.isValid()) {
                log.error("Malformed MonitoringParameter");
                throw new MalformedElementException("Malformed MonitoringParameter");
            }
        }
        log.debug("Updating list of monitoring parameters on service");
        service.get().setMonitoringParameters(monitoringParameters);
        log.debug("Updating list of monitoring parameters on database");
        serviceRepository.saveAndFlush(service.get());

    }

    @Override
    public void deleteMonitoringParameters(Long serviceId, Set<MonitoringParameter> monitoringParameters)
        throws NotExistingEntityException, MalformedElementException {
        log.info("Request to delete a list of monitoring parameters for a specific SDK Service " + serviceId);
        Optional<SdkService> service = serviceRepository.findById(serviceId);
        if (!service.isPresent()) {
            log.error("The Service with ID: " + serviceId + " is not present in database");
            throw new NotExistingEntityException("The Service with ID: " + serviceId + " is not present in database");
        }
        for (MonitoringParameter param : monitoringParameters) {
            if (!param.isValid()) {
                log.error("Malformed MonitoringParameter");
                throw new MalformedElementException("Malformed MonitoringParameter");
            }
        }
        log.debug("All monitoring parameters are valid. Deleting them from SDK Service");
        service.get().getMonitoringParameters().removeAll(monitoringParameters);
        log.debug("All monitoring parameters have been deleted. Saving to database");
        serviceRepository.saveAndFlush(service.get());

    }

    @Override
    public List<MonitoringParameter> getMonitoringParameters(Long serviceId) throws NotExistingEntityException {
        log.info("Request to get the list of monitoring parameters for a specific SDK Service " + serviceId);
        Optional<SdkService> service = serviceRepository.findById(serviceId);
        if (!service.isPresent()) {
            log.error("The Service with ID: " + serviceId + " is not present in database");
            throw new NotExistingEntityException("The Service with ID: " + serviceId + " is not present in database");
        }
        return new ArrayList<>(service.get().getMonitoringParameters());
    }
}
