package it.nextworks.sdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;
import it.nextworks.sdk.enums.AggregatorFunc;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;


/**
 * AggregatedMonitoringParameter
 * <p>
 * 
 * 
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "aggregatorFunc",
    "parametersId"
})
public class AggregatedMonParam extends MonitoringParameter {

    private AggregatorFunc aggregatorFunc;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OrderColumn
    private List<String> parametersId = new ArrayList<>();

    public AggregatedMonParam(){
        //JPA only
    }

    @JsonProperty("aggregatorFunc")
    public AggregatorFunc getAggregatorFunc() {
        return aggregatorFunc;
    }

    @JsonProperty("aggregatorFunc")
    public void setAggregatorFunc(AggregatorFunc aggregatorFunc) {
        this.aggregatorFunc = aggregatorFunc;
    }

    @JsonProperty("parametersId")
    public List<String> getParametersId() {
        return parametersId;
    }

    @JsonProperty("parametersId")
    public void setParametersId(List<String> parametersId) {
        this.parametersId = parametersId;
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return super.isValid()
            && aggregatorFunc != null
            && parametersId != null
            && parametersId.size() > 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AggregatedMonParam.class.getName()).append('[');
        sb.append("aggregatorFunc");
        sb.append('=');
        sb.append(((this.aggregatorFunc == null)?"<null>":this.aggregatorFunc));
        sb.append(',');
        sb.append("parametersId");
        sb.append('=');
        sb.append(((this.parametersId == null)?"<null>":this.parametersId));
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
        result = ((result* 31)+((this.aggregatorFunc == null)? 0 :this.aggregatorFunc.hashCode()));
        result = ((result* 31)+((this.parametersId == null)? 0 :this.parametersId.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AggregatedMonParam) == false) {
            return false;
        }
        AggregatedMonParam rhs = ((AggregatedMonParam) other);
        return (((this.aggregatorFunc == rhs.aggregatorFunc)||((this.aggregatorFunc!= null)&&this.aggregatorFunc.equals(rhs.aggregatorFunc)))&&((this.parametersId == rhs.parametersId)||((this.parametersId!= null)&&this.parametersId.equals(rhs.parametersId))));
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void postLoad() {
        // Cleanup persistence artifacts and weird collection implementations
        parametersId = new ArrayList<>(parametersId);
    }

}
