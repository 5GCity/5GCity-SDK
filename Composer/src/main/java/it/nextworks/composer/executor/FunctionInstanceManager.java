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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.nextworks.composer.executor.interfaces.FunctionInstanceManagerProviderInterface;
import it.nextworks.composer.executor.repositories.SDKFunctionInstanceRepository;
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.SDKFunctionInstance;
import it.nextworks.sdk.enums.Flavour;
import it.nextworks.sdk.exceptions.MalformattedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;

@Service
public class FunctionInstanceManager implements FunctionInstanceManagerProviderInterface{

	private static final Logger log = LoggerFactory.getLogger(FunctionInstanceManager.class);

	@Autowired
	private SDKFunctionInstanceRepository functionInstanceRepository;
	
	public FunctionInstanceManager() {}
	
	@Override
	public SDKFunctionInstance getFunction(UUID id) throws NotExistingEntityException {
		Optional<SDKFunctionInstance> result = functionInstanceRepository.findByUuid(id);
		if(result.isPresent()) {
			return result.get();
		} else {
			log.error("No SDKFunction with UUID: " + id + " was found.");
			throw new NotExistingEntityException("No SDKFunction with UUID: " + id + " was found.");
		}
	}

	

	
	@Override
	public List<SDKFunctionInstance> getFunctions() {
		List<SDKFunctionInstance> functionList = functionInstanceRepository.findAll();
		if(functionList.size() == 0) {
			log.debug("No Functions are available");
		} else 
			log.debug("SDK Functions present in DB: " + functionList.size());
		return functionList;
	}

	@Override
	public List<SDKFunctionInstance> getFunctionInstancesForFunction(UUID functionId) {
		List<SDKFunctionInstance> functionList = functionInstanceRepository.findAll();
		List<SDKFunctionInstance> result = new ArrayList<>();
		for(SDKFunctionInstance instance: functionList) {
			if(instance.getFunction().getUuid().equals(functionId)) {
				result.add(instance);
			}
		}
		return result;
	}
	
	@Override
	public void updateFlavor(UUID functionId, Flavour flavour) throws NotExistingEntityException {
		log.info("Request to update the flavor for a specific SDK Service " + functionId.toString());
		Optional<SDKFunctionInstance> function = functionInstanceRepository.findByUuid(functionId);
		if(!function.isPresent()) {
			log.error("The Service with UUID: " + functionId.toString() + " is not present in database");
			throw new NotExistingEntityException("The Service with UUID: " + functionId.toString() + " is not present in database");
		}
		
		
	}

	@Override
	public void updateMonitoringParameters(UUID functionId, List<MonitoringParameter> monitoringParameters)
			throws NotExistingEntityException, MalformattedElementException {
		log.info("Request to update list of scalingAspects for a specific SDK FunctionInstance " + functionId.toString());
		Optional<SDKFunctionInstance> function = functionInstanceRepository.findByUuid(functionId);
		if(!function.isPresent()) {
			log.error("The Function with UUID: " + functionId.toString() + " is not present in database");
			throw new NotExistingEntityException("The Function with UUID: " + functionId.toString() + " is not present in database");
		}
		for(MonitoringParameter param: monitoringParameters) {
			if(!param.isValid()) {
				log.error("Malformed MonitoringParameter");
				throw new MalformattedElementException("Malformed MonitoringParameter");
			}
		}
		log.debug("Updating list of monitoring parameters on function");
		function.get().setMonitoringParameters(monitoringParameters);
		log.debug("Updating list of monitoring parameters on database");
		functionInstanceRepository.saveAndFlush(function.get());		
	}

	@Override
	public void deleteMonitoringParameters(UUID functionId, List<MonitoringParameter> monitoringParameters)
			throws NotExistingEntityException, MalformattedElementException {
		log.info("Request to delete a list of monitoring parameters for a specific SDK FunctionInstance " + functionId.toString());
		Optional<SDKFunctionInstance> function = functionInstanceRepository.findByUuid(functionId);
		if(!function.isPresent()) {
			log.error("The Function with UUID: " + functionId.toString() + " is not present in database");
			throw new NotExistingEntityException("The Function with UUID: " + functionId.toString() + " is not present in database");
		} 
		for(MonitoringParameter param: monitoringParameters) {
			if(!param.isValid()) {
				log.error("Malformed MonitoringParameter");
				throw new MalformattedElementException("Malformed MonitoringParameter");
			}
		}
		log.debug("All monitoring parameters are valid. Deleting them from SDK Function");
		for(MonitoringParameter param: monitoringParameters) {
			function.get().deleteMonitoringParameter(param);
		}
		log.debug("All monitoring parameters have been deleted. Saving to database");
		functionInstanceRepository.saveAndFlush(function.get());
		
	}

	@Override
	public List<MonitoringParameter> getMonitoringParameters(UUID functionId) throws NotExistingEntityException {
		log.info("Request to get the list of monitoring parameters for a specific SDK FunctionInstance " + functionId.toString());
		Optional<SDKFunctionInstance> function = functionInstanceRepository.findByUuid(functionId);
		if(!function.isPresent()) {
			log.error("The Function with UUID: " + functionId.toString() + " is not present in database");
			throw new NotExistingEntityException("The Function with UUID: " + functionId.toString() + " is not present in database");
		} 
		List<MonitoringParameter> list = new ArrayList<>();
		for(MonitoringParameter param: function.get().getMonitoringParameters())
			list.add(param);
 		return list;
	}

}
