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
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.ScalingAspect;
import it.nextworks.sdk.SdkService;
import it.nextworks.sdk.exceptions.AlreadyPublishedServiceException;
import it.nextworks.sdk.exceptions.ExistingEntityException;
import it.nextworks.sdk.exceptions.MalformedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.NotPublishedServiceException;
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
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/sdk/composer")
@Api(value = "Sdk NBI", description = "Operations on Composer Module - SdkService APIs")
public class ServiceRestController {

    private static final Logger log = LoggerFactory.getLogger(ServiceRestController.class);

    @Autowired
    private ServiceManager serviceManager;

    public ServiceRestController() {

    }

    /**
     * The method returns the list of services available on the local db
     *
     * @return servicesList List<Service>
     */
    @ApiOperation(value = "Get the complete list of the SdkServices available in database", response = SdkService.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "")})
    @RequestMapping(value = "/services", method = RequestMethod.GET)
    public ResponseEntity<?> getServices() {
        log.info("Request for get SERVICES");
        List<SdkService> response = serviceManager.getServices();
        log.info("Returned list for getServices with " + response.size() + " elements");
        return new ResponseEntity<List<SdkService>>(response, HttpStatus.OK);
    }

    /**
     * The method return a specific service identified by the given serviceId
     *
     * @param serviceId Id of the service to be returned
     * @return service
     */
    @ApiOperation(value = "Search a SdkService with an UUID", response = SdkService.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Query without parameter serviceId"),
        @ApiResponse(code = 404, message = "SdkService not found on database"),
        @ApiResponse(code = 200, message = "")})
    @RequestMapping(value = "/services/{serviceId}", method = RequestMethod.GET)
    public ResponseEntity<?> getService(@PathVariable Long serviceId) {
        log.info("Request for get specific service id: " + serviceId);
        if (serviceId == null) {
            log.error("Query without parameter serviceId");
            return new ResponseEntity<String>("Query without parameter functionId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                SdkService response = serviceManager.getServiceById(serviceId);
                return new ResponseEntity<SdkService>(response, HttpStatus.OK);
            } catch (NotExistingEntityException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
    }

    /**
     * @param request
     * @return
     */
    @ApiOperation(value = "Create a new Service")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Service already present in db or service cannot be validated"),
        @ApiResponse(code = 201, message = "Service Created")})
    @RequestMapping(value = "/services", method = RequestMethod.POST)
    public ResponseEntity<?> createService(@RequestBody SdkService request) {
        log.info("Request for creation of a new service");
        if (request.isValid()) {
            try {
                serviceManager.createService(request);
                log.debug("Service entity created");
                return new ResponseEntity<>(HttpStatus.CREATED);
            } catch (ExistingEntityException e) {
                log.error("Service with id " + request.getId() + " is already present in database");
                return new ResponseEntity<String>(
                    "Service with id " + request.getId() + " is already present in database",
                    HttpStatus.BAD_REQUEST);
            } catch (NotExistingEntityException e2) {
                log.error("Function id used to build one of the SdkFunctionInstances is not present in database");
                return new ResponseEntity<String>(
                    "Function id used to build one of the SdkFunctionInstances is not present in database",
                    HttpStatus.BAD_REQUEST);
            } catch (MalformedElementException e3) {
                log.error("Malformatted request");
                return new ResponseEntity<String>(
                    "Malformatted request",
                    HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("The service provided cannot be validated");
            return new ResponseEntity<String>("The service provided cannot be validated", HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Modify an existing Service")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Service not present in db or service cannot be validated"),
        @ApiResponse(code = 204, message = "Service Updated")})
    @RequestMapping(value = "/services", method = RequestMethod.PUT)
    public ResponseEntity<?> updateService(@RequestBody SdkService request) {
        log.info("Request for update of a service");
        if (request.isValid()) {
            try {
                serviceManager.updateService(request);
                log.debug("Service entity updated");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (NotExistingEntityException e) {
                log.error("Service with id " + request.getId() + " is not present in database");
                return new ResponseEntity<String>(
                    "Service with id " + request.getId() + " is not present in database", HttpStatus.BAD_REQUEST);
            } catch (MalformedElementException e1) {
                log.error("Service with id " + request.getId() + " is malformatted");
                return new ResponseEntity<String>(
                    "Service with id " + request.getId() + " is malformatted", HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("The service provided cannot be validated");
            return new ResponseEntity<String>("The service provided cannot be validated", HttpStatus.BAD_REQUEST);
        }

    }

    @ApiOperation(value = "Delete Service From database")
    @ApiResponses(value = {@ApiResponse(code = 204, message = ""),
        @ApiResponse(code = 404, message = "Entity to be deleted not found"),
        @ApiResponse(code = 400, message = "Deletion request without parameter serviceId")})
    @RequestMapping(value = "/services/{serviceId}", method = RequestMethod.DELETE)
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
                log.error("Requested deletion for an entity which doesn't exist");
                return new ResponseEntity<String>("Requested deletion for an entity which doesn't exist",
                    HttpStatus.NOT_FOUND);
            }
        }
    }

    @ApiOperation(value = "Instantiate Service")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ""),
        @ApiResponse(code = 404, message = "Entity to be instantiated not found"),
        @ApiResponse(code = 400, message = "Null service or invalid parameters provided")})
    @RequestMapping(value = "/services/{serviceId}/instantiate", method = RequestMethod.POST)
    public ResponseEntity<?> instantiateService(
        @PathVariable Long serviceId,
        @PathVariable List<BigDecimal> parameterValues
    ) {
        log.info("Request for instantiation of a service with id: {}, params: {}.", serviceId, parameterValues);
        if (serviceId == null) {
            log.error("Instantiation request without parameter serviceId");
            return new ResponseEntity<>(
                "Instantiation request without parameter serviceId",
                HttpStatus.BAD_REQUEST
            );
        }
        // else:
        try {
            String instanceId = serviceManager.instantiateService(serviceId, parameterValues);
            return new ResponseEntity<>(instanceId, HttpStatus.OK);
        } catch (NotExistingEntityException e) {
            return new ResponseEntity<>(
                e.getMessage(),
                HttpStatus.NOT_FOUND
            );
        } catch (MalformedElementException e) {
            return new ResponseEntity<>(
                e.getMessage(),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    @ApiOperation(value = "Publish Service to Public Catalogue")
    @ApiResponses(value = {
        @ApiResponse(
            code = 202,
            message = "Instance created with returned id. The service will be published to the public catalogue"
        ),
        @ApiResponse(code = 404, message = "Entity to be instantiated not found"),
        @ApiResponse(code = 400, message = "Null service or invalid parameters provided")})
    @RequestMapping(value = "/service/{serviceId}/publish", method = RequestMethod.POST)
    public ResponseEntity<?> publishService(@PathVariable Long serviceId, List<BigDecimal> parameterValues) {
        log.info(
            "Request for instantiation and publication of a service with id: {}, params: {}.",
            serviceId,
            parameterValues
        );
        if (serviceId == null) {
            log.error("Instantiation request without parameter serviceId");
            return new ResponseEntity<>(
                "Instantiation/publication request without parameter serviceId",
                HttpStatus.BAD_REQUEST
            );
        }
        // else:
        try {
            serviceManager.publishService(serviceId, parameterValues);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (NotExistingEntityException e) {
            return new ResponseEntity<>(
                e.getMessage(),
                HttpStatus.NOT_FOUND
            );
        } catch (MalformedElementException e) {
            return new ResponseEntity<>(
                e.getMessage(),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    @ApiOperation(value = "Publish Service to Public Catalogue")
    @ApiResponses(value = {@ApiResponse(code = 202, message = "The service will be published to the public catalogue"),
        @ApiResponse(code = 404, message = "Entity to be published not found"),
        @ApiResponse(code = 400, message = "Publication request without parameter serviceId or already published service")})
    @RequestMapping(value = "/service-instance/{serviceId}/publish", method = RequestMethod.POST)
    public ResponseEntity<?> publishService(@PathVariable Long serviceInstanceId) {
        log.info("Request to publish the service " + serviceInstanceId + " to the public catalogue");
        if (serviceInstanceId == null) {
            log.error("Publishing request without parameter serviceId");
            return new ResponseEntity<String>("Publishing request without parameter serviceId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                serviceManager.publishService(serviceInstanceId);
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            } catch (NotExistingEntityException e1) {
                log.error("Requested publication for an entity which doesn't exist");
                return new ResponseEntity<String>("Requested publication for an entity which doesn't exist",
                    HttpStatus.NOT_FOUND);
            } catch (AlreadyPublishedServiceException e2) {
                log.error("Requested publication for an entity already has been published");
                return new ResponseEntity<String>("Requested publication for an entity already has been published",
                    HttpStatus.BAD_REQUEST);
            }
        }
    }

    @ApiOperation(value = "Unpublish Service from Public Catalogue")
    @ApiResponses(value = {@ApiResponse(code = 202, message = "The service will be removed from the public catalogue"),
        @ApiResponse(code = 404, message = "Entity to be unpublished not found"),
        @ApiResponse(code = 400, message = "Request without parameter serviceId or not yet published service")})
    @RequestMapping(value = "/service-instance/{serviceId}/unpublish", method = RequestMethod.POST)
    public ResponseEntity<?> unPublishService(@PathVariable Long serviceInstanceId) {
        log.info("Request to unpublish the service " + serviceInstanceId + " from the public catalogue");
        if (serviceInstanceId == null) {
            log.error("Request without parameter serviceId");
            return new ResponseEntity<String>("Request without parameter serviceId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                serviceManager.unPublishService(serviceInstanceId);
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            } catch (NotExistingEntityException e1) {
                log.error("Requested deletion of publication for an entity which doesn't exist");
                return new ResponseEntity<String>("Requested deletion of publication for an entity which doesn't exist",
                    HttpStatus.NOT_FOUND);
            } catch (NotPublishedServiceException e2) {
                log.error("Requested publication for an entity that has not been published yet");
                return new ResponseEntity<String>("Requested publication for an entity that has not been published yet",
                    HttpStatus.BAD_REQUEST);
            }
        }

    }

    @ApiOperation(value = "Modify an existing list of scaling aspects")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Service not present in db or request cannot be validated"),
        @ApiResponse(code = 204, message = "ScalingAspects Updated")})
    @RequestMapping(value = "/services/{serviceId}/scalingaspects", method = RequestMethod.PUT)
    public ResponseEntity<?> updateScalingAspects(@PathVariable Long serviceId, @RequestBody Set<ScalingAspect> scalingAspects) {
        log.info("Request for update of a scaling aspect list");
        for (ScalingAspect scaleAspect : scalingAspects) {
            if (!scaleAspect.isValid()) {
                log.error("ScalingAspect provided cannot be validated");
                return new ResponseEntity<>("ScalingAspect provided cannot be validated", HttpStatus.BAD_REQUEST);
            }
        }
        try {
            serviceManager.updateScalingAspect(serviceId, scalingAspects);
            log.debug("Service entity updated with the requested scaling aspects");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotExistingEntityException e1) {
            log.error("Service with id " + serviceId + " is not present in database");
            return new ResponseEntity<String>("Service with id " + serviceId + " is not present in database",
                HttpStatus.BAD_REQUEST);
        } catch (MalformedElementException e2) {
            log.error("Malformed format for element Scaling Aspect. Unable to update service: " + serviceId);
            return new ResponseEntity<String>(
                "Malformed format for element Scaling Aspect. Unable to update service: " + serviceId,
                HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Get the list of  ScalingAspects for a given SdkService identified by UUID", response = ScalingAspect.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Query without parameter serviceId"),
        @ApiResponse(code = 404, message = "SdkService not found on database"),
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/services/{serviceId}/scalingaspects", method = RequestMethod.GET)
    public ResponseEntity<?> getScalingAspectsForService(@PathVariable Long serviceId) {
        log.info("Request for get list of scalingAspect available on a specific service, identified by id: "
            + serviceId);

        try {
            List<ScalingAspect> response = serviceManager.getScalingAspect(serviceId);
            log.debug("Returning list of scaling aspect related to a specific Sdk Service identified by uuid: "
                + serviceId);
            return new ResponseEntity<List<ScalingAspect>>(response, HttpStatus.OK);
        } catch (NotExistingEntityException e) {
            log.debug("SdkService with uuid " + serviceId + " not found on database ");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Delete ScalingAspect list from SdkService")
    @ApiResponses(value = {@ApiResponse(code = 204, message = ""),
        @ApiResponse(code = 404, message = "Entity to be deleted not found"),
        @ApiResponse(code = 400, message = "Deletion request without parameter serviceId")})
    @RequestMapping(value = "/services/{serviceId}/scalingaspects", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteScalingAspects(@PathVariable Long serviceId, @RequestBody Set<ScalingAspect> scalingAspects) {
        log.info("Request for deletion of a list of scalingAspects from service identified by id: "
            + serviceId);

        try {
            serviceManager.deleteScalingAspect(serviceId, scalingAspects);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotExistingEntityException e) {
            log.error("Requested deletion for an entity which doesn't exist");
            return new ResponseEntity<String>("Requested deletion for an entity which doesn't exist",
                HttpStatus.NOT_FOUND);
        } catch (MalformedElementException e) {
            log.error("Malformed Request. Scaling Aspect parameters are malformed");
            return new ResponseEntity<String>("Malformed Request. Scaling Aspect parameters are malformed",
                HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Modify an existing list of monitoring parameters related to a given SdkService")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Service not present in db or request cannot be validated"),
        @ApiResponse(code = 204, message = "Monitoring Param list Updated")})
    @RequestMapping(value = "/services/{serviceId}/monitoringparams", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMonitoringParametersForService(@PathVariable Long serviceId,
                                                                  @RequestBody Set<MonitoringParameter> monitoringParameters) {
        log.info("Request for update of a monitoringParameter list");
        for (MonitoringParameter param : monitoringParameters) {
            if (!param.isValid()) {
                log.error("Monitoring param list provided cannot be validated");
                return new ResponseEntity<String>("Monitoring param list provided cannot be validated",
                    HttpStatus.BAD_REQUEST);
            }
        }
        try {
            serviceManager.updateMonitoringParameters(serviceId, monitoringParameters);
            log.debug("Service entity updated with the requested monitoring parameters");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotExistingEntityException e1) {
            log.error("Service with id " + serviceId + " is not present in database");
            return new ResponseEntity<String>("Service with id " + serviceId + " is not present in database",
                HttpStatus.BAD_REQUEST);
        } catch (MalformedElementException e2) {
            log.error("Malformed format for element MonitoringParameter. Unable to update service: "
                + serviceId);
            return new ResponseEntity<String>(
                "Malformed format for element MonitoringParameter. Unable to update service: "
                    + serviceId,
                HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Get the list of  Monitoring Paramters for a given SdkService identified by UUID", response = MonitoringParameter.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Query without parameter serviceId"),
        @ApiResponse(code = 404, message = "SdkService not found on database"),
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/services/{serviceId}/monitoringparams", method = RequestMethod.GET)
    public ResponseEntity<?> getMonitoringParametersForService(@PathVariable Long serviceId) {
        log.info("Request for get list of monitoringParams available on a specific service, identified by id: "
            + serviceId);

        try {
            List<MonitoringParameter> response = serviceManager.getMonitoringParameters(serviceId);
            log.debug("Returning list of monitoringParams related to a specific Sdk Service identified by uuid: "
                + serviceId);
            return new ResponseEntity<List<MonitoringParameter>>(response, HttpStatus.OK);
        } catch (NotExistingEntityException e) {
            log.debug("SdkService with uuid " + serviceId + " not found on database ");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Delete monitoring param list from SdkService")
    @ApiResponses(value = {@ApiResponse(code = 204, message = ""),
        @ApiResponse(code = 404, message = "Entity to be deleted not found"),
        @ApiResponse(code = 400, message = "Deletion request without parameter serviceId")})
    @RequestMapping(value = "/services/{serviceId}/monitoringparams", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMonitoringParametersForService(@PathVariable Long serviceId,
                                                                  @RequestBody Set<MonitoringParameter> monitoringParameters) {
        log.info("Request for deletion of a list of monitoring parameters from service identified by id: "
            + serviceId);

        try {
            serviceManager.deleteMonitoringParameters(serviceId, monitoringParameters);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotExistingEntityException e) {
            log.error("Requested deletion for an entity which doesn't exist");
            return new ResponseEntity<String>("Requested deletion for an entity which doesn't exist",
                HttpStatus.NOT_FOUND);
        } catch (MalformedElementException e) {
            log.error("Malformed Request. Monitoring parameters are malformed");
            return new ResponseEntity<String>("Malformed Request. Monitoring parameters are malformed",
                HttpStatus.NOT_FOUND);
        }

    }

}
