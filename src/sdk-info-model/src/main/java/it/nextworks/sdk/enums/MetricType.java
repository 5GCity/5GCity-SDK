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
public enum MetricType {

    APPLICATION("APPLICATION"),
    SYSTEM("SYSTEM");
    private final static Map<String, MetricType> CONSTANTS;

    static {
        HashMap<String, MetricType> temp = new HashMap<>();
        for (MetricType c : values()) {
            temp.put(c.value, c);
        }
        CONSTANTS = Collections.unmodifiableMap(temp);
    }

    private final String value;

    private MetricType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static MetricType fromValue(String value) {
        MetricType constant = CONSTANTS.get(value);
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
