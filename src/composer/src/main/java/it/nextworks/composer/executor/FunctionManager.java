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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import it.nextworks.composer.adaptor.interfaces.ServicesAdaptorProviderInterface;
import it.nextworks.composer.auth.KeycloakUtils;
import it.nextworks.composer.controller.elements.SliceResource;
import it.nextworks.composer.executor.interfaces.FunctionManagerProviderInterface;
import it.nextworks.composer.executor.repositories.*;
import it.nextworks.composer.plugins.catalogue.ArchiveBuilder;
import it.nextworks.composer.plugins.catalogue.FiveGCataloguePlugin;
import it.nextworks.composer.plugins.catalogue.ArchiveParser;
import it.nextworks.composer.plugins.catalogue.CSARInfo;
import it.nextworks.composer.plugins.catalogue.api.management.ProjectResource;
import it.nextworks.composer.plugins.catalogue.sol005.vnfpackagemanagement.elements.VnfPkgInfo;
import it.nextworks.nfvmano.libs.common.exceptions.*;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.nfvmano.libs.descriptors.templates.VirtualLinkPair;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VDU.VDUComputeNode;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VDU.VDUVirtualBlockStorageNode;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VNF.VNFNode;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VnfExtCp.VnfExtCpNode;
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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class FunctionManager implements FunctionManagerProviderInterface {

    private static final Logger log = LoggerFactory.getLogger(FunctionManager.class);

    @Autowired
    private SdkFunctionRepository functionRepository;

    @Autowired
    private SliceRepository sliceRepository;

    @Autowired
    private SdkSubFunctionRepository subFunctionRepository;

    @Autowired
    private SdkServiceDescriptorRepository descriptorRepository;

    @Autowired
    private MonitoringParameterRepository monitoringParameterRepository;

    @Autowired
    private RequiredPortRepository requiredPortRepository;

    @Autowired
    private ConnectionpointRepository connectionpointRepository;

    @Autowired
    private FiveGCataloguePlugin cataloguePlugin;

    @Autowired
    private ArchiveParser archiveParser;

    @Autowired
    private TaskExecutor executor;

    @Autowired
    @Qualifier("expressionAdapter")
    private ServicesAdaptorProviderInterface adapter;

    @Value("${keycloak.enabled:true}")
    private boolean keycloakEnabled;

    @Autowired
    private KeycloakUtils keycloakUtils;

    @Value("${admin.user.name:admin}")
    private String adminUserName;

    public FunctionManager() {

    }

    @Scheduled(fixedDelayString = "${catalogue.vnfPkg.polling.time.in.milliseconds}")
    public void getVnfdFromCatalogue() throws FailedOperationException{
        log.info("Started retrieving VNFDs from Catalogue");

        String storagePath = "/tmp/fromCatalogue/";
        try{
            Files.createDirectories(Paths.get(storagePath));
        } catch (IOException e) {
            //log.error("Not able to create folder for retrieving VNFs from catalogue");
            throw new FailedOperationException("Not able to create folder for retrieving VNFs from catalogue", e);
        }

        CSARInfo csarInfo;
        DescriptorTemplate dt;
        String mf;

        String authorization = null;
        if(keycloakEnabled) {
            try{
                authorization = keycloakUtils.getAccessToken().getToken();
            } catch (Exception e){
                log.debug(null, e);
                log.error("Keycloak server is not working properly. Please check Keycloak configuration or disable authentication");
            }
        }

        List<VnfPkgInfo> vnfPackageInfoList;
        try {
            vnfPackageInfoList = cataloguePlugin.getVnfPackageInfoList(null, "Bearer " + authorization);
        }catch (RestClientException e){
            log.debug(null, e);
            return;
        }

        Long startUpdate = Instant.now().getEpochSecond();

        for(VnfPkgInfo vnfPkgInfo : vnfPackageInfoList){
            try {
                log.info("Retrieving VNF Pkg with ID " + vnfPkgInfo.getId().toString() + " from project " + vnfPkgInfo.getProjectId());
                //check if project exists as slice, if not create it
                Optional<SliceResource> sliceOptional = sliceRepository.findBySliceId(vnfPkgInfo.getProjectId());
                SliceResource slice;
                if(!sliceOptional.isPresent()) {
                    log.info("Slice with id " + vnfPkgInfo.getProjectId() + " doesn't exist");
                    //get project information from catalogue
                    ProjectResource projectResource = cataloguePlugin.getProject(vnfPkgInfo.getProjectId(), authorization);
                    slice = new SliceResource(vnfPkgInfo.getProjectId(), vnfPkgInfo.getProjectId(), projectResource.getUsers());
                    if(!projectResource.getUsers().contains(adminUserName))
                        slice.addUser(adminUserName);
                    sliceRepository.saveAndFlush(slice);
                    log.info("Created slice with id " + vnfPkgInfo.getProjectId());
                }
                MultipartFile vnfPkg = cataloguePlugin.getVnfPkgContent(vnfPkgInfo.getId().toString(), vnfPkgInfo.getProjectId(),null, storagePath, "Bearer " + authorization);
                csarInfo = ArchiveParser.archiveToMainDescriptor(vnfPkg);
                csarInfo.setPackageFilename(vnfPkgInfo.getId().toString());
                dt = csarInfo.getMst();
                mf = archiveParser.getMFContent();

                Optional<SdkFunction> functionOptional = functionRepository.findByVnfdIdAndVersionAndSliceId(dt.getMetadata().getDescriptorId(), dt.getMetadata().getVersion(), vnfPkgInfo.getProjectId());
                if (functionOptional.isPresent()) {
                    log.info("Function with vnfdID " + dt.getMetadata().getDescriptorId() + " and version " + dt.getMetadata().getVersion() + " already present");
                    functionOptional.get().setEpoch(Instant.now().getEpochSecond());
                    functionOptional.get().setStatus(SdkFunctionStatus.COMMITTED);
                    functionOptional.get().setVnfInfoId(vnfPkgInfo.getId().toString());
                    functionRepository.saveAndFlush(functionOptional.get());
                }else {
                    log.info("Creating function with vnfdID " + dt.getMetadata().getDescriptorId() + " and version " + dt.getMetadata().getVersion());
                    createFunctionFromVnfd(csarInfo, mf, storagePath, vnfPkgInfo.getProjectId(), authorization);
                }
            }catch (IOException | FailedOperationException | RestClientException e) {
                log.debug(null, e);
                log.error("Error while parsing VNF Pkg : " + e.getMessage());
                //throw new MalformattedElementException("Error while parsing VNF Pkg : " + e.getMessage());
            } catch (MalformattedElementException e) {
                log.debug(null, e);
                log.error("Error while parsing VNF Pkg, not aligned with CSAR format: " + e.getMessage());
                //throw new MalformattedElementException("Error while parsing VNF Pkg, not aligned with CSAR format : " + e.getMessage());
            }catch(NotExistingEntityException | NotAuthorizedOperationException e){
                log.debug(null, e);
                //exception cannot be raised in this case
            }
        }

        List<SdkFunction> sdkFunctions = functionRepository.findAll();
        for(SdkFunction sdkFunction : sdkFunctions){
            if(sdkFunction.getEpoch() < startUpdate && sdkFunction.getStatus().equals(SdkFunctionStatus.COMMITTED)){
                log.info("Function with vnfdID " + sdkFunction.getVnfdId() + " and version " + sdkFunction.getVersion() + " no longer present in catalogue");
                sdkFunction.setStatus(SdkFunctionStatus.SAVED);
                functionRepository.saveAndFlush(sdkFunction);
            }
        }

        log.info("Finished retrieving VNFDs from Catalogue");
    }

    @Override
    public SdkFunction getFunction(Long id) throws NotExistingEntityException, NotAuthorizedOperationException {
        Optional<SdkFunction> result = functionRepository.findById(id);
        if (result.isPresent()) {
            if(keycloakEnabled)
                authSecurityChecks(result.get(), 1);
            return result.get();
        } else {
            //log.error("No function with ID " + id + " was found");
            throw new NotExistingEntityException("No function with ID " + id + " was found");
        }
    }

    @Override
    public List<SdkFunction> getFunctions(String sliceId) throws NotExistingEntityException, NotAuthorizedOperationException {
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

        List<SdkFunction> functionList = functionRepository.findAll();
        Iterator<SdkFunction> functionIterator = functionList.iterator();
        for (; functionIterator.hasNext() ;) {
            SdkFunction function = functionIterator.next();
            // filter functions per slice
            if (sliceId != null && !function.getSliceId().equals(sliceId))
                functionIterator.remove();
            /*
                Filter functions if Keycloak is enabled
                User can view a function if:
                    - visibility is public or user is the owner if the visibility is private
                    - user accessLevel <= function accessLevel
                    - user belongs to the slice
            */
            else if (keycloakEnabled && !keycloakUtils.getUserNameFromJWT().equals(adminUserName)){
                if(keycloakUtils.getAccessLevelFromJWT().compareTo(function.getAccessLevel()) > 0)
                    functionIterator.remove();
                else if (function.getVisibility().equals(Visibility.PRIVATE) &&
                            !keycloakUtils.getUserNameFromJWT().equals(function.getOwnerId())){
                    functionIterator.remove();
                }
            }
        }
        if (functionList.size() == 0) {
            log.debug("No functions are available");
        } else
            log.debug("Functions present in database: " + functionList.size());
        return functionList;
    }

    @Override
    public String createFunction(SdkFunction function, boolean isInternalRequest)
        throws MalformedElementException, AlreadyExistingEntityException, NotExistingEntityException, NotAuthorizedOperationException {
        log.info("Storing into database a new function");
        if(function.getId() != null){
            //log.error("Function ID cannot be specified in function creation");
            throw new MalformedElementException("Function ID cannot be specified in function creation");
        }

        function.isValid();
        log.debug("Function is valid");

        if(keycloakEnabled && !isInternalRequest)
            authSecurityChecks(function, 0);

        checkAndResolveFunction(function);

        for(MonitoringParameter mp : function.getMonitoringParameters()){
            if (mp.getId() != null) {
                //log.error("Monitoring parameter ID cannot be specified in function creation");
                throw new MalformedElementException("Monitoring parameter ID cannot be specified in function creation");
            }
        }
        for(RequiredPort rp : function.getRequiredPorts()){
            if (rp.getId() != null) {
                //log.error("Required port ID cannot be specified in function creation");
                throw new MalformedElementException("Required port ID cannot be specified in function creation");
            }
        }
        for(ConnectionPoint cp : function.getConnectionPoint()){
            if (cp.getId() != null) {
                //log.error("Connection point ID cannot be specified in function creation");
                throw new MalformedElementException("Connection point ID cannot be specified in function creation");
            }
        }

        log.debug("Storing into database function with name: " + function.getName());

        // Saving the function
        functionRepository.saveAndFlush(function);
        return function.getId().toString();
    }

    @Override
    public String updateFunction(SdkFunction function) throws NotExistingEntityException, MalformedElementException, NotPermittedOperationException, NotAuthorizedOperationException {
        log.info("Updating an existing Function with ID " + function.getId());

        if(function.getId() == null){
            //log.error("Function ID needs to be specified");
            throw new MalformedElementException("Function ID needs to be specified");
        }

        function.isValid();
        log.debug("Function is valid");

        //Check if Function exists
        Optional<SdkFunction> func = functionRepository.findById(function.getId());
        if (!func.isPresent()) {
            //log.error("Function with ID " + function.getId() + " not present in database");
            throw new NotExistingEntityException("Function with ID " + function.getId() + " not present in database");
        }
        log.debug("Function found on db");

        if(keycloakEnabled)
            authSecurityChecks(func.get(), 0);

        //TODO create a function with the following two checks to reduce redundant code
        //update not allowed if the function is published to catalogue
        if(func.get().getStatus().equals(SdkFunctionStatus.COMMITTED)){
            //log.error("Function with ID " + function.getId() + " published to the catalogue. Please unpublish it before updating");
            throw  new NotPermittedOperationException("Function with ID " + function.getId() + " published to the catalogue. Please unpublish it before updating");
        }

        //update not allowed if the function is used by a service
        List<SubFunction> subFunctions = subFunctionRepository.findByComponentId(function.getId());
        if(subFunctions.size() != 0){
            List<Long> serviceIds = subFunctions.stream().map(SubFunction::getOuterService).map(SdkService::getId).collect(Collectors.toList());
            //log.error("Function with ID " + function.getId() + " used by services with IDs " + serviceIds.toString());
            throw  new NotPermittedOperationException("Function with ID " + function.getId() + " used by services with IDs " + serviceIds.toString());
        }

        //TODO create a function with the following three checks to reduce redundant code
        for(MonitoringParameter param : function.getMonitoringParameters()) {
            if (param.getId() != null) {
                Optional<MonitoringParameter> mp = monitoringParameterRepository.findById(param.getId());
                if (!mp.isPresent()) {
                    //log.error("Monitoring parameter with ID " + param.getId() + " is not present in database");
                    throw new NotExistingEntityException("Monitoring parameter with ID " + param.getId() + " is not present in database");
                }

                if ((mp.get().getSdkFunction() == null) || (!mp.get().getSdkFunction().getId().equals(function.getId()))) {
                    //log.error("Monitoring parameter with ID " + param.getId() + " does not belong to function with ID " + function.getId());
                    throw new NotPermittedOperationException("Monitoring parameter with ID " + param.getId() + " does not belong to function with ID " + function.getId());
                }
            }
        }

        for(ConnectionPoint param : function.getConnectionPoint()) {
            if (param.getId() != null) {
                Optional<ConnectionPoint> cp = connectionpointRepository.findById(param.getId());
                if (!cp.isPresent()) {
                    //log.error("Connection point with ID " + param.getId() + " is not present in database");
                    throw new NotExistingEntityException("Connection point with ID " + param.getId() + " is not present in database");
                }

                if ((cp.get().getSdkFunction() == null) || (!cp.get().getSdkFunction().getId().equals(function.getId()))) {
                    //log.error("Connection point with ID " + param.getId() + " does not belong to function with ID " + function.getId());
                    throw new NotPermittedOperationException("Connection point with ID " + param.getId() + " does not belong to function with ID " + function.getId());
                }
            }
        }

        for(RequiredPort param : function.getRequiredPorts()) {
            if (param.getId() != null) {
                Optional<RequiredPort> rp = requiredPortRepository.findById(param.getId());
                if (!rp.isPresent()) {
                    //log.error("Required port with ID " + param.getId() + " is not present in database");
                    throw new NotExistingEntityException("Required port with ID " + param.getId() + " is not present in database");
                }

                if ((rp.get().getFunction() == null) || (!rp.get().getFunction().getId().equals(function.getId()))) {
                    //log.error("Required port with ID " + param.getId() + " does not belong to function with ID " + function.getId());
                    throw new NotPermittedOperationException("Required port with ID " + param.getId() + " does not belong to function with ID " + function.getId());
                }
            }
        }

        try {
            checkAndResolveFunction(function);
        }catch (AlreadyExistingEntityException e){
                //exception cannot be raised in this case
        }

        if(keycloakEnabled)
            authSecurityChecks(function, 0);

        log.debug("Updating into database Function with ID " + function.getId());

        cleanOldRelations(func.get());

        // Update Function on DB
        functionRepository.saveAndFlush(function);
        return function.getId().toString();
    }

    /*
    @Override
    public SdkFunction getFunctionById(Long id) throws NotExistingEntityException {
        log.info("Request for Function with ID " + id);
        Optional<SdkFunction> function = functionRepository.findById(id);
        if (function.isPresent()) {
            return function.get();
        } else {
            //log.error("Function with ID " + id + " not found");
            throw new NotExistingEntityException("Function with ID " + id + " not found");
        }
    }
    */

    @Override
    public void deleteFunction(Long functionId) throws NotExistingEntityException, NotPermittedOperationException, NotAuthorizedOperationException {
        log.info("Request for deletion of Function with ID " + functionId);
        // No deletion required: all that depends on the Function will cascade.
        Optional<SdkFunction> function = functionRepository.findById(functionId);
        SdkFunction s = function.orElseThrow(() -> {
            //log.error("Function with ID " + functionId + " not found");
            return new NotExistingEntityException("Function with ID " + functionId + " not found");
        });

        if(keycloakEnabled)
            authSecurityChecks(s, 0);

        //delete not allowed if the function is published to catalogue
        if(s.getStatus().equals(SdkFunctionStatus.COMMITTED)){
            //log.error("Function with ID " + s.getId() + " published to the catalogue. Please unpublish it before deleting");
            throw  new NotPermittedOperationException("Function with ID " + s.getId() + " published to the catalogue. Please unpublish it before deleting");
        }

        //delete not allowed if the function is used by a service
        List<SubFunction> subFunctions = subFunctionRepository.findByComponentId(functionId);
        if(subFunctions.size() != 0){
            List<Long> serviceIds = subFunctions.stream().map(SubFunction::getOuterService).map(SdkService::getId).collect(Collectors.toList());
            //log.error("Function with ID " + functionId + " used by services with IDs " + serviceIds.toString());
            throw  new NotPermittedOperationException("Function with ID " + functionId + " used by services with IDs " + serviceIds.toString());
        }

        functionRepository.delete(s);
    }

    /*
    @Override
    public String createFunctionDescriptor(Long functionId, List<BigDecimal> parameterValues)
        throws NotExistingEntityException, MalformedElementException, NotYetImplementedException {
        log.info("Request create-descriptor of function with uuid: " + functionId);

        // Check if function exists
        Optional<SdkFunction> optFunction = functionRepository.findById(functionId);

        SdkFunction function = optFunction.orElseThrow(() -> {
            log.error("Function with UUID: " + functionId + " is not present in database");
            return new NotExistingEntityException("Function with UUID: " + functionId + " is not present in database");
        });

        throw new NotYetImplementedException();
        /*
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
    */

    @Override
    public void updateMonitoringParameters(Long functionId, Set<MonitoringParameter> monitoringParameters)
        throws NotExistingEntityException, NotPermittedOperationException, MalformedElementException, NotAuthorizedOperationException {
        log.info("Request to update list of monitoring parameters for a specific SDK Function " + functionId);

        for (MonitoringParameter param : monitoringParameters) {
            if (!param.isValid()) {
                //log.error("Monitoring param list provided cannot be validated");
                throw new MalformedElementException("Monitoring param list provided cannot be validated");
            }

            if(param.getId() != null) {
                Optional<MonitoringParameter> mp = monitoringParameterRepository.findById(param.getId());
                if (!mp.isPresent()) {
                    //log.error("Monitoring parameter with ID " + param.getId() + " is not present in database");
                    throw new NotExistingEntityException("Monitoring parameter with ID " + param.getId() + " is not present in database");
                }

                if ((mp.get().getSdkFunction() == null) || (!mp.get().getSdkFunction().getId().equals(functionId))) {
                    //log.error("Monitoring parameter with ID " + param.getId() + " does not belong to function with ID " + functionId);
                    throw new NotPermittedOperationException("Monitoring parameter with ID " + param.getId() + " does not belong to function with ID " + functionId);
                }
            }
        }

        Optional<SdkFunction> function = functionRepository.findById(functionId);
        if (!function.isPresent()) {
            //log.error("Function with ID " + functionId + " is not present in database");
            throw new NotExistingEntityException("Function with ID " + functionId + " is not present in database");
        }

        if(keycloakEnabled)
            authSecurityChecks(function.get(), 0);

        //update not allowed if the function is published to catalogue
        if(function.get().getStatus().equals(SdkFunctionStatus.COMMITTED)){
            //log.error("Function with ID " + functionId + " published to the catalogue. Please unpublish it before updating");
            throw  new NotPermittedOperationException("Function with ID " + functionId + " published to the catalogue. Please unpublish it before updating");
        }

        //update not allowed if the function is used by a service
        List<SubFunction> subFunctions = subFunctionRepository.findByComponentId(functionId);
        if(subFunctions.size() != 0){
            List<Long> serviceIds = subFunctions.stream().map(SubFunction::getOuterService).map(SdkService::getId).collect(Collectors.toList());
            //log.error("Function with ID " + functionId + " used by services with IDs " + serviceIds.toString());
            throw  new NotPermittedOperationException("Function with ID " + functionId + " used by services with IDs " + serviceIds.toString());
        }

        for(MonitoringParameter mp : function.get().getMonitoringParameters()){
            mp.setSdkFunction(null);
        }

        log.debug("Updating list of monitoring parameters on function with ID " + functionId);
        function.get().setMonitoringParameters(monitoringParameters);

        function.get().isValid();

        log.debug("Updating list of monitoring parameters on database");
        functionRepository.saveAndFlush(function.get());
    }

    @Override
    public void deleteMonitoringParameters(Long functionId, Long monitoringParameterId)
        throws NotExistingEntityException, NotPermittedOperationException, MalformedElementException, NotAuthorizedOperationException {

        log.info("Request to delete a monitoring parameter identified by id " + monitoringParameterId + " for a specific SDK Service " + functionId);

        Optional<MonitoringParameter> mp = monitoringParameterRepository.findById(monitoringParameterId);
        if (!mp.isPresent()) {
            //log.error("Monitoring parameter with ID " + monitoringParameterId + " is not present in database");
            throw new NotExistingEntityException("Monitoring parameter with ID " + monitoringParameterId + " is not present in database");
        }

        Optional<SdkFunction> function = functionRepository.findById(functionId);
        if (!function.isPresent()) {
            //log.error("Function with ID: " + functionId + " is not present in database");
            throw new NotExistingEntityException("Function with ID " + functionId + " is not present in database");
        }

        if(keycloakEnabled)
            authSecurityChecks(function.get(), 0);

        if((mp.get().getSdkFunction() == null) || (mp.get().getSdkFunction().getId().equals(functionId))){
            //log.error("Monitoring parameter with ID " + monitoringParameterId + " does not belong to function with ID " + functionId);
            throw  new NotPermittedOperationException("Monitoring parameter with ID " + monitoringParameterId + " does not belong to function with ID " + functionId);
        }

        //delete not allowed if the function is published to catalogue
        if(function.get().getStatus().equals(SdkFunctionStatus.COMMITTED)){
            //log.error("Function with ID " + functionId + " published to the catalogue. Please unpublish it before deleting");
            throw  new NotPermittedOperationException("Function with ID " + functionId + " published to the catalogue. Please unpublish it before deleting");
        }

        //delete not allowed if the function is used by a service
        List<SubFunction> subFunctions = subFunctionRepository.findByComponentId(functionId);
        if(subFunctions.size() != 0){
            List<Long> serviceIds = subFunctions.stream().map(SubFunction::getOuterService).map(SdkService::getId).collect(Collectors.toList());
            //log.error("Function with ID " + functionId + " used by services with IDs " + serviceIds.toString());
            throw  new NotPermittedOperationException("Function with ID " + functionId + " used by services with IDs " + serviceIds.toString());
        }

        Set<MonitoringParameter> monitoringParameters = new HashSet<>();

        monitoringParameters.addAll(function.get().getMonitoringParameters());
        for (MonitoringParameter param : monitoringParameters) {
            if(param.getId().compareTo(monitoringParameterId) == 0){
                param.setSdkFunction(null);
                monitoringParameters.remove(param);
                break;
            }
        }

        function.get().setMonitoringParameters(monitoringParameters);

        function.get().isValid();

        functionRepository.saveAndFlush(function.get());
        log.debug("Monitoring parameter has been deleted.");
    }

    @Override
    public Set<MonitoringParameter> getMonitoringParameters(Long functionId) throws NotExistingEntityException, NotAuthorizedOperationException {
        log.info("Request to get the list of monitoring parameters for a specific SDK Function " + functionId);
        Optional<SdkFunction> function = functionRepository.findById(functionId);
        if (!function.isPresent()) {
            //log.error("Function with ID " + functionId + " is not present in database");
            throw new NotExistingEntityException("Function with ID " + functionId + " is not present in database");
        }
        if(keycloakEnabled)
            authSecurityChecks(function.get(), 1);

        return (function.get().getMonitoringParameters());
    }

    @Override
    public void publishFunction(Long functionId, String authorization)
        throws NotExistingEntityException, AlreadyPublishedServiceException, NotAuthorizedOperationException {
        log.info("Request for publication of function with ID " + functionId);

        // Check if function exists
        Optional<SdkFunction> optFunction = functionRepository.findById(functionId);

        SdkFunction function = optFunction.orElseThrow(() -> {
            //log.error("Function  with ID " + functionId + " is not present in database");
            return new NotExistingEntityException("Function with ID " + functionId + " is not present in database");
        });

        if(keycloakEnabled)
            authSecurityChecks(function, 1);

        synchronized (this) { // To avoid multiple simultaneous calls
            if (!function.getStatus().equals(SdkFunctionStatus.SAVED)) {
                //log.error("Requested publication for an entity already has been published");
                throw new AlreadyPublishedServiceException("Requested publication for an entity already has been published");
            }
            function.setStatus(SdkFunctionStatus.CHANGING);
            functionRepository.saveAndFlush(function);
            // After setting the status, no one can operate on this anymore (except us)
        }

        DescriptorTemplate vnfd = adapter.generateVirtualNetworkFunctionDescriptor(function);
        String functionPackagePath = ArchiveBuilder.createVNFCSAR(vnfd, function.getMonitoringParameters(), function.getMetadata().get("cloud-init"));

        // A thread will be created to handle this request in order to perform it
        // asynchronously.
        dispatchPublishRequest(
            functionPackagePath,
            function.getSliceId(),
            authorization,
            vnfdInfoId -> {
                if (vnfdInfoId != null) {
                    log.info("Function with ID {} successfully published to project {}", functionId, function.getSliceId());
                    function.setStatus(SdkFunctionStatus.COMMITTED);
                    function.setVnfInfoId(vnfdInfoId);
                    functionRepository.saveAndFlush(function);
                } else {
                    function.setStatus(SdkFunctionStatus.SAVED);
                    functionRepository.saveAndFlush(function);
                    log.error("Error while publishing function with ID {} to project {}", functionId, function.getSliceId());
                }
            }
        );
    }

    @Override
    public void unPublishFunction(Long functionId, String authorization)
        throws NotExistingEntityException, NotPublishedServiceException, NotPermittedOperationException, NotAuthorizedOperationException {
        log.info("Requested deletion of the publication of the function with ID {}", functionId);
        Optional<SdkFunction> optFunction = functionRepository.findById(functionId);
        SdkFunction function = optFunction.orElseThrow(() -> {
            //log.error("The Function with ID {} is not present in database", functionId);
            return new NotExistingEntityException(String.format("Function with ID %s is not present in database", functionId));
        });

        if(keycloakEnabled)
            authSecurityChecks(function, 0);

        //unpublish not allowed if the function is used by a commited service
        List<SubFunction> subFunctions = subFunctionRepository.findByComponentId(functionId);
        for(SubFunction subFunction : subFunctions){
            Long serviceId = subFunction.getOuterService().getId();
            List<SdkServiceDescriptor> descriptors = descriptorRepository.findByTemplateId(serviceId);
            for(SdkServiceDescriptor descriptor : descriptors){
                if(descriptor.getStatus().equals(SdkServiceStatus.COMMITTED)){
                    //log.error("Function with ID " + functionId + " used by a published service with ID " + serviceId);
                    throw  new NotPermittedOperationException("Function with ID " + functionId + " used by a published service with ID " + serviceId);
                }
            }
        }

        synchronized (this) { // To avoid multiple simultaneous calls
            // Check if is already published
            if (!function.getStatus().equals(SdkFunctionStatus.COMMITTED)) {
                //log.error("Function with ID {} is not in status COMMITTED.", functionId);
                throw new NotPublishedServiceException(String.format("Function with ID %s is not in status COMMITTED", functionId));
            }
            function.setStatus(SdkFunctionStatus.CHANGING);
            functionRepository.saveAndFlush(function);
            // After setting the status, no one can operate on this anymore (except us)
        }

        dispatchUnPublishRequest(
            function.getVnfInfoId(),
            authorization,
            successful -> {
                if (successful) {
                    function.setStatus(SdkFunctionStatus.SAVED);
                    function.setVnfInfoId(null);
                    functionRepository.saveAndFlush(function);
                    log.info("Successfully un-published function with ID {}", functionId);
                } else {
                    function.setStatus(SdkFunctionStatus.COMMITTED);
                    functionRepository.saveAndFlush(function);
                    log.error("Error while un-publishing function with ID {}", functionId);
                }
            }
        );
    }

    @Override
    public DescriptorTemplate generateTemplate(Long functionId)
        throws NotExistingEntityException, NotAuthorizedOperationException {
        Optional<SdkFunction> optFunction = functionRepository.findById(functionId);

        SdkFunction function = optFunction.orElseThrow(() -> {
            //log.error("Function with ID {} is not present in database", functionId);
            return new NotExistingEntityException(String.format("Function with ID {} is not present in database", functionId));
        });
        if(keycloakEnabled)
            authSecurityChecks(function, 1);

        return adapter.generateVirtualNetworkFunctionDescriptor(function);
    }

    private void checkAndResolveFunction(SdkFunction function) throws AlreadyExistingEntityException, NotExistingEntityException {
        //In case of new function, check if a function with the same vnfdId and version is present
        if(function.getId() == null) {
            Optional<SdkFunction> functionOptional = functionRepository.findByVnfdIdAndVersionAndSliceId(function.getVnfdId(), function.getVersion(), function.getSliceId());
            if (functionOptional.isPresent()) {
                //log.error("Function with vnfdID " + function.getVnfdId() + " and version " + function.getVersion() + " is already present with ID " + functionOptional.get().getId());
                throw new AlreadyExistingEntityException("Function with vnfdID " + function.getVnfdId() + " and version " + function.getVersion() + " is already present with ID " + functionOptional.get().getId());
            }
        }

        //check if slice is present in database
        Optional<SliceResource> sliceOptional = sliceRepository.findBySliceId(function.getSliceId());
        if(!sliceOptional.isPresent())
            throw new NotExistingEntityException("Slice with sliceId " + function.getSliceId() + " is not present in database");

        int i = 0;
        for(ConnectionPoint cp : function.getConnectionPoint()){
            cp.setInternalLink("link_" + ++i);
        }

        function.setEpoch(Instant.now().getEpochSecond());

        //for the moment we consider single DF and IL, then we set static the expression
        //function.setFlavourExpression("static_df");
        //function.setInstantiationLevelExpression("static_il");

        function.setStatus(SdkFunctionStatus.SAVED);
    }

    private void cleanOldRelations(SdkFunction function){
        for(MonitoringParameter mp : function.getMonitoringParameters()){
            mp.setSdkFunction(null);
        }
        for(Metadata mp : function.getMetadata2()){
            mp.setFunction(null);
        }
        for(ConnectionPoint mp : function.getConnectionPoint()){
            mp.setSdkFunction(null);
        }
        for(RequiredPort rp : function.getRequiredPorts()){
            rp.setFunction(null);
        }
    }

    private void dispatchPublishRequest(String functionPackagePath, String project, String authorization, Consumer<String> callback) {
        // TODO: dispatch publish operation to driver, then return immediately
        executor.execute(() -> {
                try {
                    String vnfdInfoId = cataloguePlugin.uploadNetworkFunction(functionPackagePath, project, "multipart/form-data", null, authorization);
                    callback.accept(vnfdInfoId);
                } catch (Exception e) {
                    log.error("Could not push function package. Cause: {}", e.getMessage());
                    log.debug(null, e);
                    callback.accept(null);
                }
            }
        );
    }

    private void dispatchUnPublishRequest(String vnfInfoId, String authorization, Consumer<Boolean> callback) {
        // TODO: dispatch unpublish operation to driver, then return immediately
        executor.execute(() -> {
                try {
                    cataloguePlugin.deleteNetworkFunction(vnfInfoId, null, authorization);
                    callback.accept(true);
                } catch (Exception e) {
                    log.error("Could not delete function package. Cause: {}", e.getMessage());
                    log.debug("Details: ", e);
                    callback.accept(false);
                }
            }
        );
    }

    private void createFunctionFromVnfd(CSARInfo csarInfo, String mf, String storagePath, String projectId, String authorization) throws MalformattedElementException, IOException, FailedOperationException, NotExistingEntityException, NotAuthorizedOperationException{
        SdkFunction sdkFunction = new SdkFunction();
        DescriptorTemplate dt = csarInfo.getMst();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            if(dt.getMetadata() == null)
                throw new MalformedElementException("TOSCA Descriptor Template without metadata");
            sdkFunction.setVnfdId(dt.getMetadata().getDescriptorId());
            sdkFunction.setVersion(dt.getMetadata().getVersion());
            sdkFunction.setDescription(dt.getDescription());
            sdkFunction.setVendor(dt.getMetadata().getVendor());
            //For the moment consider only one VNFNode
            if(dt.getTopologyTemplate().getVNFNodes() == null)
                throw new MalformedElementException("TOSCA Descriptor Template without VNF nodes");
            VNFNode vnfNode = dt.getTopologyTemplate().getVNFNodes().values().iterator().next();
            if(vnfNode.getProperties().getProductName() != null)
                sdkFunction.setName(vnfNode.getProperties().getProductName());
            else
                sdkFunction.setName(dt.getTopologyTemplate().getVNFNodes().keySet().iterator().next());
            //sdkFunction.setVnfdProvider(vnfNode.getProperties().getProvider());

            sdkFunction.setSliceId(projectId);
            //check if is correct assigning ownership to the provider
            //sdkFunction.setOwnerId(vnfNode.getProperties().getProvider());
            sdkFunction.setOwnerId(adminUserName);
            sdkFunction.setVisibility(Visibility.PUBLIC);
            sdkFunction.setAccessLevel(4);

            Set<ConnectionPoint> connectionPoints = new HashSet<>();
            if(dt.getTopologyTemplate() == null)
                throw new MalformedElementException("TOSCA Descriptor Template without topology template");
            if(dt.getTopologyTemplate().getSubstituitionMappings() == null || dt.getTopologyTemplate().getSubstituitionMappings().getRequirements() == null
                || dt.getTopologyTemplate().getSubstituitionMappings().getRequirements().getVirtualLink() == null)
                throw new MalformedElementException("TOSCA Descriptor Template without virtual link in substitution mappings requirements");
            List<VirtualLinkPair> virtualLinkPairs = dt.getTopologyTemplate().getSubstituitionMappings().getRequirements().getVirtualLink();
            Map<String, VnfExtCpNode> cpNodes = dt.getTopologyTemplate().getVnfExtCpNodes();
            if(cpNodes == null)
                throw new MalformedElementException("TOSCA Descriptor Template without external connection points");
            for (Map.Entry<String, VnfExtCpNode> cpNode : cpNodes.entrySet()) {
                ConnectionPoint cp = new ConnectionPoint();
                cp.setName(cpNode.getKey());
                cp.setType(ConnectionPointType.EXTERNAL);
                for(VirtualLinkPair virtualLinkPair : virtualLinkPairs){
                    if(virtualLinkPair.getCp().equals(cp.getName())){
                        cp.setInternalLink(virtualLinkPair.getVl());
                        break;
                    }
                }
                connectionPoints.add(cp);
            }

            sdkFunction.setConnectionPoint(connectionPoints);

            //For the moment consider only one VDU
            if(dt.getTopologyTemplate().getVDUComputeNodes() == null)
                throw new MalformedElementException("TOSCA Descriptor Template without VDU compute nodes");
            VDUComputeNode vduNode = dt.getTopologyTemplate().getVDUComputeNodes().values().iterator().next();
            if(vduNode.getProperties() == null)
                throw new MalformedElementException("TOSCA Descriptor Template without VDU compute node properties");
            if(vduNode.getProperties().getVduProfile() == null)
                throw new MalformedElementException("TOSCA Descriptor Template without VDU compute node profile");
            if(vduNode.getProperties().getVduProfile().getMaxNumberOfInstances() != null)
                sdkFunction.setMaxInstancesCount(vduNode.getProperties().getVduProfile().getMaxNumberOfInstances());
            else
                sdkFunction.setMaxInstancesCount(1);
            if(vduNode.getProperties().getVduProfile().getMinNumberOfInstances() != null)
                sdkFunction.setMinInstancesCount(vduNode.getProperties().getVduProfile().getMinNumberOfInstances());
            else
                sdkFunction.setMinInstancesCount(1);

            SwImageData imageData = new SwImageData();
            //For the moment consider only one VDU block storage node
            if(dt.getTopologyTemplate().getVDUBlockStorageNodes() == null)
                throw new MalformedElementException("TOSCA Descriptor Template without VDU block storage nodes");
            VDUVirtualBlockStorageNode storageNode = dt.getTopologyTemplate().getVDUBlockStorageNodes().values().iterator().next();
            if(storageNode.getProperties() == null || storageNode.getProperties().getSwImageData() == null)
                throw new MalformedElementException("TOSCA Descriptor Template without VDU software image data");
            imageData.setImgName(storageNode.getProperties().getSwImageData().getImageName());
            imageData.setImgVersion(storageNode.getProperties().getSwImageData().getVersion());
            imageData.setChecksum(storageNode.getProperties().getSwImageData().getChecksum());
            if(storageNode.getProperties().getSwImageData().getContainerFormat() != null)
                imageData.setContainerFormat(storageNode.getProperties().getSwImageData().getContainerFormat().toString());
            if(storageNode.getProperties().getSwImageData().getDiskFormat() != null)
                imageData.setDiskFormat(storageNode.getProperties().getSwImageData().getDiskFormat().toString());
            imageData.setSize(storageNode.getProperties().getSwImageData().getSize());
            if(storageNode.getProperties().getVirtualBlockStorageData() == null)
                throw new MalformedElementException("TOSCA Descriptor Template without VDU block storage data");
            imageData.setMinDisk(storageNode.getProperties().getVirtualBlockStorageData().getSizeOfStorage());
            if(vduNode.getCapabilities() == null || vduNode.getCapabilities().getVirtualCompute() == null || vduNode.getCapabilities().getVirtualCompute().getProperties() == null
                || vduNode.getCapabilities().getVirtualCompute().getProperties().getVirtualMemory() == null || vduNode.getCapabilities().getVirtualCompute().getProperties().getVirtualCpu() == null)
                throw new MalformedElementException("TOSCA Descriptor Template without VDU virtual compute data");
            imageData.setMinRam(vduNode.getCapabilities().getVirtualCompute().getProperties().getVirtualMemory().getVirtualMemSize());
            imageData.setMinCpu(vduNode.getCapabilities().getVirtualCompute().getProperties().getVirtualCpu().getNumVirtualCpu());
            sdkFunction.setSwImageData(imageData);

            sdkFunction.setFlavourExpression("static_df");
            sdkFunction.setInstantiationLevelExpression("static_il");

            String cloudInitFileName = findCloudInit(mf);
            if(cloudInitFileName != null){
                Map<String, String> metadata = new HashMap<>();
                String content = new String(Files.readAllBytes(Paths.get(storagePath + csarInfo.getPackageFilename() + "/" + cloudInitFileName)));
                metadata.put("cloud-init", content);
                sdkFunction.setMetadata(metadata);
            }

            String monitoringParamFileName = findMonitoringParameters(mf);
            if(monitoringParamFileName != null){
                Set<MonitoringParameter> monitoringParameters;
                try{
                    monitoringParameters = objectMapper.readValue(new File(storagePath + csarInfo.getPackageFilename() + "/", monitoringParamFileName), new TypeReference<Set<MonitoringParameter>>(){});
                    monitoringParameters.forEach(param -> param.setId(null));
                    sdkFunction.setMonitoringParameters(monitoringParameters);
                } catch(EOFException | MismatchedInputException e) {
                    //empty file or format not correct
                    log.error("Error while parsing monitoring parameters : " + e.getMessage());
                    throw new FailedOperationException("Error while parsing monitoring parameters : " + e.getMessage());
                }
            }

            createFunction(sdkFunction, true);

            sdkFunction.setStatus(SdkFunctionStatus.COMMITTED);
            //package filename is the vnfPkgInfo ID
            sdkFunction.setVnfInfoId(csarInfo.getPackageFilename());
            functionRepository.saveAndFlush(sdkFunction);
        }catch(NullPointerException | MalformedElementException | AlreadyExistingEntityException e){
            log.debug(null, e);
            log.error(e.getMessage());
            throw new FailedOperationException(e.getMessage());
        }
    }

    //TODO collapse in a single function
    private String findCloudInit(String mf) throws IOException{
        String cloudInitFilename = null;
        BufferedReader br = new BufferedReader(new StringReader(mf));
        try {
            String line;
            String regexRoot = "cloud_init:";
            String regex = "^Source: (Files\\/Scripts\\/[\\s\\S]*)$";
            while ((line = br.readLine()) != null) {
                line = line.trim();
                log.debug("MF line: <" + line + ">");
                if (line.matches(regexRoot)) {
                    line = br.readLine();

                    if (line != null) {
                        line = line.trim();
                        log.debug("Next MF line: <" + line + ">");
                        if (line.matches(regex)) {
                            Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                            Matcher matcher = pattern.matcher(line.trim());
                            if (matcher.find()) {
                                cloudInitFilename = matcher.group(1);
                                log.debug("Found cloud-init file with name: " + cloudInitFilename);
                                return cloudInitFilename;
                            }
                        }
                    }
                }
            }
            return null;
        } finally {
            br.close();
        }
    }

    //TODO collapse in a single function
    private String findMonitoringParameters(String mf) throws IOException{
        String monitoringParametersFilename = null;
        BufferedReader br = new BufferedReader(new StringReader(mf));
        try {
            String line;
            String regexRoot = "main_monitoring_descriptor:";
            String regex = "^Source: (Files\\/Monitoring\\/[\\s\\S]*)$";
            while ((line = br.readLine()) != null) {
                line = line.trim();
                log.debug("MF line: <" + line + ">");
                if (line.matches(regexRoot)) {
                    line = br.readLine();

                    if (line != null) {
                        line = line.trim();
                        log.debug("Next MF line: <" + line + ">");
                        if (line.matches(regex)) {
                            Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                            Matcher matcher = pattern.matcher(line.trim());
                            if (matcher.find()) {
                                monitoringParametersFilename = matcher.group(1);
                                log.debug("Found monitoring parameters file file with name: " + monitoringParametersFilename);
                                return monitoringParametersFilename;
                            }
                        }
                    }
                }
            }
            return null;
        } finally {
            br.close();
        }
    }

    private void authSecurityChecks(SdkFunction function, int checkToPerform) throws NotAuthorizedOperationException{
        log.debug("Checking if the user can access the resource");

        // The admin can access all the resources
        if(keycloakUtils.getUserNameFromJWT().equals(adminUserName))
            return;

        /*
            foreach function in (slice==sliceId) {
                if(user.userName != admin.userName) {
                    if (slice.users not contains user.userName): 	User cannot do anything
                    if (user.accessLevel > function.accessLevel): 	User cannot do anything
                    if (user.userName != function.ownerId) {
                        if (resource.visibility==PRIVATE):  		User cannot do anything
                        if (resource.visibility==PUBLIC):   		User can read, publish, use in a service; User cannot update nor unpublish nor delete
                    }
                }
                User can create, read, update, delete, publish, unpublish, use in a service
            }

            0 : Create, Update, Delete and Unpublish
                Is possible if:
                    - user is the owner
                    - user accessLevel <= function accessLevel
                    - user belongs to the slice
            1 : Publish and Read
                Is possible if:
                    - visibility is public or user is the owner if the visibility is private
                    - user accessLevel <= function accessLevel
                    - user belongs to the slice
        */

        switch (checkToPerform) {
            case 0:
                keycloakUtils.checkUserSlices(keycloakUtils.getUserNameFromJWT(), function.getSliceId());
                keycloakUtils.checkUserAccessLevel(keycloakUtils.getAccessLevelFromJWT(), function.getAccessLevel());
                keycloakUtils.checkUserId(keycloakUtils.getUserNameFromJWT(), function.getOwnerId());
                break;
            case 1:
                keycloakUtils.checkUserSlices(keycloakUtils.getUserNameFromJWT(), function.getSliceId());
                keycloakUtils.checkUserAccessLevel(keycloakUtils.getAccessLevelFromJWT(), function.getAccessLevel());
                if(function.getVisibility().equals(Visibility.PRIVATE))
                    keycloakUtils.checkUserId(keycloakUtils.getUserNameFromJWT(), function.getOwnerId());
        }
        log.debug("User can access the resource");
    }
}

