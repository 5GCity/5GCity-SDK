package it.nextworks.composer.adaptor.expression;

import java.util.Map;

/**
 * Created by Marco Capitani on 12/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class FunctionMetadata {
    public String name;
    public String description;
    public String vendor;
    public String version;
    public Map<String, String> metadata;

    public FunctionMetadata name(String name) {
        this.name = name;
        return this;
    }

    public FunctionMetadata description(String description) {
        this.description = description;
        return this;
    }

    public FunctionMetadata vendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    public FunctionMetadata version(String version) {
        this.version = version;
        return this;
    }

    public FunctionMetadata metadata(Map<String, String> metadata) {
        this.metadata = metadata;
        return this;
    }
}
