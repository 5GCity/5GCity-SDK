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

import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.SdkFunctionInstance;
import it.nextworks.sdk.SdkService;
import it.nextworks.sdk.exceptions.ExistingEntityException;
import it.nextworks.sdk.exceptions.MalformedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;

import java.util.List;

public interface FunctionInstanceManagerProviderInterface {


    /**
     * @param id
     * @return
     * @throws NotExistingEntityException
     */
    public SdkFunctionInstance getFunction(Long id) throws NotExistingEntityException;


    /**
     * @return
     */
    public List<SdkFunctionInstance> getFunctions();


    /**
     *
     */
    public List<SdkFunctionInstance> getFunctionInstancesForFunction(Long functionId);

    public void updateMonitoringParameters(Long functionId, List<MonitoringParameter> monitoringParameters) throws NotExistingEntityException, MalformedElementException;

    public void deleteMonitoringParameters(Long functionId, List<MonitoringParameter> monitoringParameters) throws NotExistingEntityException, MalformedElementException;

    public List<MonitoringParameter> getMonitoringParameters(Long functionId) throws NotExistingEntityException;

    public String createInstance(SdkFunctionInstance instance, SdkService service) throws ExistingEntityException, NotExistingEntityException, MalformedElementException;

    public String updateInstance(SdkFunctionInstance instance, SdkService service) throws NotExistingEntityException, MalformedElementException;

    public void deleteInstance(Long id) throws NotExistingEntityException;
}
