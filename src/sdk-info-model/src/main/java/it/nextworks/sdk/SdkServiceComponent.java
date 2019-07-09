package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.nextworks.sdk.enums.SdkServiceComponentType;
import it.nextworks.sdk.evalex.ExtendedExpression;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * SDKServiceComponent
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "componentId",
    "componentType",
    "mappingExpressions",
    "componentIndex"
})
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "componentType")
@JsonSubTypes({
    @Type(value = SubFunction.class, name = "SDK_FUNCTION"),
    @Type(value = SubService.class, name = "SDK_SERVICE")
})
@MappedSuperclass
abstract public class SdkServiceComponent<T extends InstantiableCandidate> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    protected Long componentId;

    @Transient
    protected SdkServiceComponentType type;

    protected Integer componentIndex;

    @ManyToOne
    protected T sdkComponent;

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

    @JsonProperty("componentId")
    public Long getComponentId() {
        return componentId;
    }

    @JsonProperty("componentId")
    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    @JsonProperty("componentType")
    public SdkServiceComponentType getType() {
        return type;
    }

    @JsonProperty("componentType")
    public void setType(SdkServiceComponentType type) {
        this.type = type;
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
    public T getComponent() {
        if (sdkComponent == null) {
            throw new IllegalStateException("Component not yet set, resolve the component first");
        }
        return sdkComponent;
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
            throw new IllegalArgumentException(String.format(
                "Invalid component with ID %d provided: parameter-expression mismatch",
                componentId
            ));
        }
        this.sdkComponent = component;
    }

    @JsonProperty("mappingExpressions")
    public List<String> getMappingExpression() {
        return mappingExpression;
    }

    @JsonIgnore
    public boolean isValid() {
        return componentId != null
            && componentIndex != null
            && mappingExpression != null
            && validateExpressions();
    }

    @JsonIgnore
    public boolean isResolved() {
        return sdkComponent != null;
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
        sb.append(((this.sdkComponent == null) ? "<null>" : this.sdkComponent));
        sb.append(',');
        sb.append("mappingExpressions");
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SdkServiceComponent)) return false;
        SdkServiceComponent<?> that = (SdkServiceComponent<?>) o;
        return getId().equals(that.getId()) &&
            Objects.equals(getComponentId(), that.getComponentId()) &&
            getType() == that.getType() &&
            Objects.equals(getMappingExpression(), that.getMappingExpression());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getComponentId(), getType(), getMappingExpression());
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

    public SdkComponentInstance instantiate(Map<String, BigDecimal> parameterValues) {
        return getComponent().makeDescriptor(computeParams(parameterValues));
    }

    @PrePersist
    private void prePersist() {
        if (!isResolved()) {
            throw new IllegalStateException("Cannot persist, component is not resolved");
        }
    }
}
