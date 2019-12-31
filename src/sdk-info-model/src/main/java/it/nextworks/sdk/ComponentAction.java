package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.Entity;


/**
 * ComponentAction
 * <p>
 * 
 * 
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "componentIndex",
    "componentActionId"
})
public class ComponentAction extends ServiceAction{

    private String componentActionId;

    public ComponentAction(){
        //JPA only
    }

    @JsonProperty("componentActionId")
    public String getComponentActionId() {
        return componentActionId;
    }

    @JsonProperty("componentActionId")
    public void setComponentActionId(String componentActionId) {
        this.componentActionId = componentActionId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ComponentAction.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("componentActionId");
        sb.append('=');
        sb.append(((this.componentActionId == null)?"<null>":this.componentActionId));
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
        result = ((result* 31)+((this.componentActionId == null)? 0 :this.componentActionId.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ComponentAction) == false) {
            return false;
        }
        ComponentAction rhs = ((ComponentAction) other);
        return super.equals(other) &&((this.componentActionId == rhs.componentActionId)||((this.componentActionId!= null)&&this.componentActionId.equals(rhs.componentActionId)));
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return super.isValid()
            && componentActionId != null;
    }

}
