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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;

/**
 * RuleCondition
 * <p>
 * 
 * 
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "parameterId",
    "value",
    "comparator"
})
@Entity
public class RuleCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String parameterId;

    private Double value;

    private Comparator comparator;

    @ManyToOne
    private ServiceActionRule serviceActionRule;

    public RuleCondition(){
        //JPA only
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("parameterName")
    public String getParameterId() {
        return parameterId;
    }

    @JsonProperty("parameterName")
    public void setParameterId(String parameterId) {
        this.parameterId = parameterId;
    }

    @JsonProperty("value")
    public Double getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(Double value) {
        this.value = value;
    }

    @JsonProperty("comparator")
    public Comparator getComparator() {
        return comparator;
    }

    @JsonProperty("comparator")
    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    @JsonIgnore
    public ServiceActionRule getServiceActionRule() {
        return serviceActionRule;
    }

    @JsonIgnore
    public void setServiceActionRule(ServiceActionRule serviceActionRule) {
        this.serviceActionRule = serviceActionRule;
    }

    @Override
    public String toString() {
        return "RuleCondition{" +
            "id=" + id +
            ", parameterId='" + parameterId + '\'' +
            ", value=" + value +
            ", comparator='" + comparator + '\'' +
            ", serviceActionRule=" + serviceActionRule +
            '}';
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.comparator == null)? 0 :this.comparator.hashCode()));
        result = ((result* 31)+((this.parameterId == null)? 0 :this.parameterId.hashCode()));
        result = ((result* 31)+((this.value == null)? 0 :this.value.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RuleCondition) == false) {
            return false;
        }
        RuleCondition rhs = ((RuleCondition) other);
        return ((((this.comparator == rhs.comparator)||((this.comparator!= null)&&this.comparator.equals(rhs.comparator)))&&((this.parameterId == rhs.parameterId)||((this.parameterId!= null)&&this.parameterId.equals(rhs.parameterId))))&&((this.value == rhs.value)||((this.value!= null)&&this.value.equals(rhs.value))));
    }

    public enum Comparator {

        G("g"),
        GEQ("geq"),
        L("l"),
        LEQ("leq"),
        EQ("eq");
        private final String value;
        private final static Map<String, Comparator> CONSTANTS = new HashMap<String, Comparator>();

        static {
            for (Comparator c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Comparator(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static Comparator fromValue(String value) {
            Comparator constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    @JsonIgnore
    public boolean isValid() {
        return parameterId != null
            && value != null
            && comparator != null;
    }
}
