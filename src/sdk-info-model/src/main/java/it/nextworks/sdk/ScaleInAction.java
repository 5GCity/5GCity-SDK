package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.Entity;


/**
 * ScaleInAction
 * <p>
 * 
 * 
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "componentIndex",
    "step",
    "min"
})
public class ScaleInAction extends ServiceAction{

    private String componentIndex;

    private Integer step = 1;

    private Integer min = 0;

    public ScaleInAction(){
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

    @JsonProperty("min")
    public Integer getMin() {
        return min;
    }

    @JsonProperty("min")
    public void setMin(Integer min) {
        this.min = min;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ScaleInAction.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("componentIndex");
        sb.append('=');
        sb.append(((this.componentIndex == null)?"<null>":this.componentIndex));
        sb.append(',');
        sb.append("step");
        sb.append('=');
        sb.append(((this.step == null)?"<null>":this.step));
        sb.append(',');
        sb.append("min");
        sb.append('=');
        sb.append(((this.min == null)?"<null>":this.min));
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
        result = ((result* 31)+((this.min == null)? 0 :this.min.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ScaleInAction) == false) {
            return false;
        }
        ScaleInAction rhs = ((ScaleInAction) other);
        return super.equals(other) && ((((this.componentIndex == rhs.componentIndex)||((this.componentIndex!= null)&&this.componentIndex.equals(rhs.componentIndex)))&&((this.step == rhs.step)||((this.step!= null)&&this.step.equals(rhs.step))))&&((this.min == rhs.min)||((this.min!= null)&&this.min.equals(rhs.min))));
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return super.isValid()
            && componentIndex != null;
    }
}
