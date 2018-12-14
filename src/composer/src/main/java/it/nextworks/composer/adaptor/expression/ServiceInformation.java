package it.nextworks.composer.adaptor.expression;


import java.util.Map;
import java.util.Set;

/**
 * Created by Marco Capitani on 23/11/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class ServiceInformation {

    private ServiceMetadata serviceMetadata;

    private Set<String> serviceLinks;

    private Set<VnfdData> vnfdData;

    public ServiceInformation(
        ServiceMetadata serviceMetadata,
        Set<String> serviceLinks,
        Set<VnfdData> vnfdData
    ) {
        this.serviceMetadata = serviceMetadata;
        this.serviceLinks = serviceLinks;
        this.vnfdData = vnfdData;
    }

    public String getUniqueId() {
        return serviceMetadata.getUniqueId();
    }

    public Set<VnfdData> getVnfdData() {
        return vnfdData;
    }

    public String getName() {
        return serviceMetadata.name;
    }

    public String getVersion() {
        return serviceMetadata.version;
    }

    public String getDesigner() {
        return serviceMetadata.designer;
    }

    public String getLicense() {
        return serviceMetadata.license;
    }

    public Map<String, String> getMetadata() {
        return serviceMetadata.metadata;
    }

    public Set<String> getServiceLinks() {
        return serviceLinks;
    }
}

