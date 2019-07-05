/*
 * Copyright 2018 Nextworks s.r.l.
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
package it.nextworks.composer;

import it.nextworks.composer.executor.repositories.SdkFunctionRepository;
import it.nextworks.composer.plugins.catalogue.Catalogue;
import it.nextworks.composer.plugins.catalogue.CatalogueType;
import it.nextworks.composer.plugins.catalogue.DescriptorsParser;
import it.nextworks.composer.plugins.catalogue.FiveGCataloguePlugin;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.sdk.SdkFunction;
import it.nextworks.sdk.SdkFunctionTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.net.URI;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class ComposerApplicationTests {


    @Value("${catalogue.host}")
    private String catalogueHost;


    @Autowired
    private SdkFunctionRepository functionRepository;


    @Test
    public void contextLoads() {


    }


//    @Test
//    @Ignore
//    public void testPostToCatalogue() throws Exception {
//
//        Catalogue catalogue = new Catalogue("5gCatalogue", catalogueHost, false, null, null);
//        FiveGCataloguePlugin plugin = new FiveGCataloguePlugin(CatalogueType.FIVEG_CATALOGUE, catalogue);
//
//        URI uri = this.getClass().getClassLoader().getResource("vCDN_UC3_5GMEDIA.yaml").toURI();
//
//        File file = new File(uri);
//
//        DescriptorTemplate template = DescriptorsParser.fileToDescriptorTemplate(file);
//
//        plugin.uploadNetworkService(template, "multipart/form-data", null);
//    }

//    @Test
//    @Ignore // requires DB
//    public void testCityService() {
//
//        SdkFunction miniWeb = SdkFunctionTest.makeDemoMiniwebObject();
//
//        SdkFunction firewall = SdkFunctionTest.makeDemoFirewallObject();
//
//        assertTrue(miniWeb.isValid());
//        assertTrue(firewall.isValid());
//
//        functionRepository.saveAndFlush(miniWeb);
//
//        functionRepository.saveAndFlush(firewall);
//
//        Optional<SdkFunction> mwb = functionRepository.findById(miniWeb.getId());
//
//        Optional<SdkFunction> fwb = functionRepository.findById(firewall.getId());
//
//        assertTrue(mwb.isPresent());
//        assertTrue(fwb.isPresent());
//
//        SdkFunction mw2 = mwb.get();
//        assertEquals(miniWeb, mw2);
//
//        SdkFunction fw2 = fwb.get();
//        assertEquals(firewall, fw2);
//
//    }

}
