package it.nextworks.sdk;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Marco Capitani on 23/11/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class ServiceInformation {

    // No need for any sofistication, these are just throwaway ids never exposed outside the class
    private static IntSupplier functionIds = IntStream.iterate(0, i -> i+1).iterator()::next;
    private static IntSupplier serviceIds = IntStream.iterate(0, i -> i+1).iterator()::next;

    private Map<Integer, SdkFunction> functions = new HashMap<>();

    private Map<Integer, Integer> funcId2Service = new HashMap<>();

    private Map<SdkServiceBase, Integer> service2Id = new HashMap<>();

    private Map<Integer, String> func2vnfd = new HashMap<>();

    private Map<Integer, String> func2flavour = new HashMap<>();

    private Map<Integer, String> func2level = new HashMap<>();

    private Map<Integer, List<L3Connectivity>> service2Rules = new HashMap<>();

    private Map<Integer, List<Link>> links = new HashMap<>();

    private List<MonitoringParameter> monitoringParameters = new LinkedList<>();

    private List<ScalingAspect> scalingAspects = new LinkedList<>();

    public ServiceInformation() {

    }

    public ServiceInformation(SdkFunctionInstance function) {
        Integer id = function.getId();
        SdkFunction template = function.getTemplate();
        functions.put(id, template);
        funcId2Service.put(id, function.getOuterServiceId());
        func2vnfd.put(id, template.getVnfdId());
        func2flavour.put(id, function.getFlavour());
        func2level.put(id, function.getLevel());
        monitoringParameters.addAll(template.getMonitoringParameters());
    }

    public VnfdData getVnfdData(int functionId) {
        SdkFunction function = functions.get(functionId);
        List<L3Connectivity> rules = service2Rules.get(funcId2Service.get(functionId)).stream()
                .filter(
                        c -> function.getConnectionPoint().stream()
                                .anyMatch(fc -> fc.getId().equals(c.getConnectionPointId()))
                ).collect(Collectors.toList());
        return new VnfdData(
                func2vnfd.get(functionId),
                func2flavour.get(functionId),
                func2level.get(functionId),
                rules
        );
    }

    public Stream<VnfdData> streamVnfdData() {
        return functions.keySet().stream().map(this::getVnfdData);
    }

    public Map<Integer, List<Link>> getLinks() {
        return links;
    }

    public List<MonitoringParameter> getMonitoringParameters() {
        return monitoringParameters;
    }

    public List<ScalingAspect> getScalingAspects() {
        return scalingAspects;
    }

    public void addRule(int serviceId, List<L3Connectivity> rules) {
        this.service2Rules.put(serviceId, rules);
    }

    public void addLink(int serviceId, List<Link> links) {
        this.links.put(serviceId, links);
    }

    public void addMonitoringParameter(MonitoringParameter monitoringParameter) {
        this.monitoringParameters.add(monitoringParameter);
    }

    public void addScalingAspect(ScalingAspect scalingAspect) {
        this.scalingAspects.add(scalingAspect);
    }

    public ServiceInformation merge(ServiceInformation other) {
        functions.putAll(other.functions);
        funcId2Service.putAll(other.funcId2Service);
        func2vnfd.putAll(other.func2vnfd);
        func2flavour.putAll(other.func2flavour);
        func2level.putAll(other.func2level);
        service2Rules.putAll(other.service2Rules);
        links.putAll(other.links);
        monitoringParameters.addAll(other.monitoringParameters);
        scalingAspects.addAll(other.scalingAspects);
        return this;
    }

    public ServiceInformation addServiceRelatedInfo(SdkServiceInstance service) {
        Integer id = service.getId();
        SdkServiceTemplate template = service.getTemplate();
        addLink(id, template.getLink());
        addRule(id, template.getL3Connectivity());
        scalingAspects.addAll(template.getScalingAspect());
        monitoringParameters.addAll(template.getMonitoringParameters());
        return this; // For chaining methods together
    }

    public static class VnfdData {
        public final String vnfd;
        public final String flavour;
        public final String instantiationLevel;
        public final List<L3Connectivity> rules;

        public VnfdData(
                String vnfd,
                String flavour,
                String instantiationLevel,
                List<L3Connectivity> rules
        ) {
            this.vnfd = vnfd;
            this.flavour = flavour;
            this.instantiationLevel = instantiationLevel;
            this.rules = rules;
        }
    }
}
