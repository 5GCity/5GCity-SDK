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
import it.nextworks.composer.executor.interfaces.FunctionManagerProviderInterface;
import it.nextworks.composer.executor.repositories.*;
import it.nextworks.composer.plugins.catalogue.ArchiveBuilder;
import it.nextworks.composer.plugins.catalogue.FiveGCataloguePlugin;
import it.nextworks.composer.plugins.catalogue.ArchiveParser;
import it.nextworks.composer.plugins.catalogue.CSARInfo;
import it.nextworks.composer.plugins.catalogue.sol005.vnfpackagemanagement.elements.VnfPkgInfo;
import it.nextworks.nfvmano.libs.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.common.exceptions.NotPermittedOperationException;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VDU.VDUComputeNode;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VDU.VDUVirtualBlockStorageNode;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VNF.VNFNode;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VnfExtCp.VnfExtCpNode;
import it.nextworks.sdk.*;
import it.nextworks.sdk.enums.ConnectionPointType;
import it.nextworks.sdk.enums.SdkFunctionStatus;
import it.nextworks.sdk.enums.SdkServiceStatus;
import it.nextworks.sdk.enums.Visibility;
import it.nextworks.sdk.exceptions.AlreadyPublishedServiceException;
import it.nextworks.sdk.exceptions.MalformedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.NotPublishedServiceException;
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@EnableScheduling
public class FunctionManager implements FunctionManagerProviderInterface {

    private static final Logger log = LoggerFactory.getLogger(FunctionManager.class);

    @Autowired
    private SdkFunctionRepository functionRepository;

    @Autowired
    private SdkSubFunctionRepository subFunctionRepository;

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

    public FunctionManager() {

    }

    //@PostConstruct
    @Scheduled(fixedDelay = 3600000/*, initialDelay = 3600000*/)//run every hour
    public void getVnfdFromCatalogue() throws MalformattedElementException, FailedOperationException{
        log.info("Started retrieving VNFDs from Catalogue");

        String storagePath = "/tmp/fromCatalogue/";
        try{
            Files.createDirectories(Paths.get(storagePath));
        } catch (IOException e) {
            log.error("Not able to create folder for retrieving VNFs from catalogue");
            throw new FailedOperationException("Not able to create folder for retrieving VNFs from catalogue");
        }

        CSARInfo csarInfo;
        DescriptorTemplate dt;
        String mf;

        List<VnfPkgInfo> vnfPackageInfoList = cataloguePlugin.getVnfPackageInfoList();

        Long startUpdate = Instant.now().getEpochSecond();

        for(VnfPkgInfo vnfPkgInfo : vnfPackageInfoList){
            try {
                log.info("Retrieving VNF Pkg with id " + vnfPkgInfo.getId().toString());
                MultipartFile vnfPkg = cataloguePlugin.getVnfPkgContent(vnfPkgInfo.getId().toString(), null, storagePath);
                csarInfo = archiveParser.archiveToMainDescriptor(vnfPkg);
                csarInfo.setPackageFilename(vnfPkgInfo.getId().toString());
                dt = csarInfo.getMst();
                mf = archiveParser.getMFContent();

                Optional<SdkFunction> functionOptional = functionRepository.findByVnfdIdAndVersion(dt.getMetadata().getDescriptorId(), dt.getMetadata().getVersion());
                if (functionOptional.isPresent()) {
                    log.info("Function with vnfdID " + dt.getMetadata().getDescriptorId() + " and version " + dt.getMetadata().getVersion() + " already present");
                    functionOptional.get().setEpoch(Instant.now().getEpochSecond());
                    functionOptional.get().setStatus(SdkFunctionStatus.COMMITTED);
                    functionOptional.get().setVnfInfoId(vnfPkgInfo.getId().toString());
                    functionRepository.saveAndFlush(functionOptional.get());
                }else {
                    log.info("Creating function with vnfdID " + dt.getMetadata().getDescriptorId() + " and version " + dt.getMetadata().getVersion());
                    createFunctionFromVnfd(csarInfo, mf, storagePath);
                }
            }catch (IOException | FailedOperationException e) {
                log.error("Error while parsing VNF Pkg : " + e.getMessage());
                throw new MalformattedElementException("Error while parsing VNF Pkg : " + e.getMessage());
            } catch (MalformattedElementException e) {
                log.error("Error while parsing VNF Pkg, not aligned with CSAR format: " + e.getMessage());
                throw new MalformattedElementException("Error while parsing VNF Pkg, not aligned with CSAR format : " + e.getMessage());
            }
        }

        List<SdkFunction> sdkFunctions = functionRepository.findAll();
        for(SdkFunction sdkFunction : sdkFunctions){
            if(sdkFunction.getEpoch() < startUpdate && sdkFunction.getStatus() == SdkFunctionStatus.COMMITTED){
                log.info("Function with VNFD Id " + sdkFunction.getVnfdId() + " and version " + sdkFunction.getVersion() + " no longer present in catalogue");
                sdkFunction.setStatus(SdkFunctionStatus.SAVED);
                functionRepository.saveAndFlush(sdkFunction);
            }
        }

        log.info("Finished retrieving VNFDs from Catalogue");
    }

