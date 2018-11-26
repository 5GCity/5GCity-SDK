package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.nextworks.sdk.enums.SdkServiceStatus;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * SDKService
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "status"
})
@Entity
public class SdkServiceInstance implements SdkComponentInstance<SdkServiceBase> {

    // TODO JPA annotations & equals, to String, hashcode
    // TODO also, add nsdId and catalogue identifiers
    // TODO Should also save with "cascade", which requires setting the parent

    private Integer id;

    private SdkServiceStatus status;

    private List<BigDecimal> parameterValues;

    private SdkServiceTemplate template;

    private SdkServiceInstance outerService;

    public Integer getId() {
        return id;
    }

    public SdkServiceTemplate getTemplate() {
        return template;
    }

    public SdkServiceInstance(
            SdkServiceTemplate template,
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
    private Map<String, BigDecimal> getParamsMap() {
        Map<String, BigDecimal> output = new HashMap<>();
        for (int i = 0; i < parameterValues.size(); i++) {
            output.put(template.getParameters().get(i), parameterValues.get(i));
        }
        return output;
    }

    @Override
    public ServiceInformation makeInformation() {
        if (id == null) {
            throw new IllegalStateException("Not persisted, cannot create information");
        }
        List<ServiceInformation> infos = template.getComponents().stream()
                .map(c -> c.instantiate(getParamsMap(), this))
                .map(SdkComponentInstance::makeInformation)
                .collect(Collectors.toList());

        // Merge all info into one
        ServiceInformation lower = infos.stream()
                .collect(
                        ServiceInformation::new,
                        ServiceInformation::merge,
                        ServiceInformation::merge
                );
        return lower.addServiceRelatedInfo(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SdkServiceInstance.class.getName())
                .append('@')
                .append(Integer.toHexString(System.identityHashCode(this)))
                .append('[');
        sb.append("status");
        sb.append('=');
        sb.append(((this.status == null)?"<null>":this.status));
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
        int result = super.hashCode();
        result = ((result* 31)+((this.status == null)? 0 :this.status.hashCode()));
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
        return ((this.status == rhs.status)||((this.status!= null)&&this.status.equals(rhs.status)))
                && super.equals(other);
    }

    @JsonIgnore
    public Integer getFreeParametersNumber() {
        return 0;
    }

}
