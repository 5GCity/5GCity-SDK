package it.nextworks.composer.adaptor.expression;

import it.nextworks.composer.adaptor.interfaces.ServicesAdaptorProviderInterface;
import it.nextworks.nfvmano.libs.common.enums.*;
import it.nextworks.nfvmano.libs.descriptors.capabilities.VirtualComputeCapability;
import it.nextworks.nfvmano.libs.descriptors.capabilities.VirtualComputeCapabilityProperties;
import it.nextworks.nfvmano.libs.descriptors.elements.*;
import it.nextworks.nfvmano.libs.descriptors.elements.SwImageData;
import it.nextworks.nfvmano.libs.descriptors.interfaces.LcmOperation;
import it.nextworks.nfvmano.libs.descriptors.interfaces.Vnflcm;
import it.nextworks.nfvmano.libs.descriptors.nsd.nodes.NS.NSNode;
import it.nextworks.nfvmano.libs.descriptors.nsd.nodes.NS.NSProperties;
import it.nextworks.nfvmano.libs.descriptors.nsd.nodes.NS.NSRequirements;
import it.nextworks.nfvmano.libs.descriptors.nsd.nodes.NsVirtualLink.NsVirtualLinkNode;
import it.nextworks.nfvmano.libs.descriptors.nsd.nodes.NsVirtualLink.NsVirtualLinkProperties;
import it.nextworks.nfvmano.libs.descriptors.templates.*;
import it.nextworks.nfvmano.libs.descriptors.templates.Metadata;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VDU.*;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VNF.VNFInterfaces;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VNF.VNFNode;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VNF.VNFProperties;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VNF.VNFRequirements;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VnfExtCp.VnfExtCpNode;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VnfExtCp.VnfExtCpProperties;
import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VnfExtCp.VnfExtCpRequirements;
import it.nextworks.sdk.*;
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

    /* Network Service Descriptor */

    private static LinkBitrateRequirements DEFAULT_MAX_BITRATE_REQ = new LinkBitrateRequirements(1000000, 100000);

    private static LinkBitrateRequirements DEFAULT_MIN_BITRATE_REQ = new LinkBitrateRequirements(100000, 10000);

    @Override
    public SdkServiceDescriptor createServiceDescriptor(SdkService service, List<BigDecimal> parameterValues) {
        return service.makeDescriptor(parameterValues);
    }

    @Override
    public DescriptorTemplate generateNetworkServiceDescriptor(SdkServiceDescriptor serviceInstance) {
        ServiceInformation info = consumeService(serviceInstance).build();
        return makeNSD(info);
    }

    private ServiceInfoBuilder consumeComponent(SdkComponentInstance instance) {
        if (instance instanceof SdkFunctionDescriptor) {
            return consumeFunction((SdkFunctionDescriptor) instance);
        } else if (instance instanceof SdkServiceDescriptor) {
            return consumeService((SdkServiceDescriptor) instance);
        } else {
            throw new IllegalArgumentException(String.format("Unknown component type %s", instance.getClass().getSimpleName()));
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
            new VnfProfile(vnfdData.instantiationLevel, null, null)
        );
        VNFRequirements vnfRequirements = new VNFRequirements(vnfdData.vLinksAssociation);
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
            "5GCity-SDK",
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
                data -> String.format("%s_%s_%s",data.name, data.vnfdVersion, data.vendor),
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
            String.format("NS descriptor: %s. Generated with 5GCity SDK", info.getName()),
            new Metadata(
                info.getUUID(),
                info.getDesigner(),
                info.getVersion()
            ),
            topologyTemplate
        );
    }

    /* Virtual Network Function Descriptor */

    @Override
    public DescriptorTemplate generateVirtualNetworkFunctionDescriptor(SdkFunction functionInstance){

        List<VirtualLinkPair> virtualLinkPairs = new ArrayList<>();
        for(ConnectionPoint cp : functionInstance.getConnectionPoint()){
            VirtualLinkPair lp = new VirtualLinkPair(cp.getName(), cp.getInternalLink());
            virtualLinkPairs.add(lp);
        }
        SubstitutionMappingsRequirements requirements = new SubstitutionMappingsRequirements(
            null,
            virtualLinkPairs
        );

        SubstitutionMappings substitutionMappings = new SubstitutionMappings(
            null,
            "tosca.nodes.nfv.VNF",
            null,
            requirements,
            null
        );

        LinkedHashMap<String, Node> nodeTemplates = new LinkedHashMap<>();
        nodeTemplates.put(functionInstance.getName(), makeVnfNode(functionInstance));
        nodeTemplates.put(functionInstance.getName() + "_vdu", makeVduNode(functionInstance));
        nodeTemplates.put(functionInstance.getName() + "_storage", makeStorageNode(functionInstance));

        for (ConnectionPoint cp : functionInstance.getConnectionPoint()){
            nodeTemplates.put(cp.getName(), makeCpNode(cp));
        }

        TopologyTemplate topologyTemplate = new TopologyTemplate(
            null,
            substitutionMappings,
            new HashMap<>(),
            nodeTemplates,
            new HashMap<>(),
            new HashMap<>()
        );

        return new DescriptorTemplate(
            "tosca_sol001_v0_10",
            null,
            String.format("VNF descriptor: %s. Generated with 5GCity SDK", functionInstance.getName()),
            new Metadata(
                functionInstance.getVnfdId(),
                functionInstance.getVendor(),
                functionInstance.getVersion()
            ),
            topologyTemplate
        );
    }

    private VNFNode makeVnfNode(SdkFunction function){

        //TODO generate descriptor with severl deployment flavours and instantiation levels
        ArrayList<String> vnfmInfo = new ArrayList<>();
        vnfmInfo.add("juju");
        VNFProperties vnfProperties = new VNFProperties(
            function.getVnfdId(),
            function.getVersion(),
            function.getVendor(),
            function.getName(),
            function.getSwImageData().getVersion(),
            function.getName(),
            function.getDescription(),
            vnfmInfo,
            Collections.singletonList("EN"),
            "EN",
            null,
            null,
            null,
            new ArrayList<>(),
            "static_df",
            "static_df",
            new VnfProfile("static_il", null, null)
        );

        VNFInterfaces interfaces = null;
        if(function.getMetadata().keySet().contains("cloud-init")){
            interfaces = new VNFInterfaces(
                null,
                new Vnflcm(
                    null,
                    new LcmOperation("cloud-init.txt"),
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null, null
                )
            );
        }

        return new VNFNode(
            "tosca.nodes.nfv.VNF",
            null,  // name is ignored
            vnfProperties,
            null,
            null,  // not used
            interfaces
        );
    }

    private VDUVirtualBlockStorageNode makeStorageNode(SdkFunction function){
        VDUVirtualBlockStorageProperties properties = new VDUVirtualBlockStorageProperties(
            new VirtualBlockStorageData(
                function.getSwImageData().getMinDisk(),
                null,
                false
            ),
            new SwImageData(
                function.getSwImageData().getImgName(),
                function.getSwImageData().getVersion(),
                function.getSwImageData().getChecksum(),
                ContainerFormat.valueOf(function.getSwImageData().getContainerFormat().toUpperCase()),
                DiskFormat.valueOf(function.getSwImageData().getDiskFormat().toUpperCase()),
                null,
                null,
                function.getSwImageData().getSize(),
                null,
                null
            )
        );

        return new VDUVirtualBlockStorageNode(
            "tosca.nodes.nfv.Vdu.VirtualBlockStorage",
            properties
        );
    }

    private VDUComputeNode makeVduNode(SdkFunction function){

        VDUComputeProperties properties = new VDUComputeProperties(
            function.getName() + "_vdu",
            null,
            null,
            null,
            null,
            null,
            null,
            new VduProfile(
                function.getMinInstancesCount(),
                function.getMaxInstancesCount()
            ),
            null
        );

        VDUComputeCapabilities capabilities = new VDUComputeCapabilities(
            null,
            new VirtualComputeCapability(
                new VirtualComputeCapabilityProperties(
                    null,
                    null,
                    null,
                    new VirtualMemory(
                        function.getSwImageData().getMinRam(),
                        null,
                        null,
                        false
                    ),
                    new VirtualCpu(
                        null,
                        null,
                        function.getSwImageData().getMinCpu(),
                        null,
                        null,
                        null,
                        null
                    ),
                    null
                )
            )
        );

        List<String> storageNodes = new ArrayList<>();
        //For the moment Consider a single storage node
        storageNodes.add(function.getName() + "_storage");
        VDUComputeRequirements requirements = new VDUComputeRequirements(
            null,
            storageNodes
        );

        return new VDUComputeNode(
            "tosca.nodes.nfv.Vdu.Compute",
            properties,
            capabilities,
            requirements
        );
    }

    private VnfExtCpNode makeCpNode(ConnectionPoint cp){
        List<LayerProtocol> layerProtocols = new ArrayList<>();
        layerProtocols.add(LayerProtocol.IPV4);
        VnfExtCpProperties properties = new VnfExtCpProperties(
            null,
            layerProtocols,
            CpRole.LEAF,
            cp.getName(),
            null,
            false,
            null
        );

        //TODO requirements? externalVirtualLinks? 
        VnfExtCpRequirements requirements = new VnfExtCpRequirements(
            null,
            null,
            null
        );

        return new VnfExtCpNode(
            "tosca.nodes.nfv.VnfExtCp",
            properties,
            requirements
        );
    }
}

