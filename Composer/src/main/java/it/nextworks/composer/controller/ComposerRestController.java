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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.composer.executor.FunctionManager;
import it.nextworks.composer.executor.ServiceManager;
import it.nextworks.sdk.SDKFunction;
import it.nextworks.sdk.SDKService;
import it.nextworks.sdk.exceptions.AlreadyPublishedServiceException;
import it.nextworks.sdk.exceptions.ExistingEntityException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.NotPublishedServiceException;

@RestController
@CrossOrigin
@RequestMapping("/sdk/composer")
@Api(value="SDK NBI", description="Operations on Composer Module")
public class ComposerRestController {

	private static final Logger log = LoggerFactory.getLogger(ComposerRestController.class);
	
	@Autowired
	private FunctionManager functionManager;
	
	@Autowired
	private ServiceManager serviceManager;
	
	
	public ComposerRestController() {
		
	}

	/**
	 * The method returns the full list of functions stored in the local database
	 * @return functionList List<Function>
	 */
	@ApiOperation(value = "Get the complete list of the SDKFunctions available in database",response = SDKFunction.class, responseContainer = "List")
	@ApiResponses(value = {
		      @ApiResponse(code = 200, message = "")
	})
	@RequestMapping(value = "/functions", method = RequestMethod.GET)
	public ResponseEntity<?> getFunctions() {
		log.info("Request for get FUNCTIONS");
		List<SDKFunction> response = new ArrayList<>();
		response = functionManager.getFunctions();
		return new ResponseEntity<List<SDKFunction>>(response, HttpStatus.OK);
	}
	
	/**
	 * 
	 * @param functionId Id of the function to be returned
	 * @return function
	 */
	@ApiOperation(value = "Search a SDKFunction with an UUID",response = SDKFunction.class)
	@ApiResponses(value = {
		      @ApiResponse(code = 400, message = "Query without parameter functionId"),
		      @ApiResponse(code = 404, message = "SDKFunction not found on database"),
		      @ApiResponse(code = 200, message = "")
	})
	@RequestMapping(value = "/function/{functionId}", method = RequestMethod.GET)
	public ResponseEntity<?> getFunction(@PathVariable UUID functionId) {
		log.info("Receiving a request to get the function identified by id: " + functionId);
		if(functionId == null) {
			return new ResponseEntity<String>("Query without parameter functionId", HttpStatus.BAD_REQUEST);
		} else {
			try {
				SDKFunction result = functionManager.getFunction(functionId);
				return new ResponseEntity<SDKFunction>(result, HttpStatus.OK);
			} catch(NotExistingEntityException e) {
				log.debug("The SDK function identified by the functionId provided is not present");
				return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
			}
		}
	}
	
	
	/**
	 * The method returns the list of services available on the local db
	 * @return servicesList List<Service>
	 */
	@ApiOperation(value = "Get the complete list of the SDKServices available in database",response = SDKService.class, responseContainer = "List" )
	@ApiResponses(value = {
		      @ApiResponse(code = 200, message = "")
	})
	@RequestMapping(value = "/services", method = RequestMethod.GET)
	public ResponseEntity<?> getServices() {
		log.info("Request for get SERVICES");
		List<SDKService> response = new ArrayList<>();
		response = serviceManager.getServices();
		return new ResponseEntity<List<SDKService>>(response, HttpStatus.OK);
	}
	
