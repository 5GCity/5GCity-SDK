package it.nextworks.sdk;
import com.fasterxml.jackson.annotation.*;

import java.util.HashSet;
import java.util.Set;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "extMonitoringParameters",
    "intMonitoringParameters"
})
public class MonitoringParameterWrapper {

    private Set<MonitoringParameter> extMonitoringParameters = new HashSet<>();

    private Set<MonitoringParameter> intMonitoringParameters = new HashSet<>();

    public MonitoringParameterWrapper(){

    }

    public MonitoringParameterWrapper (Set<MonitoringParameter> extMonitoringParameters, Set<MonitoringParameter> intMonitorimgParameters){
        this.extMonitoringParameters = extMonitoringParameters;
        this.intMonitoringParameters = intMonitorimgParameters;
    }

    @JsonProperty("extMonitoringParameters")
    public Set<MonitoringParameter> getExtMonitoringParameters() {
        return extMonitoringParameters;
    }

    @JsonProperty("extMonitoringParameters")
    public void setExtMonitoringParameters(Set<MonitoringParameter> extMonitoringParameters) {
        this.extMonitoringParameters = extMonitoringParameters;
    }

    @JsonProperty("intMonitoringParameters")
    public Set<MonitoringParameter> getIntMonitoringParameters() {
        return intMonitoringParameters;
    }

    @JsonProperty("intMonitoringParameters")
    public void setIntMonitoringParameters(Set<MonitoringParameter> intMonitoringParameters) {
        this.intMonitoringParameters = intMonitoringParameters;
    }

    @JsonIgnore
    public boolean isValid() {
        return validateMonitoringParameters(extMonitoringParameters)
            && validateMonitoringParameters(intMonitoringParameters);
    }

    private boolean validateMonitoringParameters(Set<MonitoringParameter> param){
        if(param != null)
            return param.stream().allMatch(MonitoringParameter::isValid);
        else
            return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MonitoringParameterWrapper.class.getName()).append('[');
        sb.append("extMonitoringParameters");
        sb.append('=');
        sb.append(((this.extMonitoringParameters == null)?"<null>":this.extMonitoringParameters));
        sb.append(',');
        sb.append("intMonitoringParameters");
        sb.append('=');
        sb.append(((this.intMonitoringParameters == null)?"<null>":this.intMonitoringParameters));
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
        result = ((result* 31)+((this.extMonitoringParameters == null)? 0 :this.extMonitoringParameters.hashCode()));
        result = ((result* 31)+((this.intMonitoringParameters == null)? 0 :this.intMonitoringParameters.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MonitoringParameterWrapper) == false) {
            return false;
        }
        MonitoringParameterWrapper rhs = ((MonitoringParameterWrapper) other);
        return (((this.extMonitoringParameters == rhs.extMonitoringParameters)||((this.extMonitoringParameters != null)&&this.extMonitoringParameters.equals(rhs.extMonitoringParameters)))&&((this.intMonitoringParameters == rhs.intMonitoringParameters)||((this.intMonitoringParameters != null)&&this.intMonitoringParameters.equals(rhs.intMonitoringParameters))));
    }
}
