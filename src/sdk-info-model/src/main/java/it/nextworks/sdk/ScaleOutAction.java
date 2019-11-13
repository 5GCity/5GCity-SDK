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
    "step"
})
public class ScaleOutAction extends ServiceAction{

    private Integer step = 1;

    public ScaleOutAction(){
        //JPA only
    }

    @JsonProperty("step")
    public Integer getStep() {
        return step;
    }

    @JsonProperty("step")
    public void setStep(Integer step) {
        this.step = step;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ScaleOutAction.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("step");
        sb.append('=');
        sb.append(((this.step == null)?"<null>":this.step));
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
        result = ((result* 31)+((this.step == null)? 0 :this.step.hashCode()));
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
        return super.equals(other) &&((this.step == rhs.step)||((this.step!= null)&&this.step.equals(rhs.step)));
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return super.isValid();
    }
}
