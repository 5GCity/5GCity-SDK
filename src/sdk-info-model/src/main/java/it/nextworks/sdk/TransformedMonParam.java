package it.nextworks.sdk;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.*;
import it.nextworks.sdk.enums.Transform;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;


/**
 * TransformedMonitoringParameter
 * <p>
 * 
 * 
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "transform",
    "argumentList",
    "targetParameterId"
})
public class TransformedMonParam extends MonitoringParameter {

    private Transform transform;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OrderColumn
    private List<Double> argumentList = new ArrayList<Double>();

    private String targetParameterId;

    public TransformedMonParam(){
        //JPA only
    }

    @JsonProperty("transform")
    public Transform getTransform() {
        return transform;
    }

    @JsonProperty("transform")
    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    @JsonProperty("argumentList")
    public List<Double> getArgumentList() {
        return argumentList;
    }

    @JsonProperty("argumentList")
    public void setArgumentList(List<Double> argumentList) {
        this.argumentList = argumentList;
    }

    @JsonProperty("targetParameterName")
    public String getTargetParameterId() {
        return targetParameterId;
    }

    @JsonProperty("targetParameterName")
    public void setTargetParameterId(String targetParameterId) {
        this.targetParameterId = targetParameterId;
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return super.isValid()
            && transform != null
            && argumentList != null
            && targetParameterId != null
            && targetParameterId.length() > 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TransformedMonParam.class.getName()).append('[');
        sb.append("transform");
        sb.append('=');
        sb.append(((this.transform == null)?"<null>":this.transform));
        sb.append(',');
        sb.append("argumentList");
        sb.append('=');
        sb.append(((this.argumentList == null)?"<null>":this.argumentList));
        sb.append(',');
        sb.append("targedParameterId");
        sb.append('=');
        sb.append(((this.targetParameterId == null)?"<null>":this.targetParameterId));
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
        result = ((result* 31)+((this.argumentList == null)? 0 :this.argumentList.hashCode()));
        result = ((result* 31)+((this.transform == null)? 0 :this.transform.hashCode()));
        result = ((result* 31)+((this.targetParameterId == null)? 0 :this.targetParameterId.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof TransformedMonParam) == false) {
            return false;
        }
        TransformedMonParam rhs = ((TransformedMonParam) other);
        return super.equals(other) && ((((this.argumentList == rhs.argumentList)||((this.argumentList!= null)&&this.argumentList.equals(rhs.argumentList)))&&((this.transform == rhs.transform)||((this.transform!= null)&&this.transform.equals(rhs.transform))))&&((this.targetParameterId == rhs.targetParameterId)||((this.targetParameterId != null)&&this.targetParameterId.equals(rhs.targetParameterId))));
    }
}
