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
public enum LinkType {

    INTERNAL("INTERNAL"),
    EXTERNAL("EXTERNAL");
    private final static Map<String, LinkType> CONSTANTS = new HashMap<String, LinkType>();

    static {
        for (LinkType c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private final String value;

    private LinkType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static LinkType fromValue(String value) {
        LinkType constant = CONSTANTS.get(value);
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
