package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.nextworks.sdk.enums.ConnectionPointType;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * ConnectionPoint
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "type",
    "required_port"
})
@Entity
public class ConnectionPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name; // Should be unique in the parent entity (function/service)

    @JsonProperty("type")
    private ConnectionPointType type;

    @ManyToOne
    private SdkFunction sdkFunction;

    @ManyToOne
    private SdkService sdkService;

    @ManyToOne
    private Link link;

    @ManyToOne
    private L3Connectivity l3Connectivity;

    private Long internalCpId;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<Integer> requiredPort = new HashSet<>();

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    void setId(Long id) {
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

    @JsonProperty("internal_cp_id")
    public Long getInternalCpId() {
        return internalCpId;
    }

    @JsonProperty("internal_cp_id")
    public void setInternalCpId(Long internalCpId) {
        this.internalCpId = internalCpId;
    }

    @JsonProperty("type")
    public ConnectionPointType getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(ConnectionPointType type) {
        this.type = type;
    }

    @JsonProperty("required_port")
    public Set<Integer> getRequiredPort() {
        return requiredPort;
    }

    @JsonIgnore
    public void setRequiredPort(Integer... requiredPort) {
        this.requiredPort = Arrays.stream(requiredPort).collect(Collectors.toSet());
    }

    @JsonProperty("required_port")
    public void setRequiredPort(Set<Integer> port) {
        this.requiredPort = port;
    }

    @JsonIgnore
    public SdkFunction getSdkFunction() {
        return sdkFunction;
    }

    @JsonIgnore
    public void setSdkFunction(SdkFunction sdkFunction) {
        this.sdkFunction = sdkFunction;
    }

    @JsonIgnore
    public SdkService getSdkService() {
        return sdkService;
    }

    @JsonIgnore
    public void setSdkService(SdkService sdkService) {
        this.sdkService = sdkService;
    }

    @JsonIgnore
    public Link getLink() {
        return link;
    }

    @JsonIgnore
    public void setLink(Link link) {
        this.link = link;
    }

    @JsonIgnore
    public L3Connectivity getL3Connectivity() {
        return l3Connectivity;
    }

    @JsonIgnore
    public void setL3Connectivity(L3Connectivity l3Connectivity) {
        this.l3Connectivity = l3Connectivity;
    }

    @JsonIgnore
    public boolean isValid() {
        return type != null
            && name != null
            && requiredPort != null
            // internal -> intCpId == null
            && (!type.equals(ConnectionPointType.INTERNAL) || internalCpId == null)
            // external -> intCpId != null
            && (!type.equals(ConnectionPointType.EXTERNAL) || internalCpId != null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConnectionPoint.class.getName())
            .append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null) ? "<null>" : this.id));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null) ? "<null>" : this.name));
        sb.append(',');
        sb.append("type");
        sb.append('=');
        sb.append(((this.type == null) ? "<null>" : this.type));
        sb.append(',');
        sb.append("required_port");
        sb.append('=');
        sb.append(((this.requiredPort == null) ? "<null>" : this.requiredPort));
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
        result = ((result * 31) + ((this.type == null) ? 0 : this.type.hashCode()));
        result = ((result * 31) + ((this.requiredPort == null) ? 0 : this.requiredPort.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ConnectionPoint)) {
            return false;
        }
        ConnectionPoint rhs = ((ConnectionPoint) other);
        return (((this.name == rhs.name) || ((this.name != null) && this.name.equals(rhs.name)))
            && ((this.id == rhs.id) || ((this.id != null) && this.id.equals(rhs.id)))
            && ((this.type == rhs.type) || ((this.type != null) && this.type.equals(rhs.type))))
            && ((this.requiredPort == rhs.requiredPort) || ((this.requiredPort != null) && this.requiredPort.equals(rhs.requiredPort)));
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void postLoad() {
        // Cleanup persistence artifacts and weird collection implementations
        requiredPort = new HashSet<>(requiredPort);
    }

}
