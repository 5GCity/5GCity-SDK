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
public enum LicenseType {

    PRIVATE("PRIVATE"),
    PUBLIC("PUBLIC");
    private final String value;
    private final static Map<String, LicenseType> CONSTANTS = new HashMap<String, LicenseType>();

    static {
        for (LicenseType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private LicenseType(String value) {
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
    public static LicenseType fromValue(String value) {
        LicenseType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
