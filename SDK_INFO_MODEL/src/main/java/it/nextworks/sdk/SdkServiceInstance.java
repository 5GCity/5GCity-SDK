package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.nextworks.sdk.enums.SdkServiceStatus;
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
import javax.persistence.OrderColumn;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * SDKService
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "status"
})
@Entity
public class SdkServiceInstance implements SdkComponentInstance<SdkService> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private SdkServiceStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OrderColumn
    private List<BigDecimal> parameterValues;

    @ManyToOne
    private SdkService template;

    @Transient
    private SdkServiceInstance outerService;

    private SdkServiceInstance() {
        // JPA only
    }

    public SdkServiceInstance(
        SdkService template,
        List<BigDecimal> parameterValues,
        SdkServiceInstance outerService
    ) {
        if (!(template.getFreeParametersNumber() == parameterValues.size())) {
            throw new IllegalArgumentException(String.format(
                "Parameter number not matching: expected %s, got %s",
                template.getFreeParametersNumber(),
                parameterValues.size()
            ));
        }
        this.parameterValues = parameterValues;
        this.template = template;
        this.outerService = outerService;
    }

    public Long getId() {
        return id;
    }

    public SdkService getTemplate() {
        return template;
    }

    @JsonProperty("status")
    public SdkServiceStatus getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(SdkServiceStatus status) {
        this.status = status;
    }

    @JsonIgnore
    public boolean isValid() {
        return template != null
            && parameterValues != null
            && template.getFreeParametersNumber() == parameterValues.size();
    }

    @JsonIgnore
    public Map<String, BigDecimal> getParamsMap() {
        Map<String, BigDecimal> output = new HashMap<>();
        for (int i = 0; i < parameterValues.size(); i++) {
            output.put(template.getParameters().get(i), parameterValues.get(i));
        }
        return output;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SdkServiceInstance.class.getName())
            .append('[');
        sb.append("status");
        sb.append('=');
        sb.append(((this.status == null) ? "<null>" : this.status));
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
        int result = super.hashCode();
        result = ((result * 31) + ((this.status == null) ? 0 : this.status.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SdkServiceInstance)) {
            return false;
        }
        SdkServiceInstance rhs = ((SdkServiceInstance) other);
        return ((this.status == rhs.status) || ((this.status != null) && this.status.equals(rhs.status)))
            && super.equals(other);
    }

    @JsonIgnore
    public Integer getFreeParametersNumber() {
        return 0;
    }

}
