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
import it.nextworks.composer.executor.repositories.SDKFunctionRepository;
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.SDKFunction;
import it.nextworks.sdk.SDKFunctionInstance;
import it.nextworks.sdk.SDKService;
import it.nextworks.sdk.enums.Flavour;
import it.nextworks.sdk.exceptions.ExistingEntityException;
import it.nextworks.sdk.exceptions.MalformattedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;

@Service
public class FunctionInstanceManager implements FunctionInstanceManagerProviderInterface {

	private static final Logger log = LoggerFactory.getLogger(FunctionInstanceManager.class);

	@Autowired
	private SDKFunctionInstanceRepository functionInstanceRepository;

	@Autowired
	private MonitoringParameterRepository monitoringParamRepository;

	@Autowired
	private SDKFunctionRepository functionRepository;

	@Autowired
	private FunctionManager functionManager;

	public FunctionInstanceManager() {
	}

	@Override
	public SDKFunctionInstance getFunction(Long id) throws NotExistingEntityException {
		Optional<SDKFunctionInstance> result = functionInstanceRepository.findById(id);
		if (result.isPresent()) {
			return result.get();
		} else {
			log.error("No SDKFunction with UUID: " + id + " was found.");
			throw new NotExistingEntityException("No SDKFunction with UUID: " + id + " was found.");
		}
	}

	@Override
	public List<SDKFunctionInstance> getFunctions() {
		List<SDKFunctionInstance> functionList = functionInstanceRepository.findAll();
		if (functionList.size() == 0) {
			log.debug("No Functions are available");
		} else
			log.debug("SDK Functions present in DB: " + functionList.size());
		return functionList;
	}

	@Override
	public List<SDKFunctionInstance> getFunctionInstancesForFunction(Long functionId) {
		List<SDKFunctionInstance> functionList = functionInstanceRepository.findAll();
		List<SDKFunctionInstance> result = new ArrayList<>();
		for (SDKFunctionInstance instance : functionList) {
			if (instance.getFunctionId() == functionId) {
				result.add(instance);
			}
		}
		return result;
	}

	@Override
	public void updateFlavor(Long functionId, Flavour flavour) throws NotExistingEntityException {
		log.info("Request to update the flavor for a specific SDK Service " + functionId);
		Optional<SDKFunctionInstance> function = functionInstanceRepository.findById(functionId);
		if (!function.isPresent()) {
			log.error("The Service with UUID: " + functionId + " is not present in database");
			throw new NotExistingEntityException(
					"The Service with UUID: " + functionId + " is not present in database");
		}

	}

	@Override
	public void updateMonitoringParameters(Long functionId, List<MonitoringParameter> monitoringParameters)
			throws NotExistingEntityException, MalformattedElementException {
		log.info("Request to update list of scalingAspects for a specific SDK FunctionInstance " + functionId);
		Optional<SDKFunctionInstance> instance = functionInstanceRepository.findById(functionId);
		if (!instance.isPresent()) {
			log.error("The FunctionInstance with UUID: " + functionId + " is not present in database");
			throw new NotExistingEntityException(
					"The FunctionInstance with UUID: " + functionId + " is not present in database");
		}
		Optional<SDKFunction> function = functionRepository.findById(instance.get().getFunctionId());
		if(!function.isPresent()) {
			log.error("The FunctionInstance with UUID: " + functionId + " is not referring to the correct Function");
			throw new NotExistingEntityException(
					"The FunctionInstance with UUID: " + functionId + " is not referring to the correct Function");
		}
		for (MonitoringParameter param : monitoringParameters) {
			if (!param.isValid()) {
				log.error("Malformed MonitoringParameter");
				throw new MalformattedElementException("Malformed MonitoringParameter");
			}
		}
		for(MonitoringParameter param : monitoringParameters) {
			if(!function.get().getMonitoringParameters().contains(param)) {
				log.error("Monitoring Parameter is not contained in the SDKFunction Monitoring Parameters list");
				throw new MalformattedElementException("Monitoring Parameter is not contained in the SDKFunction Monitoring Parameters list");
			}
		}
		log.debug("Updating list of monitoring parameters on instance");
		instance.get().setMonitoringParameters(monitoringParameters);
		log.debug("Updating list of monitoring parameters on database");
		functionInstanceRepository.saveAndFlush(instance.get());
	}

	@Override
	public void deleteMonitoringParameters(Long functionId, List<MonitoringParameter> monitoringParameters)
			throws NotExistingEntityException, MalformattedElementException {
		log.info("Request to delete a list of monitoring parameters for a specific SDK FunctionInstance " + functionId);
		Optional<SDKFunctionInstance> function = functionInstanceRepository.findById(functionId);
		if (!function.isPresent()) {
			log.error("The Function with UUID: " + functionId + " is not present in database");
			throw new NotExistingEntityException(
					"The Function with UUID: " + functionId + " is not present in database");
		}
		for (MonitoringParameter param : monitoringParameters) {
			if (!param.isValid()) {
				log.error("Malformed MonitoringParameter");
				throw new MalformattedElementException("Malformed MonitoringParameter");
			}
		}
		log.debug("All monitoring parameters are valid. Deleting them from SDK Function");
		for (MonitoringParameter param : monitoringParameters) {
			function.get().deleteMonitoringParameter(param);
		}
		log.debug("All monitoring parameters have been deleted. Saving to database");
		functionInstanceRepository.saveAndFlush(function.get());

	}

