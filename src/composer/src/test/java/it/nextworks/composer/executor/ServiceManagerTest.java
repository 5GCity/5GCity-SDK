package it.nextworks.composer.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.nextworks.composer.adaptor.interfaces.ServicesAdaptorProviderInterface;
import it.nextworks.composer.executor.interfaces.FunctionManagerProviderInterface;
import it.nextworks.composer.executor.repositories.CatalogueRepository;
import it.nextworks.composer.executor.repositories.ConnectionpointRepository;
import it.nextworks.composer.executor.repositories.LinkRepository;
import it.nextworks.composer.executor.repositories.MonitoringParameterRepository;
import it.nextworks.composer.executor.repositories.ScalingAspectRepository;
import it.nextworks.composer.executor.repositories.SdkFunctionRepository;
import it.nextworks.composer.executor.repositories.SdkServiceInstanceRepository;
import it.nextworks.composer.executor.repositories.SdkServiceRepository;
import it.nextworks.composer.plugins.catalogue.FiveGCataloguePlugin;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.sdk.SdkFunction;
import it.nextworks.sdk.SdkFunctionTest;
import it.nextworks.sdk.SdkService;
import it.nextworks.sdk.SdkServiceInstance;
import it.nextworks.sdk.SdkServiceTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.task.TaskExecutor;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Marco Capitani on 10/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceManagerTest {

    @Mock
    private ServicesAdaptorProviderInterface adapter;

    @Mock
    private SdkServiceRepository serviceRepository;

    @Mock
    private SdkServiceInstanceRepository serviceInstanceRepository;

    @Mock
    private SdkFunctionRepository functionRepository;

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private ConnectionpointRepository cpRepository;

    @Mock
    private MonitoringParameterRepository monitoringParamRepository;

    @Mock
    private ScalingAspectRepository scalingRepository;

    @Mock
    private FunctionManagerProviderInterface functionManager;

    @Mock
    private CatalogueRepository catalogueRepo;

    @Mock
    private FiveGCataloguePlugin cataloguePlugin;

    @Mock
    private TaskExecutor executor;

    @InjectMocks
    ServiceManager manager;

    @Test
    public void getServices() {
        // Setup
        List<SdkService> returnedList = Arrays.asList(
            SdkServiceTest.makeTestObject(1L, Arrays.asList("p1", "p2"), Collections.singletonMap("1", 1L)),
            SdkServiceTest.makeTestObject(2L, Arrays.asList("p3", "p4", "p5"), Collections.singletonMap("2", 2L))
        );
        when(serviceRepository.findAll()).thenReturn(returnedList);

        // Main test
        List<SdkService> services = manager.getServices();
        assertEquals(returnedList, services);
    }
/*
    @Test
    public void createService() throws Exception {
        // Setup
        SdkFunction function = SdkFunctionTest.makeTestObject(1L, 2L);
        when(functionManager.getFunctions()).thenReturn(Collections.singletonList(function));
        when(serviceRepository.findAll()).thenReturn(Collections.emptyList());
        SdkService service = SdkServiceTest.makeTestObject(
            1L,
            Arrays.asList("param1", "param2"),
            Collections.singletonMap("VIDEO", 2L)
        );
        when(serviceRepository.saveAndFlush(service))
            .then(m -> SdkServiceTest.setServiceId(service, 3L));

        // Call
        String ret = manager.createService(service);
        assertEquals("3", ret);
    }

    @Test
    @Ignore // Depends on functions etc...
    public void createServiceFromJson() throws Exception {
        // Setup
        URL resource = this.getClass().getClassLoader().getResource("s_req.json");
        ObjectMapper mapper = new ObjectMapper();
        SdkService service = mapper.readValue(resource, SdkService.class);
        SdkFunction function = SdkFunctionTest.makeTestObject(1L, 2L);
        when(functionManager.getFunctions()).thenReturn(Collections.singletonList(function));
        when(serviceRepository.findAll()).thenReturn(Collections.emptyList());
        when(serviceRepository.saveAndFlush(service))
            .then(m -> SdkServiceTest.setServiceId(service, 3L));

        // Call
        String ret = manager.createService(service);
        assertEquals("3", ret);
    }
*/
    @Test
    public void updateService() {
    }

    @Test
    public void getServiceById() {
    }

    @Test
    public void deleteService() {
    }
/*
    @Test
    public void publishService() throws Exception {

        // Setup
        Long serviceId = 1L;
        SdkService service = SdkServiceTest.setServiceId(SdkServiceTest.makeTestObject(
            1L,
            Arrays.asList("param1", "param2"),
            Collections.singletonMap("VIDEO", 2L)
            ),
            serviceId
        );

        SdkFunction function = SdkFunctionTest.makeTestObject(1L, 2L);

        service.resolveComponents(Collections.singleton(function), new HashSet<>());

        List<BigDecimal> paramValues = Arrays.asList(new BigDecimal(1), new BigDecimal(1));
        SdkServiceInstance instance = service.instantiate(paramValues);

        Long instanceId = 4L;

        DescriptorTemplate fakeNsd = new DescriptorTemplate();

        when(serviceRepository.findById(serviceId))
            .thenReturn(Optional.of(service));
        when(adapter.instantiateSdkService(service, paramValues))
            .thenReturn(instance);
        when(adapter.generateNetworkServiceDescriptor(any()))
            .thenReturn(fakeNsd);
        when(serviceInstanceRepository.saveAndFlush(instance))
            .thenReturn(SdkServiceTest.setInstanceId(instance, instanceId)); // ID needed?

        // test
        String ret = manager.publishService(serviceId, paramValues);

        // verification
        assertEquals("4", ret);

        verify(serviceRepository).findById(serviceId);
        verify(adapter).instantiateSdkService(service, paramValues);
        verify(serviceInstanceRepository).saveAndFlush(instance);
        verify(executor).execute(any());
    }
*/
    @Test
    public void unPublishService() {
    }

    @Test
    public void updateScalingAspect() {
    }

    @Test
    public void deleteScalingAspect() {
    }

    @Test
    public void getScalingAspect() {
    }

    @Test
    public void updateMonitoringParameters() {
    }

    @Test
    public void deleteMonitoringParameters() {
    }

    @Test
    public void getMonitoringParameters() {
    }

}