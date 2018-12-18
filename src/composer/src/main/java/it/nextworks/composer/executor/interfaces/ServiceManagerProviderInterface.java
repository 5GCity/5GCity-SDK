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

import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.ScalingAspect;
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
        throws NotExistingEntityException;

    List<SdkService> getServices();

    List<SdkService> getServicesUsingFunction(Long functionId)
        throws NotExistingEntityException;

    String createService(SdkService service)
        throws MalformedElementException;

    String updateService(SdkService service)
        throws NotExistingEntityException, MalformedElementException;

    void deleteService(Long serviceId)
        throws NotExistingEntityException;

    String createServiceDescriptor(Long serviceId, List<BigDecimal> parameterValues)
        throws NotExistingEntityException, MalformedElementException;

    List<SdkServiceDescriptor> getAllDescriptors();

    SdkServiceDescriptor getServiceDescriptor(Long descriptorId)
        throws NotExistingEntityException;

    void deleteServiceDescriptor(Long descriptorId)
        throws NotExistingEntityException;

    String publishService(Long serviceId, List<BigDecimal> parameterValues)
        throws NotExistingEntityException, MalformedElementException;

    void publishService(Long serviceInstanceId)
        throws NotExistingEntityException, AlreadyPublishedServiceException;

    DescriptorTemplate generateTemplate(Long serviceDescriptorId)
        throws NotExistingEntityException;

    void unPublishService(Long serviceInstanceId)
        throws NotExistingEntityException, NotPublishedServiceException;

    void updateScalingAspect(Long serviceId, Set<ScalingAspect> scalingAspects)
        throws NotExistingEntityException, MalformedElementException;

    void deleteScalingAspect(Long serviceId, Set<ScalingAspect> scalingAspects)
        throws NotExistingEntityException, MalformedElementException;

    List<ScalingAspect> getScalingAspect(Long serviceId)
        throws NotExistingEntityException;

    void updateMonitoringParameters(Long serviceId, Set<MonitoringParameter> monitoringParameters)
        throws NotExistingEntityException, MalformedElementException;

    void deleteMonitoringParameters(Long serviceId, Set<MonitoringParameter> monitoringParameters)
        throws NotExistingEntityException, MalformedElementException;

    List<MonitoringParameter> getMonitoringParameters(Long serviceId)
        throws NotExistingEntityException;

}

