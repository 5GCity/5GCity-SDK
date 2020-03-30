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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.*;
import it.nextworks.sdk.enums.AggregatorFunc;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;


/**
 * AggregatedMonitoringParameter
 * <p>
 * 
 * 
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "aggregatorFunc",
    "parametersId"
})
public class MonParamAggregated extends MonitoringParameter {

    private AggregatorFunc aggregatorFunc;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OrderColumn
    private List<String> parametersId = new ArrayList<>();

    public MonParamAggregated(){
        //JPA only
    }

    @JsonProperty("aggregatorFunc")
    public AggregatorFunc getAggregatorFunc() {
        return aggregatorFunc;
    }

    @JsonProperty("aggregatorFunc")
    public void setAggregatorFunc(AggregatorFunc aggregatorFunc) {
        this.aggregatorFunc = aggregatorFunc;
    }

    @JsonProperty("parametersName")
    public List<String> getParametersId() {
        return parametersId;
    }

    @JsonProperty("parametersName")
    public void setParametersId(List<String> parametersId) {
        this.parametersId = parametersId;
    }

    @JsonIgnore
    @Override
    public boolean isValid() {
        return super.isValid()
            && aggregatorFunc != null
            && parametersId != null
            && parametersId.size() > 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MonParamAggregated.class.getName()).append('[');
        sb.append("aggregatorFunc");
        sb.append('=');
        sb.append(((this.aggregatorFunc == null)?"<null>":this.aggregatorFunc));
        sb.append(',');
        sb.append("parametersId");
        sb.append('=');
        sb.append(((this.parametersId == null)?"<null>":this.parametersId));
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
        result = ((result* 31)+((this.aggregatorFunc == null)? 0 :this.aggregatorFunc.hashCode()));
        result = ((result* 31)+((this.parametersId == null)? 0 :this.parametersId.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MonParamAggregated) == false) {
            return false;
        }
        MonParamAggregated rhs = ((MonParamAggregated) other);
        return super.equals(other) && (((this.aggregatorFunc == rhs.aggregatorFunc)||((this.aggregatorFunc!= null)&&this.aggregatorFunc.equals(rhs.aggregatorFunc)))&&((this.parametersId == rhs.parametersId)||((this.parametersId!= null)&&this.parametersId.equals(rhs.parametersId))));
    }
}
