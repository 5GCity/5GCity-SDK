package it.nextworks.composer.adaptor.interfaces;


import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
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

    // Should there be a "publish" op? Isn't that for the service?
    /* *
     * Publish a Network Service Descriptor to the public catalogue
     *
     * @param node Node to be pushed into the public catalogue
     * @return the ID of the instance published
     */
    // String publishToCatalogue(NSNode node);

}
