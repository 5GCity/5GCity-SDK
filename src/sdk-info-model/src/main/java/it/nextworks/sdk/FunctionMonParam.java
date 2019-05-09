package it.nextworks.sdk;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;
import it.nextworks.sdk.enums.MetricType;

import javax.persistence.*;


/**
 * FunctionMonitoringParameter
 * <p>
 * 
 * 
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "metricName",
    "metricType"
})
public class FunctionMonParam extends MonitoringParameter {

    private String metricName;

    private MetricType metricType;

    public FunctionMonParam(){
        //JPA only
    }

    @JsonProperty("metricName")
    public String getMetricName() {
        return metricName;
    }

    @JsonProperty("metricName")
    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    @JsonProperty("metricType")
    public MetricType getMetricType() {
        return metricType;
    }

    @JsonProperty("metricType")
    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return super.isValid()
            && metricName != null
            && metricType != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(FunctionMonParam.class.getName()).append('[');
        sb.append("metricName");
        sb.append('=');
        sb.append(((this.metricName == null)?"<null>":this.metricName));
        sb.append(',');
        sb.append("metricType");
        sb.append('=');
        sb.append(((this.metricType == null)?"<null>":this.metricType));
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
        result = super.hashCode();
        result = ((result* 31)+((this.metricType == null)? 0 :this.metricType.hashCode()));
        result = ((result* 31)+((this.metricName == null)? 0 :this.metricName.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof FunctionMonParam) == false) {
            return false;
        }
        FunctionMonParam rhs = ((FunctionMonParam) other);
        return super.equals(other) && (((this.metricType == rhs.metricType)||((this.metricType!= null)&&this.metricType.equals(rhs.metricType)))&&((this.metricName == rhs.metricName)||((this.metricName!= null)&&this.metricName.equals(rhs.metricName))));
    }
}
