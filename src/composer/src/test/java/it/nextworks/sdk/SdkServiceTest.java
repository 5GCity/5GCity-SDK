package it.nextworks.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.nextworks.composer.ComposerApplication;
import it.nextworks.composer.executor.FunctionManager;
import it.nextworks.composer.executor.ServiceManager;
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
import java.math.BigDecimal;
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

    @Autowired
    private ServiceManager serviceManager;

    @Autowired
    private FunctionManager functionManager;

    public static SdkService setServiceId(SdkService service, Long id) {
        service.setId(id);
        return service;
    }

    public static SdkServiceDescriptor setInstanceId(SdkServiceDescriptor instance, Long id) {
        instance.setId(id);
        return instance;
    }

    private static Set<ServiceActionRule> makeActionRules(){
        Set<ServiceActionRule> actionRules = new HashSet<>();
        ServiceActionRule rule1 = new ServiceActionRule();
        ServiceActionRule rule2 = new ServiceActionRule();
        List<String> actionsId = new ArrayList<>();
        actionsId.add("action2");
        rule1.setActionsId(actionsId);
        rule2.setActionsId(actionsId);
        Set<RuleCondition> rc = new HashSet<>();
        RuleCondition cond1 = new RuleCondition();
        cond1.setComparator(RuleCondition.Comparator.DIFF);
        cond1.setParameterId("monparam3");
        cond1.setValue(Double.valueOf(100));
        rc.add(cond1);
        Set<RuleCondition> rc2 = new HashSet<>();
        RuleCondition cond2 = new RuleCondition();
        cond2.setComparator(RuleCondition.Comparator.GEQ);
        cond2.setParameterId("monparam2");
        cond2.setValue(Double.valueOf(200));
        rc2.add(cond2);
        rule1.setConditions(rc);
        rule2.setConditions(rc2);
        actionRules.add(rule1);
        actionRules.add(rule2);
        return actionRules;
    }

    private static MonParamImported makeImportedMonParam(String name, String targetId){
        MonParamImported param = new MonParamImported();
        param.setName(name);
        param.setComponentIndex(0);
        param.setImportedParameterId(targetId);
        param.setParameterType(MonitoringParameterType.IMPORTED);
        return param;
    }

    public static MonParamAggregated makeAggregatedMonParam(){
        MonParamAggregated param = new MonParamAggregated();
        param.setParameterType(MonitoringParameterType.AGGREGATED);
        param.setName("monparam4");
        param.setAggregatorFunc(AggregatorFunc.AVG);
        List<String> ids = new ArrayList<>();
        ids.add("monparam1");
        ids.add("monparam2");
        param.setParametersId(ids);

        return param;
    }

    public static MonParamTransformed makeTransformedMonParam(){
        MonParamTransformed param = new MonParamTransformed();
        param.setParameterType(MonitoringParameterType.TRANSFORMED);
        param.setName("monparam5");
        param.setTransform(Transform.AVG_OVER_TIME);
        param.setTargetParameterId("monparam3");

        return param;
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
        //service.setGroupId("NXW");

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

        /*
        ArrayList<String> cpNames = new ArrayList<>(intCpMap.keySet());
        cpNames.add("EXT_CP");

        Link link = LinkTest.makeTestObject(
            service,
            cpNames.toArray(new String[]{})
        );
        */

        Link link1 = new Link();
        link1.setName("link1");
        link1.setService(service);
        link1.setConnectionPointNames("EXT_CP", "eth0");

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

        service.setLink(new HashSet<>(Collections.singletonList(link1)));

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

        MonParamImported param1 = makeImportedMonParam("monparam1","7");
        MonParamImported param2 = makeImportedMonParam("monparam2","8");
        MonParamImported param3 = makeImportedMonParam("monparam3","9");

        MonParamAggregated param4 = makeAggregatedMonParam();
        MonParamTransformed param5 = makeTransformedMonParam();

        service.setExtMonitoringParameters(new HashSet<MonitoringParameter>(Arrays.asList(param1, param2, param4)));
        service.setIntMonitoringParameters(new HashSet<MonitoringParameter>(Arrays.asList(param3, param5)));

        /*
        ReconfigureAction action = new ReconfigureAction();
        action.setExtMonitoringParameters(new HashSet<MonitoringParameter>(Arrays.asList(param3)));
        action.setActionRules(makeActionRules());
        action.setActionType(ServiceActionType.RECONFIGURE);
        action.setName("action");
        */
        ScaleInAction action2 = new ScaleInAction();
        action2.setName("action2");
        action2.setComponentIndex("0");
        action2.setMin(10);
        action2.setStep(3);
        action2.setActionType(ServiceActionType.SCALE_IN);
        service.setActions(new HashSet<>(Arrays.asList(action2)));
        service.setActionRules(makeActionRules());

        service.setSliceId("admin");
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
    public void testDeleteMonitoringParameter() throws Exception {

        serviceManager.deleteMonitoringParameters(Long.valueOf(34), Long.valueOf(40));
    }

    @Test
    @Ignore // requires DB
    public void testUpdateService() throws Exception {
        //File file = new File("/tmp/service.json");
        //ObjectMapper mapper = new ObjectMapper();
        //SdkService service = mapper.readValue(file, SdkService.class);
        //serviceManager.updateService(service);
        Optional<SdkService> service = serviceRepository.findById(Long.valueOf(10));
        SdkService localService = service.get();
        L3Connectivity l3Connectivity = new L3Connectivity();
        l3Connectivity.setConnectionPointName("eth0");
        L3ConnectivityRule rule = new L3ConnectivityRule();
        rule.setProtocol(Protocol.TCP);
        rule.setDstIp("11.0.0.42");
        rule.setDstPort(8998);
        rule.setSrcIp("0.0.0.0");
        rule.setSrcPort(8000);
        l3Connectivity.setL3Rules(Collections.singleton(rule));
        l3Connectivity.setService(localService);
        localService.setL3Connectivity(
            Collections.singleton(l3Connectivity)
        );
        serviceManager.updateService(localService);
    }

    @Test
    @Ignore // requires DB
    public void testPublishService() throws Exception {
        List<BigDecimal> parameterValues = new ArrayList<>();
        parameterValues.add(new BigDecimal(1000));
        serviceManager.publishService(Long.valueOf(10), parameterValues, null);
        //serviceManager.publishService(Long.valueOf(40));
    }

    @Test
    @Ignore // requires DB
    public void testGetNSD() throws Exception {
        serviceManager.generateTemplate(Long.valueOf(62));
    }

    @Test
    @Ignore // requires DB
    public void testCityService() throws Exception {

        SdkFunction firewall = SdkFunctionTest.makeNS1FirewallObject();

        functionManager.createFunction(firewall, true);

        SdkFunction vPlate = SdkFunctionTest.makeNS1vPlateObject();

	    functionManager.createFunction(vPlate, true);

        Long firewallId = firewall.getId();

        SdkService service = makeTestObject(
            firewallId,
            Arrays.asList("param1"), // I.e. param1 == traffic
            firewall.getConnectionPoint().stream().collect(Collectors.toMap(
                ConnectionPoint::getName,
                ConnectionPoint::getId
            ))
        );

        serviceManager.createService(service);

        Optional<SdkService> back = serviceRepository.findById(service.getId());

        assertTrue(back.isPresent());

        File file = new File("/tmp/service.json");
        ObjectMapper mapper = new ObjectMapper();
        byte[] bytes = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(service);
        Files.write(file.toPath(), bytes);
        File fileF = new File("/tmp/function.json");
        byte[] bytesF = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(firewall);
        Files.write(fileF.toPath(), bytesF);
    }
}

