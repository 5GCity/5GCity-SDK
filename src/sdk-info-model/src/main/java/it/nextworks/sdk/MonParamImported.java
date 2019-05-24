package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.*;


/**
 * ImportedMonitoringParameter
 * <p>
 * 
 * 
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "componentIndex",
    "importedParameterId"
})
public class MonParamImported extends MonitoringParameter {

    private Integer componentIndex;

    private String importedParameterId;

    public MonParamImported(){
        //JPA only
    }

    @JsonProperty("componentIndex")
    public Integer getComponentIndex() {
        return componentIndex;
    }

    @JsonProperty("componentIndex")
    public void setComponentIndex(Integer componentIndex) {
        this.componentIndex = componentIndex;
    }

    @JsonProperty("importedParameterId")
    public String getImportedParameterId() {
        return importedParameterId;
    }

    @JsonProperty("importedParameterId")
    public void setImportedParameterId(String importedParameterId) {
        this.importedParameterId = importedParameterId;
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return super.isValid()
            && componentIndex != null
            && importedParameterId != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MonParamImported.class.getName()).append('[');
        sb.append("componentIndex");
        sb.append('=');
        sb.append(((this.componentIndex == null)?"<null>":this.componentIndex));
        sb.append(',');
        sb.append("parameterId");
        sb.append('=');
        sb.append(((this.importedParameterId == null)?"<null>":this.importedParameterId));
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
        result = ((result* 31)+((this.importedParameterId == null)? 0 :this.importedParameterId.hashCode()));
        result = ((result* 31)+((this.componentIndex == null)? 0 :this.componentIndex.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MonParamImported) == false) {
            return false;
        }
        MonParamImported rhs = ((MonParamImported) other);
        return super.equals(other) && ((this.importedParameterId == rhs.importedParameterId)||((this.importedParameterId != null)&&this.importedParameterId.equals(rhs.importedParameterId)))&&((this.componentIndex == rhs.componentIndex)||((this.componentIndex!= null)&&this.componentIndex.equals(rhs.componentIndex)));
    }

}
