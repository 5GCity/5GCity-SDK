package it.nextworks.sdk;

import it.nextworks.composer.ComposerApplication;
import it.nextworks.composer.executor.repositories.LinkRepository;
import it.nextworks.composer.executor.repositories.SdkServiceRepository;
import it.nextworks.sdk.enums.LinkType;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Marco Capitani on 05/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComposerApplication.class)
@WebAppConfiguration
public class LinkTest {

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private SdkServiceRepository serviceRepository;

    public static Link makeTestObject(SdkService service, Long... cps) {
        Link link = new Link();
        link.setName("test-link");
        link.setService(service);
        link.setType(LinkType.EXTERNAL);
        link.setConnectionPointIds(cps);
        return link;
    }

    @Test
    @Ignore // requires DB
    public void testPersist() {
        SdkService service = new SdkService();
        Link link = makeTestObject(service, 10L, 100L, 1000L);

        serviceRepository.save(service);

        linkRepository.saveAndFlush(link);

        Optional<Link> back = linkRepository.findById(link.getId());

        assertTrue(back.isPresent());

        Link link2 = back.get();

        assertEquals(link, link2);
    }

}