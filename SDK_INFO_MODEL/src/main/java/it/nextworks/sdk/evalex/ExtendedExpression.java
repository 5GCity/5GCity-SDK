package it.nextworks.sdk.evalex;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Marco Capitani on 23/11/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public interface ExtendedExpression<T> {

    MathContext BASE_CONTEXT = new MathContext(10);

    /**
     * Create an expression (optionally) returning a string and accepting string inputs.
     * <p>
     * The return values should be provided as strings in the "expression"
     * argument. Also, the return values should not be used as "proper" variables.
     * The string inputs should be enumerated and given a numeric value via the
     * "stringInputs" argument. Note that all stringInputs keys should appear in
     * "usedVariables".
     * <p>
     * This expression has numeric output if and only if all variables appearing
     * in the "expression" argument are input variables (i.e. appearing in "usedVariables"),
     * otherwise this expression must have string output (it will raise exception if a numeric
     * value is returned by the evaluation).
     * <p>
     * Example:
     * <code>
     * List<String> results = Arrays.asList("yes", "no");
     * StringExpression e = new StringExpression("IF(a+b>3, yes, no)", results);
     * e.with("a", 1).with("b", 1);
     * System.out.println(e.eval());
     * // prints "no"
     * </code>
     * ```
     *
     * @param expression    the expression
     * @param usedVariables the non-return variables in the expression
     * @param stringInputs  maps variables to string -> value maps for substitution
     */
    static ExtendedExpression<String> stringValued(
        String expression,
        List<String> usedVariables,
        Map<String, Map<String, BigDecimal>> stringInputs
    ) {
        return new StringValuedExpression(
            expression,
            usedVariables,
            stringInputs
        );
    }

    /**
     * Creates an expression with no string inputs.
     * <p>
     * As the general case, with an empty map as third argument.
     *
     * @param expression    the expression
     * @param usedVariables the name of the input variables used in the expression
     */
    static ExtendedExpression<String> stringValued(String expression, List<String> usedVariables) {
        return new StringValuedExpression(expression, usedVariables, Collections.emptyMap());
    }

    /**
     * Creates an expression with no input variables.
     * <p>
     * All variables in the expression are considered string outputs. This expression has
     * numeric output if and only if the "expression" argument contains exactly zero variables.
     *
     * @param expression the expression
     */
    static ExtendedExpression<String> stringValued(String expression) {
        return new StringValuedExpression(expression, Collections.emptyList(), Collections.emptyMap());
    }

    /**
     * Creates a numeric expression
     *
     * @param expression the expression
     * @return a numeric-valued expression
     */
    static ExtendedExpression<BigDecimal> numeric(String expression) {
        return new NumericStringExpression(expression, Collections.emptyMap());
    }

    /**
     * Creates a numeric expression accepting string input on (some of its) variables
     *
     * @param expression   the expression
     * @param stringInputs the string input mappings
     * @return a numeric-valued expression
     */
    static ExtendedExpression<BigDecimal> numeric(
        String expression,
        Map<String, Map<String, BigDecimal>> stringInputs
    ) {
        return new NumericStringExpression(expression, stringInputs);
    }

    T eval();

    List<String> getUsedVariables();

    public ExtendedExpression<T> with(String varName, String value);

    public ExtendedExpression<T> with(String varName, int value);

    public ExtendedExpression<T> with(String varName, long value);

    public ExtendedExpression<T> with(String varName, BigDecimal value);
}
