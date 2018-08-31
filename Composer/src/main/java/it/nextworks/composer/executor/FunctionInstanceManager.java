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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.nextworks.composer.executor.interfaces.FunctionInstanceManagerProviderInterface;
import it.nextworks.composer.executor.repositories.MonitoringParameterRepository;
import it.nextworks.composer.executor.repositories.SDKFunctionInstanceRepository;
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.SDKFunction;
import it.nextworks.sdk.SDKFunctionInstance;
import it.nextworks.sdk.SDKService;
import it.nextworks.sdk.enums.Flavour;
import it.nextworks.sdk.exceptions.ExistingEntityException;
import it.nextworks.sdk.exceptions.MalformattedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;

@Service
public class FunctionInstanceManager implements FunctionInstanceManagerProviderInterface{

	private static final Logger log = LoggerFactory.getLogger(FunctionInstanceManager.class);

	@Autowired
	private SDKFunctionInstanceRepository functionInstanceRepository;
	
	@Autowired 
	private MonitoringParameterRepository monitoringParamRepository;
	

	@Autowired
	private FunctionManager functionManager;
	
	public FunctionInstanceManager() {}
	
	@Override
	public SDKFunctionInstance getFunction(String id) throws NotExistingEntityException {
		Optional<SDKFunctionInstance> result = functionInstanceRepository.findById(Long.parseLong(id));
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
	public List<SDKFunctionInstance> getFunctionInstancesForFunction(String functionId) {
		List<SDKFunctionInstance> functionList = functionInstanceRepository.findAll();
		List<SDKFunctionInstance> result = new ArrayList<>();
		for(SDKFunctionInstance instance: functionList) {
			if(instance.getFunction().equalsIgnoreCase(functionId)) {
				result.add(instance);
			}
		}
		return result;
	}
	
	@Override
	public void updateFlavor(String functionId, Flavour flavour) throws NotExistingEntityException {
		log.info("Request to update the flavor for a specific SDK Service " + functionId);
		Optional<SDKFunctionInstance> function = functionInstanceRepository.findById(Long.parseLong(functionId));
		if(!function.isPresent()) {
			log.error("The Service with UUID: " + functionId + " is not present in database");
			throw new NotExistingEntityException("The Service with UUID: " + functionId + " is not present in database");
		}
		
		
	}

	@Override
	public void updateMonitoringParameters(String functionId, List<MonitoringParameter> monitoringParameters)
			throws NotExistingEntityException, MalformattedElementException {
		log.info("Request to update list of scalingAspects for a specific SDK FunctionInstance " + functionId);
		Optional<SDKFunctionInstance> function = functionInstanceRepository.findById(Long.parseLong(functionId));
		if(!function.isPresent()) {
			log.error("The Function with UUID: " + functionId + " is not present in database");
			throw new NotExistingEntityException("The Function with UUID: " + functionId + " is not present in database");
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
	public void deleteMonitoringParameters(String functionId, List<MonitoringParameter> monitoringParameters)
			throws NotExistingEntityException, MalformattedElementException {
		log.info("Request to delete a list of monitoring parameters for a specific SDK FunctionInstance " + functionId);
		Optional<SDKFunctionInstance> function = functionInstanceRepository.findById(Long.parseLong(functionId));
		if(!function.isPresent()) {
			log.error("The Function with UUID: " + functionId + " is not present in database");
			throw new NotExistingEntityException("The Function with UUID: " + functionId + " is not present in database");
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
	public List<MonitoringParameter> getMonitoringParameters(String functionId) throws NotExistingEntityException {
		log.info("Request to get the list of monitoring parameters for a specific SDK FunctionInstance " + functionId);
		Optional<SDKFunctionInstance> function = functionInstanceRepository.findById(Long.parseLong(functionId));
		if(!function.isPresent()) {
			log.error("The Function with UUID: " + functionId + " is not present in database");
			throw new NotExistingEntityException("The Function with UUID: " + functionId + " is not present in database");
		} 
		List<MonitoringParameter> list = new ArrayList<>();
		for(MonitoringParameter param: function.get().getMonitoringParameters())
			list.add(param);
 		return list;
	}

	@Override
	public String createInstance(SDKFunctionInstance instance, SDKService service) throws ExistingEntityException, NotExistingEntityException, MalformattedElementException {

		//Check if FunctionID is correct
		SDKFunction function = null;
		try {
			function = functionManager.getFunction(instance.getFunction());
		} catch (NotExistingEntityException e) {
			log.error("The Function with UUID: " + instance.getFunction() + " is not present in database");
			throw new NotExistingEntityException("The Function with UUID: " + instance.getFunction() + " is not present in database");
		}
		if (!function.getFlavour().contains(instance.getFlavour())) {
			log.error("The flavour chosen for the FunctionInstance is not available. Your choise: " + instance.getFlavour().toString());
			throw new MalformattedElementException("The flavour chosen for the FunctionInstance is not available. Your choise: " + instance.getFlavour().toString());
		}
		instance.setFunction(function.getId().toString());
		instance.setService(service);
		functionInstanceRepository.saveAndFlush(instance);
		//Getting MonitoringParameters
		List<MonitoringParameter> monitoringParameters = instance.getMonitoringParameters();
		//Create monitoring parameters
		if(monitoringParameters != null) {
			for(MonitoringParameter param: monitoringParameters) {
				if(param.isValid()) {
					param.setFunctionInstance(instance);
					monitoringParamRepository.saveAndFlush(param);
				} else {
					log.warn("Error validating monitoring parameter with ID "+ param.getId() +". Skipping it from the monitoring parameter list");
				}
			}
		}
		return instance.getId().toString();
	}

}
