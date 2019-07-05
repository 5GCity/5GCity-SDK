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
public enum ServiceActionType {

    //RECONFIGURE("RECONFIGURE"),
    SCALE_IN("SCALE_IN"),
    SCALE_OUT("SCALE_OUT"),
    COMPONENT("COMPONENT");
    private final static Map<String, ServiceActionType> CONSTANTS;

    static {
        HashMap<String, ServiceActionType> temp = new HashMap<>();
        for (ServiceActionType c : values()) {
            temp.put(c.value, c);
        }
        CONSTANTS = Collections.unmodifiableMap(temp);
    }

    private final String value;

    private ServiceActionType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ServiceActionType fromValue(String value) {
        ServiceActionType constant = CONSTANTS.get(value);
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
