package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.nextworks.sdk.enums.SdkServiceComponentType;
import it.nextworks.sdk.evalex.ExtendedExpression;
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
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Marco Capitani on 25/11/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
@Entity
public class SdkFunctionInstance extends SdkComponentInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private SdkFunction template;


    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OrderColumn
    private List<BigDecimal> parameterValues;

    public SdkFunctionInstance() {
        super();
    }

    public SdkFunctionInstance(
        SdkFunction template,
        List<BigDecimal> parameterValues
    ) {
        super();
        if (!(template.getFreeParametersNumber() == parameterValues.size())) {
            throw new IllegalArgumentException(String.format(
                "Parameter number not matching: expected %s, got %s",
                template.getFreeParametersNumber(),
                parameterValues.size()
            ));
        }
        this.parameterValues = parameterValues;
        this.template = template;
    }

    @Override
    @JsonProperty("component_type")
    public SdkServiceComponentType getType() {
        return SdkServiceComponentType.SDK_FUNCTION;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SdkFunction getTemplate() {
        return template;
    }

    @JsonIgnore
    public Long getOuterServiceId() {
        return outerService.getId();
    }

    @JsonIgnore
    public boolean isValid() {
        return template != null
            && parameterValues != null
            && template.getFreeParametersNumber() == parameterValues.size();
    }

    @JsonIgnore
    public String getFlavour() {
        List<String> parameters = template.getParameters();
        ExtendedExpression<String> flavourExpr = template.getFlavourCompiledExpression();
        Set<String> vars = new HashSet<>(flavourExpr.getUsedVariables());
        for (int i = 0; i < parameters.size(); i++) {
            // Make sure it appears in this expression
            if (vars.contains(parameters.get(i))) {
                flavourExpr.with(parameters.get(i), parameterValues.get(i));
            }
        }
        return flavourExpr.eval();
    }

    @JsonIgnore
    public String getLevel() {
        List<String> parameters = template.getParameters();
        ExtendedExpression<String> levelExpr = template.getILCompiledExpression();
        Set<String> vars = new HashSet<>(levelExpr.getUsedVariables());
        for (int i = 0; i < parameters.size(); i++) {
            // Make sure it appears in this expression
            if (vars.contains(parameters.get(i))) {
                levelExpr.with(parameters.get(i), parameterValues.get(i));
            }
        }
        return levelExpr.eval();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SdkFunctionInstance)) return false;
        SdkFunctionInstance that = (SdkFunctionInstance) o;
        return Objects.equals(getId(), that.getId()) &&
            Objects.equals(getTemplate(), that.getTemplate()) &&
            Objects.equals(parameterValues, that.parameterValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTemplate(), parameterValues);
    }
}
