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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.nextworks.composer.executor.interfaces.FunctionManagerProviderInterface;
import it.nextworks.composer.executor.repositories.ConnectionpointRepository;
import it.nextworks.composer.executor.repositories.MonitoringParameterRepository;
import it.nextworks.composer.executor.repositories.SDKFunctionRepository;
import it.nextworks.sdk.ConnectionPoint;
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.SDKFunction;
import it.nextworks.sdk.enums.ConnectionPointType;
import it.nextworks.sdk.enums.Flavour;
import it.nextworks.sdk.enums.MonitoringParameterType;
import it.nextworks.sdk.exceptions.NotExistingEntityException;

@Service
public class FunctionManager implements FunctionManagerProviderInterface{

	private static final Logger log = LoggerFactory.getLogger(FunctionManager.class);
			
	@Autowired
	private SDKFunctionRepository functionRepository;
	
	@Autowired
	private ConnectionpointRepository cpRepository;
	
	@Autowired
	private MonitoringParameterRepository monitoringParamRepository;

	public FunctionManager() {
		
	}
	
	@Override
	public SDKFunction getFunction(Long id) throws NotExistingEntityException {
		Optional<SDKFunction> result = functionRepository.findById(id);
		if(result.isPresent()) {
			return result.get();
		} else {
			log.error("No SDKFunction with UUID: " + id + " was found.");
			throw new NotExistingEntityException("No SDKFunction with UUID: " + id + " was found.");
		}
	}

	
	@Override
	public List<SDKFunction> getFunctions() {
		List<SDKFunction> functionList = functionRepository.findAll();
		if(functionList.size() == 0) {
			log.debug("No Functions are available");
		} else 
			log.debug("SDK Functions present in DB: " + functionList.size());
		return functionList;
	}

	@Override
	public String createFunction() {
		List<Flavour> flavour = new ArrayList<>();
		flavour.add(Flavour.SMALL);
		Map<String, String> metadata = new HashMap<>();
		metadata.put("cloud-init", "#!/bin/vbash\r\n" + 
				"source /opt/vyatta/etc/functions/script-template\r\n" + 
				"configure\r\n" + 
				"set interfaces ethernet eth1  address 192.168.200.1/24\r\n" + 
				"\r\n" + 
				"commit\r\n" + 
				"exit");
//		metadata.put("key2", "value3");
//		metadata.put("key3", "value2");
		
		SDKFunction function = new SDKFunction("vFirewall-v3", flavour, "v3", "vfirewall_id", "Nextworks", "vFirewall", metadata);
		functionRepository.saveAndFlush(function);
		
		
		ConnectionPoint cp1 = new ConnectionPoint(ConnectionPointType.EXTERNAL, "MGMT", function, null);
		ConnectionPoint cp2 = new ConnectionPoint(ConnectionPointType.EXTERNAL, "VIDEO", function, null);
		ConnectionPoint cp3 = new ConnectionPoint(ConnectionPointType.EXTERNAL, "EXT", function, null);
		ConnectionPoint cp4 = new ConnectionPoint(ConnectionPointType.EXTERNAL, "DCNET", function, null);
		cpRepository.saveAndFlush(cp1);
		cpRepository.saveAndFlush(cp2);
		cpRepository.saveAndFlush(cp3);
		cpRepository.saveAndFlush(cp4);
		
//
//		MonitoringParameter param1 = new MonitoringParameter(MonitoringParameterType.AVERAGE_MEMORY_UTILIZATION, null, function, null);
//		monitoringParamRepository.saveAndFlush(param1);
		

		List<Flavour> flavour2 = new ArrayList<>();
		flavour2.add(Flavour.SMALL);
		Map<String, String> metadata2 = new HashMap<>();
//		metadata2.put("key1", "value1");
//		metadata2.put("key2", "value3");
//		metadata2.put("key3", "value2");
		SDKFunction function2 = new SDKFunction("vPlate-v3", flavour2, "v3", "vplate_id", "Nextworks", "VPlate: Plate recognition", metadata2);
		functionRepository.saveAndFlush(function2);
		
		
		ConnectionPoint cp12 = new ConnectionPoint(ConnectionPointType.EXTERNAL, "VIDEO", function2, null);
		cpRepository.saveAndFlush(cp12);
		

//		MonitoringParameter param12 = new MonitoringParameter(MonitoringParameterType.AVERAGE_MEMORY_UTILIZATION, null, function2, null);
//		monitoringParamRepository.saveAndFlush(param12);
		
		return function.getId().toString();
	}

}
