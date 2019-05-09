package it.nextworks.sdk;
import com.fasterxml.jackson.annotation.*;
import it.nextworks.sdk.enums.MonitoringParameterType;

import javax.persistence.*;


/**
 * BaseMonitoringParameter
 * <p>
 *
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "parameterType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AggregatedMonParam.class, 	name = "AGGREGATED"),
    @JsonSubTypes.Type(value = FunctionMonParam.class, 	name = "FUNCTION"),
    @JsonSubTypes.Type(value = ImportedMonParam.class, 	name = "IMPORTED"),
    @JsonSubTypes.Type(value = TransformedMonParam.class, 	name = "TRANSFORMED"),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "parameterId",
    "parameterType"
})
@Entity
public abstract class MonitoringParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private MonitoringParameterType parameterType;

    private String name;

    @ManyToOne
    private SdkService sdkServiceExt;

    @ManyToOne
    private SdkService sdkServiceInt;

    /*
    @ManyToOne
    private ReconfigureAction reconfigureActionExt;

    @ManyToOne
    private ReconfigureAction reconfigureActionInt;
    */

    @ManyToOne
    private SdkFunction sdkFunction;

    public MonitoringParameter(){
        //JPA only
    }

    @JsonProperty("parameterType")
    public MonitoringParameterType getParameterType() {
        return parameterType;
    }

    @JsonProperty("parameterType")
    public void setParameterType(MonitoringParameterType parameterType) {
        this.parameterType = parameterType;
    }

    @JsonProperty("parameterId")
    public String getParameterId() {
        return id.toString();
    }

    @JsonProperty("parameterId")
    public void setParameterId(String parameterId) {
        this.id = Long.valueOf(parameterId);
    }

    @JsonIgnore
    public Long getId() { return id; }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public SdkService getSdkServiceExt() {
        return sdkServiceExt;
    }

    @JsonIgnore
    public void setSdkServiceExt(SdkService sdkService) {
        this.sdkServiceExt = sdkService;
    }

    @JsonIgnore
    public SdkService getSdkServiceInt() {
        return sdkServiceInt;
    }

    @JsonIgnore
    public void setSdkServiceInt(SdkService sdkService) {
        this.sdkServiceInt = sdkService;
    }

    @JsonIgnore
    public SdkFunction getSdkFunction() { return sdkFunction; }

    @JsonIgnore
    public void setSdkFunction(SdkFunction sdkFunction) { this.sdkFunction = sdkFunction; }

    /*
    @JsonIgnore
    public ReconfigureAction getReconfigureActionExt() {
        return reconfigureActionExt;
    }

    @JsonIgnore
    public void setReconfigureActionExt(ReconfigureAction reconfigureActionExt) {
        this.reconfigureActionExt = reconfigureActionExt;
    }

    @JsonIgnore
    public ReconfigureAction getReconfigureActionInt() {
        return reconfigureActionInt;
    }

    @JsonIgnore
    public void setReconfigureActionInt(ReconfigureAction reconfigureActionInt) {
        this.reconfigureActionInt = reconfigureActionInt;
    }
*/
    @JsonIgnore
    public boolean isValid() {
        return parameterType != null
            && name != null && name.length() > 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MonitoringParameter.class.getName()).append('[');
        sb.append("parameterType");
        sb.append('=');
        sb.append(((this.parameterType == null)?"<null>":this.parameterType));
        sb.append(',');
        sb.append("parameterId");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
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
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.parameterType == null)? 0 :this.parameterType.hashCode()));
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MonitoringParameter) == false) {
            return false;
        }
        MonitoringParameter rhs = ((MonitoringParameter) other);
        return (((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id)))
            &&((this.parameterType == rhs.parameterType)||((this.parameterType!= null)&&this.parameterType.equals(rhs.parameterType)))
            &&((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name))));
    }
}
