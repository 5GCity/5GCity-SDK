/*
 * Copyright 2020 Nextworks s.r.l.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
public enum AggregatorFunc {

    SUM("sum"),
    AVG("avg"),
    MIN("min"),
    MAX("max"),
    STDEV("stdev");
    private final String value;
    private final static Map<String, AggregatorFunc> CONSTANTS = new HashMap<String, AggregatorFunc>();

    static {
        for (AggregatorFunc c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private AggregatorFunc(String value) {
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
    public static AggregatorFunc fromValue(String value) {
        AggregatorFunc constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
