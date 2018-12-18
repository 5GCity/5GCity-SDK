package it.nextworks.composer.adaptor.expression;

import it.nextworks.sdk.ConnectionPoint;
import it.nextworks.sdk.L3Connectivity;
import it.nextworks.sdk.Link;
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.ScalingAspect;
import it.nextworks.sdk.SdkFunction;
import it.nextworks.sdk.SdkFunctionDescriptor;
import it.nextworks.sdk.SdkService;
import it.nextworks.sdk.SdkServiceDescriptor;
import it.nextworks.sdk.enums.ConnectionPointType;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Marco Capitani on 12/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
class ServiceInfoBuilder {

    private Map<Long, SdkFunctionDescriptor> functions = new HashMap<>();

    private Map<Long, String> func2vnfd = new HashMap<>();

    private Map<Long, String> func2vnfdVersion = new HashMap<>();

    private Map<Long, String> func2flavour = new HashMap<>();

    private Map<Long, String> func2level = new HashMap<>();

    private Map<Long, FunctionMetadata> functionMetadata = new HashMap<>();

    private ServiceMetadata serviceMetadata = new ServiceMetadata();

    private Map<Long, Set<L3Connectivity>> service2Rules = new HashMap<>();

    private Map<Long, Set<Link>> service2Link = new HashMap<>();

    private Map<Long, Set<ConnectionPoint>> service2Cp = new HashMap<>();

    private Map<Long, ConnectionPoint> cpsById = new HashMap<>();

    private Set<MonitoringParameter> monitoringParameters = new HashSet<>();

    private Set<ScalingAspect> scalingAspects = new HashSet<>();

    // Used only at last -> not to be merged
    private Set<AdapterLink> adapterLinks = new HashSet<>();

    private Map<Long, Set<AdapterLink>> function2Link = new HashMap<>();

    private Long lastService;

    ServiceInfoBuilder() {

    }

    ServiceInfoBuilder(SdkFunctionDescriptor function) {
        Long id = function.getId();
        SdkFunction template = function.getTemplate();
        functions.put(id, function);
        func2vnfd.put(id, template.getVnfdId());
        func2vnfdVersion.put(id, template.getVnfdVersion());
        func2flavour.put(id, function.getFlavour());
        func2level.put(id, function.getLevel());
        monitoringParameters.addAll(template.getMonitoringParameters());
        for (ConnectionPoint cp : template.getConnectionPoint()) {
            cpsById.put(cp.getId(), cp);
        }
        functionMetadata.put(
            id,
            new FunctionMetadata()
                .name(template.getName())
                .version(template.getVersion())
                .description(template.getDescription())
                .vendor(template.getVendor())
                .metadata(template.getMetadata())
        );
    }

    ServiceInfoBuilder merge(ServiceInfoBuilder other) {
        functions.putAll(other.functions);
        func2vnfd.putAll(other.func2vnfd);
        func2vnfdVersion.putAll(other.func2vnfdVersion);
        func2flavour.putAll(other.func2flavour);
        func2level.putAll(other.func2level);
        functionMetadata.putAll(other.functionMetadata);
        serviceMetadata.merge(other.serviceMetadata);
        service2Rules.putAll(other.service2Rules);
        service2Link.putAll(other.service2Link);
        service2Cp.putAll(other.service2Cp);
        cpsById.putAll(other.cpsById);
        monitoringParameters.addAll(other.monitoringParameters);
        scalingAspects.addAll(other.scalingAspects);
        return this;
    }

    ServiceInfoBuilder addServiceRelatedInfo(SdkServiceDescriptor service) {
        Long id = service.getId();
        SdkService template = service.getTemplate();
        addLinks(id, template.getLink());
        addCps(id, template.getConnectionPoint());
        addRule(id, template.getL3Connectivity());
        scalingAspects.addAll(template.getScalingAspect());
        monitoringParameters.addAll(template.getMonitoringParameters());

        serviceMetadata.setName(template.getName())
            .setVersion(template.getVersion())
            .setDesigner(template.getDesigner())
            .setLicense(template.getLicense().getUrl())
            .addMetadata(template.getMetadata())
            .setInstanceId(service.getId().toString());

        lastService = id;

        return this; // For chaining methods together
    }

    private void addRule(Long serviceId, Set<L3Connectivity> rules) {
        this.service2Rules.put(serviceId, rules);
    }

