package it.nextworks.sdk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.nextworks.sdk.evalex.ExtendedExpression;
import it.nextworks.sdk.evalex.NumericStringExpression;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;


/**
 * SDKServiceComponent
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "component",
    "mappingExpression"
})
@Entity
public class SdkServiceComponent<T extends SdkComponentCandidate<T>> {

    private SdkServiceComponent() {
        // JPA only
    }

    /**
     * Constructor.
     *
     * Constraint: mappingExpression.size().equals(component.getFreeParametersNumber())
     *
     * @param component the component, should be not null
     * @param mappingExpression the mapping expressions.
     * @throws IllegalArgumentException if the arguments do not satisfy the constraint
     */
    public SdkServiceComponent(T component, List<String> mappingExpression) {
        this.component = component;
        this.mappingExpression = mappingExpression;
        if (!isValid()) {
            throw new IllegalArgumentException("Invalid arguments");
        }
    }

    private T component;

    @ElementCollection(fetch= FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> mappingExpression = new ArrayList<>();

    @JsonProperty("component")
    public T getComponent() {
        return component;
    }

    @JsonProperty("mappingExpression")
    public List<String> getMappingExpression() {
        return mappingExpression;
    }

    @JsonIgnore
    public boolean isValid() {
        return component != null && component.isValid()
                && mappingExpression != null
                && mappingExpression.size() == component.getFreeParametersNumber()
                && validateExpressions();
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
        return mappingExpression.stream().allMatch( e-> {
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
                .append('@')
                .append(Integer.toHexString(System.identityHashCode(this)))
                .append('[');
        sb.append("component");
        sb.append('=');
        sb.append(((this.component == null)?"<null>":this.component));
        sb.append(',');
        sb.append("mappingExpression");
        sb.append('=');
        sb.append(((this.mappingExpression == null)?"<null>":this.mappingExpression));
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
        result = ((result* 31)+((this.mappingExpression == null)? 0 :this.mappingExpression.hashCode()));
        result = ((result* 31)+((this.component == null)? 0 :this.component.hashCode()));
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
        return (((this.mappingExpression == rhs.mappingExpression)||((this.mappingExpression!= null)&&this.mappingExpression.equals(rhs.mappingExpression)))
                &&((this.component == rhs.component)||((this.component!= null)&&this.component.equals(rhs.component))));
    }

    private List<BigDecimal> computeParams(Map<String, BigDecimal> outerParams) {
        List<BigDecimal> output = new ArrayList<>();
        for (String expr : mappingExpression) {
            ExtendedExpression<BigDecimal> compiled = ExtendedExpression.numeric(expr);
            for (Map.Entry<String, BigDecimal> entry : outerParams.entrySet()) {
                HashSet<String> innerParams = new HashSet<>(compiled.getUsedVariables());
                // Set for ease of "contains" usage
                if (innerParams.contains(entry.getKey())) {
                    // Check that the param is used in this expression
                    compiled.with(entry.getKey(), entry.getValue());
                }
            }
            output.add(compiled.eval());
        }
        return output;
    }

    public SdkComponentInstance instantiate(Map<String, BigDecimal> parameterValues, SdkServiceInstance outerService) {
        return getComponent().instantiate(computeParams(parameterValues), outerService);
    }
}
