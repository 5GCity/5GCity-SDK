package it.nextworks.composer.controller.elements;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Marco Capitani on 14/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class MakeDescriptorRequest {

    public List<BigDecimal> parameterValues;

    public MakeDescriptorRequest() {

    }

    public MakeDescriptorRequest(List<BigDecimal> parameterValues) {
        this.parameterValues = parameterValues;
    }
}
