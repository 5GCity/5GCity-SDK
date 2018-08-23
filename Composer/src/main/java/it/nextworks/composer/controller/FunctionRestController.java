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
import it.nextworks.sdk.SDKFunction;
import it.nextworks.sdk.exceptions.NotExistingEntityException;

@RestController
@CrossOrigin
@RequestMapping("/sdk/composer")
@Api(value="SDK NBI", description="Operations on Composer Module")
public class FunctionRestController {

	private static final Logger log = LoggerFactory.getLogger(FunctionRestController.class);
	
	@Autowired
	private FunctionManager functionManager;
	
	
	public FunctionRestController() {
		
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
	
}
