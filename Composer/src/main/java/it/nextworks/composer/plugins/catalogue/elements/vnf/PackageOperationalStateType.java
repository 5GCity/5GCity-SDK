/*
 * DRAFT - SOL005 - VNF Package Management Interface
 * DRAFT - SOL005 - VNF Package Management Interface IMPORTANT: Please note that this file might be not aligned to the current version of the ETSI Group Specification it refers to and has not been approved by the ETSI NFV ISG. In case of discrepancies the published ETSI Group Specification takes precedence. Please report bugs to https://forge.etsi.org/bugzilla/buglist.cgi?component=Nfv-Openapis
 *
 * OpenAPI spec version: 2.4.1
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package it.nextworks.composer.plugins.catalogue.elements.vnf;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets PackageOperationalStateType
 */
public enum PackageOperationalStateType {

    ENABLED("ENABLED"),

    DISABLED("DISABLED");

    private String value;

    PackageOperationalStateType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static PackageOperationalStateType fromValue(String text) {
        for (PackageOperationalStateType b : PackageOperationalStateType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

