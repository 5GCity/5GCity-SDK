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

import it.nextworks.sdk.SdkFunction;
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.MalformedElementException;
import org.hibernate.cfg.NotYetImplementedException;
import java.util.List;
import java.util.Set;
import java.math.BigDecimal;

public interface FunctionManagerProviderInterface {


    /**
     * @param id
     * @return
     * @throws NotExistingEntityException
     */
    SdkFunction getFunction(Long id) throws NotExistingEntityException;


    /**
     * @return
     */
    List<SdkFunction> getFunctions();

    //String createFunction(SdkFunction function);

    String createFunction();

    String createFunction(SdkFunction function)
        throws NotExistingEntityException, MalformedElementException, NotYetImplementedException;

    String updateFunction(SdkFunction function)
        throws NotExistingEntityException, MalformedElementException;

    SdkFunction getFunctionById(Long id)
        throws NotExistingEntityException;

    void deleteFunction(Long functionId)
        throws NotExistingEntityException;

    String createFunctionDescriptor(Long functionId, List<BigDecimal> parameterValues)
        throws NotExistingEntityException, MalformedElementException, NotYetImplementedException;
    String publishFunction(Long functionId, List<BigDecimal> parameterValues)
        throws NotExistingEntityException, MalformedElementException;

    void updateMonitoringParameters(Long functionId, Set<MonitoringParameter> monitoringParameters)
        throws NotExistingEntityException, MalformedElementException, NotYetImplementedException;

    void deleteMonitoringParameters(Long functionId, Long monitoringParameterId)
        throws NotExistingEntityException, MalformedElementException, NotYetImplementedException;

    Set<MonitoringParameter> getMonitoringParameters(Long functionId)
        throws NotExistingEntityException, NotYetImplementedException;
}