	@Override
	public List<MonitoringParameter> getMonitoringParameters(Long functionId) throws NotExistingEntityException {
		log.info("Request to get the list of monitoring parameters for a specific SDK FunctionInstance " + functionId);
		Optional<SDKFunctionInstance> function = functionInstanceRepository.findById(functionId);
		if (!function.isPresent()) {
			log.error("The Function with UUID: " + functionId + " is not present in database");
			throw new NotExistingEntityException(
					"The Function with UUID: " + functionId + " is not present in database");
		}
		List<MonitoringParameter> list = new ArrayList<>();
		for (MonitoringParameter param : function.get().getMonitoringParameters())
			list.add(param);
		return list;
	}

	@Override
	public String createInstance(SDKFunctionInstance instance, SDKService service)
			throws ExistingEntityException, NotExistingEntityException, MalformattedElementException {

		instance.setService(service);
		// Check if FunctionID is correct
		SDKFunction function = null;
		try {
			function = functionManager.getFunction(instance.getFunctionId());
		} catch (NotExistingEntityException e) {
			functionInstanceRepository.delete(instance);
			log.error("The Function with UUID: " + instance.getFunctionId() + " is not present in database");
			throw new NotExistingEntityException(
					"The Function with UUID: " + instance.getFunctionId() + " is not present in database");
		}
		if (!function.getFlavour().contains(instance.getFlavour())) {
			functionInstanceRepository.delete(instance);
			log.error("The flavour chosen for the FunctionInstance is not available. Your choise: "
					+ instance.getFlavour().toString());
			throw new MalformattedElementException(
					"The flavour chosen for the FunctionInstance is not available. Your choise: "
							+ instance.getFlavour().toString());
		}
		//instance.setSdkFunction(function);
		instance.setFunctionId(function.getId());
		functionInstanceRepository.saveAndFlush(instance);
		// Getting MonitoringParameters
		List<MonitoringParameter> monitoringParameters = instance.getMonitoringParameters();
		// Create monitoring parameters
		if (monitoringParameters != null) {
			for (MonitoringParameter param : monitoringParameters) {
				if (param.isValid()) {
					param.setFunctionInstance(instance);
					monitoringParamRepository.saveAndFlush(param);
				} else {
					log.warn("Error validating monitoring parameter with ID " + param.getId()
							+ ". Skipping it from the monitoring parameter list");
				}
			}
		}
		return instance.getId().toString();
	}

	@Override
	public String updateInstance(SDKFunctionInstance instance, SDKService service)
			throws NotExistingEntityException, MalformattedElementException {
		// Check if Instance exists and it's the same with the one in DB
		Optional<SDKFunctionInstance> sdkInstance = functionInstanceRepository.findById(instance.getId());
		if(!sdkInstance.isPresent() || sdkInstance.get().getFunctionId() != instance.getFunctionId()) {
			log.error("The SDKFunctionInstance doesn't exist or SDKInstance is referring a different SDKFunction");
			throw new NotExistingEntityException();
		} 
		// Update Flavor
		// Getting the SDKFunction related
		Optional<SDKFunction> function = functionRepository.findById(instance.getFunctionId());
		if(!function.isPresent()) {
			log.error("The SDKFunctionInstance is associated to an non existent SDKFunction");
			throw new MalformattedElementException();
		}
		if(!function.get().getFlavour().contains(instance.getFlavour())) {
			log.error("Trying to associate a flavour value not compliant with the SDKFunction capabilities");
			throw new MalformattedElementException();
		}
		// Update MonitoringParameters (this has direct consequences on SDKService)
		this.updateMonitoringParameters(instance.getId(), instance.getMonitoringParameters());
		instance.setService(service);
		functionInstanceRepository.saveAndFlush(instance);
		return instance.getId().toString();
	}

	boolean validateInstance(SDKFunctionInstance instance) {
		if (!instance.isValid()) {
			return false;
		} else {
			Optional<SDKFunction> function = functionRepository.findById(instance.getFunctionId());
			if (!function.isPresent()) {
				return false;
			}
			if (!function.get().getFlavour().contains(instance.getFlavour())) {
				return false;
			}
			if(!validateMonitoringParameters(instance)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean validateMonitoringParameters(SDKFunctionInstance instance) {
		List<MonitoringParameter> monitoringParameters = instance.getMonitoringParameters();
		if (monitoringParameters != null) {
			for (MonitoringParameter param : monitoringParameters) {
				if (!param.isValid()) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	public void deleteInstance(Long id) throws NotExistingEntityException{
		Optional<SDKFunctionInstance> instance = functionInstanceRepository.findById(id);
		if(!instance.isPresent()) {
			log.error("Trying to delete an entity which is not present in database");
			throw new NotExistingEntityException();
		} else {
			functionInstanceRepository.delete(instance.get());
		}
	}
	
	
}
