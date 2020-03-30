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
package it.nextworks.composer.adaptor.interfaces;


import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.sdk.SdkFunction;
import it.nextworks.sdk.SdkService;
import it.nextworks.sdk.SdkServiceDescriptor;

import java.math.BigDecimal;
import java.util.List;


public interface ServicesAdaptorProviderInterface {

    /**
     * Instantiate an SdkService by providing values for its parameters.
     *
     * @param service         Sdk Service provided by the Composer GUI
     * @param parameterValues values for the parameters in the service
     * @return the SdkServiceInstance
     */
    SdkServiceDescriptor createServiceDescriptor(SdkService service, List<BigDecimal> parameterValues);

    /**
     * Convert an SdkServiceInstance into a network service descriptor.
     *
     * @param serviceInstance Sdk Service instance
     * @return the new Network Service
     */
    DescriptorTemplate generateNetworkServiceDescriptor(SdkServiceDescriptor serviceInstance);

    DescriptorTemplate generateVirtualNetworkFunctionDescriptor(SdkFunction functionInstance);

    // Should there be a "publish" op? Isn't that for the service?
    /* *
     * Publish a Network Service Descriptor to the public catalogue
     *
     * @param node Node to be pushed into the public catalogue
     * @return the ID of the instance published
     */
    // String publishToCatalogue(NSNode node);

}
