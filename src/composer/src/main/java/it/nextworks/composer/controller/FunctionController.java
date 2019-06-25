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
import it.nextworks.nfvmano.libs.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.common.exceptions.NotPermittedOperationException;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.SdkFunction;
import it.nextworks.sdk.exceptions.AlreadyPublishedServiceException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.MalformedElementException;
import it.nextworks.sdk.exceptions.NotPublishedServiceException;
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
@Api(value = "SDK Function Descriptor NBI", description = "Operations on SDK Composer & Editor Module - SDK Function APIs")
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
    @ApiResponses(value = {@ApiResponse(code = 200, message = "")})
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<?> getFunctions() {
        log.info("Request for getting functions");
        List<SdkFunction> response = new ArrayList<>();
        response = functionManager.getFunctions();
        return new ResponseEntity<List<SdkFunction>>(response, HttpStatus.OK);
    }

    /**
     * @param functionId Id of the function to be returned
     * @return function
     */
    @ApiOperation(value = "Search a SDK Function with ID", response = SdkFunction.class)
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Query without parameter functionId"),
        @ApiResponse(code = 404, message = "SDK Function not found in database"),
        @ApiResponse(code = 200, message = "")})
    @RequestMapping(value = "/{functionId}", method = RequestMethod.GET)
    public ResponseEntity<?> getFunction(@PathVariable Long functionId) {
        log.info("Receiving a request to get the function with ID: " + functionId);
        if (functionId == null) {
            return new ResponseEntity<String>("Query without parameter functionId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                SdkFunction result = functionManager.getFunction(functionId);
                return new ResponseEntity<SdkFunction>(result, HttpStatus.OK);
            } catch (NotExistingEntityException e) {
                log.debug(e.toString());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
        }
    }

    /**
     * @param request
     * @return
     */
    @ApiOperation(value = "Create a new SDK Function")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "SDK Function already present in database or function cannot be validated"),
        @ApiResponse(code = 201, message = "SDK Function created")})
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<?> createFunction(@RequestBody SdkFunction request) {
        log.info("Request for creation of a new function");
        try {
            functionManager.createFunction(request);
            log.debug("Function entity created");
            return new ResponseEntity<>(request.getId(), HttpStatus.CREATED);
        } catch (MalformedElementException | AlreadyExistingEntityException e) {
            log.error(e.toString());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Modify an existing SDK Function")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "SDK Function cannot be validated"),
        @ApiResponse(code = 404, message = "SDK Function not present in database"),
        @ApiResponse(code = 403, message = "SDK Function cannot be updated"),
        @ApiResponse(code = 204, message = "SDK Function updated")})
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public ResponseEntity<?> updateFunction(@RequestBody SdkFunction request) {
        log.info("Request for update of a function");

        try {
            functionManager.updateFunction(request);
            log.debug("Function entity updated");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotExistingEntityException e) {
            log.error(e.toString());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (MalformedElementException e) {
            log.error(e.toString());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotPermittedOperationException e){
                log.error(e.toString());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(value = "Delete a SDK Function from database")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = ""),
        @ApiResponse(code = 404, message = "SDK Function not present in database"),
        @ApiResponse(code = 403, message = "SDK Function cannot be deleted"),
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
                log.error(e.toString());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
            }catch (NotPermittedOperationException e){
                log.error(e.toString());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
            }
        }
    }

    /*
    @ApiOperation(value = "Create descriptor for SDK Function")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ""),
        @ApiResponse(code = 404, message = "Base function not found"),
        @ApiResponse(code = 400, message = "Null function or invalid parameters provided")})
    @RequestMapping(value = "/{functionId}/create_descriptor", method = RequestMethod.POST)
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
        @ApiResponse(code = 202, message = "Virtual Network Function descriptor created. The descriptor will be published to the Public Catalogue"),
        @ApiResponse(code = 404, message = "SDK Function not present in database"),
        @ApiResponse(code = 400, message = "Publish request without parameter functionId or SDK Function already published")})
    @RequestMapping(value = "/{functionId}/publish", method = RequestMethod.POST)
    public ResponseEntity<?> publishFunction(@PathVariable Long functionId) {
        log.info("Request publication of a function with ID {}", functionId);
        if (functionId == null) {
            log.error("Publication request without parameter functionId");
            return new ResponseEntity<>("Publication request without parameter functionId", HttpStatus.BAD_REQUEST);
        }
        try {
            functionManager.publishFunction(functionId);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (NotExistingEntityException e) {
            log.error(e.toString());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (AlreadyPublishedServiceException e) {
            log.error(e.toString());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Unpublish SDK Function from Public Catalogue")
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "The SDK Function will be removed from the Public Catalogue"),
        @ApiResponse(code = 404, message = "SDK Function not present in database"),
        @ApiResponse(code = 400, message = "Publish request without parameter functionId or SDK Function not yet published")})
    @RequestMapping(value = "/{functionId}/unpublish", method = RequestMethod.POST)
    public ResponseEntity<?> unPublishFunction(@PathVariable Long functionId) {
        log.info("Request to unpublish function with ID " + functionId + " from the Public Catalogue");
        if (functionId == null) {
            log.error("Request without parameter functionId");
            return new ResponseEntity<String>("Request without parameter functionId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                functionManager.unPublishFunction(functionId);
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            } catch (NotExistingEntityException e) {
                log.error(e.toString());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
            } catch (NotPublishedServiceException e) {
                log.error(e.toString());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    @ApiOperation(value = "Modify an existing list of monitoring parameters related to a SDK Function")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "SDK Function or Monitoring Parameters cannot be validated"),
        @ApiResponse(code = 404, message = "SDK Function or Monitoring Parameters not present in database"),
        @ApiResponse(code = 403, message = "Monitoring Parameters cannot be updated"),
        @ApiResponse(code = 204, message = "Monitoring Parameters updated")})
    @RequestMapping(value = "/{functionId}/monitoring_params", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMonitoringParametersForFunction(@PathVariable Long functionId, @RequestBody Set<MonitoringParameter> monitoringParameters) {
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
    }

    @ApiOperation(value = "Get the list of  Monitoring Parameters for a SDK Function with ID", response = MonitoringParameter.class, responseContainer = "List")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Query without parameter functionId"),
        @ApiResponse(code = 404, message = "SDK Function or Monitoring Parameters not present in database"),
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/{functionId}/monitoring_params", method = RequestMethod.GET)
    public ResponseEntity<?> getMonitoringParametersForFunction(@PathVariable Long functionId) {
        log.info("Request for get list of monitoringParams available on a specific function with ID " + functionId);
        if (functionId == null) {
            log.error("Request without parameter functionId");
            return new ResponseEntity<String>("Request without parameter functionId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                Set<MonitoringParameter> response = functionManager.getMonitoringParameters(functionId);
                log.debug("Returning list of monitoringParams related to a specific function with ID " + functionId);
                return new ResponseEntity<Set<MonitoringParameter>>(response, HttpStatus.OK);
            } catch (NotExistingEntityException e) {
                log.debug(e.toString());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
        }
    }

    @ApiOperation(value = "Delete Monitoring Parameters from SDK Function")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = ""),
        @ApiResponse(code = 404, message = "SDK Function or Monitoring Parameters not present in database"),
        @ApiResponse(code = 403, message = "Monitoring Parameters cannot be deleted"),
        @ApiResponse(code = 400, message = "Deletion request without parameter functionId or function cannot be validated")})
    @RequestMapping(value = "/{functionId}/monitoring_params/{monitoringParameterId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMonitoringParametersForFunction(@PathVariable Long functionId, @PathVariable Long monitoringParameterId) {
        log.info("Request for deletion of monitoring parameter from SDK Function identified by id: " + functionId);
        if (functionId == null) {
            log.error("Request without parameter functionId");
            return new ResponseEntity<String>("Request without parameter functionId", HttpStatus.BAD_REQUEST);
        }else {
            try {
                functionManager.deleteMonitoringParameters(functionId, monitoringParameterId);
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

    @ApiOperation(value = "Get Vnfd from SDK Function")
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "VNFD Content"),
        @ApiResponse(code = 400, message = "SDK function not present in databse")})
    @RequestMapping(value = "/{functionId}/vnfd", method = RequestMethod.GET)
    public ResponseEntity<?> getVnfd(@PathVariable Long functionId) {
        log.info("Request GET Vnfd for function with ID " + functionId);
        if (functionId == null) {
            log.error("GET Vnfd request without parameter functionId");
            return new ResponseEntity<String>("GET Vnfd request without parameter functionId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                DescriptorTemplate descriptorTemplate = functionManager.generateTemplate(functionId);
                return new ResponseEntity<>(descriptorTemplate, HttpStatus.OK);
            }catch (NotExistingEntityException e) {
                log.error(e.toString());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
        }
    }
}