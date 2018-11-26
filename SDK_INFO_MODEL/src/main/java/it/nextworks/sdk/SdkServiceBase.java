package it.nextworks.sdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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


/**
 * SDKServiceBase
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "version",
    "designer",
    "license",
    "components",
    "link",
    "l3Connectivity",
    "monitoring_parameters",
    "scaling_aspect",
    "metadata"
})
@Entity
public abstract class SdkServiceBase implements SdkComponentCandidate<SdkServiceBase> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    private String version;

    private String designer;

    @ElementCollection(fetch= FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<SdkServiceComponent> components = new ArrayList<>();

    @Embedded
    private License license;

    @ElementCollection(fetch= FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Link> link = new ArrayList<>();

    @ElementCollection(fetch= FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<L3Connectivity> l3Connectivity = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch= FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Map<String, String> metadata = new HashMap<>();

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonProperty("monitoring_parameters")
    private List<MonitoringParameter> monitoringParameters = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<ScalingAspect> scalingAspect = new ArrayList<ScalingAspect>();

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
        return components;
    }

    @JsonProperty("components")
    public void setComponents(List<SdkServiceComponent> components) {
        this.components = components;
    }

    @JsonProperty("id")
    @Override
    public Integer getId() {
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
    public List<Link> getLink() {
        return link;
    }

    @JsonProperty("link")
    public void setLink(List<Link> link) {
        this.link = link;
    }

    @JsonProperty("l3Connectivity")
    public List<L3Connectivity> getL3Connectivity() {
        return l3Connectivity;
    }

    @JsonProperty("l3Connectivity")
    public void setL3Connectivity(List<L3Connectivity> l3Connectivity) {
        this.l3Connectivity = l3Connectivity;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("metadata")
    public Map<String, String> getMetadata() {
        return metadata;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("metadata")
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("monitoring_parameters")
    public List<MonitoringParameter> getMonitoringParameters() {
        return monitoringParameters;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("monitoring_parameters")
    public void setMonitoringParameters(List<MonitoringParameter> monitoringParameters) {
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
    public List<ScalingAspect> getScalingAspect() {
        return scalingAspect;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("scaling_aspect")
    public void setScalingAspect(List<ScalingAspect> scalingAspect) {
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

    private boolean validateComponents() {
        boolean preCheck = components != null && components.size() > 0
                && components.stream().allMatch(SdkServiceComponent::isValid);
        if (!preCheck) {
            return false;
        }
        Map<? extends Class<? extends SdkServiceComponent>, List<SdkServiceComponent>> grouped =
                components.stream().collect(Collectors.groupingBy(SdkServiceComponent::getClass));
        for (List<SdkServiceComponent> value : grouped.values()) {
            // For each component class
            if (value.stream().map(c -> c.getComponent().getId()).count() != value.size()) {
                // There are duplicate IDs
                return false;
            }
        }
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return name != null
                && designer != null && designer.length() > 0
                && version != null && version.length() > 0
                && license != null && license.isValid()
                && link != null && link.size() > 0
                && link.stream().allMatch(Link::isValid)
                && validateComponents()
                && metadata != null
                && l3Connectivity != null && l3Connectivity.stream().allMatch(L3Connectivity::isValid)
                && monitoringParameters != null && monitoringParameters.stream().allMatch(MonitoringParameter::isValid)
                && scalingAspect != null && scalingAspect.stream().allMatch(ScalingAspect::isValid);
    }

    protected void appendContents(StringBuilder sb) {
        sb.append("designer");
        sb.append('=');
        sb.append(((this.designer == null)?"<null>":this.designer));
        sb.append(',');
        sb.append("components");
        sb.append('=');
        sb.append(((this.components == null)?"<null>":this.components));
        sb.append(',');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("license");
        sb.append('=');
        sb.append(((this.license == null)?"<null>":this.license));
        sb.append(',');
        sb.append("link");
        sb.append('=');
        sb.append(((this.link == null)?"<null>":this.link));
        sb.append(',');
        sb.append("l3Connectivity");
        sb.append('=');
        sb.append(((this.l3Connectivity == null)?"<null>":this.l3Connectivity));
        sb.append(',');
        sb.append("metadata");
        sb.append('=');
        sb.append(((this.metadata == null)?"<null>":this.metadata));
        sb.append(',');
        sb.append("monitoringParameters");
        sb.append('=');
        sb.append(((this.monitoringParameters == null)?"<null>":this.monitoringParameters));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("scalingAspect");
        sb.append('=');
        sb.append(((this.scalingAspect == null)?"<null>":this.scalingAspect));
        sb.append(',');
        sb.append("version");
        sb.append('=');
        sb.append(((this.version == null)?"<null>":this.version));
        sb.append(',');
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SdkServiceBase.class.getName())
                .append('@')
                .append(Integer.toHexString(System.identityHashCode(this)))
                .append('[');
        appendContents(sb);
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.components == null)? 0 :this.components.hashCode()));
        result = ((result* 31)+((this.metadata == null)? 0 :this.metadata.hashCode()));
        result = ((result* 31)+((this.l3Connectivity == null)? 0 :this.l3Connectivity.hashCode()));
        result = ((result* 31)+((this.scalingAspect == null)? 0 :this.scalingAspect.hashCode()));
        result = ((result* 31)+((this.link == null)? 0 :this.link.hashCode()));
        result = ((result* 31)+((this.designer == null)? 0 :this.designer.hashCode()));
        result = ((result* 31)+((this.version == null)? 0 :this.version.hashCode()));
        result = ((result* 31)+((this.license == null)? 0 :this.license.hashCode()));
        result = ((result* 31)+((this.monitoringParameters == null)? 0 :this.monitoringParameters.hashCode()));
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SdkServiceBase)) {
            return false;
        }
        SdkServiceBase rhs = ((SdkServiceBase) other);
        return ((((((((((((this.components == rhs.components)||((this.components!= null)&&this.components.equals(rhs.components)))
                &&((this.metadata == rhs.metadata)||((this.metadata!= null)&&this.metadata.equals(rhs.metadata))))
                &&((this.l3Connectivity == rhs.l3Connectivity)||((this.l3Connectivity!= null)&&this.l3Connectivity.equals(rhs.l3Connectivity))))
                &&((this.scalingAspect == rhs.scalingAspect)||((this.scalingAspect!= null)&&this.scalingAspect.equals(rhs.scalingAspect))))
                &&((this.link == rhs.link)||((this.link!= null)&&this.link.equals(rhs.link))))
                &&((this.designer == rhs.designer)||((this.designer!= null)&&this.designer.equals(rhs.designer))))
                &&((this.version == rhs.version)||((this.version!= null)&&this.version.equals(rhs.version))))
                &&((this.license == rhs.license)||((this.license!= null)&&this.license.equals(rhs.license))))
                &&((this.monitoringParameters == rhs.monitoringParameters)||((this.monitoringParameters!= null)&&this.monitoringParameters.equals(rhs.monitoringParameters))))
                &&((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name))))
                &&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))));
    }
}
