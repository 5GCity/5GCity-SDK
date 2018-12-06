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
    "type"
})
@Entity
public class ConnectionPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @JsonProperty("type")
    private ConnectionPointType type;

    @ManyToOne
    private SdkFunction sdkFunction;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<Integer> port = new HashSet<>();

    @JsonProperty("id")
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
    public ConnectionPointType getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(ConnectionPointType type) {
        this.type = type;
    }

    @JsonProperty("port")
    public Set<Integer> getPort() {
        return port;
    }

    @JsonIgnore
    public void setPort(Integer... port) {
        this.port = Arrays.stream(port).collect(Collectors.toSet());
    }

    @JsonProperty("port")
    public void setPort(Set<Integer> port) {
        this.port = port;
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
    public boolean isValid() {
        return this.type != null
            && this.name != null
            && this.name.length() >= 2;
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
        sb.append("port");
        sb.append('=');
        sb.append(((this.port == null) ? "<null>" : this.port));
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
        result = ((result * 31) + ((this.port == null) ? 0 : this.port.hashCode()));
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
            && ((this.port == rhs.port) || ((this.port != null) && this.port.equals(rhs.port)));
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void postLoad() {
        // Cleanup persistence artifacts and weird collection implementations
        port = new HashSet<>(port);
    }

}
