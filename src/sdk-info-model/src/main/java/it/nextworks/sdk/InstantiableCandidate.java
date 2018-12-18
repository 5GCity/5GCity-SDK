package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Marco Capitani on 03/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public interface InstantiableCandidate extends SdkComponentCandidate {

    SdkComponentInstance makeDescriptor(List<BigDecimal> parameterValues);
}
