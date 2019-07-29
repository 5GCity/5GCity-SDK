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

import it.nextworks.nfvmano.libs.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.common.exceptions.NotAuthorizedOperationException;
import it.nextworks.nfvmano.libs.common.exceptions.NotPermittedOperationException;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.MonitoringParameterWrapper;
import it.nextworks.sdk.SdkService;
import it.nextworks.sdk.SdkServiceDescriptor;
import it.nextworks.sdk.exceptions.AlreadyPublishedServiceException;
import it.nextworks.sdk.exceptions.MalformedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.NotPublishedServiceException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface ServiceManagerProviderInterface {

    SdkService getServiceById(Long id)
        throws NotExistingEntityException, NotPermittedOperationException, NotAuthorizedOperationException;

    List<SdkService> getServices(String sliceId) throws NotExistingEntityException, NotPermittedOperationException, NotAuthorizedOperationException;

    /*
    List<SdkService> getServicesUsingFunction(Long functionId)
        throws NotExistingEntityException;
    */

    String createService(SdkService service)
        throws NotExistingEntityException, MalformedElementException, AlreadyExistingEntityException, NotPermittedOperationException, NotAuthorizedOperationException;

    String updateService(SdkService service)
        throws NotExistingEntityException, MalformedElementException, NotPermittedOperationException, NotAuthorizedOperationException;

    void deleteService(Long serviceId)
        throws NotExistingEntityException, NotPermittedOperationException, NotAuthorizedOperationException;

    String createServiceDescriptor(Long serviceId, List<BigDecimal> parameterValues)
        throws NotExistingEntityException, MalformedElementException, NotPermittedOperationException, NotAuthorizedOperationException;

    List<SdkServiceDescriptor> getAllDescriptors(String sliceId) throws NotExistingEntityException, NotAuthorizedOperationException;

    SdkServiceDescriptor getServiceDescriptor(Long descriptorId)
        throws NotExistingEntityException, NotAuthorizedOperationException;

    void deleteServiceDescriptor(Long descriptorId)
        throws NotExistingEntityException, NotPermittedOperationException, NotAuthorizedOperationException;

    String publishService(Long serviceId, List<BigDecimal> parameterValues, String authorization)
        throws NotExistingEntityException, MalformedElementException, NotPermittedOperationException, NotAuthorizedOperationException;

    void publishService(Long serviceInstanceId, String authorization)
        throws NotExistingEntityException, AlreadyPublishedServiceException, NotPermittedOperationException, MalformedElementException,NotAuthorizedOperationException;

    void unPublishService(Long serviceDescriptorId, String authorization)
        throws NotExistingEntityException, NotPublishedServiceException, NotAuthorizedOperationException;

    void updateMonitoringParameters(Long serviceId, Set<MonitoringParameter> extMonitoringParameters, Set<MonitoringParameter> intMonitoringParameters)
        throws NotExistingEntityException, MalformedElementException, NotPermittedOperationException, NotAuthorizedOperationException;

    void deleteMonitoringParameters(Long serviceId, Long monitoringParameterId)
        throws NotExistingEntityException, MalformedElementException, NotPermittedOperationException, NotAuthorizedOperationException;

    MonitoringParameterWrapper getMonitoringParameters(Long serviceId)
        throws NotExistingEntityException, NotPermittedOperationException, NotAuthorizedOperationException;

    DescriptorTemplate generateTemplate(Long serviceDescriptorId)
        throws NotExistingEntityException, MalformedElementException, NotAuthorizedOperationException;
}

