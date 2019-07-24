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


import io.swagger.annotations.*;
import it.nextworks.composer.executor.FunctionManager;
import it.nextworks.nfvmano.libs.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.common.exceptions.NotPermittedOperationException;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.SdkFunction;
import it.nextworks.sdk.exceptions.AlreadyPublishedServiceException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.MalformedElementException;
import it.nextworks.sdk.exceptions.NotPublishedServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Set;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/sdk/functions")
@Api(value = "SDK Function Descriptor NBI", description = "Operations on SDK - SDK Function APIs")
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
    @ApiOperation(value = "Get the complete list of SDK Functions available in database", response = SdkFunction.class, responseContainer = "List")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 404, message = "Slice not found"),
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<?> getFunctions(@RequestParam(required = false) String sliceId, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Request for getting functions");
        try {
            List<SdkFunction> response = new ArrayList<>();
            response = functionManager.getFunctions(sliceId);
            log.debug("Function entities retrived");
            return new ResponseEntity<List<SdkFunction>>(response, HttpStatus.OK);
        }catch (NotExistingEntityException e){
            log.debug(null, e);
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (NotPermittedOperationException e){
            log.debug(null, e);
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    /**
     * @param functionId Id of the function to be returned
     * @return function
     */
    @ApiOperation(value = "Search a SDK Function with ID", response = SdkFunction.class)
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 400, message = "Query without parameter functionId"),
        @ApiResponse(code = 404, message = "SDK Function not found in database or slice not found"),
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/{functionId}", method = RequestMethod.GET)
    public ResponseEntity<?> getFunction(@PathVariable Long functionId, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Receiving a request to get the function with ID: " + functionId);
        if (functionId == null) {
            return new ResponseEntity<String>("Query without parameter functionId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                SdkFunction result = functionManager.getFunction(functionId);
                log.debug("Function entity retrived");
                return new ResponseEntity<SdkFunction>(result, HttpStatus.OK);
            } catch (NotExistingEntityException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }catch (NotPermittedOperationException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
            }
        }
    }

    /**
     * @param request
     * @return
     */
    @ApiOperation(value = "Create a new SDK Function")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 400, message = "SDK Function already present in database or function cannot be validated"),
        @ApiResponse(code = 404, message = "Slice not found"),
        @ApiResponse(code = 201, message = "SDK Function created")})
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<?> createFunction(@RequestBody SdkFunction request, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Request for creation of a new function");
        try {
            functionManager.createFunction(request);
            log.debug("Function entity created");
            return new ResponseEntity<>(request.getId(), HttpStatus.CREATED);
        } catch (AlreadyExistingEntityException e) {
            log.debug(null, e);
            log.error(e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch(MalformedElementException e){
            log.debug(null, e);
            log.error(e.getMessage());
            return new ResponseEntity<String>("Malformed SdkFunction - " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch(NotExistingEntityException e){
            log.debug(null, e);
            log.error(e.getMessage());
            return new ResponseEntity<String>("Malformed SdkFunction - " + e.getMessage(), HttpStatus.NOT_FOUND);
        }catch(NotPermittedOperationException e){
            log.debug(null, e);
            log.error(e.getMessage());
            return new ResponseEntity<String>("Malformed SdkFunction - " + e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(value = "Modify an existing SDK Function")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 400, message = "SDK Function cannot be validated"),
        @ApiResponse(code = 404, message = "SDK Function not present in database or slice not found"),
        @ApiResponse(code = 409, message = "SDK Function cannot be updated"),
        @ApiResponse(code = 204, message = "SDK Function updated")})
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public ResponseEntity<?> updateFunction(@RequestBody SdkFunction request, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Request for update of a function");

        try {
            functionManager.updateFunction(request);
            log.debug("Function entity updated");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotExistingEntityException e) {
            log.debug(null, e);
            log.error(e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (MalformedElementException e) {
            log.debug(null, e);
            log.error(e.getMessage());
            return new ResponseEntity<String>("Malformed SdkFunction - " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotPermittedOperationException e){
            log.debug(null, e);
            log.error(e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @ApiOperation(value = "Delete a SDK Function from database")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 204, message = "SDK Function deleted"),
        @ApiResponse(code = 404, message = "SDK Function not present in database"),
        @ApiResponse(code = 409, message = "SDK Function cannot be deleted"),
        @ApiResponse(code = 400, message = "Deletion request without parameter functionId")})
    @RequestMapping(value = "/{functionId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFunction(@PathVariable Long functionId, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Request for deletion of a function with id: " + functionId);
        if (functionId == null) {
            log.error("Deletion request without parameter functionId");
            return new ResponseEntity<String>("Deletion request without parameter functionId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                functionManager.deleteFunction(functionId);
                log.debug("Function entity deleted");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (NotExistingEntityException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
            }catch (NotPermittedOperationException e){
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
            }
        }
    }

    /*
    @ApiOperation(value = "Create descriptor for SDK Function")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ""),
        @ApiResponse(code = 404, message = "Base function not found"),
        @ApiResponse(code = 400, message = "Null function or invalid parameters provided")})
    @RequestMapping(value = "/{functionId}/create_descriptor", method = RequestMethod.POST)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = false, allowEmptyValue = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token", format = "Bearer ")
    public ResponseEntity<?> createDescriptor(
        @PathVariable Long functionId,
        @RequestBody MakeDescriptorRequest makeDescriptorRequest
    ) {
        List<BigDecimal> parameterValues = makeDescriptorRequest.parameterValues;
        log.info("Request create_descriptor of a function with id: {}, params: {}.", functionId, parameterValues);
        if (functionId == null) {
            log.error("Create_descriptor request without parameter functionId");
            return new ResponseEntity<>(
                "Create_descriptor request without parameter functionId",
                HttpStatus.BAD_REQUEST
            );
        }
        // else:
        try {
            String descriptorId = functionManager.createFunctionDescriptor(functionId, parameterValues);
            return new ResponseEntity<>(descriptorId, HttpStatus.OK);
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
    */

    @ApiOperation(value = "Publish SDK Function to Public Catalogue")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 202, message = "Virtual Network Function descriptor created. The descriptor will be published to the Public Catalogue"),
        @ApiResponse(code = 404, message = "SDK Function not present in database"),
        @ApiResponse(code = 400, message = "Publish request without parameter functionId or SDK Function already published")})
    @RequestMapping(value = "/{functionId}/publish", method = RequestMethod.POST)
    public ResponseEntity<?> publishFunction(@PathVariable Long functionId, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Request publication of a function with ID {}", functionId);
        if (functionId == null) {
            log.error("Publication request without parameter functionId");
            return new ResponseEntity<>("Publication request without parameter functionId", HttpStatus.BAD_REQUEST);
        }
        try {
            functionManager.publishFunction(functionId, authorization);
            log.debug("Function entity will be published to the Public Catalogue");
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (NotExistingEntityException e) {
            log.debug(null, e);
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (AlreadyPublishedServiceException e) {
            log.debug(null, e);
            log.error(e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (NotPermittedOperationException e) {
            log.debug(null, e);
            log.error(e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(value = "Unpublish SDK Function from Public Catalogue")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 202, message = "SDK Function will be removed from the Public Catalogue"),
        @ApiResponse(code = 404, message = "SDK Function not present in database"),
        @ApiResponse(code = 409, message = "SDK Function cannot be unpublished"),
        @ApiResponse(code = 400, message = "Unpublish request without parameter functionId or SDK Function not yet published")})
    @RequestMapping(value = "/{functionId}/unpublish", method = RequestMethod.POST)
    public ResponseEntity<?> unPublishFunction(@PathVariable Long functionId, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Request to unpublish function with ID " + functionId + " from the Public Catalogue");
        if (functionId == null) {
            log.error("Request without parameter functionId");
            return new ResponseEntity<String>("Request without parameter functionId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                functionManager.unPublishFunction(functionId, authorization);
                log.debug("Function entity will be unpublished from the Public Catalogue");
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            } catch (NotExistingEntityException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
            } catch (NotPublishedServiceException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }catch (NotPermittedOperationException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
            }
        }
    }

    @ApiOperation(value = "Modify an existing list of monitoring parameters related to a SDK Function")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 400, message = "SDK Function or Monitoring Parameters cannot be validated"),
        @ApiResponse(code = 404, message = "SDK Function or Monitoring Parameters not present in database"),
        @ApiResponse(code = 409, message = "Monitoring Parameters cannot be updated"),
        @ApiResponse(code = 204, message = "Monitoring Parameters updated")})
    @RequestMapping(value = "/{functionId}/monitoring_params", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMonitoringParametersForFunction(@PathVariable Long functionId, @RequestBody Set<MonitoringParameter> monitoringParameters, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Request for update of a monitoringParameter list");
        if (functionId == null) {
            log.error("Request without parameter functionId");
            return new ResponseEntity<String>("Request without parameter functionId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                functionManager.updateMonitoringParameters(functionId, monitoringParameters);
                log.debug("Function entity updated with the requested monitoring parameters");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (NotExistingEntityException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
            } catch (MalformedElementException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>("Malformed SdkFunction - " + e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch(NotPermittedOperationException e){
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
            }
        }
    }

    @ApiOperation(value = "Get the list of  Monitoring Parameters for a SDK Function with ID", response = MonitoringParameter.class, responseContainer = "List")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 400, message = "Query without parameter functionId"),
        @ApiResponse(code = 404, message = "SDK Function or Monitoring Parameters not present in database"),
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/{functionId}/monitoring_params", method = RequestMethod.GET)
    public ResponseEntity<?> getMonitoringParametersForFunction(@PathVariable Long functionId, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Request for get list of monitoringParams available on a specific function with ID " + functionId);
        if (functionId == null) {
            log.error("Request without parameter functionId");
            return new ResponseEntity<String>("Request without parameter functionId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                Set<MonitoringParameter> response = functionManager.getMonitoringParameters(functionId);
                log.debug("Returning list of Monitoring Parameters related to a specific function with ID " + functionId);
                return new ResponseEntity<Set<MonitoringParameter>>(response, HttpStatus.OK);
            } catch (NotExistingEntityException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }catch (NotPermittedOperationException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
            }
        }
    }

    @ApiOperation(value = "Delete Monitoring Parameters from SDK Function")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 204, message = "Monitoring parameter deleted"),
        @ApiResponse(code = 404, message = "SDK Function or Monitoring Parameters not present in database"),
        @ApiResponse(code = 409, message = "Monitoring Parameters cannot be deleted"),
        @ApiResponse(code = 400, message = "Deletion request without parameter functionId or function cannot be validated")})
    @RequestMapping(value = "/{functionId}/monitoring_params/{monitoringParameterId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMonitoringParametersForFunction(@PathVariable Long functionId, @PathVariable Long monitoringParameterId, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Request for deletion of monitoring parameter from SDK Function identified by id: " + functionId);
        if (functionId == null) {
            log.error("Request without parameter functionId");
            return new ResponseEntity<String>("Request without parameter functionId", HttpStatus.BAD_REQUEST);
        }else {
            try {
                functionManager.deleteMonitoringParameters(functionId, monitoringParameterId);
                log.debug("Monitoring Parameter deleted");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (NotExistingEntityException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
            } catch (NotPermittedOperationException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
            } catch (MalformedElementException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>("Malformed SdkFunction - " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    @ApiOperation(value = "Get Vnfd from SDK Function")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "SDK Function not present in databse")})
    @RequestMapping(value = "/{functionId}/vnfd", method = RequestMethod.GET)
    public ResponseEntity<?> getVnfd(@PathVariable Long functionId, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Request GET Vnfd for function with ID " + functionId);
        if (functionId == null) {
            log.error("GET Vnfd request without parameter functionId");
            return new ResponseEntity<String>("GET Vnfd request without parameter functionId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                DescriptorTemplate descriptorTemplate = functionManager.generateTemplate(functionId);
                log.debug("Vnfd entity retrived");
                return new ResponseEntity<>(descriptorTemplate, HttpStatus.OK);
            }catch (NotExistingEntityException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
        }
    }
}