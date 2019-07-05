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
