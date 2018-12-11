package it.nextworks.composer.adaptor.expression;

import it.nextworks.sdk.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.addAll;

/**
 * Created by Marco Capitani on 23/11/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class ServiceInformation {

    private Map<Long, SdkFunction> functions = new HashMap<>();

    private Map<Long, Long> funcId2Service = new HashMap<>();

    private Map<Long, String> func2vnfd = new HashMap<>();

    private Map<Long, String> func2flavour = new HashMap<>();

    private Map<Long, String> func2level = new HashMap<>();

    private Map<Long, FunctionMetadata> functionMetadata = new HashMap<>();

    private ServiceMetadata serviceMetadata = new ServiceMetadata();

    private Map<Long, Set<L3Connectivity>> service2Rules = new HashMap<>();

    private Map<Long, Set<Link>> func2links = new HashMap<>();

    private Set<MonitoringParameter> monitoringParameters = new HashSet<>();

    private Set<ScalingAspect> scalingAspects = new HashSet<>();

    public ServiceInformation() {

    }

    public ServiceInformation(SdkFunctionInstance function) {
        Long id = function.getId();
        SdkFunction template = function.getTemplate();
        functions.put(id, template);
        funcId2Service.put(id, function.getOuterServiceId());
        func2vnfd.put(id, template.getVnfdId());
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

    private VnfdData getVnfdData(Long functionId) {
        SdkFunction function = functions.get(functionId);
        Set<L3Connectivity> rules = service2Rules.values().stream()
            .flatMap(Collection::stream)
            .filter(
                r -> false // TODO
            )
            .collect(Collectors.toSet());
        return new VnfdData(
            function.getName(),
            func2vnfd.get(functionId),
            func2flavour.get(functionId),
            func2level.get(functionId),
            func2links.get(functionId).stream().map(Link::getName).collect(Collectors.toSet()),
            rules
        );
    }

    public Stream<VnfdData> streamVnfdData() {
        return functions.keySet().stream().map(this::getVnfdData);
    }

    private void addRule(Long serviceId, Set<L3Connectivity> rules) {
        this.service2Rules.put(serviceId, rules);
    }

    private void addLinks(Set<Link> links) { // TODO NO! duplicate functions in different services
        for (Map.Entry<Long, SdkFunction> e : functions.entrySet()) {
            func2links.putIfAbsent(e.getKey(), new HashSet<>());
            Set<Link> currentFuncLinks = func2links.get(e.getKey());
            links.stream()
                .filter( // the ones mentioning a connection point in the function
                    l -> {
                        HashSet<ConnectionPoint> linkCP = new HashSet<>(l.getConnectionPoints());
                        linkCP.retainAll(e.getValue().getConnectionPoint());
                        return !linkCP.isEmpty();
                    }
                )
                .forEach(currentFuncLinks::add);
        }
    }

    public String getName() {
        return serviceMetadata.name;
    }

    public String getVersion() {
        return serviceMetadata.version;
    }

    public String getDesigner() {
        return serviceMetadata.designer;
    }

    public String getLicense() {
        return serviceMetadata.license;
    }

    public Map<String, String> getMetadata() {
        return serviceMetadata.metadata;
    }

    public ServiceInformation merge(ServiceInformation other) {
        functions.putAll(other.functions);
        funcId2Service.putAll(other.funcId2Service);
        func2vnfd.putAll(other.func2vnfd);
        func2flavour.putAll(other.func2flavour);
        func2level.putAll(other.func2level);
        service2Rules.putAll(other.service2Rules);
        func2links.putAll(other.func2links);
        monitoringParameters.addAll(other.monitoringParameters);
        scalingAspects.addAll(other.scalingAspects);
        functionMetadata.putAll(other.functionMetadata);
        serviceMetadata.merge(other.serviceMetadata);
        return this;
    }

    public ServiceInformation addServiceRelatedInfo(SdkServiceInstance service) {
        Long id = service.getId();
        SdkService template = service.getTemplate();
        addLinks(template.getLink());
        addRule(id, template.getL3Connectivity());
        scalingAspects.addAll(template.getScalingAspect());
        monitoringParameters.addAll(template.getMonitoringParameters());

        serviceMetadata.setName(template.getName());
        serviceMetadata.setVersion(template.getVersion());
        serviceMetadata.setDesigner(template.getDesigner());
        serviceMetadata.setLicense(template.getLicense().getUrl());
        serviceMetadata.addMetadata(template.getMetadata());

        return this; // For chaining methods together
    }

    public static class VnfdData {
        public final String name;
        public final String vnfd;
        public final String flavour;
        public final String instantiationLevel;
        public final Set<String> vLinks;
        public final Set<L3Connectivity> rules;

        public VnfdData(
            String name,
            String vnfd,
            String flavour,
            String instantiationLevel,
            Set<String> vLinks,
            Set<L3Connectivity> rules
        ) {
            this.name = name;
            this.vnfd = vnfd;
            this.flavour = flavour;
            this.instantiationLevel = instantiationLevel;
            this.vLinks = vLinks;
            this.rules = rules;
        }
    }

    public static class ServiceMetadata {
        public String name;
        public String version;
        public String designer;
        public String license;
        public Map<String, String> metadata = new HashMap<>();

        public ServiceMetadata() {

        }

        public ServiceMetadata(String name, String version, String designer, String license, Map<String, String> metadata) {
            this.name = name;
            this.version = version;
            this.designer = designer;
            this.license = license;
            this.metadata = metadata;
        }

        public ServiceMetadata setName(String name) {
            this.name = name;
            return this;
        }

        public ServiceMetadata setVersion(String version) {
            this.version = version;
            return this;
        }

        public ServiceMetadata setDesigner(String designer) {
            this.designer = designer;
            return this;
        }

        public ServiceMetadata setLicense(String license) {
            // validate licensing issues?
            this.license = license;
            return this;
        }

        public ServiceMetadata addMetadata(Map<String, String> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }

        private ServiceMetadata merge(ServiceMetadata other) {
            setName(other.name);
            setVersion(other.version);
            setDesigner(other.designer);
            setLicense(other.license);
            addMetadata(other.metadata);
            return this;
        }
    }

    public static class FunctionMetadata {
        public String name;
        public String description;
        public String vendor;
        public String version;
        public Map<String, String> metadata;

        public FunctionMetadata name(String name) {
            this.name = name;
            return this;
        }

        public FunctionMetadata description(String description) {
            this.description = description;
            return this;
        }

        public FunctionMetadata vendor(String vendor) {
            this.vendor = vendor;
            return this;
        }

        public FunctionMetadata version(String version) {
            this.version = version;
            return this;
        }

        public FunctionMetadata metadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }
    }
}
