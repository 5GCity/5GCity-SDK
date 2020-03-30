/*
 * Copyright 2020 Nextworks s.r.l.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

        //TODO check if importedParameter id is present in db and if belong to the right component index
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
