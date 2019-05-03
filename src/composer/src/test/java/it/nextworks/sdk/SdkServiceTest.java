package it.nextworks.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.nextworks.composer.ComposerApplication;
import it.nextworks.composer.executor.repositories.SdkFunctionRepository;
import it.nextworks.composer.executor.repositories.SdkServiceRepository;
import it.nextworks.sdk.enums.*;
//import it.nextworks.sdk.enums.Direction;
//import it.nextworks.sdk.enums.MonitoringParameterName;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

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
public class SdkServiceTest {

    @Autowired
    private SdkServiceRepository serviceRepository;

    @Autowired
    private SdkFunctionRepository functionRepository;

    public static SdkService setServiceId(SdkService service, Long id) {
        service.setId(id);
        return service;
    }

    public static SdkServiceDescriptor setInstanceId(SdkServiceDescriptor instance, Long id) {
        instance.setId(id);
        return instance;
    }

    /*
    service parameters are param1 and param2
     */
    public static SdkService makeTestObject(Long functionId, List<String> mappingExpressions, Map<String, Long> intCpMap) {

        assertTrue(intCpMap.size() > 0);

        SdkService service = new SdkService();

        service.setName("test-service");
        service.setVersion("0.1");
        service.setDesigner("Nextworks");
        service.setOwnerId("Nextworks");

        License license = new License();
        license.setType(LicenseType.PUBLIC);
        license.setUrl("http://example.org");
        service.setLicense(license);

        service.setParameters(Arrays.asList(
            "param1"
        ));

        SubFunction subFunction = new SubFunction(
            functionId,
            0,
            mappingExpressions,
            service
        );

        service.setComponents(Collections.singleton(subFunction));

        ArrayList<String> cpNames = new ArrayList<>(intCpMap.keySet());
        cpNames.add("EXT_CP");

        Link link = LinkTest.makeTestObject(
            service,
            cpNames.toArray(new String[]{})
        );

        Set<ConnectionPoint> cps = new HashSet<>();

        for (Map.Entry<String, Long> e : intCpMap.entrySet()) {
            ConnectionPoint intCp = new ConnectionPoint();
            intCp.setName(e.getKey());
            intCp.setInternalCpId(e.getValue());
            intCp.setComponentIndex(0);
            intCp.setType(ConnectionPointType.INTERNAL);
            intCp.setRequiredPort(e.getValue().intValue() % 1000 + 8000);
            cps.add(intCp);
        }

        ConnectionPoint extCp = new ConnectionPoint();
        extCp.setName("EXT_CP");
        extCp.setType(ConnectionPointType.EXTERNAL);
        extCp.setRequiredPort(9042);
        cps.add(extCp);

        service.setConnectionPoint(cps);

        service.setLink(new HashSet<>(Collections.singletonList(link)));

        L3Connectivity l3Connectivity = new L3Connectivity();
        l3Connectivity.setConnectionPointName("EXT_CP");
        L3ConnectivityRule rule = new L3ConnectivityRule();
        rule.setProtocol(Protocol.TCP);
        rule.setDstIp("10.0.0.42");
        rule.setDstPort(8998);
        rule.setSrcIp("0.0.0.0");
        rule.setSrcPort(8000);
        l3Connectivity.setL3Rules(Collections.singleton(rule));
        l3Connectivity.setService(service);
        service.setL3Connectivity(
            Collections.singleton(l3Connectivity)
        );

        Map<String, String> metadata = new HashMap<>();
        metadata.put("use.spam", "egg");
        service.setMetadata(metadata);

        AggregatedMonParam param1 = SdkFunctionTest.makeAggregatedMonParam();
        TransformedMonParam param2 = SdkFunctionTest.makeTransformedMonParam();

        service.setExtMonitoringParameters(new HashSet<MonitoringParameter>(Arrays.asList(param1)));
        service.setIntMonitoringParameters(new HashSet<MonitoringParameter>(Arrays.asList(param2)));

        /*
        ScalingAspect scalingAspect = new ScalingAspect();
        scalingAspect.setName("scaling-aspect-test");
        scalingAspect.setService(service);
        scalingAspect.setAction(ScalingAction.SCALE_UP);
        service.setScalingAspect(Collections.singleton(scalingAspect));
        */

        return service;
    }

/*
    @Test
    @Ignore // requires DB
    public void testPersist() throws Exception {

        SdkFunction function = SdkFunctionTest.makeTestObject();
        functionRepository.saveAndFlush(function);

        Long functionId = function.getId();

        SdkService service = makeTestObject(
            functionId,
            Arrays.asList("param1", "param2"), // I.e. param1 == secure and param2 == small
            function.getConnectionPoint().stream().collect(Collectors.toMap(
                ConnectionPoint::getName,
                ConnectionPoint::getId
            ))
        );

        assertTrue(service.isValid());

        service.resolveComponents(Collections.singleton(function), Collections.emptySet());

        serviceRepository.saveAndFlush(service);

        Optional<SdkService> back = serviceRepository.findById(service.getId());

        assertTrue(back.isPresent());

        SdkService service2 = back.get();
        assertEquals(service, service2);

        File file = new File("/tmp/service.json");
        ObjectMapper mapper = new ObjectMapper();
        byte[] bytes = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(service);
        Files.write(file.toPath(), bytes);
        File fileF = new File("/tmp/function.json");
        byte[] bytesF = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(function);
        Files.write(fileF.toPath(), bytesF);
    }
*/

/*
    @Test
    @Ignore
    public void createCityServiceFromJson() throws Exception {
        // Setup
        URL resource = this.getClass().getClassLoader().getResource("cityService.json");
        ObjectMapper mapper = new ObjectMapper();
        SdkService service = mapper.readValue(resource, SdkService.class);

        assertTrue(service.isValid());

        Set<SdkFunction> functions = new HashSet<>(functionRepository.findAll());

        service.resolveComponents(functions, Collections.emptySet());

        serviceRepository.saveAndFlush(service);

        SdkService sdkService2 = serviceRepository.findById(service.getId()).get();

        assertEquals(service, sdkService2);

    }
*/

    @Test
    @Ignore // requires DB
    public void testCityService() throws Exception {

        SdkFunction firewall = SdkFunctionTest.makeNS1FirewallObject();

        assertTrue(firewall.isValid());

        functionRepository.saveAndFlush(firewall);

        Long firewallId = firewall.getId();

        SdkService service = makeTestObject(
            firewallId,
            Arrays.asList("param1"), // I.e. param1 == traffic
            firewall.getConnectionPoint().stream().collect(Collectors.toMap(
                ConnectionPoint::getName,
                ConnectionPoint::getId
            ))
        );

        assertTrue(service.isValid());

        service.resolveComponents(Collections.singleton(firewall), Collections.emptySet());

        serviceRepository.saveAndFlush(service);

        Optional<SdkService> back = serviceRepository.findById(service.getId());

        assertTrue(back.isPresent());

        SdkService service2 = back.get();
        assertEquals(service, service2);

        File file = new File("/tmp/service.json");
        ObjectMapper mapper = new ObjectMapper();
        byte[] bytes = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(service);
        Files.write(file.toPath(), bytes);
        File fileF = new File("/tmp/function.json");
        byte[] bytesF = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(firewall);
        Files.write(fileF.toPath(), bytesF);
    }
}

