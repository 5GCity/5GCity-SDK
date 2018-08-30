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
package it.nextworks.composer.executor.interfaces;

import java.util.List;

import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.SDKFunctionInstance;
import it.nextworks.sdk.enums.Flavour;
import it.nextworks.sdk.exceptions.MalformattedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;

public interface FunctionInstanceManagerProviderInterface {
	
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws NotExistingEntityException 
	 */
	public SDKFunctionInstance getFunction(String id) throws NotExistingEntityException;
	
	
	/**
	 * 
	 * @return
	 */
	public List<SDKFunctionInstance> getFunctions();
	
	
	/**
	 * 
	 */
	public List<SDKFunctionInstance> getFunctionInstancesForFunction(String functionId);
	
	
	public void updateFlavor(String functionId, Flavour flavour) throws NotExistingEntityException;
	
	public void updateMonitoringParameters(String functionId, List<MonitoringParameter> monitoringParameters) throws NotExistingEntityException, MalformattedElementException;

	public void deleteMonitoringParameters(String functionId, List<MonitoringParameter> monitoringParameters) throws NotExistingEntityException, MalformattedElementException;

	public List<MonitoringParameter> getMonitoringParameters(String functionId) throws NotExistingEntityException;
	
	
	
}
