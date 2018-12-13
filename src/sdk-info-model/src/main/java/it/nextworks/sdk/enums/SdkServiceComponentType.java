package it.nextworks.sdk.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marco Capitani on 06/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public enum SdkServiceComponentType {

    SDK_FUNCTION("SDK_FUNCTION"),
    SDK_SERVICE("SDK_SERVICE");

    private final static Map<String, SdkServiceComponentType> CONSTANTS = new HashMap<>();

    static {
        for (SdkServiceComponentType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private final String value;

    SdkServiceComponentType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static SdkServiceComponentType fromValue(String value) {
        SdkServiceComponentType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }
}
