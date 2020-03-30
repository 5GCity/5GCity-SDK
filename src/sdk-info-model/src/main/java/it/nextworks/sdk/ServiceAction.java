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

import com.fasterxml.jackson.annotation.*;
import it.nextworks.sdk.enums.ServiceActionType;

import javax.persistence.*;
import java.util.Set;


/**
 * BaseServiceAction
 * <p>
 * 
 * 
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "actionType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ScaleInAction.class, 	name = "SCALE_IN"),
    @JsonSubTypes.Type(value = ScaleOutAction.class, 	name = "SCALE_OUT"),
    @JsonSubTypes.Type(value = ComponentAction.class, 	name = "COMPONENT"),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "componentIndex",
    "actionId",
    "actionType"
})
@Entity
public abstract  class ServiceAction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private ServiceActionType actionType;

    private String name;

    private String componentIndex;

    @ManyToOne
    private SdkService sdkService;

    public ServiceAction(){
        //JPA only
    }

    @JsonProperty("id")
    public String getActionId() {
        return id.toString();
    }

    @JsonProperty("id")
    public void setActionId(String actionId) {
        this.id = Long.valueOf(actionId);
    }

    @JsonIgnore
    public Long getId() { return id; }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("actionType")
    public ServiceActionType getActionType() {
        return actionType;
    }

    @JsonProperty("actionType")
    public void setActionType(ServiceActionType actionType) {
        this.actionType = actionType;
    }

    @JsonProperty("componentIndex")
    public String getComponentIndex() {
        return componentIndex;
    }

    @JsonProperty("componentIndex")
    public void setComponentIndex(String componentIndex) {
        this.componentIndex = componentIndex;
    }

    @JsonIgnore
    public SdkService getSdkService() {
        return sdkService;
    }

    @JsonIgnore
    public void setSdkService(SdkService sdkService) {
        this.sdkService = sdkService;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ServiceAction.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("actionId");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("actionType");
        sb.append('=');
        sb.append(((this.actionType == null)?"<null>":this.actionType));
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
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.actionType == null)? 0 :this.actionType.hashCode()));
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.componentIndex == null)? 0 :this.componentIndex.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ServiceAction) == false) {
            return false;
        }
        ServiceAction rhs = ((ServiceAction) other);
        return (((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id)))
            &&((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name)))
            &&((this.actionType == rhs.actionType)||((this.actionType!= null)&&this.actionType.equals(rhs.actionType)))
            &&((this.componentIndex == rhs.componentIndex)||((this.componentIndex!= null)&&this.componentIndex.equals(rhs.componentIndex))));
    }

    @JsonIgnore
    public boolean isValid() {
        return actionType != null
            && name != null && name.length() > 0
            && componentIndex != null;
    }
}
