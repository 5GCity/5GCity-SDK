package it.nextworks.sdk.evalex;

import com.udojava.evalex.Expression;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Marco Capitani on 23/11/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class NumericStringExpression implements ExtendedExpression<BigDecimal> {

    private Expression expression;

    private Map<String, Map<String, BigDecimal>> stringInputs;

    private List<String> usedVariables;

    /*
    Returns a numeric expression (fixes "usedVariables" to all variables
    of the expression argument)
    Used in the implementation of the static methods below
     */
    public NumericStringExpression(
            String expression,
            Map<String, Map<String, BigDecimal>> stringInputs
    ) {
        this.expression = new Expression(expression, BASE_CONTEXT);
        List<String> usedVariables = this.expression.getUsedVariables();
        if (!(usedVariables.containsAll(stringInputs.keySet()))) {
            Set<String> errorVars = new HashSet<>(stringInputs.keySet());
            errorVars.removeAll(usedVariables);
            throw new IllegalArgumentException(String.format(
                    "String inputs registered for variables '%s' which are not declared",
                    errorVars
            ));
        }
        this.stringInputs = stringInputs;
        this.usedVariables = this.expression.getUsedVariables();
    }

    public List<String> getUsedVariables() {
        return usedVariables;
    }

    /**
     * Provides a value to a string variable in the expression.
     *
     *
     *
     * @param varName the name of the variable to populate
     * @param value the desired value
     * @return this StringExpression instance, for chaining
     */
    public NumericStringExpression with(String varName, String value) {
        if (!stringInputs.containsKey(varName)) {
            throw new IllegalArgumentException(String.format(
                    "Variable '%s' does not accept string values",
                    varName
            ));
        }
        if (!stringInputs.get(varName).containsKey(value)) {
            throw new IllegalArgumentException(String.format(
                    "Illegal value '%s' for variable '%s'. Accepted: '%s'",
                    value,
                    varName,
                    stringInputs.get(varName).keySet()
            ));
        }
        with(varName, stringInputs.get(varName).get(value));
        return this;
    }


    /**
     * Provides a value to a variable in the expression
     *
     * @param varName the name of the variable to populate
     * @param value the desired value
     * @return this StringExpression instance, for chaining
     */
    public NumericStringExpression with(String varName, int value) {
        with(varName, new BigDecimal(value));
        return this;
    }

    /**
     * Provides a value to a variable in the expression
     *
     * @param varName the name of the variable to populate
     * @param value the desired value
     * @return this StringExpression instance, for chaining
     */
    public NumericStringExpression with(String varName, long value) {
        with(varName, new BigDecimal(value));
        return this;
    }

    /**
     * Provides a value to a variable in the expression
     *
     * @param varName the name of the variable to populate
     * @param value the desired value
     * @return this StringExpression instance, for chaining
     */
    public NumericStringExpression with(String varName, BigDecimal value) {
        expression.with(varName, value);
        return this;
    }

    public BigDecimal eval() {
        return expression.eval();
    }

}