    private void addLinks(Long serviceId, Set<Link> links) {
        service2Link.put(serviceId, links);
    }

    private void addCps(Long serviceId, Set<ConnectionPoint> cps) {
        service2Cp.put(serviceId, cps);
        for (ConnectionPoint cp : cps) {
            cpsById.put(cp.getId(), cp);
        }
    }

    private VnfdData getVnfdData(Long functionInstanceId) {
        Set<L3Connectivity> rules = service2Rules.values().stream()
            .flatMap(Collection::stream)
            .filter(
                r -> false // TODO in a future release
            )
            .collect(Collectors.toSet());
        Long functionId = functions.get(functionInstanceId).getTemplate().getId();
        return new VnfdData(
            functionMetadata.get(functionInstanceId).name,
            functionMetadata.get(functionInstanceId).description,
            functionMetadata.get(functionInstanceId).vendor,
            func2vnfd.get(functionInstanceId),
            func2vnfdVersion.get(functionInstanceId),
            func2flavour.get(functionInstanceId),
            func2level.get(functionInstanceId),
            function2Link.getOrDefault(functionInstanceId, new HashSet<>()).stream()
                .map(al -> al.name + '/' + al.function2CpName.get(functionId))
                .collect(Collectors.toSet()),
            rules
        );
    }

    private void makeAdapterLinks() {
        Map<Long, String> mainLinks = service2Link.get(lastService).stream()
            .collect(Collectors.toMap(
                Link::getId,
                Link::getName
            ));

        AdapterLink.AdapterLinkBuilder builder = AdapterLink.builder(
            serviceMetadata.name,
            mainLinks
        );

        for (Set<Link> linkSet : service2Link.values()) {
            for (Link link : linkSet) {
                builder.addLink(link);
            }
        }

        for (ConnectionPoint cp : cpsById.values()) {
            builder.addCp(cp);
        }

        adapterLinks = builder.build();

        for (SdkFunctionDescriptor functionInstance : functions.values()) {
            Long functionInstanceId = functionInstance.getId();
            Long functionId = functionInstance.getTemplate().getId();
            for (AdapterLink aLink : adapterLinks) {
                if (aLink.function2CpName.containsKey(functionId)) {
                    function2Link.putIfAbsent(functionInstanceId, new HashSet<>());
                    function2Link.get(functionInstanceId).add(aLink);
                }
            }
        }
        for (ConnectionPoint connectionPoint : service2Cp.get(lastService)) {
            if (connectionPoint.getType().equals(ConnectionPointType.EXTERNAL)) {
                // Mark containing link as needing a SAP (i.e. _mgmt)
                Link externalLink = findFirstAttachedLink(connectionPoint);
                for (AdapterLink adapterLink : adapterLinks) {
                    if (adapterLink.linkIds.contains(externalLink.getId())) {
                        if (!adapterLink.name.endsWith("_mgmt")) {
                            adapterLink.name = String.format("%s_mgmt", adapterLink.name);
                        }
                        break;
                    }
                }
            }
        }
    }

    private Link findFirstAttachedLink(ConnectionPoint cp) {
        if (cp.getLink() != null) {
            return cp.getLink();
        } else if (cp.getInternalCpId() != null) {
            ConnectionPoint intCp = cpsById.get(cp.getInternalCpId());
            if (intCp == null) {
                throw new IllegalStateException(String.format(
                    "Declared CP %s not found",
                    cp.getInternalCpId()
                ));
            }
            Long lowerCpId = intCp.getInternalCpId();
            if (lowerCpId == null) {
                throw new IllegalStateException(String.format(
                    "Internal CP %s without corresponding lower CP",
                    intCp.getId()
                ));
            }
            ConnectionPoint lowerCp = cpsById.get(lowerCpId);
            if (lowerCp == null) {
                throw new IllegalStateException(String.format(
                    "Declared CP %s not found",
                    lowerCpId
                ));
            }
            return findFirstAttachedLink(lowerCp);
        } else {
            throw new IllegalStateException(String.format(
                "Orphan external CP %s",
                cp.getId()
            ));
        }
    }

    ServiceInformation build() {
        makeAdapterLinks();
        return new ServiceInformation(
            serviceMetadata,
            adapterLinks.stream().map(al -> al.name).collect(Collectors.toSet()),
            functions.keySet().stream().map(this::getVnfdData).collect(Collectors.toSet())
        );
    }
}

