package it.nextworks.sdk;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.nextworks.sdk.enums.ScalingAction;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;


/**
 * ScalingAspect
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "action",
    "id",
    "monitoring_parameter",
    "name"
})
@Entity
public class ScalingAspect {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private ScalingAction action;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonProperty("monitoring_parameters")
    private List<MonitoringParameter> monitoringParameter = new ArrayList<>();

    private String name;

    @JsonProperty("action")
    public ScalingAction getAction() {
        return action;
    }

    @JsonProperty("action")
    public void setAction(ScalingAction action) {
        this.action = action;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("monitoring_parameter")
    public List<MonitoringParameter> getMonitoringParameter() {
        return monitoringParameter;
    }

    @JsonProperty("monitoring_parameter")
    public void setMonitoringParameter(List<MonitoringParameter> monitoringParameter) {
        this.monitoringParameter = monitoringParameter;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public boolean isValid() {
        return name != null && name.length() > 0
                && action != null
                && monitoringParameter != null && monitoringParameter.size() > 0
                && monitoringParameter.stream().allMatch(MonitoringParameter::isValid);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ScalingAspect.class.getName())
                .append('@')
                .append(Integer.toHexString(System.identityHashCode(this)))
                .append('[');
        sb.append("action");
        sb.append('=');
        sb.append(((this.action == null)?"<null>":this.action));
        sb.append(',');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("monitoringParameter");
        sb.append('=');
        sb.append(((this.monitoringParameter == null)?"<null>":this.monitoringParameter));
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
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.action == null)? 0 :this.action.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.monitoringParameter == null)? 0 :this.monitoringParameter.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ScalingAspect)) {
            return false;
        }
        ScalingAspect rhs = ((ScalingAspect) other);
        return (((((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name)))
                &&((this.action == rhs.action)||((this.action!= null)&&this.action.equals(rhs.action))))
                &&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))
                &&((this.monitoringParameter == rhs.monitoringParameter)||((this.monitoringParameter!= null)&&this.monitoringParameter.equals(rhs.monitoringParameter))));
    }

}
