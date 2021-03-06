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
package it.nextworks.composer.plugins.catalogue.sol005.nsdmanagement.elements;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The enumeration PnfdUsageStateType shall comply with the provisions defined
 * in Table 5.5.4.7-1 of GS NFV-SOL005. It indicates the usage state of the
 * resource. IN-USE = The resource is in use. NOT_IN_USE = The resource is
 * not-in-use.
 */
public enum PnfdUsageStateType {

    IN_USE("IN_USE"),

    NOT_IN_USE("NOT_IN_USE");

    private String value;

    PnfdUsageStateType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static PnfdUsageStateType fromValue(String text) {
        for (PnfdUsageStateType b : PnfdUsageStateType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }
}
