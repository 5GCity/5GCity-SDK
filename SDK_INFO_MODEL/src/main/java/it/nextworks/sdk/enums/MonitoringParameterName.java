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
public enum MonitoringParameterName {

    AVERAGE_MEMORY_UTILIZATION("AVERAGE_MEMORY_UTILIZATION");
    private final String value;
    private final static Map<String, MonitoringParameterName> CONSTANTS = new HashMap<String, MonitoringParameterName>();

    static {
        for (MonitoringParameterName c : values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private MonitoringParameterName(String value) {
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
    public static MonitoringParameterName fromValue(String value) {
        MonitoringParameterName constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
