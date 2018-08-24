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
import it.nextworks.composer.executor.FunctionInstanceManager;
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.SDKFunction;
import it.nextworks.sdk.SDKFunctionInstance;
import it.nextworks.sdk.enums.Flavour;
import it.nextworks.sdk.exceptions.MalformattedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;

@RestController
@CrossOrigin
@RequestMapping("/sdk/composer")
@Api(value="SDK NBI", description="Operations on Composer Module - SDKFunctionInstance APIs")
public class FunctionInstanceRestController {

	private static final Logger log = LoggerFactory.getLogger(FunctionInstanceRestController.class);
	
	
	@Autowired
	private FunctionInstanceManager functionInstanceManager;
	
	
	public FunctionInstanceRestController() {
		
	}
	
	/**
	 * 
	 * @param functionId Id of the function to be returned
	 * @return function
	 */
	@ApiOperation(value = "Search a SDKFunctionInstance with an UUID",response = SDKFunction.class)
	@ApiResponses(value = {
		      @ApiResponse(code = 400, message = "Query without parameter functionId"),
		      @ApiResponse(code = 404, message = "SDKFunctionInstance not found on database"),
		      @ApiResponse(code = 200, message = "")
	})
	@RequestMapping(value = "/function/instance/{functionId}", method = RequestMethod.GET)
	public ResponseEntity<?> getFunctionInstance(@PathVariable UUID functionId) {
		log.info("Receiving a request to get the functionInstance identified by id: " + functionId);
		if(functionId == null) {
			return new ResponseEntity<String>("Query without parameter functionId", HttpStatus.BAD_REQUEST);
		} else {
			try {
				SDKFunctionInstance result = functionInstanceManager.getFunction(functionId);
				return new ResponseEntity<SDKFunctionInstance>(result, HttpStatus.OK);
			} catch(NotExistingEntityException e) {
				log.debug("The SDK function instance identified by the functionId provided is not present");
				return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
			}
		}
	}
	
	
	/**
	 * The method returns the full list of functions stored in the local database
	 * @return functionList List<Function>
	 */
	@ApiOperation(value = "Get the complete list of the SDKFunctionsInstance available in database",response = SDKFunctionInstance.class, responseContainer = "List")
	@ApiResponses(value = {
		      @ApiResponse(code = 200, message = "")
	})
	@RequestMapping(value = "/function/instances", method = RequestMethod.GET)
	public ResponseEntity<?> getFunctionInstances() {
		log.info("Request for get FUNCTIONS");
		List<SDKFunctionInstance> response = new ArrayList<>();
		response = functionInstanceManager.getFunctions();
		return new ResponseEntity<List<SDKFunctionInstance>>(response, HttpStatus.OK);
	}
	
	
	/**
	 * The method returns the full list of functions stored in the local database generated from a given SDKFunction
	 * @return functionList List<Function>
	 */
	@ApiOperation(value = "Get the complete list of the SDKFunctionsInstance available in database for a given SDK Function",response = SDKFunctionInstance.class, responseContainer = "List")
	@ApiResponses(value = {
		      @ApiResponse(code = 200, message = "")
	})
	@RequestMapping(value = "/function/{functionId}/instances", method = RequestMethod.GET)
	public ResponseEntity<?> getFunctionInstancesPerFunction(@PathVariable UUID functionId) {
		log.info("Request for get FUNCTIONS");
		List<SDKFunctionInstance> response = new ArrayList<>();
		response = functionInstanceManager.getFunctionInstancesForFunction(functionId);
		return new ResponseEntity<List<SDKFunctionInstance>>(response, HttpStatus.OK);
	}
	
	
	
	
	
