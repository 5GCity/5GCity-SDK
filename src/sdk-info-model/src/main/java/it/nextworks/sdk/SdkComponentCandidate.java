package it.nextworks.sdk;

import it.nextworks.sdk.enums.SdkServiceComponentType;
import it.nextworks.sdk.exceptions.MalformedElementException;

import java.util.List;
import java.util.Map;

/**
 * Marker interface for the classes that can be an SdkServiceComponent
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public interface SdkComponentCandidate {

    Integer getFreeParametersNumber();

    List<String> getParameters();

    void isValid() throws MalformedElementException;

    Long getId();

    SdkServiceComponentType getType();
}
