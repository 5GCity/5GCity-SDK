package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Helper class to avoid having string in keys
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
@Entity
public class Metadata {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "metadata_key")
    private String key;

    @Column(name = "metadata_value")
    private String value;

    @ManyToOne
    private SdkFunction function;

    @ManyToOne
    private SdkService service;

    private Metadata() {
        // JPA only
    }

    public Metadata(String key, String value, SdkFunction function) {
        this.key = key;
        this.value = value;
        this.function = function;
    }

    public Metadata(String key, String value, SdkService service) {
        this.key = key;
        this.value = value;
        this.service = service;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @JsonIgnore
    public SdkFunction getFunction() {
        return function;
    }

    @JsonIgnore
    public void setFunction(SdkFunction function) {
        this.function = function;
    }

    @JsonIgnore
    public SdkService getService() {
        return service;
    }

    @JsonIgnore
    public void setService(SdkService service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Metadata.class.getSimpleName() + "[", "]")
            .add("key='" + key + "'")
            .add("value='" + value.split("\n")[0] + "'")
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Metadata)) return false;
        Metadata metadata = (Metadata) o;
        return Objects.equals(getKey(), metadata.getKey()) &&
            Objects.equals(getValue(), metadata.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue());
    }
}
