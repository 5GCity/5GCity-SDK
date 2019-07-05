package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.Entity;


/**
 * ScaleOutAction
 * <p>
 * 
 * 
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "componentIndex",
    "step",
    "max"
})
public class ScaleOutAction extends ServiceAction{


    private String componentIndex;

    private Integer step = 1;

    private Integer max = -1;

    public ScaleOutAction(){
        //JPA only
    }

    @JsonProperty("componentIndex")
    public String getComponentIndex() {
        return componentIndex;
    }

    @JsonProperty("componentIndex")
    public void setComponentIndex(String componentIndex) {
        this.componentIndex = componentIndex;
    }

    @JsonProperty("step")
    public Integer getStep() {
        return step;
    }

    @JsonProperty("step")
    public void setStep(Integer step) {
        this.step = step;
    }

    @JsonProperty("max")
    public Integer getMax() {
        return max;
    }

    @JsonProperty("max")
    public void setMax(Integer max) {
        this.max = max;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ScaleOutAction.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("componentIndex");
        sb.append('=');
        sb.append(((this.componentIndex == null)?"<null>":this.componentIndex));
        sb.append(',');
        sb.append("step");
        sb.append('=');
        sb.append(((this.step == null)?"<null>":this.step));
        sb.append(',');
        sb.append("max");
        sb.append('=');
        sb.append(((this.max == null)?"<null>":this.max));
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
        result = ((result* 31)+((this.componentIndex == null)? 0 :this.componentIndex.hashCode()));
        result = ((result* 31)+((this.step == null)? 0 :this.step.hashCode()));
        result = ((result* 31)+((this.max == null)? 0 :this.max.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ScaleOutAction) == false) {
            return false;
        }
        ScaleOutAction rhs = ((ScaleOutAction) other);
        return super.equals(other) && ((((this.componentIndex == rhs.componentIndex)||((this.componentIndex!= null)&&this.componentIndex.equals(rhs.componentIndex)))&&((this.step == rhs.step)||((this.step!= null)&&this.step.equals(rhs.step))))&&((this.max == rhs.max)||((this.max!= null)&&this.max.equals(rhs.max))));
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return super.isValid()
            && componentIndex != null;
    }
}
