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
public enum Direction {

    GREATER_THAN("GREATER_THAN"),
    LOWER_THAN("LOWER_THAN");
    private final static Map<String, Direction> CONSTANTS = new HashMap<String, Direction>();

    static {
        for (Direction c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private final String value;

    private Direction(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Direction fromValue(String value) {
        Direction constant = CONSTANTS.get(value);
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
