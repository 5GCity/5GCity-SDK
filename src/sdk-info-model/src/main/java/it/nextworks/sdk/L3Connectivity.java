package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "connectionPointId",
    "l3Rules"
})
@Entity
public class L3Connectivity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String connectionPointName;

    @Transient
    private ConnectionPoint connectionPoint;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<L3ConnectivityRule> l3Rules = new HashSet<>();

    @ManyToOne
    private SdkService service;

    @JsonIgnore
    public SdkService getService() {
        return service;
    }

    @JsonIgnore
    public void setService(SdkService service) {
        this.service = service;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @JsonProperty("connectionPointId")
    public String getConnectionPointName() {
        return connectionPointName;
    }

    @JsonProperty("connectionPointId")
    public void setConnectionPointName(String connectionPointName) {
        this.connectionPointName = connectionPointName;
    }

    @JsonProperty("l3Rules")
    public Set<L3ConnectivityRule> getL3Rules() {
        return l3Rules;
    }

    @JsonProperty("l3Rules")
    public void setL3Rules(Set<L3ConnectivityRule> l3Rules) {
        this.l3Rules = l3Rules;
    }

    @JsonIgnore
    public ConnectionPoint getConnectionPoint() {
        return connectionPoint;
    }

    @JsonIgnore
    public void setConnectionPoint(ConnectionPoint connectionPoint) {
        Objects.requireNonNull(connectionPoint, "Invalid connection point: null");
        if (!connectionPointName.equals(connectionPoint.getName())) {
            throw new IllegalArgumentException(String.format(
                "Invalid connection point, not matching: expected %s, got: %s",
                connectionPointName,
                connectionPoint.getId()
            ));
        }
        this.connectionPoint = connectionPoint;
    }

    @JsonIgnore
    public boolean isValid() {
        return connectionPointName != null
            && l3Rules != null && l3Rules.size() > 0
            && l3Rules.stream().allMatch(L3ConnectivityRule::isValid);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(L3Connectivity.class.getName()).append('[');
        sb.append("connectionPointId");
        sb.append('=');
        sb.append(((this.connectionPointName == null) ? "<null>" : this.connectionPointName));
        sb.append(',');
        sb.append("l3Rules");
        sb.append('=');
        sb.append(((this.l3Rules == null) ? "<null>" : this.l3Rules));
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
        result = ((result * 31) + ((this.l3Rules == null) ? 0 : this.l3Rules.hashCode()));
        result = ((result * 31) + ((this.connectionPointName == null) ? 0 : this.connectionPointName.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof L3Connectivity)) {
            return false;
        }
        L3Connectivity rhs = ((L3Connectivity) other);
        return (((this.l3Rules == rhs.l3Rules) || ((this.l3Rules != null) && this.l3Rules.equals(rhs.l3Rules)))
            && ((this.connectionPointName == rhs.connectionPointName) || ((this.connectionPointName != null) && this.connectionPointName.equals(rhs.connectionPointName))));
    }

    private boolean isResolved() {
        return connectionPoint != null;
    }

    @PrePersist
    @PreUpdate
    private void prePersist() {
        if (!isResolved()) {
            throw new IllegalStateException(String.format(
                "Cannot persist: l3 connectivity %s not resolved",
                getId()
            ));
        }
        connectionPoint.setL3Connectivity(this);
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void fixPersistence() {
        l3Rules = new HashSet<>(l3Rules);
    }
}
