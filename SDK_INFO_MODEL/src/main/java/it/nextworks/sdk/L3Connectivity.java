package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import java.util.HashSet;
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

    private Long connectionPointId;

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
    public Long getConnectionPointId() {
        return connectionPointId;
    }

    @JsonProperty("connectionPointId")
    public void setConnectionPointId(Long connectionPointId) {
        this.connectionPointId = connectionPointId;
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
    public boolean isValid() {
        return connectionPointId != null
            && l3Rules != null && l3Rules.size() > 0
            && l3Rules.stream().allMatch(L3ConnectivityRule::isValid);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(L3Connectivity.class.getName()).append('[');
        sb.append("connectionPointId");
        sb.append('=');
        sb.append(((this.connectionPointId == null) ? "<null>" : this.connectionPointId));
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
        result = ((result * 31) + ((this.connectionPointId == null) ? 0 : this.connectionPointId.hashCode()));
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
        return (((this.l3Rules == rhs.l3Rules) || ((this.l3Rules != null) && this.l3Rules.equals(rhs.l3Rules))) && ((this.connectionPointId == rhs.connectionPointId) || ((this.connectionPointId != null) && this.connectionPointId.equals(rhs.connectionPointId))));
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void fixPersistence() {
        l3Rules = new HashSet<>(l3Rules);
    }
}
