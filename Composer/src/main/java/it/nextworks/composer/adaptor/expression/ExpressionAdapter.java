package it.nextworks.composer.adaptor.expression;

import it.nextworks.composer.adaptor.interfaces.ServicesAdaptorProviderInterface;
import it.nextworks.nfvmano.libs.descriptors.nsd.nodes.NS.NSNode;
import it.nextworks.sdk.SdkComponentCandidate;
import it.nextworks.sdk.SdkComponentInstance;
import it.nextworks.sdk.SdkFunctionInstance;
import it.nextworks.sdk.SdkService;
import it.nextworks.sdk.SdkServiceInstance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Marco Capitani on 05/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
@Service
@Qualifier("expression-adapter")
public class ExpressionAdapter implements ServicesAdaptorProviderInterface {

    private <T extends SdkComponentCandidate> ServiceInformation consumeComponent(SdkComponentInstance<T> instance) {
        if (instance instanceof SdkFunctionInstance) {
            return consumeFunction((SdkFunctionInstance) instance);
        } else if (instance instanceof SdkServiceInstance) {
            return consumeService((SdkServiceInstance) instance);
        } else {
            throw new IllegalArgumentException(String.format(
                "Unknown component type %s",
                instance.getClass().getSimpleName()
            ));
        }
    }

    ServiceInformation consumeFunction(SdkFunctionInstance function) {
        return new ServiceInformation(function);
    }

    ServiceInformation consumeService(SdkServiceInstance service) {
        if (service.getId() == null) {
            throw new IllegalStateException("Not persisted, cannot create information");
        }
        SdkService template = service.getTemplate();
        ServiceInformation information = template.getComponents().stream()
            .map(c -> c.instantiate(service.getParamsMap(), service))
            .map(this::consumeComponent)
            // Merge all info into one
            .collect(
                ServiceInformation::new,
                ServiceInformation::merge,
                ServiceInformation::merge
            );
        return information.addServiceRelatedInfo(service);
    }

    NSNode makeNSD(ServiceInformation info) {
        return null;
    }

    @Override
    public SdkServiceInstance instantiateSdkService(SdkService service, List<BigDecimal> parameterValues) {
        return new SdkServiceInstance(service, parameterValues, null);
    }

    @Override
    public NSNode generateNetworkServiceDescriptor(SdkServiceInstance serviceInstance) {
        return makeNSD(consumeService(serviceInstance));
    }
}