	/**
	 * The method return a specific service identified by the given serviceId
	 * @param serviceId Id of the service to be returned
	 * @return service 
	 */
	@ApiOperation(value = "Search a SDKService with an UUID",response = SDKService.class)
	@ApiResponses(value = {
		      @ApiResponse(code = 400, message = "Query without parameter serviceId"),
		      @ApiResponse(code = 404, message = "SDKService not found on database"),
		      @ApiResponse(code = 200, message = "")
	})
	@RequestMapping(value = "/service/{serviceId}", method = RequestMethod.GET)
	public ResponseEntity<?> getService(@PathVariable UUID serviceId) {
		log.info("Request for get specific service id: " + serviceId);
		if(serviceId == null) {
			log.error("Query without parameter serviceId");
			return new ResponseEntity<String>("Query without parameter functionId", HttpStatus.BAD_REQUEST);
		} else {
			try {
				SDKService response = serviceManager.getServiceByUuid(serviceId);
				return new ResponseEntity<SDKService>(response, HttpStatus.OK);
			} catch(NotExistingEntityException e) {
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
		      @ApiResponse(code = 201, message = "Service Created")
	})
	@RequestMapping(value = "/service", method = RequestMethod.POST)
	public ResponseEntity<?> createService(SDKService request) {
		log.info("Request for creation of a new service");
		if(request.isValid()) {
			try {
				serviceManager.createService(request);
				log.debug("Service entity created");
				return new ResponseEntity<>(HttpStatus.CREATED);
			} catch(ExistingEntityException e) {
				log.error("Service with id " + request.getUuid() + " is already present in database");
				return new ResponseEntity<String>("Service with id " + request.getUuid() + " is already present in database", HttpStatus.BAD_REQUEST);
			}
		} else {
			log.error("The service provided cannot be validated");
			return new ResponseEntity<String>("The service provided cannot be validated", HttpStatus.BAD_REQUEST);
		}
	}
	
	@ApiOperation(value = "Create a new Service")
	@ApiResponses(value = {
		      @ApiResponse(code = 400, message = "Service not present in db or service cannot be validated"),
		      @ApiResponse(code = 204, message = "Service Updated")
	})
	@RequestMapping(value = "/service", method = RequestMethod.PUT)
	public ResponseEntity<?> updateService(SDKService request) {
		log.info("Request for update of a service");
		if(request.isValid()) {
			try {
				serviceManager.updateService(request);
				log.debug("Service entity updated");
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} catch(NotExistingEntityException e) {
				log.error("Service with id " + request.getUuid() + " is not present in database");
				return new ResponseEntity<String>("Service with id " + request.getUuid() + " is not present in database", HttpStatus.BAD_REQUEST);
			}
		} else {
			log.error("The service provided cannot be validated");
			return new ResponseEntity<String>("The service provided cannot be validated", HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@ApiOperation(value = "Delete Service From database")
	@ApiResponses(value = {
		      @ApiResponse(code = 204, message = ""),
		      @ApiResponse(code = 404, message = "Entity to be deleted not found"),
		      @ApiResponse(code = 400, message = "Deletion request without parameter serviceId")
	})
	@RequestMapping(value = "/service/{serviceId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> updateService(@PathVariable UUID serviceId) {
		log.info("Request for deletion of a service with id: " + serviceId);
		if(serviceId == null) {
			log.error("Deletion request without parameter serviceId");
			return new ResponseEntity<String>("Deletion request without parameter serviceId", HttpStatus.BAD_REQUEST);
		} else {
			try {
				serviceManager.deleteService(serviceId);
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} catch(NotExistingEntityException e) {
				log.error("Requested deletion for an entity which doesn't exist");
				return new ResponseEntity<String>("Requested deletion for an entity which doesn't exist", HttpStatus.NOT_FOUND);
			}
		}
	}
	
	
	@ApiOperation(value = "Publish Service to Public Catalogue")
	@ApiResponses(value = {
		      @ApiResponse(code = 202, message = "The service will be published to the public catalogue"),
		      @ApiResponse(code = 404, message = "Entity to be published not found"),
		      @ApiResponse(code = 400, message = "Publication request without parameter serviceId or already published service")
	})
	@RequestMapping(value = "/service/{serviceId}/publish", method = RequestMethod.PUT)
	public ResponseEntity<?> publishService(@PathVariable UUID serviceId){
		log.info("Request to publish the service " + serviceId + " to the public catalogue");
		if(serviceId == null) {
			log.error("Publishing request without parameter serviceId");
			return new ResponseEntity<String>("Publishing request without parameter serviceId", HttpStatus.BAD_REQUEST);
		} else {
			try {
				serviceManager.publishService(serviceId);
				return new ResponseEntity<>(HttpStatus.ACCEPTED);
			} catch(NotExistingEntityException e1) {
				log.error("Requested publication for an entity which doesn't exist");
				return new ResponseEntity<String>("Requested publication for an entity which doesn't exist", HttpStatus.NOT_FOUND);
			} catch(AlreadyPublishedServiceException e2) {
				log.error("Requested publication for an entity already has been published");
				return new ResponseEntity<String>("Requested publication for an entity already has been published", HttpStatus.BAD_REQUEST);
			}
		}
		
	}
	
	
	@ApiOperation(value = "Unpublish Service from Public Catalogue")
	@ApiResponses(value = {
		      @ApiResponse(code = 202, message = "The service will be removed from the public catalogue"),
		      @ApiResponse(code = 404, message = "Entity to be unpublished not found"),
		      @ApiResponse(code = 400, message = "Request without parameter serviceId or not yet published service")
	})
	@RequestMapping(value = "/service/{serviceId}/unpublish", method = RequestMethod.PUT)
	public ResponseEntity<?> unPublishService(@PathVariable UUID serviceId){
		log.info("Request to unpublish the service " + serviceId + " from the public catalogue");
		if(serviceId == null) {
			log.error("Request without parameter serviceId");
			return new ResponseEntity<String>("Request without parameter serviceId", HttpStatus.BAD_REQUEST);
		} else {
			try {
				serviceManager.unPublishService(serviceId);
				return new ResponseEntity<>(HttpStatus.ACCEPTED);
			} catch(NotExistingEntityException e1) {
				log.error("Requested deletion of publication for an entity which doesn't exist");
				return new ResponseEntity<String>("Requested deletion of publication for an entity which doesn't exist", HttpStatus.NOT_FOUND);
			} catch(NotPublishedServiceException e2) {
				log.error("Requested publication for an entity that has not been published yet");
				return new ResponseEntity<String>("Requested publication for an entity that has not been published yet", HttpStatus.BAD_REQUEST);
			}
		}
		
	}
}
