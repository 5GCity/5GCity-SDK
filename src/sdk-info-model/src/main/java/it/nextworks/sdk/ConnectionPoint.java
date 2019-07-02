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
    "cpType",
    "internalCpName",
    "internalCpId",
    "requiredPort",
    "componentIndex"
})
@Entity
public class ConnectionPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name; // Should be unique in the parent entity (function/service)

    private ConnectionPointType type;

    private Integer componentIndex;

    @ManyToOne
    private SdkFunction sdkFunction;

    @ManyToOne
    private SdkService sdkService;

    @ManyToOne
    private Link link;

    private Long internalCpId;

    private String internalCpName;

    private String internalLink;

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

    @JsonProperty("internalCpId")
    public Long getInternalCpId() {
        return internalCpId;
    }

    @JsonProperty("internalCpId")
    public void setInternalCpId(Long internalCpId) {
        this.internalCpId = internalCpId;
    }

    @JsonProperty("internalCpName")
    public String getInternalCpName() {
        return internalCpName;
    }

    @JsonProperty("internalCpName")
    public void setInternalCpName(String internalCpName) {
        this.internalCpName = internalCpName;
    }

    @JsonProperty("cpType")
    public ConnectionPointType getType() {
        return type;
    }

    @JsonProperty("cpType")
    public void setType(ConnectionPointType type) {
        this.type = type;
    }

    @JsonProperty("requiredPort")
    public Set<Integer> getRequiredPort() {
        return requiredPort;
    }

    @JsonIgnore
    public void setRequiredPort(Integer... requiredPort) {
        this.requiredPort = Arrays.stream(requiredPort).collect(Collectors.toSet());
    }

    @JsonProperty("requiredPort")
    public void setRequiredPort(Set<Integer> port) {
        this.requiredPort = port;
    }

    @JsonProperty("componentIndex")
    public Integer getComponentIndex() {
        return componentIndex;
    }

    @JsonProperty("componentIndex")
    public void setComponentIndex(Integer componentIndex) {
        this.componentIndex = componentIndex;
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
    public boolean isValid() {
        boolean validInternal = true;
        if(type == ConnectionPointType.INTERNAL)
            if (internalCpId == null || componentIndex == null)
                validInternal = false;

        return type != null
            && name != null
            && validInternal;
    }

    @JsonIgnore
    public String getInternalLink() {
        return internalLink;
    }

    @JsonIgnore
    public void setInternalLink(String internalLink) {
        this.internalLink = internalLink;
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
        sb.append("cpType");
        sb.append('=');
        sb.append(((this.type == null) ? "<null>" : this.type));
        sb.append(',');
        sb.append("componentIndex");
        sb.append('=');
        sb.append(((this.componentIndex == null) ? "<null>" : this.componentIndex));
        sb.append(',');
        sb.append("internalCpId");
        sb.append('=');
        sb.append(((this.internalCpId == null) ? "<null>" : this.internalCpId));
        sb.append(',');
        sb.append("internalCpName");
        sb.append('=');
        sb.append(((this.internalCpName == null) ? "<null>" : this.internalCpName));
        sb.append(',');
        sb.append("requiredPort");
        sb.append('=');
        sb.append(((this.requiredPort == null) ? "<null>" : this.requiredPort));
        sb.append(',');
        sb.append("internalLink");
        sb.append('=');
        sb.append(((this.internalLink == null) ? "<null>" : this.internalLink));
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
        result = ((result * 31) + ((this.componentIndex == null) ? 0 : this.componentIndex.hashCode()));
        result = ((result * 31) + ((this.internalCpName == null) ? 0 : this.internalCpName.hashCode()));
        result = ((result * 31) + ((this.internalCpId == null) ? 0 : this.internalCpId.hashCode()));
        result = ((result * 31) + ((this.requiredPort == null) ? 0 : this.requiredPort.hashCode()));
        result = ((result * 31) + ((this.internalLink == null) ? 0 : this.internalLink.hashCode()));
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
            && ((this.type == rhs.type) || ((this.type != null) && this.type.equals(rhs.type)))
            && ((this.componentIndex == rhs.componentIndex) || ((this.componentIndex != null) && this.componentIndex.equals(rhs.componentIndex)))
            && ((this.internalCpName == rhs.internalCpName) || ((this.internalCpName != null) && this.internalCpName.equals(rhs.internalCpName)))
            && ((this.internalCpId == rhs.internalCpId) || ((this.internalCpId != null) && this.internalCpId.equals(rhs.internalCpId)))
            && ((this.internalLink == rhs.internalLink) || ((this.internalLink != null) && this.internalLink.equals(rhs.internalLink)))
            && ((this.requiredPort == rhs.requiredPort) || ((this.requiredPort != null) && this.requiredPort.equals(rhs.requiredPort))));
    }
}
