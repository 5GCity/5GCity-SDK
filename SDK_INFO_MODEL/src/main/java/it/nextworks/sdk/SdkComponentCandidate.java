package it.nextworks.sdk;

import java.math.BigDecimal;
import java.util.List;

/**
 * Marker interface for the classes that can be an SdkServiceComponent
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public interface SdkComponentCandidate<T> {

    Integer getFreeParametersNumber();

    List<String> getParameters();

    boolean isValid();

    Integer getId();

    SdkComponentInstance<T> instantiate(List<BigDecimal> parameterValues, SdkServiceInstance outerService);
}
