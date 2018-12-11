package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.nextworks.sdk.enums.SdkServiceComponentType;
import it.nextworks.sdk.evalex.ExtendedExpression;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OrderColumn;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * SDKServiceComponent
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "component_id",
    "component_type",
    "mapping_expression"
})
@MappedSuperclass
abstract public class SdkServiceComponent<S extends SdkComponentCandidate, T extends InstantiableCandidate<S>> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Transient
    protected Long componentId;

    @Transient
    protected SdkServiceComponentType type;

    @ManyToOne
    protected T component;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OrderColumn
    protected List<String> mappingExpression = new ArrayList<>();

    @ManyToOne
    protected SdkService outerService;

    protected SdkServiceComponent() {
        // JPA only
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonIgnore
    public SdkService getOuterService() {
        return outerService;
    }

    @JsonIgnore
    public void setOuterService(SdkService outerService) {
        this.outerService = outerService;
    }

    @JsonProperty("component_id")
    public Long getComponentId() {
        return componentId;
    }

    @JsonProperty("component_id")
    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    @JsonProperty("component_type")
    public SdkServiceComponentType getType() {
        return type;
    }

    @JsonProperty("component_type")
    public void setType(SdkServiceComponentType type) {
        this.type = type;
    }

    @JsonIgnore
    public T getComponent() {
        if (component == null) {
            throw new IllegalStateException("Component not yet set, resolve the component first");
        }
        return component;
    }

    @JsonIgnore
    public void setComponent(T component) {
        if (component == null) {
            throw new IllegalArgumentException("Null component provided");
        }
        if (!type.equals(component.getType())) {
            throw new IllegalArgumentException(String.format(
                "Invalid component provided: expected type %s, got %s",
                type,
                component.getType()
            ));
        }
        if (!componentId.equals(component.getId())) {
            throw new IllegalArgumentException(String.format(
                "Invalid component provided: expected id %d, got %d",
                componentId,
                component.getId()
            ));
        }
        if (!(mappingExpression.size() == component.getFreeParametersNumber())) {
            throw new IllegalArgumentException(
                "Invalid component provided: parameter-expression mismatch"
            );
        }
        this.component = component;
    }

    @JsonProperty("mapping_expression")
    public List<String> getMappingExpression() {
        return mappingExpression;
    }

    @JsonIgnore
    public boolean isValid() {
        return componentId != null
            && mappingExpression != null
            && validateExpressions();
    }

    @JsonIgnore
    public boolean isResolved() {
        return component != null;
    }

    private boolean validateExpressions() {
        // NOTE: right now string-valued expressions and string inputs are not supported in mapping
        // since non-numeric parameters are not supported
        return mappingExpression.stream().allMatch(e -> {
            try {
                ExtendedExpression.numeric(e);
                return true;
            } catch (Exception exc) {
                return false;
            }
        });
    }

    boolean validateParameters(List<String> acceptedParameters) {
        // NOTE: right now string-valued expressions and string inputs are not supported in mapping
        // since non-numeric parameters are not supported
        return mappingExpression.stream().allMatch(e -> {
            try {
                ExtendedExpression<BigDecimal> expr = ExtendedExpression.numeric(e);
                return acceptedParameters.containsAll(expr.getUsedVariables());
            } catch (Exception exc) {
                return false;
            }
        });
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SdkServiceComponent.class.getName())
            .append('[');
        sb.append("component");
        sb.append('=');
        sb.append(((this.component == null) ? "<null>" : this.component));
        sb.append(',');
        sb.append("mappingExpression");
        sb.append('=');
        sb.append(((this.mappingExpression == null) ? "<null>" : this.mappingExpression));
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
        result = ((result * 31) + ((this.mappingExpression == null) ? 0 : this.mappingExpression.hashCode()));
        result = ((result * 31) + ((this.component == null) ? 0 : this.component.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SdkServiceComponent)) {
            return false;
        }
        SdkServiceComponent rhs = ((SdkServiceComponent) other);
        return (((this.mappingExpression == rhs.mappingExpression) || ((this.mappingExpression != null) && this.mappingExpression.equals(rhs.mappingExpression)))
            && ((this.component == rhs.component) || ((this.component != null) && this.component.equals(rhs.component))));
    }

    private List<BigDecimal> computeParams(Map<String, BigDecimal> outerParams) {
        List<BigDecimal> output = new ArrayList<>();
        for (String expr : mappingExpression) {
            ExtendedExpression<BigDecimal> compiled = ExtendedExpression.numeric(expr);
            for (Map.Entry<String, BigDecimal> entry : outerParams.entrySet()) {
                HashSet<String> innerParams = new HashSet<>(compiled.getUsedVariables());
                // Set for ease of "contains" usage
                if (innerParams.contains(entry.getKey())) {
                    // If param is used in this expression, valorize it
                    compiled.with(entry.getKey(), entry.getValue());
                }
            }
            output.add(compiled.eval());
        }
        return output;
    }

    public SdkComponentInstance<S> instantiate(Map<String, BigDecimal> parameterValues, SdkServiceInstance outerService) {
        return getComponent().instantiate(computeParams(parameterValues), outerService);
    }

    @PrePersist
    private void prePersist() {
        if (!isResolved()) {
            throw new IllegalStateException("Cannot persist, component is not resolved");
        }
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void fixPersistence() {
        mappingExpression = new ArrayList<>(mappingExpression);
        componentId = component.getId();
        type = component.getType();
    }
}
