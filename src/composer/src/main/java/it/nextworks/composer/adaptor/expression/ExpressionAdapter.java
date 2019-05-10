package it.nextworks.composer.adaptor.expression;

import it.nextworks.composer.adaptor.interfaces.ServicesAdaptorProviderInterface;
import it.nextworks.nfvmano.libs.common.enums.FlowPattern;
import it.nextworks.nfvmano.libs.common.enums.LayerProtocol;
import it.nextworks.nfvmano.libs.common.enums.ServiceAvailabilityLevel;
import it.nextworks.nfvmano.libs.descriptors.elements.ConnectivityType;
import it.nextworks.nfvmano.libs.descriptors.elements.LinkBitrateRequirements;
import it.nextworks.nfvmano.libs.descriptors.elements.ServiceAvailability;
import it.nextworks.nfvmano.libs.descriptors.elements.VlProfile;
import it.nextworks.nfvmano.libs.descriptors.nsd.nodes.NS.NSNode;
import it.nextworks.nfvmano.libs.descriptors.nsd.nodes.NS.NSProperties;
import it.nextworks.nfvmano.libs.descriptors.nsd.nodes.NS.NSRequirements;
import it.nextworks.nfvmano.libs.descriptors.nsd.nodes.NsVirtualLink.NsVirtualLinkNode;
import it.nextworks.nfvmano.libs.descriptors.nsd.nodes.NsVirtualLink.NsVirtualLinkProperties;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.nfvmano.libs.descriptors.templates.Metadata;
import it.nextworks.nfvmano.libs.descriptors.templates.Node;
import it.nextworks.nfvmano.libs.descriptors.templates.TopologyTemplate;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VNF.VNFNode;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VNF.VNFProperties;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VNF.VNFRequirements;
import it.nextworks.sdk.SdkComponentInstance;
import it.nextworks.sdk.SdkFunctionDescriptor;
import it.nextworks.sdk.SdkService;
import it.nextworks.sdk.SdkServiceDescriptor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Marco Capitani on 05/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
@Service
public class ExpressionAdapter implements ServicesAdaptorProviderInterface {

    private static LinkBitrateRequirements DEFAULT_MAX_BITRATE_REQ = new LinkBitrateRequirements(
        1000000,
        100000
    );

    private static LinkBitrateRequirements DEFAULT_MIN_BITRATE_REQ = new LinkBitrateRequirements(
        100000,
        10000
    );

    private ServiceInfoBuilder consumeComponent(SdkComponentInstance instance) {
        if (instance instanceof SdkFunctionDescriptor) {
            return consumeFunction((SdkFunctionDescriptor) instance);
        } else if (instance instanceof SdkServiceDescriptor) {
            return consumeService((SdkServiceDescriptor) instance);
        } else {
            throw new IllegalArgumentException(String.format(
                "Unknown component type %s",
                instance.getClass().getSimpleName()
            ));
        }
    }

    private ServiceInfoBuilder consumeFunction(SdkFunctionDescriptor function) {
        return new ServiceInfoBuilder(function);
    }

    private ServiceInfoBuilder consumeService(SdkServiceDescriptor service) {
        if (service.getId() == null) {
            throw new IllegalStateException("Not persisted, cannot create information");
        }
        return service.getSubDescriptors().stream()
            .map(this::consumeComponent)
            // Merge all info into one
            .collect(
                ServiceInfoBuilder::new,
                ServiceInfoBuilder::merge,
                ServiceInfoBuilder::merge
            )
            .addServiceRelatedInfo(service);
    }

    private VNFNode makeVnfd(VnfdData vnfdData) {
        ArrayList<String> vnfmInfo = new ArrayList<>();
        vnfmInfo.add("juju");
        VNFProperties vnfProperties = new VNFProperties(
            vnfdData.vnfd,
            vnfdData.vnfdVersion,
            vnfdData.vendor,
            vnfdData.name,
            vnfdData.vnfdVersion,
            vnfdData.name,
            vnfdData.description,
            vnfmInfo,
            Collections.singletonList("EN"),
            "EN",
            null,
            null,
            null,
            new ArrayList<>(),
            vnfdData.flavour,
            vnfdData.flavour,
            null
        );
        VNFRequirements vnfRequirements = new VNFRequirements(
            new ArrayList<>(vnfdData.vLinks)
        );
        return new VNFNode(
            "tosca.nodes.nfv.VNF",
            null,  // name is ignored
            vnfProperties,
            vnfRequirements,
            null,  // not used
            null
        );
    }

    private NSNode makeNsNode(ServiceInformation info) {
        NSProperties nsProperties = new NSProperties(
            info.getUUID(),
            info.getDesigner(),
            info.getVersion(),
            info.getName(),
            info.getinvariantId()
        );
        NSRequirements nsRequirements = new NSRequirements(
            new ArrayList<>(info.getServiceLinks())
        );
        return new NSNode(
            "tosca.nodes.nfv.NS",
            nsProperties,
            nsRequirements
        );
    }

    private NsVirtualLinkNode makeVLink() {
        VlProfile vlProfile = new VlProfile(
            DEFAULT_MAX_BITRATE_REQ,
            DEFAULT_MIN_BITRATE_REQ,
            null,
            null
        );
        ConnectivityType connectivityType = new ConnectivityType(
            Collections.singletonList(LayerProtocol.IPV4),
            FlowPattern.LINE
        );
        ServiceAvailability serviceAvailability = new ServiceAvailability(
            ServiceAvailabilityLevel.LEVEL_1
        );
        NsVirtualLinkProperties nsVirtualLinkProperties = new NsVirtualLinkProperties(
            null,
            "5G-City-SDK",
            vlProfile,
            connectivityType,
            new ArrayList<>()
        );
        return new NsVirtualLinkNode(
            "tosca.nodes.nfv.NsVirtualLink",
            nsVirtualLinkProperties,
            null
        );
    }

    private DescriptorTemplate makeNSD(ServiceInformation info) {
        LinkedHashMap<String, Node> nodeTemplates = new LinkedHashMap<>(
            info.getVnfdData().stream().collect(Collectors.toMap(
                data -> data.name,
                this::makeVnfd
            ))
        );
        nodeTemplates.put(info.getName(), makeNsNode(info));

        for (String link : info.getServiceLinks()) {
            nodeTemplates.put(link, makeVLink());
        }

        TopologyTemplate topologyTemplate = new TopologyTemplate(
            null,
            null,
            new HashMap<>(),
            nodeTemplates,
            new HashMap<>(),
            new HashMap<>()
        );

        return new DescriptorTemplate(
            "tosca_sol001_v0_10",
            null,
            String.format("NS descriptor: %s. Generated with 5G-City SDK", info.getName()),
            new Metadata(
                info.getUUID(),
                info.getDesigner(),
                info.getVersion()
            ),
            topologyTemplate
        );
    }

    @Override
    public SdkServiceDescriptor createServiceDescriptor(SdkService service, List<BigDecimal> parameterValues) {
        return service.makeDescriptor(parameterValues);
    }

    @Override
    public DescriptorTemplate generateNetworkServiceDescriptor(SdkServiceDescriptor serviceInstance) {
        ServiceInformation info = consumeService(serviceInstance).build();
        return makeNSD(info);
    }
}

