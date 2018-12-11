package it.nextworks.composer.adaptor.expression;

import it.nextworks.composer.adaptor.interfaces.ServicesAdaptorProviderInterface;
import it.nextworks.nfvmano.libs.descriptors.nsd.nodes.NS.NSNode;
import it.nextworks.nfvmano.libs.descriptors.nsd.nodes.NS.NSProperties;
import it.nextworks.nfvmano.libs.descriptors.nsd.nodes.NS.NSRequirements;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.nfvmano.libs.descriptors.templates.Metadata;
import it.nextworks.nfvmano.libs.descriptors.templates.Node;
import it.nextworks.nfvmano.libs.descriptors.templates.TopologyTemplate;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VNF.VNFNode;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VNF.VNFProperties;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VNF.VNFRequirements;
import it.nextworks.sdk.SdkComponentCandidate;
import it.nextworks.sdk.SdkComponentInstance;
import it.nextworks.sdk.SdkFunctionInstance;
import it.nextworks.sdk.SdkService;
import it.nextworks.sdk.SdkServiceInstance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Marco Capitani on 05/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
@Service
@Qualifier("expression-adapter")
public class ExpressionAdapter implements ServicesAdaptorProviderInterface {

    private ServiceInformation consumeComponent(SdkComponentInstance<?> instance) {
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

    private ServiceInformation consumeService(SdkServiceInstance service) {
        if (service.getId() == null) {
            throw new IllegalStateException("Not persisted, cannot create information");
        }
        new HashSet<ServiceInformation>().stream().collect(
            ServiceInformation::new,
            ServiceInformation::merge,
            ServiceInformation::merge
        );
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

    private VNFNode makeVnfd(ServiceInformation.VnfdData vnfdData) {
        VNFProperties vnfProperties = new VNFProperties(

        );
        VNFRequirements vnfRequirements = new VNFRequirements(
            //vnfdData.
        );
        return new VNFNode(
            "tosca.nodes.nfv.VNF",
            null,  // name is ignored
            vnfProperties,
            vnfRequirements,
            null  // not used
        );
    }

    private NSNode makeNsNode(ServiceInformation info) {
        return null; // TODO
    }

    private DescriptorTemplate makeNSD(ServiceInformation info) {
        NSRequirements requirements = new NSRequirements();
        NSProperties properties = new NSProperties();

        Map<String, Node> nodeTemplates = new HashMap<>(
            info.streamVnfdData().collect(Collectors.toMap(
                data -> data.name,
                this::makeVnfd
            ))
        );

        TopologyTemplate topologyTemplate = new TopologyTemplate(
            null,
            null,
            nodeTemplates,
            new HashMap<>()
        );

        return new DescriptorTemplate(
            "tosca_sol001_v0_10",
            null,
            String.format("NS descriptor: %s. Generated with 5G-City SDK", info.getName()),
            new Metadata(
                info.getName(),
                info.getDesigner(),
                info.getVersion()
            ),
                topologyTemplate
            );
    }

    @Override
    public SdkServiceInstance instantiateSdkService(SdkService service, List<BigDecimal> parameterValues) {
        return new SdkServiceInstance(service, parameterValues, null);
    }

    @Override
    public DescriptorTemplate generateNetworkServiceDescriptor(SdkServiceInstance serviceInstance) {
        return makeNSD(consumeService(serviceInstance));
    }
}
