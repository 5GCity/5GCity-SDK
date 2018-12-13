package it.nextworks.composer.adaptor.expression;

import it.nextworks.sdk.ConnectionPoint;
import it.nextworks.sdk.L3Connectivity;
import it.nextworks.sdk.Link;
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.ScalingAspect;
import it.nextworks.sdk.SdkFunction;
import it.nextworks.sdk.SdkFunctionInstance;
import it.nextworks.sdk.SdkService;
import it.nextworks.sdk.SdkServiceInstance;

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

    // TODO make SAP by appending _mgmt to the appropriate link

    private Map<Long, SdkFunctionInstance> functions = new HashMap<>();

    private Map<Long, Long> funcId2Service = new HashMap<>();

    private Map<Long, String> func2vnfd = new HashMap<>();

    private Map<Long, String> func2vnfdVersion = new HashMap<>();

    private Map<Long, String> func2flavour = new HashMap<>();

    private Map<Long, String> func2level = new HashMap<>();

    private Map<Long, FunctionMetadata> functionMetadata = new HashMap<>();

    private ServiceMetadata serviceMetadata = new ServiceMetadata();

    private Map<Long, Set<L3Connectivity>> service2Rules = new HashMap<>();

    private Map<Long, Set<Link>> service2Link = new HashMap<>();

    private Map<Long, Set<ConnectionPoint>> service2Cp = new HashMap<>();

    private Set<MonitoringParameter> monitoringParameters = new HashSet<>();

    private Set<ScalingAspect> scalingAspects = new HashSet<>();

    private Set<AdapterLink> adapterLinks = new HashSet<>();

    private Map<Long, Set<AdapterLink>> function2Link = new HashMap<>();

    private Long lastService;

    ServiceInfoBuilder() {

    }

    ServiceInfoBuilder(SdkFunctionInstance function) {
        Long id = function.getId();
        SdkFunction template = function.getTemplate();
        functions.put(id, function);
        funcId2Service.put(id, function.getOuterServiceId());
        func2vnfd.put(id, template.getVnfdId());
        func2vnfdVersion.put(id, template.getVnfdVersion());
        func2flavour.put(id, function.getFlavour());
        func2level.put(id, function.getLevel());
        monitoringParameters.addAll(template.getMonitoringParameters());
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
        funcId2Service.putAll(other.funcId2Service);
        func2vnfd.putAll(other.func2vnfd);
        func2vnfdVersion.putAll(other.func2vnfdVersion);
        func2flavour.putAll(other.func2flavour);
        func2level.putAll(other.func2level);
        service2Rules.putAll(other.service2Rules);
        service2Link.putAll(other.service2Link);
        monitoringParameters.addAll(other.monitoringParameters);
        scalingAspects.addAll(other.scalingAspects);
        functionMetadata.putAll(other.functionMetadata);
        serviceMetadata.merge(other.serviceMetadata);
        return this;
    }

    ServiceInfoBuilder addServiceRelatedInfo(SdkServiceInstance service) {
        Long id = service.getId();
        SdkService template = service.getTemplate();
        addLinks(id, template.getLink());
        addCps(id, template.getConnectionPoint());
        addRule(id, template.getL3Connectivity());
        scalingAspects.addAll(template.getScalingAspect());
        monitoringParameters.addAll(template.getMonitoringParameters());

        serviceMetadata.setName(template.getName());
        serviceMetadata.setVersion(template.getVersion());
        serviceMetadata.setDesigner(template.getDesigner());
        serviceMetadata.setLicense(template.getLicense().getUrl());
        serviceMetadata.addMetadata(template.getMetadata());

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
    }

    private VnfdData getVnfdData(Long functionId) {
        SdkFunctionInstance functionInstance = functions.get(functionId);
        Set<L3Connectivity> rules = service2Rules.values().stream()
            .flatMap(Collection::stream)
            .filter(
                r -> false // TODO in a future release
            )
            .collect(Collectors.toSet());
        return new VnfdData(
            functionMetadata.get(functionId).name,
            functionMetadata.get(functionId).description,
            functionMetadata.get(functionId).vendor,
            func2vnfd.get(functionId),
            func2vnfdVersion.get(functionId),
            func2flavour.get(functionId),
            func2level.get(functionId),
            function2Link.getOrDefault(functionId, new HashSet<>()).stream()
                .map(al -> al.name)
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

        for (Set<ConnectionPoint> cpSet : service2Cp.values()) {
            for (ConnectionPoint cp : cpSet) {
                builder.addCp(cp);
            }
        }

        adapterLinks = builder.build();

        for (Long functionId : functions.keySet()) {
            for (AdapterLink aLink : adapterLinks) {
                if (aLink.function2CpName.containsKey(functionId)) {
                    function2Link.putIfAbsent(functionId, new HashSet<>());
                    function2Link.get(functionId).add(aLink);
                }
            }
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
