package it.nextworks.composer.adaptor.expression;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.nextworks.composer.adaptor.interfaces.ServicesAdaptorProviderInterface;
import it.nextworks.composer.executor.repositories.SdkFunctionRepository;
import it.nextworks.composer.executor.repositories.SdkServiceDescriptorRepository;
import it.nextworks.composer.executor.repositories.SdkServiceRepository;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.sdk.ConnectionPoint;
import it.nextworks.sdk.SdkFunction;
import it.nextworks.sdk.SdkFunctionTest;
import it.nextworks.sdk.SdkService;
import it.nextworks.sdk.SdkServiceDescriptor;
import it.nextworks.sdk.SdkServiceTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Marco Capitani on 13/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ExpressionAdapterTest {

    @Autowired
    @Qualifier("expressionAdapter")
    private ServicesAdaptorProviderInterface adapter;

    @Autowired
    private SdkFunctionRepository functionRepo;

    @Autowired
    private SdkServiceRepository serviceRepo;

    @Autowired
    private SdkServiceDescriptorRepository instanceRepo;

    private SdkFunction function;

    private SdkService service;

    @Before
    public void setupService() {
//        function = SdkFunctionTest.makeTestObject();
//
//        assertTrue(function.isValid());
//
//        functionRepo.saveAndFlush(function);
//
//        ConnectionPoint cp = function.getConnectionPoint().iterator().next();
//
//        Map<String, Long> cps = Collections.singletonMap(cp.getName(), cp.getId());
//
//        service = SdkServiceTest.makeTestObject(
//            function.getId(),
//            Arrays.asList("param1", "param2"),
//            cps
//        );
//
//        assertTrue(service.isValid());
//
//        service.resolveComponents(Collections.singleton(function), new HashSet<>());
//
//        serviceRepo.saveAndFlush(service);
    }

    @Test
    @Ignore // needs DB
    public void instantiateSdkService() {
        SdkServiceDescriptor instance = adapter.createServiceDescriptor(
            service,
            Arrays.asList(new BigDecimal(1), new BigDecimal(1))
        );
        instanceRepo.saveAndFlush(instance);
    }


    @Test
    @Ignore // needs DB
    public void instantiateSdkServiceCity() throws Exception {
        URL resource = this.getClass().getClassLoader().getResource("cityService.json");
        ObjectMapper mapper = new ObjectMapper();
        SdkService service = mapper.readValue(resource, SdkService.class);

        HashSet<SdkFunction> sdkFunctions = new HashSet<>(functionRepo.findAll());
        service.resolveComponents(sdkFunctions, Collections.emptySet());

        serviceRepo.saveAndFlush(service);
        // TODO push functions in DB
        SdkServiceDescriptor instance = adapter.createServiceDescriptor(
            service,
            Arrays.asList(new BigDecimal(1), new BigDecimal(1))
        );
        instanceRepo.saveAndFlush(instance);
        System.out.println(instance.getId());
    }

    @Test
    @Ignore // needs DB
    public void generateNetworkServiceDescriptor() throws Exception {
        SdkServiceDescriptor instance = adapter.createServiceDescriptor(
            service,
            Arrays.asList(new BigDecimal(0), new BigDecimal(0))
        );
        instanceRepo.saveAndFlush(instance);
        DescriptorTemplate descriptorTemplate = adapter.generateNetworkServiceDescriptor(instance);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.writeValue(System.out, descriptorTemplate);
    }

}