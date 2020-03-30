/*
 * Copyright 2020 Nextworks s.r.l.
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
import it.nextworks.composer.auth.KeycloakUtils;
import it.nextworks.composer.controller.elements.SliceResource;
import it.nextworks.composer.executor.interfaces.FunctionManagerProviderInterface;
import it.nextworks.composer.executor.interfaces.ServiceManagerProviderInterface;
import it.nextworks.composer.executor.repositories.*;
import it.nextworks.composer.plugins.catalogue.ArchiveBuilder;
import it.nextworks.composer.plugins.catalogue.FiveGCataloguePlugin;
import it.nextworks.nfvmano.libs.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.common.exceptions.NotAuthorizedOperationException;
import it.nextworks.nfvmano.libs.common.exceptions.NotPermittedOperationException;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.sdk.*;
import it.nextworks.sdk.enums.*;
import it.nextworks.sdk.exceptions.AlreadyPublishedServiceException;
import it.nextworks.sdk.exceptions.MalformedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.NotPublishedServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
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
    private SliceRepository sliceRepository;

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

    @Value("${keycloak.enabled:true}")
    private boolean keycloakEnabled;

    @Autowired
    private KeycloakUtils keycloakUtils;

    @Value("${admin.user.name:admin}")
    private String adminUserName;

    public ServiceManager() {

    }

    @Override
    public List<SdkService> getServices(String sliceId) throws NotExistingEntityException, NotAuthorizedOperationException {
        log.info("Request for all service stored in database");
        //check if the slice is present
        if (sliceId != null) {
            Optional<SliceResource> sliceOptional = sliceRepository.findBySliceId(sliceId);
            if (!sliceOptional.isPresent()) {
                log.error("Slice with sliceId " + sliceId + " does not exist");
                throw new NotExistingEntityException("Slice with sliceId " + sliceId + " does not exist");
            }
            if(keycloakEnabled)
                keycloakUtils.checkUserSlices(keycloakUtils.getUserNameFromJWT(), sliceId);
        }

        List<SdkService> serviceList = serviceRepository.findAll();
        Iterator<SdkService> serviceIterator = serviceList.iterator();
        for (; serviceIterator.hasNext() ;) {
            SdkService service = serviceIterator.next();
            //filter services per slice
            if (sliceId != null && !service.getSliceId().equals(sliceId))
                serviceIterator.remove();
            /*
                Filter services if Keycloak is enabled
                User can view a service if:
                    - visibility is public or user is the owner if the visibility is private
                    - user accessLevel <= service accessLevel
                    - user belongs to the slice
            */
            else if (keycloakEnabled && !keycloakUtils.getUserNameFromJWT().equals(adminUserName)){
                if(keycloakUtils.getAccessLevelFromJWT().compareTo(service.getAccessLevel()) > 0)
                    serviceIterator.remove();
                else if (service.getVisibility().equals(Visibility.PRIVATE) &&
                    !keycloakUtils.getUserNameFromJWT().equals(service.getOwnerId())){
                    serviceIterator.remove();
                }
            }
        }
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
        throws NotExistingEntityException, MalformedElementException, AlreadyExistingEntityException, NotAuthorizedOperationException{

        log.info("Storing into database a new service");

        if(service.getId() != null){
            //log.error("Service ID cannot be specified in service creation");
            throw new MalformedElementException("Service ID cannot be specified in service creation");
        }

        service.isValid();
        log.debug("Service is valid");

        if(keycloakEnabled)
            authSecurityChecks(service, 0);

        checkAndResolveService(service);

        for(SdkServiceComponent component : service.getComponents()){
            if (component.getId() != null) {
                //log.error("Component ID cannot be specified in service creation");
                throw new MalformedElementException("Component ID cannot be specified in service creation");
            }
        }
        for(MonitoringParameter mp : service.getExtMonitoringParameters()){
            if (mp.getId() != null) {
                //log.error("Monitoring parameter ID cannot be specified in service creation");
                throw new MalformedElementException("Monitoring parameter ID cannot be specified in service creation");
            }
        }
        for(MonitoringParameter mp : service.getIntMonitoringParameters()){
            if (mp.getId() != null) {
                //log.error("Monitoring parameter ID cannot be specified in function creation");
                throw new MalformedElementException("Monitoring parameter ID cannot be specified in function creation");
            }
        }
        for(ConnectionPoint cp : service.getConnectionPoint()){
            if (cp.getId() != null) {
                //log.error("Connection point ID cannot be specified in service creation");
                throw new MalformedElementException("Connection point ID cannot be specified in service creation");
            }
        }
        for(Link link : service.getLink()){
            if (link.getId() != null) {
                //log.error("Link ID cannot be specified in service creation");
                throw new MalformedElementException("Link ID cannot be specified in service creation");
            }
        }
        for(L3Connectivity lc : service.getL3Connectivity()){
            if (lc.getId() != null) {
                //log.error("L3 connectivity ID cannot be specified in service creation");
                throw new MalformedElementException("L3 connectivity ID cannot be specified in service creation");
            }
        }
        for(ServiceAction sa : service.getActions()){
            if (sa.getId() != null) {
                //log.error("Service action ID cannot be specified in service creation");
                throw new MalformedElementException("Service action ID cannot be specified in service creation");
            }
        }
        for(ServiceActionRule rule : service.getActionRules()){
            if (rule.getId() != null) {
                //log.error("Service action rule ID cannot be specified in service creation");
                throw new MalformedElementException("Service action rule ID cannot be specified in service creation");
            }
        }

        log.debug("Storing into database service with name: " + service.getName());
        // Saving the service
        serviceRepository.saveAndFlush(service);
        return service.getId().toString();
    }

    @Override
    public String updateService(SdkService service) throws NotExistingEntityException, MalformedElementException, NotPermittedOperationException, NotAuthorizedOperationException {
		log.info("Updating an existing service with ID " + service.getId());
        if(service.getId() == null){
            //log.error("Service ID needs to be specified");
            throw new MalformedElementException("Service ID needs to be specified");
        }

        service.isValid();
		log.debug("Service is valid");

		// Check if service exists
		Optional<SdkService> srv = serviceRepository.findById(service.getId());
		if (!srv.isPresent()) {
			//log.error("Service with ID " + service.getId() + " not present in database");
			throw new NotExistingEntityException("Service with ID " + service.getId() + " not present in database");
		}
		log.debug("Service found on db");

        if(keycloakEnabled)
            authSecurityChecks(srv.get(), 0);

        //update not allowed if the service has at least one descriptor
        List<SdkServiceDescriptor> descriptors = serviceDescriptorRepository.findByTemplateId(service.getId());
        if(descriptors.size() != 0){
            List<Long> descriptorIds = descriptors.stream().map(SdkServiceDescriptor::getId).collect(Collectors.toList());
            //log.error("Service with ID " + service.getId() + " has descriptors with IDs " + descriptorIds.toString() + ". Please delete them before updating");
            throw  new NotPermittedOperationException("Service with ID " + service.getId() + " has descriptors with IDs " + descriptorIds.toString() + ". Please delete them before updating");
        }

        //update not allowed if the service is used by other services
        List<SubService> subServices = subServiceRepository.findByComponentId(service.getId());
        if(subServices.size() != 0){
            List<Long> serviceIds = subServices.stream().map(SubService::getOuterService).map(SdkService::getId).collect(Collectors.toList());
            //log.error("Service with ID " + service.getId() + " used by services with IDs " + serviceIds.toString());
            throw  new NotPermittedOperationException("Service with ID " + service.getId() + " used by services with IDs " + serviceIds.toString());
        }

        for(SdkServiceComponent component : service.getComponents()){
            if (component.getId() != null) {
                Optional<SubFunction> subFunction = subFunctionRepository.findById(component.getId());
                Optional<SubService> subService = subServiceRepository.findById(component.getId());
                if (!subFunction.isPresent() && !subService.isPresent()) {
                    //log.error("Component with ID " + component.getId() + " is not present in database");
                    throw new NotExistingEntityException("Component with ID " + component.getId() + " is not present in database");
                }
                if(subFunction.isPresent()){
                    if ((subFunction.get().getOuterService() == null) || (!subFunction.get().getOuterService().getId().equals(service.getId()))) {
                        //log.error("Component with ID " + component.getId() + " does not belong to service with ID " + service.getId());
                        throw new NotPermittedOperationException("Component with ID " + component.getId() + " does not belong to service with ID " + service.getId());
                    }
                }
                if(subService.isPresent()){
                    if ((subService.get().getOuterService() == null) || (!subService.get().getOuterService().getId().equals(service.getId()))) {
                        //log.error("Component with ID " + component.getId() + " does not belong to service with ID " + service.getId());
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
                    //log.error("Monitoring parameter with ID " + param.getId() + " is not present in database");
                    throw new NotExistingEntityException("Monitoring parameter with ID " + param.getId() + " is not present in database");
                }
                if ((mp.get().getSdkFunction() != null)
                        || ((mp.get().getSdkServiceExt() != null) && (!mp.get().getSdkServiceExt().getId().equals(service.getId())))
                        || ((mp.get().getSdkServiceInt() != null) && (!mp.get().getSdkServiceInt().getId().equals(service.getId())))
                ){
                    //log.error("Monitoring parameter with ID " + param.getId() + " does not belong to service with ID " + service.getId());
                    throw new NotPermittedOperationException("Monitoring parameter with ID " + param.getId() + " does not belong to service with ID " + service.getId());
                }
            }
        }
        for(ConnectionPoint param : service.getConnectionPoint()) {
            if (param.getId() != null) {
                Optional<ConnectionPoint> cp = cpRepository.findById(param.getId());
                if (!cp.isPresent()) {
                    //log.error("Connection point with ID " + param.getId() + " is not present in database");
                    throw new NotExistingEntityException("Connection point with ID " + param.getId() + " is not present in database");
                }
                if ((cp.get().getSdkService() == null) || (!cp.get().getSdkService().getId().equals(service.getId()))) {
                    //log.error("Connection point with ID " + param.getId() + " does not belong to service with ID " + service.getId());
                    throw new NotPermittedOperationException("Connection point with ID " + param.getId() + " does not belong to service with ID " + service.getId());
                }
            }
        }
        for(Link link : service.getLink()) {
            if (link.getId() != null) {
                Optional<Link> rp = linkRepository.findById(link.getId());
                if (!rp.isPresent()) {
                    //log.error("Link with ID " + link.getId() + " is not present in database");
                    throw new NotExistingEntityException("Link with ID " + link.getId() + " is not present in database");
                }
                if ((rp.get().getService() == null) || (!rp.get().getService().getId().equals(service.getId()))) {
                    //log.error("Link with ID " + link.getId() + " does not belong to service with ID " + service.getId());
                    throw new NotPermittedOperationException("Link with ID " + link.getId() + " does not belong to service with ID " + service.getId());
                }
            }
        }
        for(L3Connectivity lc : service.getL3Connectivity()) {
            if (lc.getId() != null) {
                Optional<L3Connectivity> rp = l3ConnectivityRepository.findById(lc.getId());
                if (!rp.isPresent()) {
                    //log.error("L3 connectivity with ID " + lc.getId() + " is not present in database");
                    throw new NotExistingEntityException("L3 connectivity with ID " + lc.getId() + " is not present in database");
                }
                if ((rp.get().getService() == null) || (!rp.get().getService().getId().equals(service.getId()))) {
                    //log.error("L3 connectivity with ID " + lc.getId() + " does not belong to service with ID " + service.getId());
                    throw new NotPermittedOperationException("L3 connectivity with ID " + lc.getId() + " does not belong to service with ID " + service.getId());
                }
            }
        }
        for(ServiceAction sa : service.getActions()) {
            if (sa.getId() != null) {
                Optional<ServiceAction> rp = serviceActionRepository.findById(sa.getId());
                if (!rp.isPresent()) {
                    //log.error("Service action with ID " + sa.getId() + " is not present in database");
                    throw new NotExistingEntityException("Service action with ID " + sa.getId() + " is not present in database");
                }
                if ((rp.get().getSdkService() == null) || (!rp.get().getSdkService().getId().equals(service.getId()))) {
                    //log.error("Service action with ID " + sa.getId() + " does not belong to service with ID " + service.getId());
                    throw new NotPermittedOperationException("Service action with ID " + sa.getId() + " does not belong to service with ID " + service.getId());
                }
            }
        }
        for(ServiceActionRule ar : service.getActionRules()) {
            if (ar.getId() != null) {
                Optional<ServiceActionRule> rp = serviceActionRuleRepository.findById(ar.getId());
                if (!rp.isPresent()) {
                    //log.error("Service action rule with ID " + ar.getId() + " is not present in database");
                    throw new NotExistingEntityException("Service action rule with ID " + ar.getId() + " is not present in database");
                }
                if ((rp.get().getSdkService() == null) || (!rp.get().getSdkService().getId().equals(service.getId()))) {
                    //log.error("Service action rule with ID " + ar.getId() + " does not belong to service with ID " + service.getId());
                    throw new NotPermittedOperationException("Service action rule with ID " + ar.getId() + " does not belong to service with ID " + service.getId());
                }
            }
        }

        try{
            checkAndResolveService(service);
        }catch (AlreadyExistingEntityException e){
            //exception cannot be raised in this case
        }

        if(keycloakEnabled)
            authSecurityChecks(service, 0);

		log.debug("Updating into database service with id: " + service.getId());
        cleanOldRelations(srv.get());
        // Update service on DB
        serviceRepository.saveAndFlush(service);
        return service.getId().toString();
    }

    @Override
    public SdkService getServiceById(Long id) throws NotExistingEntityException, NotAuthorizedOperationException {
        log.info("Request for service with ID " + id);
        Optional<SdkService> service = serviceRepository.findById(id);
        if (service.isPresent()) {
            if(keycloakEnabled)
                authSecurityChecks(service.get(), 1);
            return service.get();
        } else {
            //log.error("Service with ID " + id + " not found");
            throw new NotExistingEntityException("Service with ID " + id + " not found");
        }
    }

    @Override
    public void deleteService(Long serviceId) throws NotExistingEntityException, NotPermittedOperationException, NotAuthorizedOperationException {
        log.info("Request for deletion of service with ID " + serviceId);
        // No deletion required: all that depends on the service will cascade.
        Optional<SdkService> service = serviceRepository.findById(serviceId);
        SdkService s = service.orElseThrow(() -> {
            //log.error("Service with ID " + serviceId + " not found");
            return new NotExistingEntityException("Service with ID " + serviceId + " not found");
        });

        if(keycloakEnabled)
            authSecurityChecks(s, 0);

        //delete not allowed if the service has at least one descriptor
        List<SdkServiceDescriptor> descriptors = serviceDescriptorRepository.findByTemplateId(serviceId);
        if(descriptors.size() != 0){
            List<Long> descriptorIds = descriptors.stream().map(SdkServiceDescriptor::getId).collect(Collectors.toList());
            //log.error("Service with ID " + serviceId + " has descriptors with IDs " + descriptorIds.toString() + ". Please delete them before updating");
            throw  new NotPermittedOperationException("Service with ID " + serviceId + " has descriptors with IDs " + descriptorIds.toString() + ". Please delete them before updating");
        }

        //delete not allowed if the service is used by other services
        List<SubService> subServices = subServiceRepository.findByComponentId(serviceId);
        if(subServices.size() != 0){
            List<Long> serviceIds = subServices.stream().map(SubService::getOuterService).map(SdkService::getId).collect(Collectors.toList());
            //log.error("Service with ID " + serviceId + " used by services with IDs " + serviceIds.toString());
            throw  new NotPermittedOperationException("Service with ID " + serviceId + " used by services with IDs " + serviceIds.toString());
        }

        serviceRepository.delete(s);
    }

    @Override
    public void updateMonitoringParameters(Long serviceId, Set<MonitoringParameter> extMonitoringParameters, Set<MonitoringParameter> intMonitoringParameters)
        throws NotExistingEntityException, MalformedElementException, NotPermittedOperationException, NotAuthorizedOperationException {
        log.info("Request to update list of scalingAspects for a specific SDK Service " + serviceId);
        Set<MonitoringParameter> monitoringParameters = new HashSet<>();
        monitoringParameters.addAll(extMonitoringParameters);
        monitoringParameters.addAll(intMonitoringParameters);
        for(MonitoringParameter param : monitoringParameters) {
            if (!param.isValid()) {
                //log.error("Monitoring param list provided cannot be validated");
                throw new MalformedElementException("Monitoring param list provided cannot be validated");
            }
            if (param.getId() != null) {
                Optional<MonitoringParameter> mp = monitoringParamRepository.findById(param.getId());
                if (!mp.isPresent()) {
                    //log.error("Monitoring parameter with ID " + param.getId() + " is not present in database");
                    throw new NotExistingEntityException("Monitoring parameter with ID " + param.getId() + " is not present in database");
                }
                if ((mp.get().getSdkFunction() != null)
                        || ((mp.get().getSdkServiceExt() != null) && (!mp.get().getSdkServiceExt().getId().equals(serviceId)))
                        || ((mp.get().getSdkServiceInt() != null) && (!mp.get().getSdkServiceInt().getId().equals(serviceId)))
                ){
                    //log.error("Monitoring parameter with ID " + param.getId() + " does not belong to service with ID " + serviceId);
                    throw new NotPermittedOperationException("Monitoring parameter with ID " + param.getId() + " does not belong to service with ID " + serviceId);
                }
            }
        }

        Optional<SdkService> service = serviceRepository.findById(serviceId);
        if (!service.isPresent()) {
            //log.error("Service with ID " + serviceId + " is not present in database");
            throw new NotExistingEntityException("Service with ID " + serviceId + " is not present in database");
        }

        if(keycloakEnabled)
            authSecurityChecks(service.get(), 0);

        //update not allowed if the service has at least one descriptor
        List<SdkServiceDescriptor> descriptors = serviceDescriptorRepository.findByTemplateId(serviceId);
        if(descriptors.size() != 0){
            List<Long> descriptorIds = descriptors.stream().map(SdkServiceDescriptor::getId).collect(Collectors.toList());
            //log.error("Service with ID " + serviceId + " has descriptors with IDs " + descriptorIds.toString() + ". Please delete them before updating");
            throw  new NotPermittedOperationException("Service with ID " + serviceId + " has descriptors with IDs " + descriptorIds.toString() + ". Please delete them before updating");
        }

        //update not allowed if the service is used by other services
        List<SubService> subServices = subServiceRepository.findByComponentId(serviceId);
        if(subServices.size() != 0){
            List<Long> serviceIds = subServices.stream().map(SubService::getOuterService).map(SdkService::getId).collect(Collectors.toList());
            //log.error("Service with ID " + serviceId + " used by services with IDs " + serviceIds.toString());
            throw  new NotPermittedOperationException("Service with ID " + serviceId + " used by services with IDs " + serviceIds.toString());
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

        service.get().isValid();

        log.debug("Updating list of monitoring parameters on database");
        serviceRepository.saveAndFlush(service.get());
    }

    @Override
    public void deleteMonitoringParameters(Long serviceId, Long monitoringParameterId)
        throws NotExistingEntityException, MalformedElementException, NotPermittedOperationException, NotAuthorizedOperationException {
        log.info("Request to delete a monitoring parameter with ID " + monitoringParameterId + " for a specific SDK Service " + serviceId);
        Optional<MonitoringParameter> mp = monitoringParamRepository.findById(monitoringParameterId);
        if (!mp.isPresent()) {
            //log.error("Monitoring parameter with ID " + monitoringParameterId + " is not present in database");
            throw new NotExistingEntityException("Monitoring parameter with ID " + monitoringParameterId + " is not present in database");
        }

        Optional<SdkService> service = serviceRepository.findById(serviceId);
        if (!service.isPresent()) {
            //log.error("Service with ID " + serviceId + " is not present in database");
            throw new NotExistingEntityException("Service with ID " + serviceId + " is not present in database");
        }

        if(keycloakEnabled)
            authSecurityChecks(service.get(), 0);

        if ((mp.get().getSdkFunction() != null)
                || ((mp.get().getSdkServiceExt() != null) && (!mp.get().getSdkServiceExt().getId().equals(serviceId)))
                || ((mp.get().getSdkServiceInt() != null) && (!mp.get().getSdkServiceInt().getId().equals(serviceId)))
        ){
            //log.error("Monitoring parameter with ID " + monitoringParameterId + " does not belong to service with ID " + serviceId);
            throw new NotPermittedOperationException("Monitoring parameter with ID " + monitoringParameterId + " does not belong to service with ID " + serviceId);
        }

        //delete not allowed if the service has at least one descriptor
        List<SdkServiceDescriptor> descriptors = serviceDescriptorRepository.findByTemplateId(serviceId);
        if(descriptors.size() != 0){
            List<Long> descriptorIds = descriptors.stream().map(SdkServiceDescriptor::getId).collect(Collectors.toList());
            //log.error("Service with ID " + serviceId + " has descriptors with IDs " + descriptorIds.toString() + ". Please delete them before updating");
            throw  new NotPermittedOperationException("Service with ID " + serviceId + " has descriptors with IDs " + descriptorIds.toString() + ". Please delete them before updating");
        }

        //delete not allowed if the service is used by other services
        List<SubService> subServices = subServiceRepository.findByComponentId(serviceId);
        if(subServices.size() != 0){
            List<Long> serviceIds = subServices.stream().map(SubService::getOuterService).map(SdkService::getId).collect(Collectors.toList());
            //log.error("Service with ID " + serviceId + " used by services with IDs " + serviceIds.toString());
            throw  new NotPermittedOperationException("Service with ID " + serviceId + " used by services with IDs " + serviceIds.toString());
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

        service.get().isValid();

        serviceRepository.saveAndFlush(service.get());
        log.debug("Monitoring parameter has been deleted.");
    }

    @Override
    public MonitoringParameterWrapper getMonitoringParameters(Long serviceId) throws NotExistingEntityException, NotAuthorizedOperationException {
        log.info("Request to get the list of monitoring parameters for a specific SDK Service " + serviceId);
        Optional<SdkService> service = serviceRepository.findById(serviceId);
        if (!service.isPresent()) {
            //log.error("Service with ID " + serviceId + " is not present in database");
            throw new NotExistingEntityException("Service with ID " + serviceId + " is not present in database");
        }
        if(keycloakEnabled)
            authSecurityChecks(service.get(), 1);
        return new MonitoringParameterWrapper(service.get().getExtMonitoringParameters(), service.get().getIntMonitoringParameters());
    }

    @Override
    public String publishService(Long serviceId, List<BigDecimal> parameterValues, String authorization)
        throws NotExistingEntityException, MalformedElementException, NotPermittedOperationException, NotAuthorizedOperationException {
        log.info("Request for publication of service with ID " + serviceId);
        // Check if service exists
        Optional<SdkService> optService = serviceRepository.findById(serviceId);

        SdkService service = optService.orElseThrow(() -> {
            //log.error("Service with ID " + serviceId + " is not present in database");
            return new NotExistingEntityException("Service with ID " + serviceId + " is not present in database");
        });

        if(keycloakEnabled)
            authSecurityChecks(service, 1);

        SdkServiceDescriptor descriptor;
        try {
            descriptor = adapter.createServiceDescriptor(service, parameterValues);
        } catch (IllegalStateException | IllegalArgumentException e) {
            //log.error("Malformed create-descriptor request: {}", e.getMessage());
            throw new MalformedElementException(e.getMessage(), e);
        }

        //check if all subfunctions are COMMITTED, if not ask to commit them
        checkFunctionStatus(descriptor.getSubDescriptors());

        descriptor.setStatus(SdkServiceStatus.CHANGING);
        serviceDescriptorRepository.saveAndFlush(descriptor);

        String serviceDescriptorId = descriptor.getId().toString();

        DescriptorTemplate nsd;
        try {
            nsd = adapter.generateNetworkServiceDescriptor(descriptor);
        }catch(IllegalStateException e){
            descriptor.setStatus(SdkServiceStatus.SAVED);
            serviceDescriptorRepository.saveAndFlush(descriptor);
            throw new MalformedElementException(e.getMessage(), e);
        }

        Set<MonitoringParameter> monitoringParameters = new HashSet<>();
        monitoringParameters.addAll(service.getExtMonitoringParameters());
        monitoringParameters.addAll(service.getIntMonitoringParameters());
        ActionWrapper toSend = new ActionWrapper(service.getActions(), service.getActionRules());
        modifyActionWrapper(service, toSend);
        String servicePackagePath = ArchiveBuilder.createNSCSAR(nsd, monitoringParameters, toSend);

        // A thread will be created to handle this request in order to perform it
        // asynchronously.
        dispatchPublishRequest(
            servicePackagePath,
            service.getSliceId(),
            authorization,
            nsInfoId -> {
                if (nsInfoId != null) {
                    log.info("Service descriptor with ID {} successfully published to project {}", serviceDescriptorId, service.getSliceId());
                    descriptor.setStatus(SdkServiceStatus.COMMITTED);
                    descriptor.setNsInfoId(nsInfoId);
                    serviceDescriptorRepository.saveAndFlush(descriptor);
                } else {
                    descriptor.setStatus(SdkServiceStatus.SAVED);
                    serviceDescriptorRepository.saveAndFlush(descriptor);
                    log.error("Error while publishing service descriptor with ID {} to project {}", serviceDescriptorId, service.getSliceId());
                }
            }
        );
        return serviceDescriptorId;
    }

    @Override
    public String createServiceDescriptor(Long serviceId, List<BigDecimal> parameterValues)
        throws NotExistingEntityException, MalformedElementException, NotAuthorizedOperationException {
        log.info("Request create-descriptor of service with ID " + serviceId);

        // Check if service exists
        Optional<SdkService> optService = serviceRepository.findById(serviceId);

        SdkService service = optService.orElseThrow(() -> {
            //log.error("Service with ID " + serviceId + " is not present in database");
            return new NotExistingEntityException("Service with ID " + serviceId + " is not present in database");
        });

        if(keycloakEnabled)
            authSecurityChecks(service, 1);

        SdkServiceDescriptor descriptor;
        try {
            descriptor = adapter.createServiceDescriptor(service, parameterValues);
        } catch (IllegalStateException | IllegalArgumentException e) {
            //log.error("Malformed create-descriptor request: {}", e.getMessage());
            throw new MalformedElementException(e.getMessage(), e);
        }
        descriptor.setStatus(SdkServiceStatus.SAVED);
        serviceDescriptorRepository.saveAndFlush(descriptor);
        log.info("Descriptor for service {} successfully created. Descriptor ID {}.", serviceId, descriptor.getId());
        return descriptor.getId().toString();
    }

    @Override
    public List<SdkServiceDescriptor> getAllDescriptors(String sliceId) throws NotExistingEntityException, NotAuthorizedOperationException {
        log.info("Request for all service descriptors stored in database");
        //check if the slice is present
        if (sliceId != null) {
            Optional<SliceResource> sliceOptional = sliceRepository.findBySliceId(sliceId);
            if (!sliceOptional.isPresent()) {
                log.error("Slice with sliceId " + sliceId + " does not exist");
                throw new NotExistingEntityException("Slice with sliceId " + sliceId + " does not exist");
            }
            if(keycloakEnabled)
                keycloakUtils.checkUserSlices(keycloakUtils.getUserNameFromJWT(), sliceId);
        }
        List<SdkServiceDescriptor> descriptors = serviceDescriptorRepository.findAll();
        Iterator<SdkServiceDescriptor> descriptorIterator = descriptors.iterator();
        for (; descriptorIterator.hasNext() ;) {
            //filters descriptors per slice
            SdkServiceDescriptor descriptor = descriptorIterator.next();
            if (sliceId != null && !descriptor.getSliceId().equals(sliceId))
                descriptorIterator.remove();
            /*
                Filter descriptors if Keycloak is enabled
                User can view a descriptors if:
                    - visibility of the corresponding service is public or user is the owner if the visibility is private
                    - user accessLevel <= service accessLevel
                    - user belongs to the slice
            */
            else if (keycloakEnabled && !keycloakUtils.getUserNameFromJWT().equals(adminUserName)){
                if(keycloakUtils.getAccessLevelFromJWT().compareTo(descriptor.getTemplate().getAccessLevel()) > 0)
                    descriptorIterator.remove();
                else if (descriptor.getTemplate().getVisibility().equals(Visibility.PRIVATE) &&
                    !keycloakUtils.getUserNameFromJWT().equals(descriptor.getTemplate().getOwnerId())){
                    descriptorIterator.remove();
                }
            }
        }
        if(descriptors.size() == 0){
            log.debug("No service descriptors are available");
        } else
            log.debug("Service descriptors present in database: " + descriptors.size());
        return descriptors;
    }

    @Override
    public SdkServiceDescriptor getServiceDescriptor(Long descriptorId)
        throws NotExistingEntityException, NotAuthorizedOperationException {
        log.info("Request for service descriptor with ID {}", descriptorId);
        Optional<SdkServiceDescriptor> byId = serviceDescriptorRepository.findById(descriptorId);
        //check if user can access the slice
        if(byId.isPresent()){
            if(keycloakEnabled)
                authSecurityChecks(byId.get().getTemplate(), 1);
        }
        return byId.orElseThrow(() -> {
            //log.error("Descriptor with ID {} not found", descriptorId);
            return new NotExistingEntityException(String.format("Descriptor with ID %d not found", descriptorId));
        });
    }

    @Override
    public void deleteServiceDescriptor(Long descriptorId) throws NotExistingEntityException, NotPermittedOperationException, NotAuthorizedOperationException {
        log.info("Request for deletion of service descriptor with ID {}", descriptorId);
        Optional<SdkServiceDescriptor> optDescriptor = serviceDescriptorRepository.findById(descriptorId);
        SdkServiceDescriptor descriptor = optDescriptor.orElseThrow(() -> {
            //log.error("Descriptor with ID {} not found", descriptorId);
            return new NotExistingEntityException(String.format("Descriptor with ID %d not found", descriptorId));
        });

        if(keycloakEnabled)
            authSecurityChecks(descriptor.getTemplate(), 0);

        //delete not allowed if the service is published to catalogue
        if(descriptor.getStatus().equals(SdkServiceStatus.COMMITTED)){
            //log.error("Service with ID " + descriptor.getTemplate().getId() + " published to the catalogue. Please unpublish it before deleting the descriptor");
            throw  new NotPermittedOperationException("Service with ID " + descriptor.getTemplate().getId() + " published to the catalogue. Please unpublish it before deleting the descriptor");
        }
        serviceDescriptorRepository.delete(descriptor);
    }

    @Override
    public void publishService(Long serviceDescriptorId, String authorization)
        throws NotExistingEntityException, AlreadyPublishedServiceException, NotPermittedOperationException, MalformedElementException, NotAuthorizedOperationException {
        Optional<SdkServiceDescriptor> optDescriptor = serviceDescriptorRepository.findById(serviceDescriptorId);

        SdkServiceDescriptor descriptor = optDescriptor.orElseThrow(() -> {
            //log.error("Service descriptor with ID {} is not present in database", serviceDescriptorId);
            return new NotExistingEntityException(String.format("Service descriptor with ID %s is not present in database", serviceDescriptorId));
        });

        if(keycloakEnabled)
            authSecurityChecks(descriptor.getTemplate(), 1);

        synchronized (this) { // To avoid multiple simultaneous calls
            if (!descriptor.getStatus().equals(SdkServiceStatus.SAVED)) {
                //log.error("Service descriptor with ID {} is not in status SAVED.", serviceDescriptorId);
                throw new AlreadyPublishedServiceException(String.format("Service descriptor with ID %s is not in status SAVED", serviceDescriptorId));
            }
            descriptor.setStatus(SdkServiceStatus.CHANGING);
            serviceDescriptorRepository.saveAndFlush(descriptor);
            // After setting the status, no one can operate on this anymore (except us)
        }

        DescriptorTemplate nsd;
        try {
            //check if all subfunctions are COMMITTED, if not ask to commit them
            checkFunctionStatus(descriptor.getSubDescriptors());
            nsd = adapter.generateNetworkServiceDescriptor(descriptor);
        }catch (NotPermittedOperationException e){
            descriptor.setStatus(SdkServiceStatus.SAVED);
            serviceDescriptorRepository.saveAndFlush(descriptor);
            throw  new NotPermittedOperationException(e.getMessage(), e);
        }catch(IllegalStateException e){
            descriptor.setStatus(SdkServiceStatus.SAVED);
            serviceDescriptorRepository.saveAndFlush(descriptor);
            throw new MalformedElementException(e.getMessage(), e);
        }

        Set<MonitoringParameter> monitoringParameters = new HashSet<>();
        monitoringParameters.addAll(descriptor.getTemplate().getExtMonitoringParameters());
        monitoringParameters.addAll(descriptor.getTemplate().getIntMonitoringParameters());
        ActionWrapper toSend = new ActionWrapper(descriptor.getTemplate().getActions(), descriptor.getTemplate().getActionRules());
        modifyActionWrapper(descriptor.getTemplate(), toSend);
        String servicePackagePath = ArchiveBuilder.createNSCSAR(nsd, monitoringParameters, toSend);

        dispatchPublishRequest(
            servicePackagePath,
            descriptor.getSliceId(),
            authorization,
            nsInfoId -> {
                if (nsInfoId != null) {
                    log.info("Service descriptor with ID {} successfully published to project {}", serviceDescriptorId, descriptor.getSliceId());
                    descriptor.setStatus(SdkServiceStatus.COMMITTED);
                    descriptor.setNsInfoId(nsInfoId);
                    serviceDescriptorRepository.save(descriptor);
                } else {
                    descriptor.setStatus(SdkServiceStatus.SAVED);
                    serviceDescriptorRepository.save(descriptor);
                    log.error("Error while publishing service descriptor with ID {} to project {}", serviceDescriptorId, descriptor.getSliceId());
                }
            }
        );
    }

    @Override
    public void unPublishService(Long serviceDescriptorId, String authorization)
        throws NotExistingEntityException, NotPublishedServiceException, NotAuthorizedOperationException {
        log.info("Requested deletion of the publication of the service descriptor with ID {}", serviceDescriptorId);

        Optional<SdkServiceDescriptor> optDescriptor = serviceDescriptorRepository.findById(serviceDescriptorId);
        SdkServiceDescriptor descriptor = optDescriptor.orElseThrow(() -> {
            //log.error("Service descriptor with ID {} is not present in database", serviceDescriptorId);
            return new NotExistingEntityException(String.format("Service descriptor with ID %s is not present in database", serviceDescriptorId));
        });

        if(keycloakEnabled)
            authSecurityChecks(descriptor.getTemplate(), 0);

        synchronized (this) { // To avoid multiple simultaneous calls
            // Check if is already published
            if (!descriptor.getStatus().equals(SdkServiceStatus.COMMITTED)) {
                //log.error("Service descriptor with ID {} is not in status COMMITTED.", serviceDescriptorId);
                throw new NotPublishedServiceException(String.format("Service descriptor with ID %s is not in status COMMITTED", serviceDescriptorId));
            }
            descriptor.setStatus(SdkServiceStatus.CHANGING);
            serviceDescriptorRepository.saveAndFlush(descriptor);
            // After setting the status, no one can operate on this anymore (except us)
        }

        dispatchUnPublishRequest(
            descriptor.getNsInfoId(),
            descriptor.getSliceId(),
            authorization,
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
        throws NotExistingEntityException, MalformedElementException, NotAuthorizedOperationException {
        Optional<SdkServiceDescriptor> optDescriptor = serviceDescriptorRepository.findById(serviceDescriptorId);

        SdkServiceDescriptor descriptor = optDescriptor.orElseThrow(() -> {
            //log.error("Service descriptor with ID {} is not present in database", serviceDescriptorId);
            return new NotExistingEntityException(String.format("Service descriptor with ID %s is not present in database", serviceDescriptorId));
        });

        if(keycloakEnabled)
            authSecurityChecks(descriptor.getTemplate(), 1);

        DescriptorTemplate nsd;
        try {
            nsd = adapter.generateNetworkServiceDescriptor(descriptor);
        }catch(IllegalStateException e){
            throw new MalformedElementException(e.getMessage(), e);
        }

        return nsd;
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

    private void checkAndResolveService(SdkService service) throws NotExistingEntityException, MalformedElementException, AlreadyExistingEntityException, NotAuthorizedOperationException{
        //In case of new service, check if a service with the same name and version is present
        if(service.getId() == null) {
            Optional<SdkService> serviceOptional = serviceRepository.findByNameAndVersionAndSliceId(service.getName(), service.getVersion(), service.getSliceId());
            if (serviceOptional.isPresent()) {
                //log.error("Service with name " + service.getName() + " and version " + service.getVersion() + " is already present with ID " + serviceOptional.get().getId());
                throw new AlreadyExistingEntityException("Service with name " + service.getName() + " and version " + service.getVersion() + " is already present with ID " + serviceOptional.get().getId());
            }
        }

        //check if slice is present in database and also check user if keycloak is enabled
        Optional<SliceResource> sliceOptional = sliceRepository.findBySliceId(service.getSliceId());
        if(!sliceOptional.isPresent())
            throw new NotExistingEntityException("Slice with sliceId " + service.getSliceId() + " is not present in database");

        log.debug("Checking functions availability");
        List<SdkFunction> availableF = functionManager.getFunctions(service.getSliceId());
        Set<Long> availableFIds = availableF.stream()
                .map(SdkFunction::getId)
                .collect(Collectors.toSet());
        Set<Long> requiredFIds = getSubFunctionIds(service).collect(Collectors.toSet());
        // TODO: optimize via repo method?
        // Check if the functions are available
        if (!availableFIds.containsAll(requiredFIds)) {
            requiredFIds.removeAll(availableFIds);
            throw new MalformedElementException(String.format("Functions %s are not available", requiredFIds));
        }
        log.debug("Checking sub-services availability");
        List<SdkService> availableS = this.getServices(service.getSliceId());
        Set<Long> availableSIds = availableS.stream()
                .map(SdkService::getId)
                .collect(Collectors.toSet());
        Set<Long> requiredSIds = getSubServiceIds(service).collect(Collectors.toSet());
        // Check if the services are available
        if (!availableSIds.containsAll(requiredSIds)) {
            requiredSIds.removeAll(availableSIds);
            throw new MalformedElementException(String.format("Functions %s are not available", requiredSIds));
        }
        validateImportedMonitoringParameter(service);
        validateInternalConnectionPoint(service);
        log.debug("Resolving service components");
        try {
            service.resolveComponents(new HashSet<>(availableF), new HashSet<>(availableS));
        } catch (IllegalStateException | IllegalArgumentException | NullPointerException e) {
            throw new MalformedElementException(e.getMessage(), e);
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

                boolean componentFound = false;
                Integer componentIndex = ((MonParamImported) mp).getComponentIndex();
                for(SdkServiceComponent component : service.getComponents()){
                    if(component.getComponentIndex().equals(componentIndex)){
                        componentFound = true;
                        if(target.get().getSdkFunction() != null){
                            if (!target.get().getSdkFunction().getId().equals(component.getComponentId())) {
                                //log.error("Monitoring parameter with ID " + target.get().getId() + " does not belong to component with componentIndex " + component.getComponentIndex());
                                throw new MalformedElementException("Monitoring parameter with ID " + target.get().getId() + " does not belong to component with componentIndex " + component.getComponentIndex());
                            }
                        }else if(target.get().getSdkServiceExt() != null){
                            if (!target.get().getSdkServiceExt().getId().equals(component.getComponentId())) {
                                //log.error("Monitoring parameter with ID " + target.get().getId() + " does not belong to component with componentIndex " + component.getId());
                                throw new MalformedElementException("Monitoring parameter with ID " + target.get().getId() + " does not belong to component with componentIndex " + component.getComponentIndex());
                            }
                        }else if(target.get().getSdkServiceInt() != null){
                            if (!target.get().getSdkServiceInt().getId().equals(component.getComponentId())) {
                                //log.error("Monitoring parameter with ID " + target.get().getId() + " does not belong to component with componentIndex " + component.getId());
                                throw new MalformedElementException("Monitoring parameter with ID " + target.get().getId() + " does not belong to component with componentIndex " + component.getComponentIndex());
                            }
                        }
                    }
                }
                if(!componentFound){
                    //log.error("Component with componentIndex " + componentIndex + " not found");
                    throw new MalformedElementException("Component with componentIndex " + componentIndex + " not found");
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

                boolean componentFound = false;
                Integer componentIndex = cp.getComponentIndex();
                for(SdkServiceComponent component : service.getComponents()){
                    if(component.getComponentIndex().equals(componentIndex)){
                        componentFound = true;
                        if(target.get().getSdkFunction() != null){
                            if (!target.get().getSdkFunction().getId().equals(component.getComponentId())) {
                                //log.error("Connection point with ID " + target.get().getId() + " does not belong to component with componentIndex " + component.getComponentIndex());
                                throw new MalformedElementException("Connection point with ID " + target.get().getId() + " does not belong to component with componentIndex " + component.getComponentIndex());
                            }
                        }else if(target.get().getSdkService() != null){
                            if (!target.get().getSdkService().getId().equals(component.getComponentId())) {
                                //log.error("Connection point with ID " + target.get().getId() + " does not belong to component with componentIndex " + component.getComponentIndex());
                                throw new MalformedElementException("Connection point with ID " + target.get().getId() + " does not belong to component with componentIndex " + component.getComponentIndex());
                            }
                        }
                    }
                }
                if(!componentFound){
                    //log.error("Component with componentIndex " + componentIndex + " not found");
                    throw new MalformedElementException("Component with componentIndex " + componentIndex + " not found");
                }
            }
        }
    }

    private void dispatchPublishRequest(String servicePackagePath, String project, String authorization, Consumer<String> callback) {
        // TODO: dispatch publish operation to driver, then return immediately
        executor.execute(() -> {
                try {
                    String nsInfoId = cataloguePlugin.uploadNetworkService(servicePackagePath, project, "multipart/form-data", null, authorization);
                    callback.accept(nsInfoId);
                } catch (Exception e) {
                    log.error("Could not push service package. Cause: {}", e.getMessage());
                    log.debug(null, e);
                    callback.accept(null);
                }
            }
        );
    }

    private void dispatchUnPublishRequest(String nsInfoId, String project, String authorization, Consumer<Boolean> callback) {
        // TODO: dispatch unpublish operation to driver, then return immediately
        executor.execute(() -> {
                try {
                    cataloguePlugin.deleteNetworkService(nsInfoId, project, authorization);
                    callback.accept(true);
                } catch (Exception e) {
                    log.error("Could not delete service package. Cause: {}", e.getMessage());
                    log.debug(null, e);
                    callback.accept(false);
                }
            }
        );
    }

    private void checkFunctionStatus(Set<SdkComponentInstance> subDescriptors) throws NotPermittedOperationException {
        for(SdkComponentInstance subDescriptor : subDescriptors){
            if(subDescriptor.getType().equals(SdkServiceComponentType.SDK_FUNCTION)){
                SdkFunction function = ((SdkFunctionDescriptor)subDescriptor).getTemplate();
                if(function.getStatus().equals(SdkFunctionStatus.SAVED)){
                    //log.error("Function with ID " + function.getId() + " not published to Public Catalogue. Please publish it before publishing the service");
                    throw  new NotPermittedOperationException("Function with ID " + function.getId() + " not published to Public Catalogue. Please publish it before publishing the service");
                }
            }else{
                checkFunctionStatus(((SdkServiceDescriptor)subDescriptor).getSubDescriptors());
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
            /*
            for(RuleCondition rc : sa.getConditions()){
                rc.setServiceActionRule(null);
            }
             */
        }
    }

    private void authSecurityChecks(SdkService service, int checkToPerform) throws NotAuthorizedOperationException{
        log.debug("Checking if the user can access the resource");

        // The admin can access all the resources
        if(keycloakUtils.getUserNameFromJWT().equals(adminUserName))
            return;

        /*
            foreach service in (slice==sliceId) {
		        if(user.userName != admin.userName) {
			        if (slice.users not contains user.userName): 	User cannot do anything
			        if (user.accessLevel > service.accessLevel): 	User cannot do anything
			        if (user.userName != service.ownerId) {
				        if (resource.visibility==PRIVATE):  		User cannot do anything
				        if (resource.visibility==PUBLIC):   		User can read, publish, create_descriptors and use in a service; User cannot update nor delete nor unpublish
			        }
		        }
		        User can create, read, update, delete, create_descriptors, publish, unpublish and use in a service
	        }

            0 : Create, Update, Delete and Unpublish
                Is possible if:
                    - user is the owner
                    - user accessLevel <= service accessLevel
                    - user belongs to the slice
            1 : Publish and Read and Create Descriptor
                Is possible if:
                    - visibility is public or user is the owner if the visibility is private
                    - user accessLevel <= service accessLevel
                    - user belongs to the slice
        */

        switch (checkToPerform) {
            case 0:
                keycloakUtils.checkUserSlices(keycloakUtils.getUserNameFromJWT(), service.getSliceId());
                keycloakUtils.checkUserAccessLevel(keycloakUtils.getAccessLevelFromJWT(), service.getAccessLevel());
                keycloakUtils.checkUserId(keycloakUtils.getUserNameFromJWT(), service.getOwnerId());
                break;
            case 1:
                keycloakUtils.checkUserSlices(keycloakUtils.getUserNameFromJWT(), service.getSliceId());
                keycloakUtils.checkUserAccessLevel(keycloakUtils.getAccessLevelFromJWT(), service.getAccessLevel());
                if(service.getVisibility().equals(Visibility.PRIVATE))
                    keycloakUtils.checkUserId(keycloakUtils.getUserNameFromJWT(), service.getOwnerId());
        }
        log.debug("User can access the resource");
    }

    private void modifyActionWrapper(SdkService service, ActionWrapper wrapper){
        Set<SdkServiceComponent> components = new HashSet<>(service.getComponents());
        Set<MonitoringParameter> exMonitoringParameters = new HashSet<>(service.getExtMonitoringParameters());
        for(ActionWrapper.ModifiedServiceAction action : wrapper.getActions()) {
            for (SdkServiceComponent component : components) {
                if (component.getComponentIndex().toString().equals(action.getVnfdId())) {
                    if (component.getType().equals(SdkServiceComponentType.SDK_FUNCTION)) {
                        SdkFunction subFunction = functionRepository.findById(component.getComponentId()).get();
                        action.setVnfdId(subFunction.getVnfdId());
                    }//TODO handle subservices
                }
            }
        }
        for(ActionWrapper.ModifiedServiceActionRule rule : wrapper.getActionRules()){
            for(ActionWrapper.ModifiedRuleCondition condition : rule.getConditions()) {
                for(MonitoringParameter monitoringParameter : exMonitoringParameters){
                    if (condition.getParameterId().equals(monitoringParameter.getName()) && monitoringParameter.getParameterType().equals(MonitoringParameterType.IMPORTED)) {
                        MonitoringParameter functionParameter = monitoringParamRepository.findById(new Long(((MonParamImported)monitoringParameter).getImportedParameterId())).get();
                        if(functionParameter.getParameterType().equals(MonitoringParameterType.FUNCTION))
                            condition.setParameterId(((MonParamFunction)functionParameter).getMetricName().toLowerCase());
                        for (SdkServiceComponent component : components) {
                            if (component.getComponentIndex().equals(((MonParamImported) monitoringParameter).getComponentIndex())) {
                                if (component.getType().equals(SdkServiceComponentType.SDK_FUNCTION)) {
                                    SdkFunction subFunction = functionRepository.findById(component.getComponentId()).get();
                                    condition.setVnfdId(subFunction.getVnfdId());
                                } //TODO handle subservices
                            }
                        }
                    } //TODO handle subservices and other types of monitoring parameter
                }
            }
        }
    }
}

