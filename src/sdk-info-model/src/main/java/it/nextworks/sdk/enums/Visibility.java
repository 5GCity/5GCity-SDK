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
public enum Visibility {

    PUBLIC("PUBLIC"),
    PRIVATE("PRIVATE");
    private final String value;
    private final static Map<String, Visibility> CONSTANTS = new HashMap<String, Visibility>();

    static {
        for (Visibility c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private Visibility(String value) {
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
    public static Visibility fromValue(String value) {
        Visibility constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
