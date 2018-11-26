package it.nextworks.sdk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.nextworks.sdk.evalex.ExtendedExpression;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;


/**
 * SDKFunction
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "connection_point",
    "description",
    "parameters",
    "vnfdId",
    "flavourExpression",
    "instantiationLevelExpression",
    "id",
    "metadata",
    "monitoringParameters",
    "requiredPorts",
    "name",
    "vendor",
    "version"
})
@Entity
public class SdkFunction implements SdkComponentCandidate<SdkFunction> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToMany(mappedBy = "sdkFunction", cascade= CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ConnectionPoint> connectionPoint = new ArrayList<>();

    private String description;

    @ElementCollection(fetch= FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> parameters = new ArrayList<>();

    private String vnfdId;

    private String flavourExpression;

    private String instantiationLevelExpression;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch= FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Map<String, String> metadata = new HashMap<>();

    @OneToMany(mappedBy = "function", cascade=CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<MonitoringParameter> monitoringParameters = new ArrayList<>();

    @ElementCollection(fetch= FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<RequiredPort> requiredPorts = new ArrayList<>();

    private String name;

    private String vendor;

    private String version;

    @JsonProperty("connection_point")
    public List<ConnectionPoint> getConnectionPoint() {
        return connectionPoint;
    }

    @JsonProperty("connection_point")
    public void setConnectionPoint(List<ConnectionPoint> connectionPoint) {
        this.connectionPoint = connectionPoint;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("parameters")
    @Override
    public List<String> getParameters() {
        return parameters;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("parameters")
    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    @JsonProperty("vnfdId")
    public String getVnfdId() {
        return vnfdId;
    }

    @JsonProperty("vnfdId")
    public void setVnfdId(String vnfdId) {
        this.vnfdId = vnfdId;
    }

    @JsonProperty("flavourExpression")
    public String getFlavourExpression() {
        return flavourExpression;
    }

    @JsonProperty("flavourExpression")
    public void setFlavourExpression(String flavourExpression) {
        this.flavourExpression = flavourExpression;
    }

    @JsonProperty("instantiationLevelExpression")
    public String getInstantiationLevelExpression() {
        return instantiationLevelExpression;
    }

    @JsonProperty("instantiationLevelExpression")
    public void setInstantiationLevelExpression(String instantiationLevelExpression) {
        this.instantiationLevelExpression = instantiationLevelExpression;
    }

    @JsonProperty("id")
    @Override
    public Integer getId() {
        return id;
    }

    @JsonProperty("metadata")
    public Map<String, String> getMetadata() {
        return metadata;
    }

    @JsonProperty("metadata")
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("monitoringParameters")
    public List<MonitoringParameter> getMonitoringParameters() {
        return monitoringParameters;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("monitoringParameters")
    public void setMonitoringParameters(List<MonitoringParameter> monitoringParameters) {
        this.monitoringParameters = monitoringParameters;
    }

    @JsonProperty("requiredPorts")
    public List<RequiredPort> getRequiredPorts() {
        return requiredPorts;
    }

    @JsonProperty("requiredPorts")
    public void setRequiredPorts(List<RequiredPort> requiredPorts) {
        this.requiredPorts = requiredPorts;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("vendor")
    public String getVendor() {
        return vendor;
    }

    @JsonProperty("vendor")
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonIgnore
    public ExtendedExpression<String> getFlavourCompiledExpression() {
        return ExtendedExpression.stringValued(flavourExpression, parameters);
    }

    @JsonIgnore
    public ExtendedExpression<String> getILCompiledExpression() {
        return ExtendedExpression.stringValued(instantiationLevelExpression, parameters);
    }

    @Override
    public SdkComponentInstance<SdkFunction> instantiate(
            List<BigDecimal> parameterValues,
            SdkServiceInstance outerService
    ) {
        return new SdkFunctionInstance(this, parameterValues, outerService);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SdkFunction.class.getName())
                .append('@')
                .append(Integer.toHexString(System.identityHashCode(this)))
                .append('[');
        sb.append("connectionPoint");
        sb.append('=');
        sb.append(((this.connectionPoint == null)?"<null>":this.connectionPoint));
        sb.append(',');
        sb.append("description");
        sb.append('=');
        sb.append(((this.description == null)?"<null>":this.description));
        sb.append(',');
        sb.append("parameters");
        sb.append('=');
        sb.append(((this.parameters == null)?"<null>":this.parameters));
        sb.append(',');
        sb.append("vnfdId");
        sb.append('=');
        sb.append(((this.vnfdId == null)?"<null>":this.vnfdId));
        sb.append(',');
        sb.append("flavourExpression");
        sb.append('=');
        sb.append(((this.flavourExpression == null)?"<null>":this.flavourExpression));
        sb.append(',');
        sb.append("instantiationLevelExpression");
        sb.append('=');
        sb.append(((this.instantiationLevelExpression == null)?"<null>":this.instantiationLevelExpression));
        sb.append(',');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("metadata");
        sb.append('=');
        sb.append(((this.metadata == null)?"<null>":this.metadata));
        sb.append(',');
        sb.append("monitoringParameters");
        sb.append('=');
        sb.append(((this.monitoringParameters == null)?"<null>":this.monitoringParameters));
        sb.append(',');
        sb.append("requiredPorts");
        sb.append('=');
        sb.append(((this.requiredPorts == null)?"<null>":this.requiredPorts));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("vendor");
        sb.append('=');
        sb.append(((this.vendor == null)?"<null>":this.vendor));
        sb.append(',');
        sb.append("version");
        sb.append('=');
        sb.append(((this.version == null)?"<null>":this.version));
        sb.append(',');
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
        result = ((result* 31)+((this.metadata == null)? 0 :this.metadata.hashCode()));
        result = ((result* 31)+((this.instantiationLevelExpression == null)? 0 :this.instantiationLevelExpression.hashCode()));
        result = ((result* 31)+((this.vnfdId == null)? 0 :this.vnfdId.hashCode()));
        result = ((result* 31)+((this.description == null)? 0 :this.description.hashCode()));
        result = ((result* 31)+((this.requiredPorts == null)? 0 :this.requiredPorts.hashCode()));
        result = ((result* 31)+((this.version == null)? 0 :this.version.hashCode()));
        result = ((result* 31)+((this.monitoringParameters == null)? 0 :this.monitoringParameters.hashCode()));
        result = ((result* 31)+((this.flavourExpression == null)? 0 :this.flavourExpression.hashCode()));
        result = ((result* 31)+((this.vendor == null)? 0 :this.vendor.hashCode()));
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.connectionPoint == null)? 0 :this.connectionPoint.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.parameters == null)? 0 :this.parameters.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SdkFunction)) {
            return false;
        }
        SdkFunction rhs = ((SdkFunction) other);
        return ((((((((((((
                ((this.metadata == rhs.metadata)||((this.metadata!= null)&&this.metadata.equals(rhs.metadata)))
                        &&((this.instantiationLevelExpression == rhs.instantiationLevelExpression)||((this.instantiationLevelExpression!= null)&&this.instantiationLevelExpression.equals(rhs.instantiationLevelExpression))))
                &&((this.vnfdId == rhs.vnfdId)||((this.vnfdId!= null)&&this.vnfdId.equals(rhs.vnfdId))))
                &&((this.description == rhs.description)||((this.description!= null)&&this.description.equals(rhs.description))))
                &&((this.requiredPorts == rhs.requiredPorts)||((this.requiredPorts!= null)&&this.requiredPorts.equals(rhs.requiredPorts))))
                &&((this.version == rhs.version)||((this.version!= null)&&this.version.equals(rhs.version))))
                &&((this.monitoringParameters == rhs.monitoringParameters)||((this.monitoringParameters!= null)&&this.monitoringParameters.equals(rhs.monitoringParameters))))
                &&((this.flavourExpression == rhs.flavourExpression)||((this.flavourExpression!= null)&&this.flavourExpression.equals(rhs.flavourExpression))))
                &&((this.vendor == rhs.vendor)||((this.vendor!= null)&&this.vendor.equals(rhs.vendor))))
                &&((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name))))
                &&((this.connectionPoint == rhs.connectionPoint)||((this.connectionPoint!= null)&&this.connectionPoint.equals(rhs.connectionPoint))))
                &&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))
                &&((this.parameters == rhs.parameters)||((this.parameters!= null)&&this.parameters.equals(rhs.parameters))));
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return name != null && name.length() > 0
                && connectionPoint != null && connectionPoint.size() > 0
                && connectionPoint.stream().allMatch(ConnectionPoint::isValid)
                && version != null && version.length() > 0
                && vendor != null && vendor.length() > 0
                && vnfdId != null && vnfdId.length() > 0
                && instantiationLevelExpression != null
                && flavourExpression != null
                && validateExpressions();
    }

    private boolean validateExpressions() {
        try {
            // Check expressions are well-formed.
            getFlavourCompiledExpression();
            getILCompiledExpression();
            return true;
            // Validate against vnfd values??
            // e.g.
            // vnfd.getDfs().containsAll(((StringValuedExpression) getFlavourCompiledExpression()).getResultVars())
        } catch (Exception exc) {
            return false;
        }
    }

    @JsonIgnore
    @Override
    public Integer getFreeParametersNumber() {
        return parameters.size();
    }
}
