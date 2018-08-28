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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.composer.executor.ServiceManager;
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.SDKService;
import it.nextworks.sdk.ScalingAspect;
import it.nextworks.sdk.exceptions.AlreadyPublishedServiceException;
import it.nextworks.sdk.exceptions.ExistingEntityException;
import it.nextworks.sdk.exceptions.MalformattedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.NotPublishedServiceException;

@RestController
@CrossOrigin
@RequestMapping("/sdk/composer")
@Api(value = "SDK NBI", description = "Operations on Composer Module - SDKService APIs")
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
	@ApiOperation(value = "Get the complete list of the SDKServices available in database", response = SDKService.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "") })
	@RequestMapping(value = "/services", method = RequestMethod.GET)
	public ResponseEntity<?> getServices() {
		log.info("Request for get SERVICES");
		List<SDKService> response = new ArrayList<>();
		response = serviceManager.getServices();
		return new ResponseEntity<List<SDKService>>(response, HttpStatus.OK);
	}

	/**
	 * The method return a specific service identified by the given serviceId
	 * 
	 * @param serviceId Id of the service to be returned
	 * @return service
	 */
	@ApiOperation(value = "Search a SDKService with an UUID", response = SDKService.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Query without parameter serviceId"),
			@ApiResponse(code = 404, message = "SDKService not found on database"),
			@ApiResponse(code = 200, message = "") })
	@RequestMapping(value = "/service/{serviceId}", method = RequestMethod.GET)
	public ResponseEntity<?> getService(@PathVariable UUID serviceId) {
		log.info("Request for get specific service id: " + serviceId);
		if (serviceId == null) {
			log.error("Query without parameter serviceId");
			return new ResponseEntity<String>("Query without parameter functionId", HttpStatus.BAD_REQUEST);
		} else {
			try {
				SDKService response = serviceManager.getServiceByUuid(serviceId);
				return new ResponseEntity<SDKService>(response, HttpStatus.OK);
			} catch (NotExistingEntityException e) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		}
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "Create a new Service")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Service already present in db or service cannot be validated"),
			@ApiResponse(code = 201, message = "Service Created") })
	@RequestMapping(value = "/service", method = RequestMethod.POST)
	public ResponseEntity<?> createService(@RequestBody SDKService request) {
		log.info("Request for creation of a new service");
		if (request.isValid()) {
			try {
				serviceManager.createService(request);
				log.debug("Service entity created");
				return new ResponseEntity<>(HttpStatus.CREATED);
			} catch (ExistingEntityException e) {
				log.error("Service with id " + request.getUuid() + " is already present in database");
				return new ResponseEntity<String>(
						"Service with id " + request.getUuid() + " is already present in database",
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
			@ApiResponse(code = 204, message = "Service Updated") })
	@RequestMapping(value = "/service", method = RequestMethod.PUT)
	public ResponseEntity<?> updateService(@RequestBody SDKService request) {
		log.info("Request for update of a service");
		if (request.isValid()) {
			try {
				serviceManager.updateService(request);
				log.debug("Service entity updated");
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} catch (NotExistingEntityException e) {
				log.error("Service with id " + request.getUuid() + " is not present in database");
				return new ResponseEntity<String>(
						"Service with id " + request.getUuid() + " is not present in database", HttpStatus.BAD_REQUEST);
			}
		} else {
			log.error("The service provided cannot be validated");
			return new ResponseEntity<String>("The service provided cannot be validated", HttpStatus.BAD_REQUEST);
		}

	}

	@ApiOperation(value = "Delete Service From database")
	@ApiResponses(value = { @ApiResponse(code = 204, message = ""),
			@ApiResponse(code = 404, message = "Entity to be deleted not found"),
			@ApiResponse(code = 400, message = "Deletion request without parameter serviceId") })
	@RequestMapping(value = "/service/{serviceId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> updateService(@PathVariable UUID serviceId) {
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

	@ApiOperation(value = "Publish Service to Public Catalogue")
	@ApiResponses(value = { @ApiResponse(code = 202, message = "The service will be published to the public catalogue"),
			@ApiResponse(code = 404, message = "Entity to be published not found"),
			@ApiResponse(code = 400, message = "Publication request without parameter serviceId or already published service") })
	@RequestMapping(value = "/service/{serviceId}/publish", method = RequestMethod.PUT)
	public ResponseEntity<?> publishService(@PathVariable UUID serviceId) {
		log.info("Request to publish the service " + serviceId + " to the public catalogue");
		if (serviceId == null) {
			log.error("Publishing request without parameter serviceId");
			return new ResponseEntity<String>("Publishing request without parameter serviceId", HttpStatus.BAD_REQUEST);
		} else {
			try {
				serviceManager.publishService(serviceId);
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
	@ApiResponses(value = { @ApiResponse(code = 202, message = "The service will be removed from the public catalogue"),
			@ApiResponse(code = 404, message = "Entity to be unpublished not found"),
			@ApiResponse(code = 400, message = "Request without parameter serviceId or not yet published service") })
	@RequestMapping(value = "/service/{serviceId}/unpublish", method = RequestMethod.PUT)
	public ResponseEntity<?> unPublishService(@PathVariable UUID serviceId) {
		log.info("Request to unpublish the service " + serviceId + " from the public catalogue");
		if (serviceId == null) {
			log.error("Request without parameter serviceId");
			return new ResponseEntity<String>("Request without parameter serviceId", HttpStatus.BAD_REQUEST);
		} else {
			try {
				serviceManager.unPublishService(serviceId);
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
			@ApiResponse(code = 204, message = "ScalingAspects Updated") })
	@RequestMapping(value = "/service/{serviceId}/scalingaspects", method = RequestMethod.PUT)
	public ResponseEntity<?> updateScalingAspects(@PathVariable UUID serviceId, @RequestBody List<ScalingAspect> scalingAspects) {
		log.info("Request for update of a scaling aspect list");
		for (ScalingAspect scaleAspect : scalingAspects) {
			if (!scaleAspect.isValid()) {
				log.error("ScalingAspect provided cannot be validated");
				return new ResponseEntity<String>("ScalingAspect provided cannot be validated", HttpStatus.BAD_REQUEST);
			}
		}
		try {
			serviceManager.updateScalingAspect(serviceId, scalingAspects);
			log.debug("Service entity updated with the requested scaling aspects");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (NotExistingEntityException e1) {
			log.error("Service with id " + serviceId.toString() + " is not present in database");
			return new ResponseEntity<String>("Service with id " + serviceId.toString() + " is not present in database",
					HttpStatus.BAD_REQUEST);
		} catch (MalformattedElementException e2) {
			log.error("Malformed format for element Scaling Aspect. Unable to update service: " + serviceId.toString());
			return new ResponseEntity<String>(
					"Malformed format for element Scaling Aspect. Unable to update service: " + serviceId.toString(),
					HttpStatus.BAD_REQUEST);
		}
	}

	@ApiOperation(value = "Get the list of  ScalingAspects for a given SDKService identified by UUID", response = ScalingAspect.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Query without parameter serviceId"),
			@ApiResponse(code = 404, message = "SDKService not found on database"),
			@ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/service/{serviceId}/scalingaspects", method = RequestMethod.GET)
	public ResponseEntity<?> getScalingAspectsForService(@PathVariable UUID serviceId) {
		log.info("Request for get list of scalingAspect available on a specific service, identified by id: "
				+ serviceId.toString());

		try {
			List<ScalingAspect> response = serviceManager.getScalingAspect(serviceId);
			log.debug("Returning list of scaling aspect related to a specific SDK Service identified by uuid: "
					+ serviceId.toString());
			return new ResponseEntity<List<ScalingAspect>>(response, HttpStatus.OK);
		} catch (NotExistingEntityException e) {
			log.debug("SDKService with uuid " + serviceId.toString() + " not found on database ");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation(value = "Delete ScalingAspect list from SDKService")
	@ApiResponses(value = { @ApiResponse(code = 204, message = ""),
			@ApiResponse(code = 404, message = "Entity to be deleted not found"),
			@ApiResponse(code = 400, message = "Deletion request without parameter serviceId") })
	@RequestMapping(value = "/service/{serviceId}/scalingaspects", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteScalingAspects(@PathVariable UUID serviceId, @RequestBody List<ScalingAspect> scalingAspects) {
		log.info("Request for deletion of a list of scalingAspects from service identified by id: "
				+ serviceId.toString());

		try {
			serviceManager.deleteScalingAspect(serviceId, scalingAspects);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (NotExistingEntityException e) {
			log.error("Requested deletion for an entity which doesn't exist");
			return new ResponseEntity<String>("Requested deletion for an entity which doesn't exist",
					HttpStatus.NOT_FOUND);
		} catch (MalformattedElementException e) {
			log.error("Malformed Request. Scaling Aspect parameters are malformed");
			return new ResponseEntity<String>("Malformed Request. Scaling Aspect parameters are malformed",
					HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation(value = "Modify an existing list of monitoring parameters related to a given SDKService")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Service not present in db or request cannot be validated"),
			@ApiResponse(code = 204, message = "Monitoring Param list Updated") })
	@RequestMapping(value = "/service/{serviceId}/monitoringparams", method = RequestMethod.PUT)
	public ResponseEntity<?> updateMonitoringParametersForService(@PathVariable UUID serviceId,
			@RequestBody List<MonitoringParameter> monitoringParameters) {
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
			log.error("Service with id " + serviceId.toString() + " is not present in database");
			return new ResponseEntity<String>("Service with id " + serviceId.toString() + " is not present in database",
					HttpStatus.BAD_REQUEST);
		} catch (MalformattedElementException e2) {
			log.error("Malformed format for element MonitoringParameter. Unable to update service: "
					+ serviceId.toString());
			return new ResponseEntity<String>(
					"Malformed format for element MonitoringParameter. Unable to update service: "
							+ serviceId.toString(),
					HttpStatus.BAD_REQUEST);
		}
	}

	@ApiOperation(value = "Get the list of  Monitoring Paramters for a given SDKService identified by UUID", response = MonitoringParameter.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Query without parameter serviceId"),
			@ApiResponse(code = 404, message = "SDKService not found on database"),
			@ApiResponse(code = 200, message = "OK") })
	@RequestMapping(value = "/service/{serviceId}/monitoringparams", method = RequestMethod.GET)
	public ResponseEntity<?> getMonitoringParametersForService(@PathVariable UUID serviceId) {
		log.info("Request for get list of monitoringParams available on a specific service, identified by id: "
				+ serviceId.toString());

		try {
			List<MonitoringParameter> response = serviceManager.getMonitoringParameters(serviceId);
			log.debug("Returning list of monitoringParams related to a specific SDK Service identified by uuid: "
					+ serviceId.toString());
			return new ResponseEntity<List<MonitoringParameter>>(response, HttpStatus.OK);
		} catch (NotExistingEntityException e) {
			log.debug("SDKService with uuid " + serviceId.toString() + " not found on database ");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation(value = "Delete monitoring param list from SDKService")
	@ApiResponses(value = { @ApiResponse(code = 204, message = ""),
			@ApiResponse(code = 404, message = "Entity to be deleted not found"),
			@ApiResponse(code = 400, message = "Deletion request without parameter serviceId") })
	@RequestMapping(value = "/service/{serviceId}/monitoringparams", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMonitoringParametersForService(@PathVariable UUID serviceId,
			@RequestBody List<MonitoringParameter> monitoringParameters) {
		log.info("Request for deletion of a list of monitoring parameters from service identified by id: "
				+ serviceId.toString());

		try {
			serviceManager.deleteMonitoringParameters(serviceId, monitoringParameters);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (NotExistingEntityException e) {
			log.error("Requested deletion for an entity which doesn't exist");
			return new ResponseEntity<String>("Requested deletion for an entity which doesn't exist",
					HttpStatus.NOT_FOUND);
		} catch (MalformattedElementException e) {
			log.error("Malformed Request. Monitoring parameters are malformed");
			return new ResponseEntity<String>("Malformed Request. Monitoring parameters are malformed",
					HttpStatus.NOT_FOUND);
		}

	}

}
