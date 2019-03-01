package it.nextworks.sdk;

import it.nextworks.composer.ComposerApplication;
import it.nextworks.composer.executor.repositories.SdkFunctionRepository;
import it.nextworks.sdk.enums.Direction;
import it.nextworks.sdk.enums.MonitoringParameterName;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Marco Capitani on 04/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComposerApplication.class)
@WebAppConfiguration
public class SdkFunctionTest {

    @Autowired
    private SdkFunctionRepository functionRepository;

//    public static SdkFunction makeTestObject() {
//        Map<String, String> metadata = new HashMap<>();
//        metadata.put("cloud-init", "#!/bin/vbash\n" +
//            "source /opt/vyatta/etc/functions/script-template\n" +
//            "configure\n" +
//            "set interfaces ethernet eth1  address 192.168.200.1/24\n" +
//            "\n" +
//            "commit\n" +
//            "exit");
//
//        SdkFunction function = new SdkFunction();
//        function.setName("vFirewall-v3");
//        function.setVersion("v3");
//        function.setVendor("Nextworks");
//        function.setDescription("vFirewall");
//        function.setMetadata(metadata);
//        function.setFlavourExpression("IF(secure != 0, secure_df, insecure_df)");
//        function.setInstantiationLevelExpression("IF(small != 0, small_il, big_il)");
//        function.setParameters(Arrays.asList("secure", "small"));
//        function.setVnfdId("vnfd_id");
//        function.setVnfdVersion("vnfd_version");
//
//        MonitoringParameter monitoringParameter = new MonitoringParameter();
//        monitoringParameter.setName(MonitoringParameterName.AVERAGE_MEMORY_UTILIZATION);
//        monitoringParameter.setFunction(function);
//        monitoringParameter.setThreshold(142.0);
//        monitoringParameter.setDirection(Direction.LOWER_THAN);
//        function.setMonitoringParameters(Collections.singleton(monitoringParameter));
//
//        ConnectionPoint cp1 = ConnectionPointTest.makeTestObject1();
//        ConnectionPoint cp2 = ConnectionPointTest.makeTestObject2();
//        ConnectionPoint cp3 = ConnectionPointTest.makeTestObject3();
//        ConnectionPoint cp4 = ConnectionPointTest.makeTestObject4();
//
//        function.setConnectionPoint(new HashSet<>(Arrays.asList(cp1, cp2, cp3, cp4)));
//        return function;
//    }

    public static SdkFunction makeNS1vPlateObject() {
        Map<String, String> metadata = new HashMap<>();
        SdkFunction function = new SdkFunction();
        function.setName("vPlate-server");
        function.setVersion("1.0");
        function.setVendor("NXW");
        function.setDescription("Image recognition service.");
        function.setMetadata(metadata);
        function.setFlavourExpression("IF(isVideo != 0, video_flv_srv, standard_flv_srv)");
        function.setInstantiationLevelExpression("IF(size <= 1, small_il, IF(size <= 10, medium_il, big_il))");
        function.setParameters(Arrays.asList("isVideo", "size"));
        function.setVnfdId("aa333a44-6587-4940-b442-c029376bbb2e");
        function.setVnfdVersion("1.0");

        ConnectionPoint cp1 = ConnectionPointTest.makeNS1vPlateObject1();

        function.setConnectionPoint(new HashSet<>(Arrays.asList(cp1)));
        return function;
    }

    public static SdkFunction makeNS1FirewallObject() {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("cloud-init", "#!/bin/vbash\n"
        		+ "source /opt/vyatta/etc/functions/script-template\n"
        		+ "configure\n"
        		+ "set interfaces ethernet eth1  address 192.168.200.1/24\n"
        		+ "commit"
        		+ "exit");

        SdkFunction function = new SdkFunction();
        function.setName("vFirewall-v5");
        function.setVersion("1.0");
        function.setVendor("Nextworks");
        function.setDescription("Virtual Firewall");
        function.setMetadata(metadata);
        function.setFlavourExpression("static_df");
        function.setInstantiationLevelExpression("IF(traffic != 0, big_il, medium_il)");
        function.setParameters(Arrays.asList("traffic"));
        function.setVnfdId("aa6a284e-e369-4d7d-a465-57ddc6e8c027");
        function.setVnfdVersion("1.0");

        ConnectionPoint cp1 = ConnectionPointTest.makeNS1FirewallObject1();
        ConnectionPoint cp2 = ConnectionPointTest.makeNS1FirewallObject2();
        ConnectionPoint cp3 = ConnectionPointTest.makeNS1FirewallObject3();
        ConnectionPoint cp4 = ConnectionPointTest.makeNS1FirewallObject4();

        function.setConnectionPoint(new HashSet<>(Arrays.asList(cp1, cp2, cp3, cp4)));
        return function;
    }
    



