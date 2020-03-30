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
package it.nextworks.composer.adaptor.expression;


import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Marco Capitani on 23/11/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class ServiceInformation {

    private ServiceMetadata serviceMetadata;

    private Set<String> serviceLinks;

    private Set<VnfdData> vnfdData;

    private UUID uuid;

    public ServiceInformation(
        ServiceMetadata serviceMetadata,
        Set<String> serviceLinks,
        Set<VnfdData> vnfdData
    ) {
        this.serviceMetadata = serviceMetadata;
        this.serviceLinks = serviceLinks;
        this.vnfdData = vnfdData;
    }

    public String getinvariantId() {
        return serviceMetadata.getUniqueId();
    }

    public synchronized String getUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        return uuid.toString();
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

