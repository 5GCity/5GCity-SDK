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
package it.nextworks.composer.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.composer.executor.ServiceManager;
import it.nextworks.nfvmano.libs.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.common.exceptions.NotPermittedOperationException;
import it.nextworks.sdk.*;
import it.nextworks.sdk.exceptions.MalformedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.NotPublishedServiceException;
import org.aspectj.weaver.ast.Not;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/sdk/services")
@Api(value = "SDK Service NBI", description = "Operations on SDK Composer Module - SDK Service APIs")
public class ServiceController {

    private static final Logger log = LoggerFactory.getLogger(ServiceController.class);

    @Autowired
    private ServiceManager serviceManager;

    public ServiceController() {

    }

    /**
     * The method returns the list of services available on the local db
     *
     * @return servicesList List<Service>
     */
    @ApiOperation(value = "Get the complete list of the SDK Services available in database", response = SdkService.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "")})
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<?> getServices() {
        log.info("Request for get services");
        List<SdkService> response = serviceManager.getServices();
        return new ResponseEntity<List<SdkService>>(response, HttpStatus.OK);
    }

    /**
     * The method return a specific service identified by the given serviceId
     *
     * @param serviceId Id of the service to be returned
     * @return service
     */
    @ApiOperation(value = "Search a SDK Service with ID", response = SdkService.class)
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Query without parameter serviceId"),
        @ApiResponse(code = 404, message = "SdkService not found in database"),
        @ApiResponse(code = 200, message = "")})
    @RequestMapping(value = "/{serviceId}", method = RequestMethod.GET)
    public ResponseEntity<?> getService(@PathVariable Long serviceId) {
        log.info("Request for get specific service with ID " + serviceId);
        if (serviceId == null) {
            log.error("Query without parameter serviceId");
            return new ResponseEntity<String>("Query without parameter serviceId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                SdkService response = serviceManager.getServiceById(serviceId);
                return new ResponseEntity<SdkService>(response, HttpStatus.OK);
            } catch (NotExistingEntityException e) {
                log.error(e.toString());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
    }

    /**
     * @param request
     * @return
     */
    @ApiOperation(value = "Create a new SDK Service")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "SDK Service already present in database or service cannot be validated"),
        @ApiResponse(code = 201, message = "SDK Service created")})
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<?> createService(@RequestBody SdkService request) {
        log.info("Request for creation of a new service");

        try {
            serviceManager.createService(request);
            log.debug("Service entity created");
            return new ResponseEntity<>(request.getId(), HttpStatus.CREATED);
        } catch (MalformedElementException | AlreadyExistingEntityException | NotExistingEntityException e) {
            log.error(e.toString());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Modify an existing SDK Service")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "SDK Service cannot be validated"),
        @ApiResponse(code = 404, message = "SDK Service not present in database"),
        @ApiResponse(code = 403, message = "SDK Service cannot be updated"),
        @ApiResponse(code = 204, message = "SDK Service updated")})
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public ResponseEntity<?> updateService(@RequestBody SdkService request) {
        log.info("Request for update of a service");

        try {
            serviceManager.updateService(request);
            log.debug("Service entity updated");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotExistingEntityException e) {
            log.error(e.toString());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (MalformedElementException e) {
            log.error(e.toString());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (NotPermittedOperationException e){
            log.error(e.toString());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(value = "Delete a SDK Service from database")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = ""),
        @ApiResponse(code = 404, message = "SDK Service not present in database"),
        @ApiResponse(code = 403, message = "SDK Service cannot be deleted"),
        @ApiResponse(code = 400, message = "Deletion request without parameter serviceId")})
    @RequestMapping(value = "/{serviceId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteService(@PathVariable Long serviceId) {
        log.info("Request for deletion of a service with id: " + serviceId);
        if (serviceId == null) {
            log.error("Deletion request without parameter serviceId");
            return new ResponseEntity<String>("Deletion request without parameter serviceId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                serviceManager.deleteService(serviceId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (NotExistingEntityException e) {
                log.error(e.toString());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
            }catch (NotPermittedOperationException e){
                log.error(e.toString());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
            }
        }
    }

    @ApiOperation(value = "Create descriptor for SDK Service")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = ""),
        @ApiResponse(code = 404, message = "SDK Service not found"),
        @ApiResponse(code = 400, message = "Create descriptor request without serviceId or provided parameters cannot be validated")})
    @RequestMapping(value = "/{serviceId}/create_descriptor", method = RequestMethod.POST)
    public ResponseEntity<?> createDescriptor(@PathVariable Long serviceId, @RequestBody MakeDescriptorRequest makeDescriptorRequest) {
        List<BigDecimal> parameterValues = makeDescriptorRequest.parameterValues;
        log.info("Request create descriptor of a service with ID {}, parameters : {}.", serviceId, parameterValues);
        if (serviceId == null) {
            log.error("Create descriptor request without parameter serviceId");
            return new ResponseEntity<>("Create descriptor request without parameter serviceId", HttpStatus.BAD_REQUEST);
        }
        try {
            String descriptorId = serviceManager.createServiceDescriptor(serviceId, parameterValues);
            return new ResponseEntity<>(descriptorId, HttpStatus.OK);
        } catch (NotExistingEntityException e) {
            log.error(e.toString());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (MalformedElementException e) {
            log.error(e.toString());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Publish SDK Service to Public Catalogue")
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "Network Service descriptor created. The descriptor will be published to the Public Catalogue"),
        @ApiResponse(code = 404, message = "SDK Service not present in database"),
        @ApiResponse(code = 403, message = "Not all components are published to the Public Catalogue"),
        @ApiResponse(code = 400, message = "Publish request without parameter serviceId or provided parameters cannot be validated")})
    @RequestMapping(value = "/service/{serviceId}/publish", method = RequestMethod.POST)
    public ResponseEntity<?> publishService(@PathVariable Long serviceId, @RequestBody MakeDescriptorRequest makeDescriptorRequest) {
        List<BigDecimal> parameterValues = makeDescriptorRequest.parameterValues;
        log.info("Request publication of a service with ID {}, parameters : {}.", serviceId, parameterValues);
        if (serviceId == null) {
            log.error("Publication request without parameter serviceId");
            return new ResponseEntity<>("Publication request without parameter serviceId", HttpStatus.BAD_REQUEST);
        }
        try {
            serviceManager.publishService(serviceId, parameterValues);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (NotExistingEntityException e) {
            log.error(e.toString());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (MalformedElementException e) {
            log.error(e.toString());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch(NotPermittedOperationException e){
            log.error(e.toString());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(value = "Modify an existing list of monitoring parameters related to a SDK Service")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "SDK Service or Monitoring Parameters cannot be validated"),
        @ApiResponse(code = 404, message = "SDK Service or Monitoring Parameters not present in database"),
        @ApiResponse(code = 403, message = "Monitoring Parameters cannot be updated"),
        @ApiResponse(code = 204, message = "Monitoring Parameters updated")})
    @RequestMapping(value = "/{serviceId}/monitoring_params", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMonitoringParametersForService(@PathVariable Long serviceId, @RequestBody MonitoringParameterWrapper monitoringParameters) {
        log.info("Request for update of a monitoringParameter list");

        if (serviceId == null) {
            log.error("Request without parameter serviceId");
            return new ResponseEntity<String>("Request without parameter serviceId", HttpStatus.BAD_REQUEST);
        }
        try {
            serviceManager.updateMonitoringParameters(serviceId, monitoringParameters.getExtMonitoringParameters(), monitoringParameters.getIntMonitoringParameters());
            log.debug("Service entity updated with the requested monitoring parameters");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotExistingEntityException e) {
            log.error(e.toString());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (MalformedElementException e) {
            log.error(e.toString());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(NotPermittedOperationException e){
            log.error(e.toString());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(value = "Get the list of  Monitoring Parameters for a SDK Service with ID", response = MonitoringParameterWrapper.class)
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Query without parameter serviceId"),
        @ApiResponse(code = 404, message = "SDK Service or Monitoring Parameters not present in database"),
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/{serviceId}/monitoring_params", method = RequestMethod.GET)
    public ResponseEntity<?> getMonitoringParametersForService(@PathVariable Long serviceId) {
        log.info("Request for get list of monitoringParams available on a specific service with ID " + serviceId);
        if (serviceId == null) {
            log.error("Request without parameter serviceId");
            return new ResponseEntity<String>("Request without parameter serviceId", HttpStatus.BAD_REQUEST);
        }
        try {
            MonitoringParameterWrapper response = serviceManager.getMonitoringParameters(serviceId);
            log.debug("Returning list of monitoringParams related to a specific service with " + serviceId);
            return new ResponseEntity<MonitoringParameterWrapper>(response, HttpStatus.OK);
        }  catch (NotExistingEntityException e) {
            log.debug(e.toString());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Delete monitoring param from SDK Service")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = ""),
        @ApiResponse(code = 404, message = "SDK Service or Monitoring Parameters not present in database"),
        @ApiResponse(code = 403, message = "Monitoring Parameters cannot be deleted"),
        @ApiResponse(code = 400, message = "Deletion request without parameter serviceId or service cannot be validated")})
    @RequestMapping(value = "/{serviceId}/monitoring_params/{monitoringParameterId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMonitoringParametersForService(@PathVariable Long serviceId, @PathVariable Long monitoringParameterId) {
        log.info("Request for deletion of monitoring parameter from service identified by id: " + serviceId);
        if (serviceId == null) {
            log.error("Request without parameter serviceId");
            return new ResponseEntity<String>("Request without parameter serviceId", HttpStatus.BAD_REQUEST);
        }
        try {
            serviceManager.deleteMonitoringParameters(serviceId, monitoringParameterId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotExistingEntityException e) {
            log.error(e.toString());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (NotPermittedOperationException e) {
            log.error(e.toString());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (MalformedElementException e) {
            log.error(e.toString());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

