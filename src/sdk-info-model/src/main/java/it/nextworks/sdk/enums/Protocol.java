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
public enum Protocol {

    UDP("UDP"),
    TCP("TCP"),
    ICMP("ICMP");
    private final static Map<String, Protocol> CONSTANTS = new HashMap<String, Protocol>();

    static {
        for (Protocol c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private final String value;

    Protocol(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Protocol fromValue(String value) {
        Protocol constant = CONSTANTS.get(value);
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