	@ApiOperation(value = "Modify an existing list of monitoring parameters related to a given SDKFunctionInstance")
	@ApiResponses(value = {
		      @ApiResponse(code = 400, message = "Function not present in db or request cannot be validated"),
		      @ApiResponse(code = 204, message = "Monitoring Param list Updated")
	})
	@RequestMapping(value = "/function/instances/{functionId}/monitoringparams", method = RequestMethod.PUT)
	public ResponseEntity<?> updateMonitoringParametersForFunction(@PathVariable UUID functionId, @PathVariable List<MonitoringParameter> monitoringParameters) {
		log.info("Request for update of a monitoringParameter list");
		for(MonitoringParameter param : monitoringParameters) {
			if(!param.isValid()) {
				log.error("Monitoring param list provided cannot be validated");
				return new ResponseEntity<String>("Monitoring param list provided cannot be validated", HttpStatus.BAD_REQUEST);
			}
		}
		try {
			functionInstanceManager.updateMonitoringParameters(functionId, monitoringParameters);
			log.debug("Function entity updated with the requested monitoring parameters");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch(NotExistingEntityException e1) {
			log.error("Function with id " + functionId.toString() + " is not present in database");
			return new ResponseEntity<String>("Function with id " + functionId.toString() + " is not present in database", HttpStatus.BAD_REQUEST);
		} catch (MalformattedElementException e2) {
			log.error("Malformed format for element MonitoringParameter. Unable to update function: " + functionId.toString());
			return new ResponseEntity<String>("Malformed format for element MonitoringParameter. Unable to update function: " + functionId.toString(), HttpStatus.BAD_REQUEST);
		} 
	}
		
	
	
	@ApiOperation(value = "Get the list of  Monitoring Paramters for a given SDKFunctionInstance identified by UUID",response = MonitoringParameter.class, responseContainer = "List")
	@ApiResponses(value = {
		      @ApiResponse(code = 400, message = "Query without parameter functionId"),
		      @ApiResponse(code = 404, message = "SDKFunctionInstance not found on database"),
		      @ApiResponse(code = 200, message = "OK")
	})
	@RequestMapping(value = "/function/instances/{functionId}/monitoringparams", method = RequestMethod.GET)
	public ResponseEntity<?> getMonitoringParametersForFunction(@PathVariable UUID functionId) {
		log.info("Request for get list of monitoringParams available on a specific function, identified by id: " + functionId.toString());
		
			try {
				List<MonitoringParameter> response = functionInstanceManager.getMonitoringParameters(functionId);
				log.debug("Returning list of monitoringParams related to a specific SDKFunctionInstance identified by uuid: " + functionId.toString());
				return new ResponseEntity<List<MonitoringParameter>>(response, HttpStatus.OK);
			} catch(NotExistingEntityException e) {
				log.debug("SDKFunctionInstance with uuid "+ functionId.toString()+" not found on database ");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
	}
	
	
	@ApiOperation(value = "Delete monitoring param list from SDKFunctionInstance")
	@ApiResponses(value = {
		      @ApiResponse(code = 204, message = ""),
		      @ApiResponse(code = 404, message = "Entity to be deleted not found"),
		      @ApiResponse(code = 400, message = "Deletion request without parameter functionId")
	})
	@RequestMapping(value = "/function/instances/{functionId}/monitoringparams", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMonitoringParametersForFunction(@PathVariable UUID functionId, @PathVariable List<MonitoringParameter> monitoringParameters) {
		log.info("Request for deletion of a list of monitoring parameters from function identified by id: " + functionId.toString());
		
			try {
				functionInstanceManager.deleteMonitoringParameters(functionId, monitoringParameters);
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} catch(NotExistingEntityException e) {
				log.error("Requested deletion for an entity which doesn't exist");
				return new ResponseEntity<String>("Requested deletion for an entity which doesn't exist", HttpStatus.NOT_FOUND);
			} catch (MalformattedElementException e) {
				log.error("Malformed Request. Monitoring parameters are malformed");
				return new ResponseEntity<String>("Malformed Request. Monitoring parameters are malformed", HttpStatus.NOT_FOUND);
			}
	}
	
	
	@ApiOperation(value = "Modify the flavour for a SDKFunctionInstance")
	@ApiResponses(value = {
		      @ApiResponse(code = 400, message = "Function not present in db or request cannot be validated"),
		      @ApiResponse(code = 204, message = "Flavour Updated")
	})
	@RequestMapping(value = "/function/instances/{functionId}/flavour", method = RequestMethod.PUT)
	public ResponseEntity<?> updateFlavourForFunction(@PathVariable UUID functionId, Flavour flavour) {
		log.info("Request for update the flavor for SDKFunctionInstance " + functionId.toString());
		try {
			functionInstanceManager.updateFlavor(functionId, flavour);
			log.debug("Function entity updated with the requested flavour.");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch(NotExistingEntityException e1) {
			log.error("Function with id " + functionId.toString() + " is not present in database");
			return new ResponseEntity<String>("Function with id " + functionId.toString() + " is not present in database", HttpStatus.BAD_REQUEST);
		} 
	}
	
}