    @Override
    public SdkFunction getFunction(Long id) throws NotExistingEntityException {
        Optional<SdkFunction> result = functionRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        } else {
            log.error("No function with ID " + id + " was found.");
            throw new NotExistingEntityException("No function with ID " + id + " was found.");
        }
    }

    @Override
    public List<SdkFunction> getFunctions() {
        List<SdkFunction> functionList = functionRepository.findAll();
        if (functionList.size() == 0) {
            log.debug("No functions are available");
        } else
            log.debug("Functions present in database: " + functionList.size());
        return functionList;
    }

    @Override
    public String createFunction(SdkFunction function)
        throws MalformedElementException, AlreadyExistingEntityException {

        log.info("Storing into database a new function");

        if(function.getId() != null){
            log.error("Function ID cannot be specified in function creation");
            throw new MalformedElementException("Function ID cannot be specified in function creation");
        }

        if (!function.isValid()) {
            log.error("Malformed SdkFunction");
            throw new MalformedElementException("Malformed SdkFunction");
        }
        log.debug("Function is valid");

        checkAndResolveFunction(function);

        for(MonitoringParameter mp : function.getMonitoringParameters()){
            if (mp.getId() != null) {
                log.error("Monitoring parameter ID cannot be specified in function creation");
                throw new MalformedElementException("Monitoring parameter ID cannot be specified in function creation");
            }
        }
        for(RequiredPort rp : function.getRequiredPorts()){
            if (rp.getId() != null) {
                log.error("Required port ID cannot be specified in function creation");
                throw new MalformedElementException("Required port ID cannot be specified in function creation");
            }
        }
        for(ConnectionPoint cp : function.getConnectionPoint()){
            if (cp.getId() != null) {
                log.error("Connection point ID cannot be specified in function creation");
                throw new MalformedElementException("Connection point ID cannot be specified in function creation");
            }
        }

        log.debug("Storing into database function with name: " + function.getName());

        // Saving the function
        functionRepository.saveAndFlush(function);
        return function.getId().toString();
    }

    @Override
    public String updateFunction(SdkFunction function) throws NotExistingEntityException, MalformedElementException, NotPermittedOperationException {
        log.info("Updating an existing Function with ID " + function.getId());

        if(function.getId() == null){
            log.error("Function ID needs to be specified");
            throw new MalformedElementException("Function ID needs to be specified");
        }

        if (!function.isValid()) {
            log.error("Malformed SdkFunction");
            throw new MalformedElementException("Malformed SdkFunction");
        }
        log.debug("Function is valid");

        //Check if Function exists
        Optional<SdkFunction> func = functionRepository.findById(function.getId());
        if (!func.isPresent()) {
            log.error("Function with ID " + function.getId() + " not present in database");
            throw new NotExistingEntityException("Function with ID " + function.getId() + " not present in database");
        }
        log.debug("Function found on db");

        //update not allowed if the function is published to catalogue
        if(func.get().getStatus().equals(SdkFunctionStatus.COMMITTED)){
            log.error("Function with ID " + function.getId() + " published to the catalogue. Please unpublish it before updating");
            throw  new NotPermittedOperationException("Function with ID " + function.getId() + " published to the catalogue. Please unpublish it before updating");
        }

        //update not allowed if the function is used by a service
        List<SubFunction> subFunctions = subFunctionRepository.findByComponentId(function.getId());
        if(subFunctions.size() != 0){
            log.error("Function with ID " + function.getId() + " used by a service");
            throw  new NotPermittedOperationException("Function with ID " + function.getId() + " used by a service");
        }

        for(MonitoringParameter param : function.getMonitoringParameters()) {
            if (param.getId() != null) {
                Optional<MonitoringParameter> mp = monitoringParameterRepository.findById(param.getId());
                if (!mp.isPresent()) {
                    log.error("Monitoring parameter with ID " + param.getId() + " is not present in database");
                    throw new NotExistingEntityException("Monitoring parameter with ID " + param.getId() + " is not present in database");
                }

                if ((mp.get().getSdkFunction() == null) || (mp.get().getSdkFunction().getId() != function.getId())) {
                    log.error("Monitoring parameter with ID " + param.getId() + " does not belong to function with ID " + function.getId());
                    throw new NotPermittedOperationException("Monitoring parameter with ID " + param.getId() + " does not belong to function with ID " + function.getId());
                }
            }
        }

        for(ConnectionPoint param : function.getConnectionPoint()) {
            if (param.getId() != null) {
                Optional<ConnectionPoint> cp = connectionpointRepository.findById(param.getId());
                if (!cp.isPresent()) {
                    log.error("Connection point with ID " + param.getId() + " is not present in database");
                    throw new NotExistingEntityException("Connection point with ID " + param.getId() + " is not present in database");
                }

                if ((cp.get().getSdkFunction() == null) || (cp.get().getSdkFunction().getId() != function.getId())) {
                    log.error("Connection point with ID " + param.getId() + " does not belong to function with ID " + function.getId());
                    throw new NotPermittedOperationException("Connection point with ID " + param.getId() + " does not belong to function with ID " + function.getId());
                }
            }
        }

        for(RequiredPort param : function.getRequiredPorts()) {
            if (param.getId() != null) {
                Optional<RequiredPort> rp = requiredPortRepository.findById(param.getId());
                if (!rp.isPresent()) {
                    log.error("Required port with ID " + param.getId() + " is not present in database");
                    throw new NotExistingEntityException("Required port with ID " + param.getId() + " is not present in database");
                }

                if ((rp.get().getFunction() == null) || (rp.get().getFunction().getId() != function.getId())) {
                    log.error("Required port with ID " + param.getId() + " does not belong to function with ID " + function.getId());
                    throw new NotPermittedOperationException("Required port with ID " + param.getId() + " does not belong to function with ID " + function.getId());
                }
            }
        }

        try {
            checkAndResolveFunction(function);
        }catch (AlreadyExistingEntityException e){
                //exception cannot be raised in this case
        }

        log.debug("Updating into database Function with ID " + function.getId());

        cleanOldRelations(func.get());

        // Update Function on DB
        functionRepository.saveAndFlush(function);
        return function.getId().toString();
    }

    @Override
    public SdkFunction getFunctionById(Long id) throws NotExistingEntityException {
        log.info("Request for Function with ID " + id);
        Optional<SdkFunction> function = functionRepository.findById(id);
        if (function.isPresent()) {
            return function.get();
        } else {
            log.error("Function with ID " + id + " not found");
            throw new NotExistingEntityException("Function with ID " + id + " not found");
        }
    }

    @Override
    public void deleteFunction(Long functionId) throws NotExistingEntityException, NotPermittedOperationException {
        log.info("Request for deletion of Function with ID " + functionId);
        // No deletion required: all that depends on the Function will cascade.
        Optional<SdkFunction> function = functionRepository.findById(functionId);
        SdkFunction s = function.orElseThrow(() -> {
            log.error("Function with ID " + functionId + " not found");
            return new NotExistingEntityException("Function with ID " + functionId + " not found");
        });

        //delete not allowed if the function is published to catalogue
        if(s.getStatus().equals(SdkFunctionStatus.COMMITTED)){
            log.error("Function with ID " + s.getId() + " published to the catalogue. Please unpublish it before deleting");
            throw  new NotPermittedOperationException("Function with ID " + s.getId() + " published to the catalogue. Please unpublish it before deleting");
        }

        //delete not allowed if the function is used by a service
        List<SubFunction> subFunctions = subFunctionRepository.findByComponentId(functionId);
        if(subFunctions.size() != 0){
            log.error("Function with ID " + functionId + " used by a service");
            throw  new NotPermittedOperationException("Function with ID " + functionId + " used by a service");
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
        throws NotExistingEntityException, NotPermittedOperationException, MalformedElementException {
        log.info("Request to update list of monitoring parameters for a specific SDK Function " + functionId);

        for (MonitoringParameter param : monitoringParameters) {
            if (!param.isValid()) {
                log.error("Monitoring param list provided cannot be validated");
                throw new MalformedElementException("Monitoring param list provided cannot be validated");
            }

            if(param.getId() != null) {
                Optional<MonitoringParameter> mp = monitoringParameterRepository.findById(param.getId());
                if (!mp.isPresent()) {
                    log.error("Monitoring parameter with ID " + param.getId() + " is not present in database");
                    throw new NotExistingEntityException("Monitoring parameter with ID " + param.getId() + " is not present in database");
                }

                if ((mp.get().getSdkFunction() == null) || (mp.get().getSdkFunction().getId() != functionId)) {
                    log.error("Monitoring parameter with ID " + param.getId() + " does not belong to function with ID " + functionId);
                    throw new NotPermittedOperationException("Monitoring parameter with ID " + param.getId() + " does not belong to function with ID " + functionId);
                }
            }
        }

        Optional<SdkFunction> function = functionRepository.findById(functionId);
        if (!function.isPresent()) {
            log.error("Function with ID " + functionId + " is not present in database");
            throw new NotExistingEntityException("Function with ID " + functionId + " is not present in database");
        }

        //update not allowed if the function is used by a service
        List<SubFunction> subFunctions = subFunctionRepository.findByComponentId(functionId);
        if(subFunctions.size() != 0){
            log.error("Function with ID " + functionId + " used by a service");
            throw  new NotPermittedOperationException("Function with ID " + functionId + " used by a service");
        }

        //update not allowed if the function is published to catalogue
        if(function.get().getStatus().equals(SdkFunctionStatus.COMMITTED)){
            log.error("Function with ID " + functionId + " published to the catalogue. Please unpublish it before updating");
            throw  new NotPermittedOperationException("Function with ID " + functionId + " published to the catalogue. Please unpublish it before updating");
        }

        for(MonitoringParameter mp : function.get().getMonitoringParameters()){
            mp.setSdkFunction(null);
        }

        log.debug("Updating list of monitoring parameters on function with ID " + functionId);
        function.get().setMonitoringParameters(monitoringParameters);

        if (!function.get().isValid()) {
            log.error("Malformed SdkFunction");
            throw new MalformedElementException("Malformed SdkFunction");
        }
        log.debug("Updating list of monitoring parameters on database");
        functionRepository.saveAndFlush(function.get());
    }

    @Override
    public void deleteMonitoringParameters(Long functionId, Long monitoringParameterId)
        throws NotExistingEntityException, NotPermittedOperationException, MalformedElementException {

        log.info("Request to delete a monitoring parameter identified by id " + monitoringParameterId + " for a specific SDK Service " + functionId);

        Optional<MonitoringParameter> mp = monitoringParameterRepository.findById(monitoringParameterId);
        if (!mp.isPresent()) {
            log.error("Monitoring parameter with ID " + monitoringParameterId + " is not present in database");
            throw new NotExistingEntityException("Monitoring parameter with ID " + monitoringParameterId + " is not present in database");
        }

        Optional<SdkFunction> function = functionRepository.findById(functionId);
        if (!function.isPresent()) {
            log.error("Function with ID: " + functionId + " is not present in database");
            throw new NotExistingEntityException("Function with ID " + functionId + " is not present in database");
        }

        if((mp.get().getSdkFunction() == null) || (mp.get().getSdkFunction().getId() != functionId)){
            log.error("Monitoring parameter with ID " + monitoringParameterId + " does not belong to function with ID " + functionId);
            throw  new NotPermittedOperationException("Monitoring parameter with ID " + monitoringParameterId + " does not belong to function with ID " + functionId);
        }

        //delete not allowed if the function is used by a service
        List<SubFunction> subFunctions = subFunctionRepository.findByComponentId(functionId);
        if(subFunctions.size() != 0){
            log.error("Function with ID " + functionId + " used by a service");
            throw  new NotPermittedOperationException("Function with ID " + functionId + " used by a service");
        }

        //delete not allowed if the function is published to catalogue
        if(function.get().getStatus().equals(SdkFunctionStatus.COMMITTED)){
            log.error("Function with ID " + functionId + " published to the catalogue. Please unpublish it before deleting");
            throw  new NotPermittedOperationException("Function with ID " + functionId + " published to the catalogue. Please unpublish it before deleting");
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

        if (!function.get().isValid()) {
            log.error("Malformed SdkFunction");
            throw new MalformedElementException("Malformed SdkFunction");
        }
        functionRepository.saveAndFlush(function.get());
        log.debug("Monitoring parameter has been deleted.");
    }

    @Override
    public Set<MonitoringParameter> getMonitoringParameters(Long functionId) throws NotExistingEntityException {
        log.info("Request to get the list of monitoring parameters for a specific SDK Function " + functionId);
        Optional<SdkFunction> function = functionRepository.findById(functionId);
        if (!function.isPresent()) {
            log.error("Function with ID " + functionId + " is not present in database");
            throw new NotExistingEntityException("Function with ID " + functionId + " is not present in database");
        }

        return (function.get().getMonitoringParameters());
    }

    @Override
    public void publishFunction(Long functionId)
        throws NotExistingEntityException, AlreadyPublishedServiceException {
        log.info("Request for publication of function with ID " + functionId);

        // Check if function exists
        Optional<SdkFunction> optFunction = functionRepository.findById(functionId);

        SdkFunction function = optFunction.orElseThrow(() -> {
            log.error("Function  with ID " + functionId + " is not present in database");
            return new NotExistingEntityException("Function with ID " + functionId + " is not present in database");
        });

        synchronized (this) { // To avoid multiple simultaneous calls
            if (!function.getStatus().equals(SdkFunctionStatus.SAVED)) {
                log.error("Requested publication for an entity already has been published");
                throw new AlreadyPublishedServiceException(String.format("Requested publication for an entity already has been published"));
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
            vnfdInfoId -> {
                if (vnfdInfoId != null) {
                    log.info("Function with ID {} successfully published", functionId);
                    function.setStatus(SdkFunctionStatus.COMMITTED);
                    function.setVnfInfoId(vnfdInfoId);
                    functionRepository.saveAndFlush(function);
                } else {
                    function.setStatus(SdkFunctionStatus.SAVED);
                    functionRepository.saveAndFlush(function);
                    log.error("Error while publishing function with ID {}", functionId);
                }
            }
        );
    }

    @Override
    public void unPublishFunction(Long functionId)
        throws NotExistingEntityException, NotPublishedServiceException {
        log.info("Requested deletion of the publication of the function with ID {}", functionId);
        Optional<SdkFunction> optFunction = functionRepository.findById(functionId);
        SdkFunction function = optFunction.orElseThrow(() -> {
            log.error("The Function with ID {} is not present in database", functionId);
            return new NotExistingEntityException(String.format("Function with ID %s is not present in database", functionId));
        });

        synchronized (this) { // To avoid multiple simultaneous calls
            // Check if is already published
            if (!function.getStatus().equals(SdkFunctionStatus.COMMITTED)) {
                log.error("Function with ID {} is not in status COMMITTED.", functionId);
                throw new NotPublishedServiceException(String.format("Function with ID %s is not in status COMMITTED", functionId));
            }
            function.setStatus(SdkFunctionStatus.CHANGING);
            functionRepository.saveAndFlush(function);
            // After setting the status, no one can operate on this anymore (except us)
        }

        dispatchUnPublishRequest(
            function.getVnfInfoId(),
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
        throws NotExistingEntityException {
        Optional<SdkFunction> optFunction = functionRepository.findById(functionId);

        SdkFunction function = optFunction.orElseThrow(() -> {
            log.error("Function with ID {} is not present in database", functionId);
            return new NotExistingEntityException(String.format("Function with ID {} is not present in database", functionId));
        });
        return adapter.generateVirtualNetworkFunctionDescriptor(function);
    }

    private void checkAndResolveFunction(SdkFunction function) throws AlreadyExistingEntityException {

        //In case of new function, check if a function with the same vnfdId and version is present
        if(function.getId() == null) {
            Optional<SdkFunction> functionOptional = functionRepository.findByVnfdIdAndVersion(function.getVnfdId(), function.getVersion());
            if (functionOptional.isPresent()) {
                log.error("Function with vnfdID " + function.getVnfdId() + " and version " + function.getVersion() + " is already present");
                throw new AlreadyExistingEntityException("Function with vnfdID " + function.getVnfdId() + " and version " + function.getVersion() + " is already present");
            }
        }

        function.setEpoch(Instant.now().getEpochSecond());

        //for the moment we consider single DF and IL, then we set static the expression
        function.setFlavourExpression("static_df");
        function.setInstantiationLevelExpression("static_il");

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

    private void dispatchPublishRequest(String functionPackagePath, Consumer<String> callback) {
        // TODO: dispatch publish operation to driver, then return immediately
        executor.execute(() -> {
                try {
                    String vnfdInfoId = cataloguePlugin.uploadNetworkFunction(functionPackagePath, "multipart/form-data", null);
                    callback.accept(vnfdInfoId);
                } catch (Exception exc) {
                    log.error(
                        "Could not push function package. Cause: {}",
                        exc.getMessage()
                    );
                    log.debug("Details: ", exc);
                    callback.accept(null);
                }
            }
        );
    }

    private void dispatchUnPublishRequest(String vnfInfoId, Consumer<Boolean> callback) {
        // TODO: dispatch unpublish operation to driver, then return immediately
        executor.execute(() -> {
                try {
                    cataloguePlugin.deleteNetworkFunction(vnfInfoId);
                    callback.accept(true);
                } catch (Exception exc) {
                    log.error(
                        "Could not delete function package. Cause: {}",
                        exc.getMessage()
                    );
                    log.debug("Details: ", exc);
                    callback.accept(false);
                }
            }
        );
    }

    private void createFunctionFromVnfd(CSARInfo csarInfo, String mf, String storagePath) throws MalformattedElementException, IOException, FailedOperationException{
        SdkFunction sdkFunction = new SdkFunction();
        DescriptorTemplate dt = csarInfo.getMst();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            //TODO change COMMITTED with PUBLISHED?
            sdkFunction.setVnfdId(dt.getMetadata().getDescriptorId());
            sdkFunction.setVersion(dt.getMetadata().getVersion());
            sdkFunction.setDescription(dt.getDescription());
            sdkFunction.setVendor(dt.getMetadata().getVendor());
            //For the moment consider only one VNFNode
            VNFNode vnfNode = dt.getTopologyTemplate().getVNFNodes().values().iterator().next();
            sdkFunction.setName(vnfNode.getProperties().getProductName());
            //sdkFunction.setVnfdProvider(vnfNode.getProperties().getProvider());

            //TODO ownerID, groupid, visibility, accessLevel?
            sdkFunction.setOwnerId("Undefined");
            sdkFunction.setGroupId("Undefined");
            sdkFunction.setVisibility(Visibility.PUBLIC);
            sdkFunction.setAccessLevel(4);

            //TODO parameters, requiredPort?

            Set<ConnectionPoint> connectionPoints = new HashSet<>();
            Map<String, VnfExtCpNode> cpNodes = dt.getTopologyTemplate().getVnfExtCpNodes();
            for (Map.Entry<String, VnfExtCpNode> cpNode : cpNodes.entrySet()) {
                ConnectionPoint cp = new ConnectionPoint();
                cp.setName(cpNode.getKey());
                cp.setType(ConnectionPointType.EXTERNAL);
                connectionPoints.add(cp);
            }
            sdkFunction.setConnectionPoint(connectionPoints);

            //For the moment consider only one VDU
            VDUComputeNode vduNode = dt.getTopologyTemplate().getVDUComputeNodes().values().iterator().next();
            sdkFunction.setMaxInstancesCount(vduNode.getProperties().getVduProfile().getMaxNumberOfInstances());
            sdkFunction.setMinInstancesCount(vduNode.getProperties().getVduProfile().getMinNumberOfInstances());

            SwImageData imageData = new SwImageData();
            VDUVirtualBlockStorageNode storageNode = dt.getTopologyTemplate().getVDUBlockStorageNodes().values().iterator().next();
            imageData.setImgName(storageNode.getProperties().getSwImageData().getImageName());
            imageData.setImgVersion(storageNode.getProperties().getSwImageData().getVersion());
            imageData.setChecksum(storageNode.getProperties().getSwImageData().getChecksum());
            imageData.setContainerFormat(storageNode.getProperties().getSwImageData().getContainerFormat().toString());
            imageData.setDiskFormat(storageNode.getProperties().getSwImageData().getDiskFormat().toString());
            imageData.setSize(storageNode.getProperties().getSwImageData().getSize());
            imageData.setMinDisk(storageNode.getProperties().getVirtualBlockStorageData().getSizeOfStorage());
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

            createFunction(sdkFunction);

            sdkFunction.setStatus(SdkFunctionStatus.COMMITTED);
            //package filename is the vnfPkgInfo ID
            sdkFunction.setVnfInfoId(csarInfo.getPackageFilename());
            functionRepository.saveAndFlush(sdkFunction);
        }catch(MalformedElementException | AlreadyExistingEntityException e){
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
}

