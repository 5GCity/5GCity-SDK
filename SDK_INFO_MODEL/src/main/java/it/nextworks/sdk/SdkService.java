package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.nextworks.sdk.enums.LinkType;
import it.nextworks.sdk.enums.SdkServiceComponentType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * SDKServiceTemplate
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "version",
    "designer",
    "parameters",
    "license",
    "link",
    "components",
    "metadata",
    "l3_connectivity",
    "monitoring_parameters",
    "scaling_aspect"

})
@Entity
public class SdkService implements InstantiableCandidate<SdkService> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String version;
    private String designer;
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OrderColumn
    private List<String> parameters = new ArrayList<>();
    @OneToMany(mappedBy = "outerService", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<SubFunction> subFunctions = new HashSet<>();
    @OneToMany(mappedBy = "outerService", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<SubService> subServices = new HashSet<>();
    @Embedded
    private License license;
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<Link> link = new HashSet<>();
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<L3Connectivity> l3Connectivity = new HashSet<>();
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<Metadata> metadata = new HashSet<>();
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<MonitoringParameter> monitoringParameters = new HashSet<>();
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<ScalingAspect> scalingAspect = new HashSet<>();

    public SdkServiceInstance instantiate(List<BigDecimal> parameterValues) {
        return new SdkServiceInstance(this, parameterValues, null);
    }

    @Override
    public SdkServiceInstance instantiate(List<BigDecimal> parameterValues, SdkServiceInstance outerService) {
        return new SdkServiceInstance(this, parameterValues, outerService);
    }

    @Override
    @JsonIgnore
    public Map<Long, ConnectionPoint> getConnectionPointMap() {
        return getComponents().stream()
            .map(SdkServiceComponent<SdkComponentCandidate, InstantiableCandidate<SdkComponentCandidate>>::getComponent)
            .flatMap(c -> c.getConnectionPointMap().entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @JsonProperty("parameters")
    @Override
    public List<String> getParameters() {
        return parameters;
    }

    @JsonProperty("parameters")
    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    @JsonIgnore
    @Override
    public Integer getFreeParametersNumber() {
        return parameters.size();
    }

    @JsonProperty("designer")
    public String getDesigner() {
        return designer;
    }

    @JsonProperty("designer")
    public void setDesigner(String designer) {
        this.designer = designer;
    }

    @JsonProperty("components")
    public List<SdkServiceComponent> getComponents() {
        ArrayList<SdkServiceComponent> output = new ArrayList<>(subFunctions);
        output.addAll(subServices);
        return output;
    }

    @JsonProperty("components")
    public void setComponents(List<SdkServiceComponent> components) {
        Map<? extends Class<?>, List<SdkServiceComponent>> byClass =
            components.stream().collect(Collectors.groupingBy(SdkServiceComponent::getClass));
        if (byClass.size() > 2) {
            throw new IllegalArgumentException(String.format(
                "Unknown component type(s). Expected %s, got %s",
                Arrays.asList(SubFunction.class.getSimpleName(), SubService.class.getSimpleName()),
                byClass.keySet()
            ));
        }
        subFunctions = byClass.getOrDefault(SubFunction.class, Collections.emptyList()).stream()
            .map(SubFunction.class::cast)
            .collect(Collectors.toSet());
        subServices = byClass.getOrDefault(SubService.class, Collections.emptyList()).stream()
            .map(SubService.class::cast)
            .collect(Collectors.toSet());
        if (!validateComponents()) {
            throw new IllegalArgumentException(
                "Invalid components provided. Make sure all are valid and saved."
            );
        }
    }

    @JsonProperty("id")
    @Override
    public Long getId() {
        return id;
    }

    @JsonProperty("license")
    public License getLicense() {
        return license;
    }

    @JsonProperty("license")
    public void setLicense(License license) {
        this.license = license;
    }

    @JsonProperty("link")
    public Set<Link> getLink() {
        return link;
    }

    @JsonProperty("link")
    public void setLink(Set<Link> link) {
        this.link = link;
    }

    @JsonProperty("l3_connectivity")
    public Set<L3Connectivity> getL3Connectivity() {
        return l3Connectivity;
    }

    @JsonProperty("l3_connectivity")
    public void setL3Connectivity(Set<L3Connectivity> l3Connectivity) {
        this.l3Connectivity = l3Connectivity;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("metadata")
    public Map<String, String> getMetadata() {
        return metadata.stream().collect(Collectors.toMap(Metadata::getKey, Metadata::getValue));
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("metadata")
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata.entrySet().stream()
            .map(e -> new Metadata(e.getKey(), e.getValue(), this))
            .collect(Collectors.toSet());
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("monitoring_parameters")
    public Set<MonitoringParameter> getMonitoringParameters() {
        return monitoringParameters;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("monitoring_parameters")
    public void setMonitoringParameters(Set<MonitoringParameter> monitoringParameters) {
        this.monitoringParameters = monitoringParameters;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("scaling_aspect")
    public Set<ScalingAspect> getScalingAspect() {
        return scalingAspect;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("scaling_aspect")
    public void setScalingAspect(Set<ScalingAspect> scalingAspect) {
        this.scalingAspect = scalingAspect;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    @JsonIgnore
    public SdkServiceComponentType getType() {
        return SdkServiceComponentType.SDK_SERVICE;
    }

    public void resolveComponents(Set<SdkFunction> functions, Set<SdkService> services) {
        Map<Long, SdkFunction> functionMap = functions.stream()
            .collect(Collectors.toMap(SdkFunction::getId, Function.identity()));
        for (SubFunction subFunction : subFunctions) {
            SdkFunction function = functionMap.get(subFunction.getComponentId());
            if (function == null) {
                throw new IllegalArgumentException(String.format(
                    "Invalid components: function %s missing",
                    subFunction.getComponentId()
                ));
            }
            subFunction.setComponent(function);
        }
        Map<Long, SdkService> serviceMap = services.stream()
            .collect(Collectors.toMap(SdkService::getId, Function.identity()));
        for (SubService subService : subServices) {
            SdkService service = serviceMap.get(subService.getComponentId());
            if (service == null) {
                throw new IllegalArgumentException(String.format(
                    "Invalid components: service %s missing",
                    subService.getComponentId()
                ));
            }
            subService.setComponent(service);
        }
        if (!validateLinks()) {
            throw new IllegalArgumentException("Invalid links");
        }
    }

    private boolean validateExpressions() {
        return getComponents().stream().allMatch(c -> c.validateParameters(parameters));
    }

    private boolean validateComponents() {

        return // pre-check
            subFunctions != null
                && subServices != null
                && (subFunctions.size() + subServices.size()) > 0
                // components are valid & registered
                && subFunctions.stream().allMatch(SdkServiceComponent::isValid)
                && subServices.stream().allMatch(SdkServiceComponent::isValid)
                // no duplicate components
                && subFunctions.stream().map(SdkServiceComponent::getComponentId).count() == subFunctions.size()
                && subServices.stream().map(SdkServiceComponent::getComponentId).count() == subServices.size();
    }

    private boolean validateLinks() {
        Map<Long, ConnectionPoint> cpMap = getConnectionPointMap();
        return link != null
            && link.size() > 0
            &&
            link.stream().allMatch(
                l -> l.isValid()
                    && l.getType() == LinkType.EXTERNAL
                    && l.validateAgainstCpMap(cpMap)
            );
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return name != null
            && designer != null && designer.length() > 0
            && version != null && version.length() > 0
            && license != null && license.isValid()
            && validateComponents()
            && metadata != null
            && l3Connectivity != null
            && l3Connectivity.stream().allMatch(L3Connectivity::isValid)
            && monitoringParameters != null
            && monitoringParameters.stream().allMatch(MonitoringParameter::isValid)
            && scalingAspect != null
            && scalingAspect.stream().allMatch(ScalingAspect::isValid)
            && parameters != null
            && validateExpressions();
    }

    private void appendContents(StringBuilder sb) {
        sb.append("\n    ");
        sb.append("designer");
        sb.append('=');
        sb.append(((this.designer == null) ? "<null>" : this.designer));
        sb.append(',');
        sb.append("\n    ");
        sb.append("subFunctions");
        sb.append('=');
        sb.append(((this.subFunctions == null) ? "<null>" : this.subFunctions));
        sb.append(',');
        sb.append("\n    ");
        sb.append("subServices");
        sb.append('=');
        sb.append(((this.subServices == null) ? "<null>" : this.subServices));
        sb.append(',');
        sb.append("\n    ");
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null) ? "<null>" : this.id));
        sb.append(',');
        sb.append("\n    ");
        sb.append("license");
        sb.append('=');
        sb.append(((this.license == null) ? "<null>" : this.license));
        sb.append(',');
        sb.append("\n    ");
        sb.append("link");
        sb.append('=');
        sb.append(((this.link == null) ? "<null>" : this.link));
        sb.append(',');
        sb.append("\n    ");
        sb.append("l3Connectivity");
        sb.append('=');
        sb.append(((this.l3Connectivity == null) ? "<null>" : this.l3Connectivity));
        sb.append(',');
        sb.append("\n    ");
        sb.append("metadata");
        sb.append('=');
        sb.append(((this.metadata == null) ? "<null>" : this.metadata));
        sb.append(',');
        sb.append("\n    ");
        sb.append("monitoringParameters");
        sb.append('=');
        sb.append(((this.monitoringParameters == null) ? "<null>" : this.monitoringParameters));
        sb.append(',');
        sb.append("\n    ");
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null) ? "<null>" : this.name));
        sb.append(',');
        sb.append("scalingAspect");
        sb.append('=');
        sb.append(((this.scalingAspect == null) ? "<null>" : this.scalingAspect));
        sb.append(',');
        sb.append("\n    ");
        sb.append("version");
        sb.append('=');
        sb.append(((this.version == null) ? "<null>" : this.version));
        sb.append(',');
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SdkService.class.getName())
            .append('[');
        sb.append("parameters");
        sb.append('=');
        sb.append(((this.parameters == null) ? "<null>" : this.parameters));
        sb.append(',');
        appendContents(sb);
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result * 31) + ((this.subFunctions == null) ? 0 : this.subFunctions.hashCode()));
        result = ((result * 31) + ((this.subServices == null) ? 0 : this.subServices.hashCode()));
        result = ((result * 31) + ((this.metadata == null) ? 0 : this.metadata.hashCode()));
        result = ((result * 31) + ((this.l3Connectivity == null) ? 0 : this.l3Connectivity.hashCode()));
        result = ((result * 31) + ((this.scalingAspect == null) ? 0 : this.scalingAspect.hashCode()));
        result = ((result * 31) + ((this.link == null) ? 0 : this.link.hashCode()));
        result = ((result * 31) + ((this.designer == null) ? 0 : this.designer.hashCode()));
        result = ((result * 31) + ((this.version == null) ? 0 : this.version.hashCode()));
        result = ((result * 31) + ((this.license == null) ? 0 : this.license.hashCode()));
        result = ((result * 31) + ((this.monitoringParameters == null) ? 0 : this.monitoringParameters.hashCode()));
        result = ((result * 31) + ((this.name == null) ? 0 : this.name.hashCode()));
        result = ((result * 31) + ((this.id == null) ? 0 : this.id.hashCode()));
        result = ((result * 31) + ((this.parameters == null) ? 0 : this.parameters.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SdkService)) {
            return false;
        }
        SdkService rhs = ((SdkService) other);
        return (((((((((((((this.subFunctions == rhs.subFunctions) || ((this.subFunctions != null) && this.subFunctions.equals(rhs.subFunctions)))
            && ((this.subServices == rhs.subServices) || ((this.subServices != null) && this.subServices.equals(rhs.subServices))))
            && ((this.metadata == rhs.metadata) || ((this.metadata != null) && this.metadata.equals(rhs.metadata))))
            && ((this.l3Connectivity == rhs.l3Connectivity) || ((this.l3Connectivity != null) && this.l3Connectivity.equals(rhs.l3Connectivity))))
            && ((this.scalingAspect == rhs.scalingAspect) || ((this.scalingAspect != null) && this.scalingAspect.equals(rhs.scalingAspect))))
            && ((this.link == rhs.link) || ((this.link != null) && this.link.equals(rhs.link))))
            && ((this.designer == rhs.designer) || ((this.designer != null) && this.designer.equals(rhs.designer))))
            && ((this.version == rhs.version) || ((this.version != null) && this.version.equals(rhs.version))))
            && ((this.license == rhs.license) || ((this.license != null) && this.license.equals(rhs.license))))
            && ((this.monitoringParameters == rhs.monitoringParameters) || ((this.monitoringParameters != null) && this.monitoringParameters.equals(rhs.monitoringParameters))))
            && ((this.name == rhs.name) || ((this.name != null) && this.name.equals(rhs.name))))
            && ((this.id == rhs.id) || ((this.id != null) && this.id.equals(rhs.id))))
            && ((this.parameters == rhs.parameters) || ((this.parameters != null) && this.parameters.equals(rhs.parameters)));
    }

    @PrePersist
    private void prePersist() {
        if (!getComponents().stream().allMatch(SdkServiceComponent::isResolved)) {
            throw new IllegalStateException("Cannot persist, there are unresolved components");
        }
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void fixPersistence() {
        // Cleanup persistence artifacts and weird collection implementations
        parameters = new ArrayList<>(parameters);
        link = new HashSet<>(link);
        l3Connectivity = new HashSet<>(l3Connectivity);
        monitoringParameters = new HashSet<>(monitoringParameters);
        scalingAspect = new HashSet<>(scalingAspect);
        subFunctions = new HashSet<>(subFunctions);
        subServices = new HashSet<>(subServices);
        metadata = new HashSet<>(metadata);
    }
}
