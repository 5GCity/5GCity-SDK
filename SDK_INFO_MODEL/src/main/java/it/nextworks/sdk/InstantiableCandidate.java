package it.nextworks.sdk;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Marco Capitani on 03/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public interface InstantiableCandidate<T extends SdkComponentCandidate> extends SdkComponentCandidate {

    SdkComponentInstance<T> instantiate(List<BigDecimal> parameterValues, SdkServiceInstance outerService);
}
