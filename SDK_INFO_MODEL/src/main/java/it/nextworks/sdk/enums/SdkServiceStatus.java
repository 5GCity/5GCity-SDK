package it.nextworks.sdk.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marco Capitani on 23/11/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public enum SdkServiceStatus {

    SAVED("SAVED"),
    COMMITTED("COMMITTED");
    private final String value;
    private final static Map<String, SdkServiceStatus> CONSTANTS = new HashMap<String, SdkServiceStatus>();

    static {
        for (SdkServiceStatus c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private SdkServiceStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static SdkServiceStatus fromValue(String value) {
        SdkServiceStatus constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
