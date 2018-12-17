package it.nextworks.composer.controller;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Marco Capitani on 14/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class InstantiationRequest {

    public List<BigDecimal> parameterValues;

    public InstantiationRequest() {

    }

    public InstantiationRequest(List<BigDecimal> parameterValues) {
        this.parameterValues = parameterValues;
    }
}
