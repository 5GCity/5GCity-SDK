package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.nextworks.sdk.enums.LicenseType;

import javax.persistence.Embeddable;


/**
 * License
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "type",
    "url"
})
@Embeddable
public class License {

    private LicenseType type;

    private String url;

    @JsonProperty("type")
    public LicenseType getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(LicenseType type) {
        this.type = type;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(License.class.getName())
            .append('[');
        sb.append("type");
        sb.append('=');
        sb.append(((this.type == null) ? "<null>" : this.type));
        sb.append(',');
        sb.append("url");
        sb.append('=');
        sb.append(((this.url == null) ? "<null>" : this.url));
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
        result = ((result * 31) + ((this.type == null) ? 0 : this.type.hashCode()));
        result = ((result * 31) + ((this.url == null) ? 0 : this.url.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof License)) {
            return false;
        }
        License rhs = ((License) other);
        return (((this.type == rhs.type) || ((this.type != null) && this.type.equals(rhs.type)))
            && ((this.url == rhs.url) || ((this.url != null) && this.url.equals(rhs.url))));
    }

    @JsonIgnore
    public boolean isValid() {
        return this.type != null
            && this.url != null
            && this.url.length() != 0;
    }
}
