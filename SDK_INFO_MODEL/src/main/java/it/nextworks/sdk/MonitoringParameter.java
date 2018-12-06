package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.nextworks.sdk.enums.Direction;
import it.nextworks.sdk.enums.MonitoringParameterName;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


/**
 * MonitoringParameter
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "direction",
    "threshold"
})
@Entity
public class MonitoringParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Direction direction;

    private MonitoringParameterName name;

    private Double threshold;

    @ManyToOne
    private SdkService service;

    @ManyToOne
    private SdkFunction function;

    @ManyToOne
    private ScalingAspect scalingAspect;

    @JsonProperty("direction")
    public Direction getDirection() {
        return direction;
    }

    @JsonProperty("direction")
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @JsonProperty("name")
    public MonitoringParameterName getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(MonitoringParameterName name) {
        this.name = name;
    }

    @JsonProperty("threshold")
    public Double getThreshold() {
        return threshold;
    }

    @JsonProperty("threshold")
    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    @JsonIgnore
    public SdkService getService() {
        return service;
    }

    @JsonIgnore
    public void setService(SdkService service) {
        this.service = service;
    }

    @JsonIgnore
    public ScalingAspect getScalingAspect() {
        return scalingAspect;
    }

    @JsonIgnore
    public void setScalingAspect(ScalingAspect scalingAspect) {
        this.scalingAspect = scalingAspect;
    }

    @JsonIgnore
    public SdkFunction getFunction() {
        return function;
    }

    @JsonIgnore
    public void setFunction(SdkFunction function) {
        this.function = function;
    }

    @JsonIgnore
    public boolean isValidForScalingPurpose() {
        return this.isValid()
            && this.threshold > 0
            && this.direction != null
            && this.scalingAspect != null;
    }

    @JsonIgnore
    public boolean isValid() {
        return this.name != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MonitoringParameter.class.getName())
            .append('[');
        sb.append("direction");
        sb.append('=');
        sb.append(((this.direction == null) ? "<null>" : this.direction));
        sb.append(',');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null) ? "<null>" : this.id));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null) ? "<null>" : this.name));
        sb.append(',');
        sb.append("threshold");
        sb.append('=');
        sb.append(((this.threshold == null) ? "<null>" : this.threshold));
        sb.append(',');
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
        result = ((result * 31) + ((this.name == null) ? 0 : this.name.hashCode()));
        result = ((result * 31) + ((this.threshold == null) ? 0 : this.threshold.hashCode()));
        result = ((result * 31) + ((this.id == null) ? 0 : this.id.hashCode()));
        result = ((result * 31) + ((this.direction == null) ? 0 : this.direction.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof MonitoringParameter)) {
            return false;
        }
        MonitoringParameter rhs = ((MonitoringParameter) other);
        return (((((this.name == rhs.name) || ((this.name != null) && this.name.equals(rhs.name)))
            && ((this.threshold == rhs.threshold) || ((this.threshold != null) && this.threshold.equals(rhs.threshold))))
            && ((this.id == rhs.id) || ((this.id != null) && this.id.equals(rhs.id))))
            && ((this.direction == rhs.direction) || ((this.direction != null) && this.direction.equals(rhs.direction))));
    }

}
