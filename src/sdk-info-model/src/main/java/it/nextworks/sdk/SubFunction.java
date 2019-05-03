package it.nextworks.sdk;

import it.nextworks.sdk.enums.SdkServiceComponentType;

import javax.persistence.Entity;
import java.util.List;

/**
 * Created by Marco Capitani on 04/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
@Entity
public class SubFunction extends SdkServiceComponent<SdkFunction> {

    private SubFunction() {
        this.type = SdkServiceComponentType.SDK_FUNCTION;
    }

    /**
     * Constructor.
     * <p>
     * Constraint: mappingExpression.size().equals(component.getFreeParametersNumber())
     *
     * @param componentId       the component, should be not null
     * @param mappingExpression the mapping expressions.
     * @throws IllegalArgumentException if the arguments do not satisfy the constraint
     */
    public SubFunction(Long componentId, Integer componentIndex, List<String> mappingExpression, SdkService outerService) {
        this.componentId = componentId;
        this.mappingExpression = mappingExpression;
        this.componentIndex = componentIndex;
        this.outerService = outerService;
        this.type = SdkServiceComponentType.SDK_FUNCTION;
        if (!isValid()) {
            throw new IllegalArgumentException("Invalid arguments");
        }
    }
}
