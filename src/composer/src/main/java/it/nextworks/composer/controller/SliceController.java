/*
 * Copyright 2020 Nextworks s.r.l.
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
import it.nextworks.composer.auth.KeycloakUtils;
import it.nextworks.composer.controller.elements.SliceResource;
import it.nextworks.composer.executor.repositories.SdkFunctionRepository;
import it.nextworks.composer.executor.repositories.SdkServiceRepository;
import it.nextworks.composer.executor.repositories.SliceRepository;
import it.nextworks.composer.plugins.catalogue.FiveGCataloguePlugin;
import it.nextworks.sdk.SdkService;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/sdk/sliceManagement")
@Api(value = "SDK Slice Management NBI", description = "Operations on SDK - SDK Slice Management APIs")
public class SliceController {

    private static final Logger log = LoggerFactory.getLogger(SliceController.class);

    @Autowired
    private SdkFunctionRepository functionRepository;

    @Autowired
    private SdkServiceRepository serviceRepository;

    @Autowired
    private SliceRepository sliceRepository;

    @Autowired
    private FiveGCataloguePlugin cataloguePlugin;

    @Value("${admin.user.name:admin}")
    private String adminUser;

    @Value("${keycloak.enabled:true}")
    private boolean keycloakEnabled;

    @Autowired
    private KeycloakUtils keycloakUtils;

    public SliceController() {
    }

    @PostConstruct
    public void init() {
        SliceResource slice = new SliceResource("admin", "Admins slice");
        slice.addUser(adminUser);
        Optional<SliceResource> optional = sliceRepository.findBySliceId(slice.getSliceId());
        if (!optional.isPresent()) {
            sliceRepository.saveAndFlush(slice);
            log.debug("Slice admin successfully created");
        }
    }

    @ApiOperation(value = "Create a new Slice on the SDK and a Project on the Public Catalogue")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 400, message = "Slice already present in database or slice cannot be validated"),
        @ApiResponse(code = 500, message = "Slice cannot be created on Public Catalogue"),
        @ApiResponse(code = 201, message = "Slice created")})
    @RequestMapping(value = "/slices", method = RequestMethod.POST)
    public ResponseEntity<?> createSlice(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                           @RequestBody SliceResource slice) {

        log.info("Received request for new Slice creation");
        if ((slice == null) || (slice.getSliceId() == null)) {
            log.error("Malformatted Slice - Not acceptable");
            return new ResponseEntity<String>("Slice or Slice ID null", HttpStatus.BAD_REQUEST);
        }
        SliceResource createdSliceResource;
        Optional<SliceResource> optional = sliceRepository.findBySliceId(slice.getSliceId());
        if (optional.isPresent()) {
            log.error("Slice already present in DB");
            return new ResponseEntity<String>("Slice already present in DB", HttpStatus.BAD_REQUEST);
        } else {
            try {
                cataloguePlugin.createProject(slice.getSliceId(), authorization);
            }catch (RestClientException e){
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            slice.clearUsers();
            slice.addUser(adminUser);
            createdSliceResource = sliceRepository.saveAndFlush(slice);
            log.info("Slice " + slice.getSliceId() + " successfully created");
        }

        return new ResponseEntity<SliceResource>(createdSliceResource, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Get the complete list of slices available in database or get the list of slices for a user", response = SliceResource.class, responseContainer = "List")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/slices", method = RequestMethod.GET)
    public ResponseEntity<?> getSlices(@RequestParam(required = false) String user, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        if(user == null)
            log.info("Received request for getting Slices");
        else
            log.info("Received request for getting Slices for User " + user);
        List<SliceResource> sliceResources = sliceRepository.findAll();
        Iterator<SliceResource> sliceIterator = sliceResources.iterator();
        for (; sliceIterator.hasNext() ;) {
            SliceResource slice = sliceIterator.next();
            if (user != null && !slice.getUsers().contains(user))
                sliceIterator.remove();
        }
        return new ResponseEntity<List<SliceResource>>(sliceResources, HttpStatus.OK);
    }

    @ApiOperation(value = "Search a Slice with ID", response = SliceResource.class)
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Operation not permitted"),
        @ApiResponse(code = 401, message = "Authorization header is missing or format not valid"),
        @ApiResponse(code = 404, message = "Slice not found in database"),
        @ApiResponse(code = 400, message = "Query without parameter sliceId"),
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/slices/{sliceId}", method = RequestMethod.GET)
    public ResponseEntity<?> getSlice(@ApiParam(value = "", required = true)
                                        @PathVariable("sliceId") String sliceId,
                                        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Received request for getting Slice with Slice ID " + sliceId);
        if (sliceId == null) {
            log.error("Query without parameter sliceId");
            return new ResponseEntity<String>("Query without parameter sliceId", HttpStatus.BAD_REQUEST);
        }
        Optional<SliceResource> optional = sliceRepository.findBySliceId(sliceId);
        if (optional.isPresent()) {
            return new ResponseEntity<SliceResource>(optional.get(), HttpStatus.OK);
        } else {
            log.error("Slice with sliceId " + sliceId + " not present in DB");
            return new ResponseEntity<String>("Slice with sliceId " + sliceId + " not present in DB", HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Delete a slice from database")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 404, message = "Slice not found in database"),
        @ApiResponse(code = 400, message = "Query without parameter sliceId"),
        @ApiResponse(code = 409, message = "Slice cannot be deleted"),
        @ApiResponse(code = 500, message = "Slice cannot be deleted on Public Catalogue"),
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/slices/{sliceId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteSlice(@ApiParam(value = "", required = true)
                                           @PathVariable("sliceId") String sliceId,
                                           @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        log.info("Received request for deleting Slice with Slice ID " + sliceId);
        Optional<SliceResource> optional = sliceRepository.findBySliceId(sliceId);
        if (optional.isPresent()) {
            if (functionRepository.findBySliceId(sliceId).size() == 0 && serviceRepository.findBySliceId(sliceId).size() == 0) {
                try {
                    cataloguePlugin.deleteProject(sliceId, authorization);
                }catch (RestClientException e){
                    log.debug(null, e);
                    log.error(e.getMessage());
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                sliceRepository.delete(optional.get());
                log.info("Slice " + sliceId + " successfully deleted");
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                log.error("Slice cannot be deleted because in use");
                return new ResponseEntity<String>("Slice cannot be deleted because in use", HttpStatus.CONFLICT);
            }
        } else {
            log.error("Slice with sliceId " + sliceId + " not present in DB");
            return new ResponseEntity<String>("Slice with sliceId " + sliceId + " not present in DB", HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Add user to a slice")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 404, message = "Slice not found in database or user not found in Keycloak"),
        @ApiResponse(code = 400, message = "Query without parameter sliceId or userName or user already present in the slice"),
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/slices/{sliceId}/users/{userName:.+}", method = RequestMethod.PUT)
    public ResponseEntity<?> addUserToSlice (@ApiParam(value = "", required = true)
                                              @PathVariable("sliceId") String sliceId,
                                              @ApiParam(value = "", required = true)
                                              @PathVariable("userName") String userName,
                                              @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Received request for adding User " + userName + " to slice " + sliceId);
        if (sliceId == null) {
            log.error("Query without parameter sliceId");
            return new ResponseEntity<String>("Query without parameter sliceId", HttpStatus.BAD_REQUEST);
        }
        if (userName == null) {
            log.error("Query without parameter userName");
            return new ResponseEntity<String>("Query without parameter userName", HttpStatus.BAD_REQUEST);
        }
        SliceResource sliceResource;
        Optional<SliceResource> optional = sliceRepository.findBySliceId(sliceId);
        if (optional.isPresent()) {
            List<UserRepresentation> users = null;
            if(keycloakEnabled) {
                try{
                    users = keycloakUtils.getUsers();
                }catch(Exception e){
                    log.debug(null, e);
                    log.error("Keycloak server is not working properly. Please check Keycloak configuration or disable authentication");
                }
            }
            if(users != null && !users.stream().map(UserRepresentation::getUsername).collect(Collectors.toList()).contains(userName)){
                log.error("User not present in Keycloak");
                return new ResponseEntity<String>("User not present in Keycloak", HttpStatus.NOT_FOUND);
            }
            //check if user is already present in the slice
            sliceResource = optional.get();
            List<String> sliceUsers = sliceResource.getUsers();
            if(sliceUsers.contains(userName)){
                log.error("User already present in the slice");
                return new ResponseEntity<String>("User already present in the slice", HttpStatus.BAD_REQUEST);
            }
            try {
                if(keycloakEnabled)
                    cataloguePlugin.addUserToProject(sliceId, userName, authorization);
            }catch (RestClientException e){
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            sliceResource.addUser(userName);
            sliceRepository.saveAndFlush(sliceResource);
            log.info("User " + userName + " successfully added to slice " + sliceId);
            return new ResponseEntity<SliceResource>(sliceResource, HttpStatus.OK);
        } else {
            log.error("Slice with sliceId " + sliceId + " not present in DB");
            return new ResponseEntity<String>("Slice with sliceId " + sliceId + " not present in DB", HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Delete user from a slice")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 404, message = "Slice not found in database or user not found in the slice"),
        @ApiResponse(code = 400, message = "Query without parameter sliceId or userName"),
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/slices/{sliceId}/users/{userName}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delUserFromSlice (@ApiParam(value = "", required = true)
                                                @PathVariable("sliceId") String sliceId,
                                                @ApiParam(value = "", required = true)
                                                @PathVariable("userName") String userName,
                                                @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Received request for delete User " + userName + " from slice " + sliceId);
        if (sliceId == null) {
            log.error("Query without parameter sliceId");
            return new ResponseEntity<String>("Query without parameter sliceId", HttpStatus.BAD_REQUEST);
        }
        if (userName == null) {
            log.error("Query without parameter userName");
            return new ResponseEntity<String>("Query without parameter userName", HttpStatus.BAD_REQUEST);
        }
        if(userName.equals(adminUser)){
            log.error("Admin user cannot be deleted from a slice");
            return new ResponseEntity<String>("Admin user cannot be deleted from a slice", HttpStatus.BAD_REQUEST);
        }
        SliceResource sliceResource;
        Optional<SliceResource> optional = sliceRepository.findBySliceId(sliceId);
        if (optional.isPresent()) {
            sliceResource = optional.get();
            //check if user is present in the slice
            List<String> sliceUsers = sliceResource.getUsers();
            if(!sliceUsers.contains(userName)){
                log.error("User " + userName + " not present in the slice");
                return new ResponseEntity<String>("User " + userName + " not present in the slice", HttpStatus.NOT_FOUND);
            }
            try {
                if(keycloakEnabled)
                    cataloguePlugin.delUserFromProject(sliceId, userName, authorization);
            }catch (RestClientException e){
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            sliceResource.delUser(userName);
            sliceRepository.saveAndFlush(sliceResource);
            log.info("User " + userName + " successfully deleted from slice " + sliceId);
            return new ResponseEntity<SliceResource>(sliceResource, HttpStatus.OK);
        } else {
            log.error("Slice with sliceId " + sliceId + " not present in DB");
            return new ResponseEntity<String>("Slice with sliceId " + sliceId + " not present in DB", HttpStatus.NOT_FOUND);
        }
    }
}
