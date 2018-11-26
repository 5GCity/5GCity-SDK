package it.nextworks.composer.adaptor.interfaces.elements;

import it.nextworks.composer.evalex.StringExpression;
import it.nextworks.sdk.SDKFunction;
import org.springframework.data.util.Pair;

import java.util.List;

/**
 * Created by Marco Capitani on 21/11/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class FunctionMapping {

    private SDKFunction function;

    private String vnfdId;

    private List<String> parameters;

    private StringExpression dfExpression;

    private StringExpression ilExpression;

    public FunctionMapping(
            SDKFunction function,
            String vnfdId,
            List<String> parameters,
            StringExpression dfExpression,
            StringExpression ilExpression
    ) {
        this.function = function;
        this.vnfdId = vnfdId;
        // Cross validation between function and vnfd?

        validateUsedVars(dfExpression, parameters);
        validateUsedVars(ilExpression, parameters);

        // Validate return values against vnfd?
        this.parameters = parameters;
        this.dfExpression = dfExpression;
        this.ilExpression = ilExpression;
    }

    public FunctionMapping(
            SDKFunction function,
            String vnfdId,
            List<String> parameters,
            String dfExpression,
            List<String> dfResults,
            String ilExpression,
            List<String> ilResults
    ) {
        this(
                function,
                vnfdId,
                parameters,
                new StringExpression(dfExpression, dfResults),
                new StringExpression(ilExpression, ilResults)
        );
    }

    private static void validateUsedVars(StringExpression expression, List<String> parameters) {
        List<String> usedVars = expression.getUsedVariables();
        if (!parameters.containsAll(usedVars)) {
            usedVars.removeAll(parameters);
            throw new IllegalArgumentException(String.format(
                    "Expression %s contains variables not declared as function parameters: '%s'",
                    expression,
                    usedVars
            ));
        }
    }

    /**
     *
     * @return a mappingResult containing computed dfId and ilId
     */
    public MappingResult map() { // TODO: see how to get input parameters

    }

    public static final class MappingResult {
        public final String dfId;
        public final String ilId;

        public MappingResult(String dfId, String ilId) {
            this.dfId = dfId;
            this.ilId = ilId;
        }
    }
}
