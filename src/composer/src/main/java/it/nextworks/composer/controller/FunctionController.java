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
import it.nextworks.composer.executor.FunctionManager;
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.SdkFunction;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.MalformedElementException;
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/sdk/functions")
@Api(value = "SDK Service Descriptor NBI", description = "Operations on SDK Composer & Editor Module - SDK Function APIs")
public class FunctionController {

    private static final Logger log = LoggerFactory.getLogger(FunctionController.class);

    @Autowired
    private FunctionManager functionManager;


    public FunctionController() {

    }

    /**
     * The method returns the full list of functions stored in the local database
     *
     * @return functionList List<Function>
     */
    @ApiOperation(value = "Get the complete list of the SDK Functions available in database", response = SdkFunction.class, responseContainer = "List")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "")
    })
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<?> getFunctions() {
//        functionManager.createFunction();
        log.info("Request for get FUNCTIONS");
        List<SdkFunction> response = new ArrayList<>();
        response = functionManager.getFunctions();
        return new ResponseEntity<List<SdkFunction>>(response, HttpStatus.OK);
    }


    /**
     * @param functionId Id of the function to be returned
     * @return function
     */
    @ApiOperation(value = "Search a SDK Function with an UUID", response = SdkFunction.class)
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Query without parameter functionId"),
        @ApiResponse(code = 404, message = "SdkFunction not found on database"),
        @ApiResponse(code = 200, message = "")
    })
    @RequestMapping(value = "/{functionId}", method = RequestMethod.GET)
    public ResponseEntity<?> getFunction(@PathVariable Long functionId) {
        log.info("Receiving a request to get the function identified by id: " + functionId);
        if (functionId == null) {
            return new ResponseEntity<String>("Query without parameter functionId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                SdkFunction result = functionManager.getFunction(functionId);
                return new ResponseEntity<SdkFunction>(result, HttpStatus.OK);
            } catch (NotExistingEntityException e) {
                log.debug("The Sdk function identified by the functionId provided is not present");
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        }
    }


    /**
     * @param request
     * @return
     */
    @ApiOperation(value = "Create a new SDK Function")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Function already present in db or service cannot be validated"),
        @ApiResponse(code = 500, message = "Internal Server Error"),
        @ApiResponse(code = 201, message = "Function Created")})
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<?> createFunction(@RequestBody SdkFunction request) {
        log.info("Request for creation of a new function");
        if (request.getId() == null && request.isValid()) {
            try {
                functionManager.createFunction(request);
                log.debug("Function entity created");
                return new ResponseEntity<>(request.getId(), HttpStatus.CREATED);
            } catch (NotYetImplementedException e) {
                log.error("NotYetImplementedException request");
                return new ResponseEntity<String>(
                    String.format("MNotYetImplementedException request"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (MalformedElementException e) {
                log.error("Malformed request: {}", e.getMessage());
                return new ResponseEntity<String>(
                    String.format("Malformed request: %s", e.getMessage()),
                    HttpStatus.BAD_REQUEST);
            } catch (NotExistingEntityException e) {
                log.error("Malformed request: {}", e.getMessage());
                return new ResponseEntity<String>(
                    String.format("Malformed request: %s", e.getMessage()),
                    HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("The function provided cannot be validated");
            return new ResponseEntity<String>("The function provided cannot be validated", HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Modify an existing  SDK Function")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Function not present in db or function cannot be validated"),
        @ApiResponse(code = 204, message = "Function Updated")})
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public ResponseEntity<?> updateFunction(@RequestBody SdkFunction request) {
        log.info("Request for update of a function");
        if (request.isValid()) {
            try {
                functionManager.updateFunction(request);
                log.debug("function entity updated");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (NotExistingEntityException e) {
                log.error(e.getMessage());
                return new ResponseEntity<String>(
                    e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (MalformedElementException e1) {
                log.error("Function with id " + request.getId() + " is malformatted");
                return new ResponseEntity<String>(
                    "Function with id " + request.getId() + " is malformatted", HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("The function provided cannot be validated");
            return new ResponseEntity<String>("The function provided cannot be validated", HttpStatus.BAD_REQUEST);
        }

    }

    @ApiOperation(value = "Delete SDK Function from database")
    @ApiResponses(value = {@ApiResponse(code = 204, message = ""),
        @ApiResponse(code = 404, message = "Entity to be deleted not found"),
        @ApiResponse(code = 400, message = "Deletion request without parameter functionId")})
    @RequestMapping(value = "/{functionId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFunction(@PathVariable Long functionId) {
        log.info("Request for deletion of a function with id: " + functionId);
        if (functionId == null) {
            log.error("Deletion request without parameter functionId");
            return new ResponseEntity<String>("Deletion request without parameter functionId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                functionManager.deleteFunction(functionId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (NotExistingEntityException e) {
                log.error("Requested deletion for an entity which doesn't exist");
                return new ResponseEntity<String>("Requested deletion for an entity which doesn't exist",
                    HttpStatus.NOT_FOUND);
            }
        }
    }

    @ApiOperation(value = "Publish SDK Function to Public Catalogue")
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "Descriptor created with returned id. The descriptor will be published to the public catalogue"),
        @ApiResponse(code = 404, message = "Function to be published not found"),
        @ApiResponse(code = 400, message = "Null function or invalid parameters provided")})
    @RequestMapping(value = "/{functionId}/publish", method = RequestMethod.POST)
    public ResponseEntity<?> publishFunction(@PathVariable Long functionId, List<BigDecimal> parameterValues) {
        log.info("Request create-function and publication of a function with id: {}, params: {}.", functionId, parameterValues);
        if (functionId == null) {
            log.error("Create-function/publication request without parameter functionId");
            return new ResponseEntity<>("Create-function/publication request without parameter functionId", HttpStatus.BAD_REQUEST);
        }
        // else:
        try {
            functionManager.publishFunction(functionId, parameterValues);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (NotExistingEntityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (MalformedElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Modify an existing list of monitoring parameters related to a SDK Function")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "SDK Function not present in db or request cannot be validated"),
        @ApiResponse(code = 204, message = "Monitoring Param list Updated")})
    @RequestMapping(value = "/{functionId}/monitoring-params", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMonitoringParametersForFunction(@PathVariable Long functionId,
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
            functionManager.updateMonitoringParameters(functionId, monitoringParameters);
            log.debug("Function entity updated with the requested monitoring parameters");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotExistingEntityException e1) {
            log.error(e1.toString());
            return new ResponseEntity<String>(e1.toString(), HttpStatus.BAD_REQUEST);
        } catch (MalformedElementException e2) {
            log.error("Malformed format for element MonitoringParameter. Unable to update service: "
                + functionId);
            return new ResponseEntity<String>(
                "Malformed format for element MonitoringParameter. Unable to update service: "
                    + functionId,
                HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Get the list of  Monitoring Parameters for a SDK Function identified by UUID", response = MonitoringParameter.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Query without parameter functionId"),
        @ApiResponse(code = 404, message = "SDK Function not found on database"),
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/{functionId}/monitoring-params", method = RequestMethod.GET)
    public ResponseEntity<?> getMonitoringParametersForService(@PathVariable Long functionId) {
        log.info("Request for get list of monitoringParams available on a specific Function, identified by id: "
            + functionId);

        try {
            Set<MonitoringParameter> response = functionManager.getMonitoringParameters(functionId);
            log.debug("Returning list of monitoringParams related to a specific SDK Function identified by uuid: "
                + functionId);
            return new ResponseEntity<Set<MonitoringParameter>>(response, HttpStatus.OK);
        } catch (NotExistingEntityException e) {
            log.debug("SdkService with uuid " + functionId + " not found on database ");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Delete monitoring param from SDK Function")
    @ApiResponses(value = {@ApiResponse(code = 204, message = ""),
        @ApiResponse(code = 404, message = "Entity to be deleted not found"),
        @ApiResponse(code = 400, message = "Deletion request without parameter functionId")})
    @RequestMapping(value = "/{functionId}/monitoring-params/{monitoringParameterId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMonitoringParametersForService(@PathVariable Long functionId,
                                                                  @PathVariable Long monitoringParameterId) {
        log.info("Request for deletion of monitoring parameter from SDK Function identified by id: "
            + functionId);

        try {
            functionManager.deleteMonitoringParameters(functionId, monitoringParameterId);
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