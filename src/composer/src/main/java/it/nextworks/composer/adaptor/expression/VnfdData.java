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

import it.nextworks.nfvmano.libs.descriptors.templates.VirtualLinkPair;
import it.nextworks.sdk.L3Connectivity;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Marco Capitani on 12/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class VnfdData {
    public final String name;
    public final String description;
    public final String vendor;
    public final String vnfd;
    public final String vnfdVersion;
    public final String flavour;
    public final String instantiationLevel;
    public final Set<String> vLinks;
    public final Map<String, String> vLinksAssociation;
    public final Set<L3Connectivity> rules;


    public VnfdData(
        String name,
        String description,
        String vendor,
        String vnfd,
        String vnfdVersion,
        String flavour,
        String instantiationLevel,
        Set<String> vLinks,
        Map<String, String> vLinksAssociation,
        Set<L3Connectivity> rules
    ) {
        this.name = name;
        this.description = description;
        this.vendor = vendor;
        this.vnfd = vnfd;
        this.vnfdVersion = vnfdVersion;
        this.flavour = flavour;
        this.instantiationLevel = instantiationLevel;
        this.vLinks = vLinks;
        this.vLinksAssociation = vLinksAssociation;
        this.rules = rules;
    }
}
