package it.nextworks.sdk;

import java.util.ArrayList;
import java.util.List;

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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "connectionPointId",
    "l3Rules"
})
@Entity
public class L3Connectivity {

    private Integer connectionPointId;

    @ElementCollection(fetch= FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<L3ConnectivityRule> l3Rules = new ArrayList<>();

    @JsonProperty("connectionPointId")
    public Integer getConnectionPointId() {
        return connectionPointId;
    }

    @JsonProperty("connectionPointId")
    public void setConnectionPointId(Integer connectionPointId) {
        this.connectionPointId = connectionPointId;
    }

    @JsonProperty("l3Rules")
    public List<L3ConnectivityRule> getL3Rules() {
        return l3Rules;
    }

    @JsonProperty("l3Rules")
    public void setL3Rules(List<L3ConnectivityRule> l3Rules) {
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
        sb.append(L3Connectivity.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("connectionPointId");
        sb.append('=');
        sb.append(((this.connectionPointId == null)?"<null>":this.connectionPointId));
        sb.append(',');
        sb.append("l3Rules");
        sb.append('=');
        sb.append(((this.l3Rules == null)?"<null>":this.l3Rules));
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
        result = ((result* 31)+((this.l3Rules == null)? 0 :this.l3Rules.hashCode()));
        result = ((result* 31)+((this.connectionPointId == null)? 0 :this.connectionPointId.hashCode()));
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
        return (((this.l3Rules == rhs.l3Rules)||((this.l3Rules!= null)&&this.l3Rules.equals(rhs.l3Rules)))&&((this.connectionPointId == rhs.connectionPointId)||((this.connectionPointId!= null)&&this.connectionPointId.equals(rhs.connectionPointId))));
    }

}
