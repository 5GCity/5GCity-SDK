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
public enum MonitoringParameterType {

    AGGREGATED("AGGREGATED"),
    FUNCTION("FUNCTION"),
    IMPORTED("IMPORTED"),
    TRANSFORMED("TRANSFORMED");
    private final static Map<String, MonitoringParameterType> CONSTANTS;

    static {
        HashMap<String, MonitoringParameterType> temp = new HashMap<>();
        for (MonitoringParameterType c : values()) {
            temp.put(c.value, c);
        }
        CONSTANTS = Collections.unmodifiableMap(temp);
    }

    private final String value;

    private MonitoringParameterType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static MonitoringParameterType fromValue(String value) {
        MonitoringParameterType constant = CONSTANTS.get(value);
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