    public static SdkFunction makeNS2FirewallObject() {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("cloud-init", "#!/bin/vbash\n"
                + "source /opt/vyatta/etc/functions/script-template\n"
                + "configure\n"
                + "set interfaces ethernet eth1  address 192.168.200.1/24\n"
                + "commit"
                + "exit");

        SdkFunction function = new SdkFunction();
        function.setName("vFirewall-uc1-ns2-v6");
        function.setVersion("1.0");
        function.setVendor("Nextworks");
        function.setDescription("vFirewall-uc1-ns2-v6");
        function.setMetadata(metadata);
        function.setFlavourExpression("static_df");
        function.setInstantiationLevelExpression("IF(traffic != 0, big_il, medium_il)");
        function.setParameters(Arrays.asList("traffic"));
        function.setVnfdId("a49ef787-aaba-4a06-a677-b30a2e883562");
        function.setVnfdVersion("1.0");

        ConnectionPoint cp1 = ConnectionPointTest.makeFirewallDemobject1();
        ConnectionPoint cp2 = ConnectionPointTest.makeFirewallDemobject2();
        ConnectionPoint cp3 = ConnectionPointTest.makeFirewallDemobject3();
        ConnectionPoint cp4 = ConnectionPointTest.makeFirewallDemobject4();

        function.setConnectionPoint(new HashSet<>(Arrays.asList(cp1, cp2, cp3, cp4)));
        return function;
    }

    public static SdkFunction makeNS2MiniwebObject() {
        Map<String, String> metadata = new HashMap<>();
        SdkFunction function = new SdkFunction();
        function.setName("miniweb-server-uc1-ns2");
        function.setVersion("1.0");
        function.setVendor("Nextworks");
        function.setDescription("miniweb-server-uc1-ns2");
        function.setMetadata(metadata);
        function.setFlavourExpression("IF(isVideo != 0, video_flv_srv, standard_flv_srv)");
        function.setInstantiationLevelExpression("IF(size <= 1, small_il, IF(size <= 10, medium_il, big_il))");
        function.setParameters(Arrays.asList("isVideo", "size"));
        function.setVnfdId("057289e2-7b8e-4280-8734-43b924f64b85");
        function.setVnfdVersion("1.0");

        ConnectionPoint cp1 = ConnectionPointTest.makeMiniwebDemobject1();

        function.setConnectionPoint(new HashSet<>(Arrays.asList(cp1)));
        return function;
    }




//    public static SdkFunction makeTestObject(Long id, Long cpId) {
//        SdkFunction function = makeTestObject();
//        function.setConnectionPoint(Collections.singleton(ConnectionPointTest.makeTestObjectId(cpId)));
//        function.setId(id);
//        return function;
//    }

//    @Test
//    @Ignore // requires DB
//    public void testCascadePersist() {
//
//        SdkFunction function = makeTestObject();
//
//        assertEquals(4, function.getConnectionPoint().size());
//
//        functionRepository.saveAndFlush(function);
//
//        Optional<SdkFunction> back = functionRepository.findById(function.getId());
//
//        assertTrue(back.isPresent());
//
//        SdkFunction function2 = back.get();
//        assertEquals(4, function2.getConnectionPoint().size());
//        assertEquals(function.getConnectionPoint(), function2.getConnectionPoint());
//        assertEquals(function, function2);
//    }

    @Test
    //@Ignore // requires DB
    public void testCityService() {

        SdkFunction ns1Firewall = makeNS1FirewallObject();

        SdkFunction ns2Firewall = makeNS2FirewallObject();

        SdkFunction minicache = makeNS2MiniwebObject();

        SdkFunction vPlate = makeNS1vPlateObject();

        assertTrue(ns1Firewall.isValid());
        assertTrue(ns2Firewall.isValid());
        assertTrue(minicache.isValid());
        assertTrue(vPlate.isValid());

        functionRepository.saveAndFlush(ns1Firewall);
        functionRepository.saveAndFlush(vPlate);
        functionRepository.saveAndFlush(ns2Firewall);
        functionRepository.saveAndFlush(minicache);

        Optional<SdkFunction> mwb = functionRepository.findById(minicache.getId());
        Optional<SdkFunction> vwb = functionRepository.findById(vPlate.getId());
        Optional<SdkFunction> f1wb = functionRepository.findById(ns1Firewall.getId());
        Optional<SdkFunction> f2wb = functionRepository.findById(ns2Firewall.getId());

        assertTrue(mwb.isPresent());
        assertTrue(vwb.isPresent());
        assertTrue(f1wb.isPresent());
        assertTrue(f2wb.isPresent());

        SdkFunction mw2 = mwb.get();
        assertEquals(minicache, mw2);

        SdkFunction f1w2 = f1wb.get();
        assertEquals(ns1Firewall, f1w2);

        SdkFunction vw2 = vwb.get();
        assertEquals(vPlate, vw2);

        SdkFunction f2w2 = f2wb.get();
        assertEquals(ns2Firewall, f2w2);
    }

}
