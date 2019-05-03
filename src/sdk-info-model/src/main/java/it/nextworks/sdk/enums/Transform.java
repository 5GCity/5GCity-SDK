package it.nextworks.sdk.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marco Capitani on 23/11/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public enum Transform {

    SUM_OVER_TIME("SUM_OVER_TIME"),
    AVG_OVER_TIME("AVG_OVER_TIME"),
    MIN_OVER_TIME("MIN_OVER_TIME"),
    MAX_OVER_TIME("MAX_OVER_TIME"),
    RATE_OVER_TIME("RATE_OVER_TIME"),
    TRANSPOSE("TRANSPOSE"),
    MULTIPLY("MULTIPLY"),
    DIVIDE("DIVIDE");
    private final static Map<String, Transform> CONSTANTS;

    static {
        HashMap<String, Transform> temp = new HashMap<>();
        for (Transform c : values()) {
            temp.put(c.value, c);
        }
        CONSTANTS = Collections.unmodifiableMap(temp);
    }

    private final String value;

    private Transform(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Transform fromValue(String value) {
        Transform constant = CONSTANTS.get(value);
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
