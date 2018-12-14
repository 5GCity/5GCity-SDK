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
import it.nextworks.sdk.ConnectionPoint;
import it.nextworks.sdk.SdkFunction;
import it.nextworks.sdk.enums.ConnectionPointType;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Override
    public String createFunction(SdkFunction function) {
        return null;
    }

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

}

