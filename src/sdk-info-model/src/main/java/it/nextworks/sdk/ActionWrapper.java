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
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import java.util.*;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "actions",
    "actionsRule"
})
public class ActionWrapper {

    private Set<ModifiedServiceAction> actions = new HashSet<>();
    private Set<ModifiedServiceActionRule> actionRules = new HashSet<>();

    public ActionWrapper(Set<ServiceAction> actions, Set<ServiceActionRule> actionRules) {
        actions.forEach(x -> this.actions.add(new ModifiedServiceAction(x)));
        actionRules.forEach(x -> this.actionRules.add(new ModifiedServiceActionRule(x)));
    }

    @JsonProperty("actionRules")
    public Set<ModifiedServiceActionRule> getActionRules() {
        return actionRules;
    }

    @JsonProperty("actions")
    public Set<ModifiedServiceAction> getActions() {
        return actions;
    }

    public class ModifiedServiceAction {

        private String actionType;
        private String name;
        private String vnfdId;
        private String step;

        ModifiedServiceAction(ServiceAction a){
            this.actionType = a.getActionType().toString();
            this.name = a.getName();
            this.vnfdId = a.getComponentIndex();
            if(a.getActionType().equals(ServiceActionType.SCALE_IN))
                this.step = ((ScaleInAction)a).getStep().toString();
            else if(a.getActionType().equals(ServiceActionType.SCALE_OUT)){
                this.step = ((ScaleOutAction)a).getStep().toString();
            }
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("actionType")
        public String getActionType() {
            return actionType;
        }

        @JsonProperty("vnfdId")
        public String getVnfdId() {
            return vnfdId;
        }

        @JsonProperty("step")
        public String getStep() { return step; }

        public void setVnfdId(String vnfdId) { this.vnfdId = vnfdId; }
    }

    public class ModifiedServiceActionRule {

        private List<String> actionsId;
        private String name;
        private String duration;
        private String severity;
        private Set<ModifiedRuleCondition> conditions = new HashSet<>();
        private String operator;

        ModifiedServiceActionRule(ServiceActionRule ar) {
            this.actionsId = ar.getActionsId();
            this.name = ar.getName();
            this.duration = ar.getDuration();
            this.severity = ar.getSeverity();
            ar.getConditions().forEach(x -> this.conditions.add(new ModifiedRuleCondition(x)));
            this.operator = ar.getOperator().toString();
        }

        @JsonProperty("actionsName")
        public List<String> getActionsId() { return actionsId; }

        @JsonProperty("name")
        public String getName() { return name; }

        @JsonProperty("duration")
        public String getDuration() { return duration; }

        @JsonProperty("severity")
        public String getSeverity() { return severity; }

        @JsonProperty("conditions")
        public Set<ModifiedRuleCondition> getConditions() { return conditions; }

        @JsonProperty("operator")
        public String getOperator() { return operator; }

    }

    public class ModifiedRuleCondition {

        private String parameterId;
        private Double value;
        private String comparator;
        private String vnfdId;

        ModifiedRuleCondition(RuleCondition rc) {
            this.parameterId = rc.getParameterId();
            this.value = rc.getValue();
            switch(rc.getComparator()) {
                case G:
                    this.comparator = ">";
                    break;
                case GEQ:
                    this.comparator = ">=";
                    break;
                case L:
                    this.comparator = "<";
                    break;
                case LEQ:
                    this.comparator = "<=";
                    break;
                case EQ:
                default:
                    this.comparator = "==";
            }
        }

        @JsonProperty("parameterName")
        public String getParameterId() { return parameterId; }

        @JsonProperty("value")
        public Double getValue() { return value;}

        @JsonProperty("comparator")
        public String getComparator() { return comparator; }

        @JsonProperty("vnfdId")
        public String getVnfdId() { return vnfdId; }

        public void setVnfdId(String vnfdId) { this.vnfdId = vnfdId; }

        public void setParameterId(String parameterId) { this.parameterId = parameterId; }
    }
}