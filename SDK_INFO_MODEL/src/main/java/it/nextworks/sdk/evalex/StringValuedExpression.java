package it.nextworks.sdk.evalex;

import com.udojava.evalex.Expression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Marco Capitani on 21/11/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class StringValuedExpression implements ExtendedExpression<String> {

    private static final BigDecimal BASE_CODE = new BigDecimal(1.23145671243E-123, BASE_CONTEXT);
    // Random number to be used as placeholder for the results.

    private Expression expression;

    private List<String> resultVars;

    private List<String> usedVariables;

    private Map<String, Map<String, BigDecimal>> stringInputs;

    /**
     * Create an expression (optionally) returning a string and accepting string inputs.
     *
     * The return values should be provided as strings in the "expression"
     * argument. Also, the return values should not be used as "proper" variables.
     * The string inputs should be enumerated and given a numeric value via the
     * "stringInputs" argument. Note that all stringInputs keys should appear in
     * "usedVariables".
     *
     * This expression has numeric output if and only if all variables appearing
     * in the "expression" argument are input variables (i.e. appearing in "usedVariables"),
     * otherwise this expression must have string output (it will raise exception if a numeric
     * value is returned by the evaluation).
     *
     * Example:
     * ```
     * List\<String\> results = Arrays.asList("yes", "no");
     * StringExpression e = new StringExpression("IF(a+b>3, yes, no)", results);
     * e.with("a", 1).with("b", 1);
     * System.out.println(e.eval());
     * // prints "no"
     * ```
     * @param expression the expression
     * @param usedVariables the non-return variables in the expression
     * @param stringInputs maps variables to string -> value maps for substitution
     */
    public StringValuedExpression(
            String expression,
            List<String> usedVariables,
            Map<String, Map<String, BigDecimal>> stringInputs
    ) {
        this.expression = new Expression(expression, BASE_CONTEXT);
        if (!(usedVariables.containsAll(stringInputs.keySet()))) {
            Set<String> errorVars = new HashSet<>(stringInputs.keySet());
            errorVars.removeAll(usedVariables);
            throw new IllegalArgumentException(String.format(
                    "String inputs registered for variables '%s' which are not declared",
                    errorVars
            ));
        }
        this.usedVariables = usedVariables;
        this.stringInputs = stringInputs;
        populateResults();
    }

    private void populateResults() {
        // Check that all results appear in the expression
        List<String> exprVars = expression.getUsedVariables();
        // Check which are the actual variables of the expression
        exprVars.removeAll(usedVariables); // Leave only results
        this.resultVars = exprVars;
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
    public StringValuedExpression with(String varName, String value) {
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
    public StringValuedExpression with(String varName, int value) {
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
    public StringValuedExpression with(String varName, long value) {
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
    public StringValuedExpression with(String varName, BigDecimal value) {
        if (resultVars.contains(varName)) {
            throw new IllegalArgumentException(String.format(
                    "Cannot assign value to %s, it's a result variable.",
                    varName
            ));
        }
        expression.with(varName, value);
        return this;
    }

    /**
     * Evaluates the expression, returning the String result
     * @return the result of the expression
     */
    public String eval() {
        // Generate a value for each return variable,
        // and substitute them in the internal expression
        Map<BigDecimal, String> resultDecoder = new HashMap<>();
        BigDecimal currentCode = BASE_CODE;
        for (String result : resultVars) {
            resultDecoder.put(currentCode, result);
            expression.with(result, currentCode);
            currentCode = currentCode.add(BASE_CODE);
        }
        // eval internal expr
        BigDecimal numResult = expression.eval();
        // decode result
        String stringResult = resultDecoder.get(numResult);
        if (stringResult == null) {
            throw new IllegalStateException(String.format(
                    "The result is not a result variable. Expression '%s'. Numeric result '%s'",
                    expression.toString(),
                    numResult
            ));
        }
        return stringResult;
    }

    public List<String> getResultVars() {
        return new ArrayList<>(resultVars);
    }

    public List<String> getUsedVariables() {
        return new ArrayList<>(usedVariables);
    }
}
