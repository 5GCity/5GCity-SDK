package it.nextworks.sdk;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.nextworks.sdk.enums.LinkType;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * Link
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "connection_point_ids",
    "id",
    "name",
    "type"
})
@Entity
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Integer> connectionPointIds = new ArrayList<>();

    private String name;

    private LinkType type;

    @JsonProperty("connection_point_ids")
    public List<Integer> getConnectionPointIds() {
        return connectionPointIds;
    }

    @JsonProperty("connection_point_ids")
    public void setConnectionPointIds(List<Integer> connectionPointIds) {
        this.connectionPointIds = connectionPointIds;
    }

    @JsonProperty("id")
    public Integer getId() {
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
    public boolean isValid() {
        return this.name != null && this.name.length() != 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Link.class.getName())
                .append('@')
                .append(Integer.toHexString(System.identityHashCode(this)))
                .append('[');
        sb.append("connectionPointIds");
        sb.append('=');
        sb.append(((this.connectionPointIds == null)?"<null>":this.connectionPointIds));
        sb.append(',');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("type");
        sb.append('=');
        sb.append(((this.type == null)?"<null>":this.type));
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
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.type == null)? 0 :this.type.hashCode()));
        result = ((result* 31)+((this.connectionPointIds == null)? 0 :this.connectionPointIds.hashCode()));
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
        return (((((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name)))
                &&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))
                &&((this.type == rhs.type)||((this.type!= null)&&this.type.equals(rhs.type))))
                &&((this.connectionPointIds == rhs.connectionPointIds)||((this.connectionPointIds!= null)&&this.connectionPointIds.equals(rhs.connectionPointIds))));
    }
}
