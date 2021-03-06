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
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Required Ports
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "connectionPointId",
    "ports"
})
@Entity
public class RequiredPort {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String connectionPointId;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Integer> ports = new ArrayList<>();

    @ManyToOne
    private SdkFunction function;

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("connectionPointName")
    public String getConnectionPointId() {
        return connectionPointId;
    }

    @JsonProperty("connectionPointName")
    public void setConnectionPointId(String connectionPointId) {
        this.connectionPointId = connectionPointId;
    }

    @JsonProperty("ports")
    public List<Integer> getPorts() {
        return ports;
    }

    @JsonProperty("ports")
    public void setPorts(List<Integer> ports) {
        this.ports = ports;
    }

    @JsonIgnore
    public SdkFunction getFunction() {
        return function;
    }

    @JsonIgnore
    public void setFunction(SdkFunction function) {
        this.function = function;
    }

    @JsonIgnore
    public boolean isValid() {
        return this.connectionPointId != null
            && connectionPointId.length() > 0
            && ports != null && !(ports.isEmpty());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(RequiredPort.class.getName())
            .append('[');
        sb.append("connectionPointId");
        sb.append('=');
        sb.append(((this.connectionPointId == null) ? "<null>" : this.connectionPointId));
        sb.append(',');
        sb.append("ports");
        sb.append('=');
        sb.append(((this.ports == null) ? "<null>" : this.ports));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result * 31) + ((this.connectionPointId == null) ? 0 : this.connectionPointId.hashCode()));
        result = ((result * 31) + ((this.ports == null) ? 0 : this.ports.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RequiredPort) == false) {
            return false;
        }
        RequiredPort rhs = ((RequiredPort) other);
        return (((this.connectionPointId == rhs.connectionPointId)||((this.connectionPointId!= null)&&this.connectionPointId.equals(rhs.connectionPointId)))
            &&((this.ports == rhs.ports)||((this.ports!= null)&&this.ports.equals(rhs.ports))));
    }
}
