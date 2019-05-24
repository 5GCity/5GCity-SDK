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

import it.nextworks.composer.executor.interfaces.FunctionManagerProviderInterface;
import it.nextworks.composer.executor.repositories.SdkFunctionRepository;
import it.nextworks.sdk.*;
import it.nextworks.sdk.enums.ConnectionPointType;
import it.nextworks.sdk.enums.SdkServiceStatus;
import it.nextworks.sdk.exceptions.MalformedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;

@Service
public class FunctionManager implements FunctionManagerProviderInterface {

    private static final Logger log = LoggerFactory.getLogger(FunctionManager.class);

    @Autowired
    private SdkFunctionRepository functionRepository;

    public FunctionManager() {

    }

    @Override
    public SdkFunction getFunction(Long id) throws NotExistingEntityException {
        Optional<SdkFunction> result = functionRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        } else {
            log.error("No SdkFunction with UUID: " + id + " was found.");
            throw new NotExistingEntityException("No SdkFunction with UUID: " + id + " was found.");
        }
    }


    @Override
    public List<SdkFunction> getFunctions() {
        List<SdkFunction> functionList = functionRepository.findAll();
        if (functionList.size() == 0) {
            log.debug("No Functions are available");
        } else
            log.debug("Sdk Functions present in DB: " + functionList.size());
        return functionList;
    }

    /*
    @Override
    public String createFunction(SdkFunction function) {
        return null;
    }
    */

    @Override
    public String createFunction(SdkFunction function)
        throws NotExistingEntityException, MalformedElementException, NotYetImplementedException {

        log.info("Storing into database a new function");

        if (!function.isValid()) {
            log.error("Malformed SdkService");
            throw new MalformedElementException("Malformed SdkService");
        }
        ////// TODO-NXW
        throw new NotYetImplementedException();
        /*
        checkAndResolveFunction(function);


        log.debug("Storing into database function with name: " + function.getName());

        // Saving the function
        functionRepository.saveAndFlush(function);
        return function.getId().toString();
        */
    }

    /*
    @Override
    public String createFunction() {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("cloud-init", "#!/bin/vbash\r\n" +
            "source /opt/vyatta/etc/functions/script-template\r\n" +
            "configure\r\n" +
            "set interfaces ethernet eth1  address 192.168.200.1/24\r\n" +
            "\r\n" +
            "commit\r\n" +
            "exit");

        SdkFunction function = new SdkFunction();
        function.setName("vFirewall-v3");
        function.setVersion("v3");
        function.setVendor("Nextworks");
        function.setDescription("vFirewall");
        function.setMetadata(metadata);

        ConnectionPoint cp1 = new ConnectionPoint();
        cp1.setName("MGMT");
        cp1.setType(ConnectionPointType.EXTERNAL);
        cp1.setSdkFunction(function);
        ConnectionPoint cp2 = new ConnectionPoint();
        cp2.setName("VIDEO");
        cp2.setType(ConnectionPointType.EXTERNAL);
        cp2.setSdkFunction(function);
        ConnectionPoint cp3 = new ConnectionPoint();
        cp3.setName("EXT");
        cp3.setType(ConnectionPointType.EXTERNAL);
        cp3.setSdkFunction(function);
        ConnectionPoint cp4 = new ConnectionPoint();
        cp4.setName("DCNET");
        cp4.setType(ConnectionPointType.EXTERNAL);
        cp4.setSdkFunction(function);

        functionRepository.saveAndFlush(function);

//
//		MonitoringParameter param1 = new MonitoringParameter(MonitoringParameterType.AVERAGE_MEMORY_UTILIZATION, null, function, null);
//		monitoringParamRepository.saveAndFlush(param1);

        Map<String, String> metadata2 = new HashMap<>();
//		metadata2.put("key1", "value1");
//		metadata2.put("key2", "value3");
//		metadata2.put("key3", "value2");
        SdkFunction function2 = new SdkFunction();
        function2.setName("vPlate-v3");
        function2.setVersion("v3");
        function2.setVendor("Nextworks");
        function2.setDescription("VPlate: Plate recognition");
        function2.setMetadata(metadata2);

        ConnectionPoint cp12 = new ConnectionPoint();
        cp12.setName("VIDEO");
        cp12.setType(ConnectionPointType.EXTERNAL);
        cp12.setSdkFunction(function2);

        functionRepository.saveAndFlush(function2);

//		MonitoringParameter param12 = new MonitoringParameter(MonitoringParameterType.AVERAGE_MEMORY_UTILIZATION, null, function2, null);
//		monitoringParamRepository.saveAndFlush(param12);

        return function.getId().toString();
    }
    */

    @Override
    public String updateFunction(SdkFunction function) throws NotExistingEntityException, MalformedElementException, NotYetImplementedException {
        log.info("Updating an existing Function with id: " + function.getId());

        throw new NotYetImplementedException();
        /*
        if (!function.isValid()) {
            log.error("Function id " + function.getId() + " is malformed");
            throw new MalformedElementException("Function id " + function.getId() + " is malformed");
        }
        log.debug("Function is valid");
        // Check if Function exists
        Optional<SdkFunction> func = functionRepository.findById(function.getId());
        if (!func.isPresent()) {
            log.error("Function id " + function.getId() + " not present in database");
            throw new NotExistingEntityException("Function id " + function.getId() + " not present in database");
        }
        log.debug("Function found on db");

        //checkAndResolveFunction(function);

        log.debug("Updating into database Function with id: " + function.getId());

        //cleanOldRelations(func.get());

        // Update Function on DB
        functionRepository.saveAndFlush(function);
        return function.getId().toString();
         */
    }


    @Override
    public SdkFunction getFunctionById(Long id) throws NotExistingEntityException {
        log.info("Request for Function with ID: " + id);
        Optional<SdkFunction> function = functionRepository.findById(id);
        if (function.isPresent()) {
            return function.get();
        } else {
            log.error("Function with UUID " + id + " not found");
            throw new NotExistingEntityException("Function with ID " + id + " not found");
        }
    }

    @Override
    public void deleteFunction(Long functionId) throws NotExistingEntityException {
        log.info("Request for deletion of Function with id: " + functionId);
        // No deletion required: all that depends on the Function will cascade.
        Optional<SdkFunction> function = functionRepository.findById(functionId);
        SdkFunction s = function.orElseThrow(() -> {
            log.error("Function with ID " + functionId + " not found");
            return new NotExistingEntityException("Function with ID " + functionId + " not found");
        });
        functionRepository.delete(s);
    }

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
        */
    }

    @Override
    public String publishFunction(Long functionId, List<BigDecimal> parameterValues)
        throws NotExistingEntityException, MalformedElementException, NotYetImplementedException {
        log.info("Request for publication of function with uuid: " + functionId);

        throw new NotYetImplementedException();
        /*
        // Check if service exists
        Optional<SdkFunction> optService = functionRepository.findById(functionId);

        SdkFunction service = optService.orElseThrow(() -> {
            log.error("The SDK Function  with UUID: " + functionId + " is not present in database");
            return new NotExistingEntityException("The SDK Function with UUID: " + functionId + " is not present in database");
        });


        return functionId;
        */
    }

    @Override
    public void updateMonitoringParameters(Long functionId, Set<MonitoringParameter> monitoringParameters)
        throws NotExistingEntityException, MalformedElementException, NotYetImplementedException {
        log.info("Request to update list of scalingAspects for a specific SDK Function " + functionId);
        Optional<SdkFunction> function = functionRepository.findById(functionId);
        if (!function.isPresent()) {
            log.error("The SDK Function with ID: " + functionId + " is not present in database");
            throw new NotExistingEntityException("The SDK Function with ID: " + functionId + " is not present in database");
        }

        for(MonitoringParameter mp : function.get().getMonitoringParameters()){
            mp.setSdkFunction(null);
        }

        log.debug("Updating list of monitoring parameters on SDF Function with ID: " + functionId);
        function.get().setMonitoringParameters(monitoringParameters);

        log.debug("Updating list of monitoring parameters on database");
        functionRepository.saveAndFlush(function.get());

    }

    @Override
    public void deleteMonitoringParameters(Long functionId, Long monitoringParameterId)
        throws NotExistingEntityException, MalformedElementException, NotYetImplementedException {

        log.info("Request to delete a monitoring parameter identified by id " + monitoringParameterId + " for a specific SDK Service " + functionId);
        Optional<SdkFunction> function = functionRepository.findById(functionId);
        if (!function.isPresent()) {
            log.error("The SDK Function with ID: " + functionId + " is not present in database");
            throw new NotExistingEntityException("The SDK Function with ID: " + functionId + " is not present in database");
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

        functionRepository.saveAndFlush(function.get());
        log.debug("Monitoring parameter has been deleted.");

    }

    @Override
    public Set<MonitoringParameter> getMonitoringParameters(Long functionId) throws NotExistingEntityException, NotYetImplementedException {
        log.info("Request to get the list of monitoring parameters for a specific SDK Function " + functionId);
        Optional<SdkFunction> function = functionRepository.findById(functionId);
        if (!function.isPresent()) {
            log.error("The SDK Function with ID: " + functionId + " is not present in database");
            throw new NotExistingEntityException("The SDK Functionwith ID: " + functionId + " is not present in database");
        }

        return (function.get().getMonitoringParameters());
    }

}

