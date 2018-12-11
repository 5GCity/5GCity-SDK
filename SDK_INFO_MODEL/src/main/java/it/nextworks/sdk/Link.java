package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.nextworks.sdk.enums.ConnectionPointType;
import it.nextworks.sdk.enums.LinkType;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Link
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "type",
    "connection_point_ids"
})
@Entity
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<Long> connectionPointIds = new HashSet<>();

    private String name;

    private LinkType type;

    @ManyToOne
    private SdkService service;

    @JsonProperty("connection_point_ids")
    public Set<Long> getConnectionPointIds() {
        return connectionPointIds;
    }

    @JsonIgnore
    public void setConnectionPointIds(Long... cps) {
        setConnectionPointIds(Arrays.stream(cps).collect(Collectors.toSet()));
    }

    @JsonProperty("connection_point_ids")
    public void setConnectionPointIds(Set<Long> connectionPointIds) {
        this.connectionPointIds = connectionPointIds;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("type")
    public LinkType getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(LinkType type) {
        this.type = type;
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
    public boolean isValid() {
        return this.name != null && this.name.length() != 0
            && connectionPointIds != null
            && !(connectionPointIds.isEmpty());
    }

    private boolean validateCpType(ConnectionPointType cpType) {
        switch (type) {
            case INTERNAL:
                return cpType.equals(ConnectionPointType.INTERNAL);
            case EXTERNAL:
                return cpType.equals(ConnectionPointType.EXTERNAL);
            default:
                throw new IllegalArgumentException(String.format(
                    "Unexpected connection link type %s",
                    type.toString()
                ));
        }
    }

    boolean validateAgainstCpMap(Map<Long, ConnectionPoint> cpMap) {
        return connectionPointIds.stream().allMatch(
            cpId -> cpMap.containsKey(cpId)
                && validateCpType(cpMap.get(cpId).getType())
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Link.class.getName())
            .append('[');
        sb.append("connectionPointIds");
        sb.append('=');
        sb.append(((this.connectionPointIds == null) ? "<null>" : this.connectionPointIds));
        sb.append(',');
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
        result = ((result * 31) + ((this.connectionPointIds == null) ? 0 : this.connectionPointIds.hashCode()));
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
        return (((((this.name == rhs.name) || ((this.name != null) && this.name.equals(rhs.name)))
            && ((this.id == rhs.id) || ((this.id != null) && this.id.equals(rhs.id))))
            && ((this.type == rhs.type) || ((this.type != null) && this.type.equals(rhs.type))))
            && ((this.connectionPointIds == rhs.connectionPointIds) || ((this.connectionPointIds != null) && this.connectionPointIds.equals(rhs.connectionPointIds))));
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void fixPersistence() {
        connectionPointIds = new HashSet<>(connectionPointIds);
    }
}
