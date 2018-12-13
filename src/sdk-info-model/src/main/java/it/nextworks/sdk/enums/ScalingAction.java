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
public enum ScalingAction {

    SCALE_UP("SCALE_UP"),
    SCALE_DOWN("SCALE_DOWN"),
    SCALE_IN("SCALE_IN"),
    SCALE_OUT("SCALE_OUT");
    private final static Map<String, ScalingAction> CONSTANTS = new HashMap<String, ScalingAction>();

    static {
        for (ScalingAction c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private final String value;

    private ScalingAction(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ScalingAction fromValue(String value) {
        ScalingAction constant = CONSTANTS.get(value);
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
