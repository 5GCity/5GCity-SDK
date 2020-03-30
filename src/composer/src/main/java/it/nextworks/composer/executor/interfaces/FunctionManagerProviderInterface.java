/*
 * Copyright 2020 Nextworks s.r.l.
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

import it.nextworks.nfvmano.libs.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.common.exceptions.NotAuthorizedOperationException;
import it.nextworks.nfvmano.libs.common.exceptions.NotPermittedOperationException;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.sdk.SdkFunction;
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.exceptions.AlreadyPublishedServiceException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.MalformedElementException;
import it.nextworks.sdk.exceptions.NotPublishedServiceException;
import org.hibernate.cfg.NotYetImplementedException;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;

import java.util.List;
import java.util.Set;
import java.math.BigDecimal;

public interface FunctionManagerProviderInterface {


    /**
     * @param id
     * @return
     * @throws NotExistingEntityException
     */
    SdkFunction getFunction(Long id) throws NotExistingEntityException, NotPermittedOperationException, NotAuthorizedOperationException;


    /**
     * @return
     */
    List<SdkFunction> getFunctions(String sliceId) throws NotExistingEntityException, NotAuthorizedOperationException;

    //String createFunction(SdkFunction function);

    //String createFunction();

    String createFunction(SdkFunction function, boolean isInternalRequest)
        throws MalformedElementException, AlreadyExistingEntityException, NotExistingEntityException, NotPermittedOperationException, NotAuthorizedOperationException;

    String updateFunction(SdkFunction function)
        throws NotExistingEntityException, MalformedElementException, NotPermittedOperationException, NotAuthorizedOperationException;

    /*
    SdkFunction getFunctionById(Long id)
        throws NotExistingEntityException;
    */

    void deleteFunction(Long functionId)
        throws NotExistingEntityException, NotPermittedOperationException, NotAuthorizedOperationException;

    /*
    String createFunctionDescriptor(Long functionId, List<BigDecimal> parameterValues)
        throws NotExistingEntityException, MalformedElementException, NotYetImplementedException;
    */

    void publishFunction(Long functionId, String authorization)
        throws NotExistingEntityException, AlreadyPublishedServiceException, NotPermittedOperationException, NotAuthorizedOperationException;

    void unPublishFunction(Long functionDescriptorId, String authorization)
        throws NotExistingEntityException, NotPublishedServiceException, NotPermittedOperationException, NotAuthorizedOperationException;

    void updateMonitoringParameters(Long functionId, Set<MonitoringParameter> monitoringParameters)
        throws NotExistingEntityException, NotPermittedOperationException, MalformedElementException, NotAuthorizedOperationException;

    void deleteMonitoringParameters(Long functionId, Long monitoringParameterId)
        throws NotExistingEntityException, NotPermittedOperationException, MalformedElementException, NotAuthorizedOperationException;

    Set<MonitoringParameter> getMonitoringParameters(Long functionId)
        throws NotExistingEntityException, NotPermittedOperationException, NotAuthorizedOperationException;

    DescriptorTemplate generateTemplate(Long functionId)
        throws NotExistingEntityException, NotAuthorizedOperationException;
}
