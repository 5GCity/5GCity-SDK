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
import javax.persistence.ManyToOne;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "connectionPointId",
    "ports"
})
@Entity
public class RequiredPort {

    private Integer connectionPointId;

    @ElementCollection(fetch= FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Integer> ports = new ArrayList<>();

    @JsonProperty("connectionPointId")
    public Integer getConnectionPointId() {
        return connectionPointId;
    }

    @JsonProperty("connectionPointId")
    public void setConnectionPointId(Integer connectionPointId) {
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
    @ManyToOne
    private SdkFunction sdkFunction;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(RequiredPort.class.getName())
                .append('@')
                .append(Integer.toHexString(System.identityHashCode(this)))
                .append('[');
        sb.append("connectionPointId");
        sb.append('=');
        sb.append(((this.connectionPointId == null)?"<null>":this.connectionPointId));
        sb.append(',');
        sb.append("ports");
        sb.append('=');
        sb.append(((this.ports == null)?"<null>":this.ports));
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
        result = ((result* 31)+((this.ports == null)? 0 :this.ports.hashCode()));
        result = ((result* 31)+((this.connectionPointId == null)? 0 :this.connectionPointId.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RequiredPort)) {
            return false;
        }
        RequiredPort rhs = ((RequiredPort) other);
        return (((this.ports == rhs.ports)||((this.ports!= null)&&this.ports.equals(rhs.ports)))
                &&((this.connectionPointId == rhs.connectionPointId)||((this.connectionPointId!= null)&&this.connectionPointId.equals(rhs.connectionPointId))));
    }

    @JsonIgnore
    public boolean isValid() {
        return this.connectionPointId != null
                && this.ports != null
                && this.ports.size() > 0;
    }
}
