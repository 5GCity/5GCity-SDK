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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Link
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "connectionPointNames"
})
@Entity
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<String> connectionPointNames = new HashSet<>();

    @Transient
    private Set<ConnectionPoint> connectionPoints = new HashSet<>();

    private String name;

    @ManyToOne
    private SdkService service;

    @JsonProperty("connectionPointNames")
    public Set<String> getConnectionPointNames() {
        return connectionPointNames;
    }

    @JsonIgnore
    public void setConnectionPointNames(String... cps) {
        setConnectionPointNames(Arrays.stream(cps).collect(Collectors.toSet()));
    }

    @JsonProperty("connectionPointNames")
    public void setConnectionPointNames(Set<String> connectionPointIds) {
        this.connectionPointNames = connectionPointIds;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public SdkService getService() {
        return service;
    }

    @JsonIgnore
    public void setService(SdkService service) {
        this.service = service;
    }

    @JsonIgnore
    public Set<ConnectionPoint> getConnectionPoints() {
        return connectionPoints;
    }

    @JsonIgnore
    public void setConnectionPoints(Set<ConnectionPoint> connectionPoints) {
        Objects.requireNonNull(
            connectionPoints,
            "Invalid connection points: null"
        );
        Set<String> names = connectionPoints.stream().map(ConnectionPoint::getName).collect(Collectors.toSet());
        if (names.size() != connectionPoints.size()) {
            throw new IllegalArgumentException(
                "Invalid connection points: duplicates present"
            );
        }
        if (!names.containsAll(connectionPointNames)) {
            throw new IllegalArgumentException(String.format(
                "Invalid connection points: ids missing. Expected: %s; got: %s",
                connectionPointNames,
                names
            ));
        }
        HashSet<ConnectionPoint> linkCPs = new HashSet<>(connectionPoints);
        linkCPs.removeIf(cp -> !connectionPointNames.contains(cp.getName()));
        this.connectionPoints = linkCPs;
        for(ConnectionPoint cp : this.connectionPoints){
            cp.setLink(this);
        }
    }


    @JsonIgnore
    public boolean isValid() {
        return this.name != null && this.name.length() != 0
            && connectionPointNames != null
            && !(connectionPointNames.isEmpty());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Link.class.getName())
            .append('[');
        sb.append("connectionPointIds");
        sb.append('=');
        sb.append(((this.connectionPointNames == null) ? "<null>" : this.connectionPointNames));
        sb.append(',');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null) ? "<null>" : this.id));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null) ? "<null>" : this.name));
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
        result = ((result * 31) + ((this.name == null) ? 0 : this.name.hashCode()));
        result = ((result * 31) + ((this.id == null) ? 0 : this.id.hashCode()));
        result = ((result * 31) + ((this.connectionPointNames == null) ? 0 : this.connectionPointNames.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Link)) {
            return false;
        }
        Link rhs = ((Link) other);
        return Objects.equals(this.name, rhs.name)
            && Objects.equals(this.id, rhs.id)
            && Objects.equals(this.connectionPointNames, rhs.connectionPointNames);
    }
}
