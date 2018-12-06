package it.nextworks.sdk;

import it.nextworks.sdk.enums.SdkServiceComponentType;

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

    boolean isValid();

    Long getId();

    Map<Long, ConnectionPoint> getConnectionPointMap();

    SdkServiceComponentType getType();
}
