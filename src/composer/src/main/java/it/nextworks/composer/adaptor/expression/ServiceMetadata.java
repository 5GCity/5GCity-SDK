package it.nextworks.composer.adaptor.expression;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marco Capitani on 12/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
class ServiceMetadata {
    public String instanceId;
    public String name;
    public String version;
    public String designer;
    public String license;
    public Map<String, String> metadata = new HashMap<>();

    ServiceMetadata() {

    }

    ServiceMetadata(
        String instanceId,
        String name,
        String version,
        String designer,
        String license,
        Map<String, String> metadata
    ) {
        this.instanceId = instanceId;
        this.name = name;
        this.version = version;
        this.designer = designer;
        this.license = license;
        this.metadata = metadata;
    }

    String getUniqueId() {
        return String.format("%s_%s_%s", name, version, instanceId);
    }

    ServiceMetadata setInstanceId(String instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    ServiceMetadata setName(String name) {
        this.name = name;
        return this;
    }

    ServiceMetadata setVersion(String version) {
        this.version = version;
        return this;
    }

    ServiceMetadata setDesigner(String designer) {
        this.designer = designer;
        return this;
    }

    ServiceMetadata setLicense(String license) {
        // validate licensing issues?
        this.license = license;
        return this;
    }

    ServiceMetadata addMetadata(Map<String, String> metadata) {
        this.metadata.putAll(metadata);
        return this;
    }

    ServiceMetadata merge(ServiceMetadata other) {
        setName(other.name);
        setVersion(other.version);
        setDesigner(other.designer);
        setLicense(other.license);
        addMetadata(other.metadata);
        return this;
    }
}
