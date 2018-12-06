package it.nextworks.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.nextworks.composer.ComposerApplication;
import it.nextworks.composer.executor.repositories.SdkFunctionRepository;
import it.nextworks.composer.executor.repositories.SdkServiceRepository;
import it.nextworks.sdk.enums.Direction;
import it.nextworks.sdk.enums.LicenseType;
import it.nextworks.sdk.enums.MonitoringParameterName;
import it.nextworks.sdk.enums.Protocol;
import it.nextworks.sdk.enums.ScalingAction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
public class SdkServiceTest {

    @Autowired
    private SdkServiceRepository serviceRepository;

    @Autowired
    private SdkFunctionRepository functionRepository;

    /*
    service parameters are param1 and param2
     */
    public SdkService makeTestObject(Long functionId, List<String> mappingExpressions, Long... connectionPoints) {

        assertTrue(connectionPoints.length > 0);

        SdkService service = new SdkService();

        service.setName("test-service");
        service.setVersion("0.1");
        service.setDesigner("Nextworks");

        License license = new License();
        license.setType(LicenseType.PUBLIC);
        license.setUrl("http://example.org");
        service.setLicense(license);

        service.setParameters(Arrays.asList(
            "param1",
            "param2"
        ));

        SubFunction subFunction = new SubFunction(
            functionId,
            mappingExpressions,
            service
        );

        service.setComponents(Collections.singletonList(subFunction));

        Link link = LinkTest.makeTestObject(
            service,
            connectionPoints
        );
        service.setLink(new HashSet<>(Collections.singletonList(link)));

        L3Connectivity l3Connectivity = new L3Connectivity();
        l3Connectivity.setConnectionPointId(connectionPoints[0]);
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

        MonitoringParameter monitoringParameter = new MonitoringParameter();
        monitoringParameter.setName(MonitoringParameterName.AVERAGE_MEMORY_UTILIZATION);
        monitoringParameter.setService(service);
        monitoringParameter.setThreshold(123.0);
        monitoringParameter.setDirection(Direction.GREATER_THAN);
        service.setMonitoringParameters(Collections.singleton(monitoringParameter));

        ScalingAspect scalingAspect = new ScalingAspect();
        scalingAspect.setName("scaling-aspect-test");
        scalingAspect.setService(service);
        scalingAspect.setAction(ScalingAction.SCALE_UP);
        MonitoringParameter scalingParameter = new MonitoringParameter();
        scalingParameter.setName(MonitoringParameterName.AVERAGE_MEMORY_UTILIZATION);
        scalingParameter.setScalingAspect(scalingAspect);
        scalingParameter.setThreshold(9.0);
        scalingParameter.setDirection(Direction.GREATER_THAN);
        scalingAspect.setMonitoringParameter(Collections.singleton(scalingParameter));
        service.setScalingAspect(Collections.singleton(scalingAspect));

        return service;
    }

    @Test
    //@Ignore // requires DB
    public void testPersist() throws Exception {

        SdkFunction function = SdkFunctionTest.makeTestObject();
        functionRepository.saveAndFlush(function);

        Long functionId = function.getId();

        SdkService service = makeTestObject(
            functionId,
            Arrays.asList("param1", "param2"), // I.e. param1 == secure and param2 == small
            function.getConnectionPointMap().keySet().toArray(new Long[]{})
        );

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
    }
}
