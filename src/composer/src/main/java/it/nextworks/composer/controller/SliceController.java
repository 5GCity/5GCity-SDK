package it.nextworks.composer.controller;

import io.swagger.annotations.*;
import it.nextworks.composer.controller.elements.SliceResource;
import it.nextworks.composer.executor.repositories.SdkFunctionRepository;
import it.nextworks.composer.executor.repositories.SdkServiceRepository;
import it.nextworks.composer.executor.repositories.SliceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

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

    @Value("${admin.user.name:admin}")
    private String adminUser;

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

        /*
        UserResource userResource = new UserResource("admin", "Admin", "Admin", "admin");
        userResource.addProject("admin");
        Optional<UserResource> optionalUserResource = userRepository.findByUserName(userResource.getUserName());
        if (!optionalUserResource.isPresent()) {
            userRepository.saveAndFlush(userResource);
            log.debug("User " + userResource.getUserName() + " successfully created");
        }
        */
    }

    @ApiOperation(value = "Create a new Slice on the SDK and a Project on the Public Catalogue")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 400, message = "Slice already present in database or slice cannot be validated"),
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
            return new ResponseEntity<String>("Slice already present in DB", HttpStatus.BAD_REQUEST);
        } else {
            slice.addUser(adminUser);
            createdSliceResource = sliceRepository.saveAndFlush(slice);
            log.info("Slice " + slice.getSliceId() + " successfully created");

            //TODO create project on Catalogue

            /*
            log.debug("Going to update user admin with new created project " + project.getProjectId());
            Optional<UserResource> optionalUserResource = userRepository.findByUserName("admin");
            if (optionalUserResource.isPresent()) {
                UserResource userResource = optionalUserResource.get();
                userResource.addProject(createdProjectResource.getProjectId());
                userRepository.saveAndFlush(userResource);
                log.debug("User admin successfully updated with new project " + createdProjectResource.getProjectId());
            } else {
                log.warn("Unable to update user admin with new created project " + createdProjectResource.getProjectId());
            }
            */
        }

        return new ResponseEntity<SliceResource>(createdSliceResource, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Get the complete list of slices available in database", response = SliceResource.class, responseContainer = "List")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/slices", method = RequestMethod.GET)
    public ResponseEntity<?> getSlices(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Received request for getting Slices");
        List<SliceResource> sliceResources = sliceRepository.findAll();
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
        if (sliceId == null)
            return new ResponseEntity<String>("Query without parameter sliceId", HttpStatus.BAD_REQUEST);
        Optional<SliceResource> optional = sliceRepository.findBySliceId(sliceId);
        if (optional.isPresent()) {
            return new ResponseEntity<SliceResource>(optional.get(), HttpStatus.OK);
        } else {
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
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/slices/{sliceId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteSlice(@ApiParam(value = "", required = true)
                                           @PathVariable("sliceId") String sliceId,
                                           @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        log.info("Received request for deleting Slice with Slice ID " + sliceId);
        SliceResource sliceResource;
        Optional<SliceResource> optional = sliceRepository.findBySliceId(sliceId);
        if (optional.isPresent()) {
            if (functionRepository.findBySliceId(sliceId).size() == 0 && serviceRepository.findBySliceId(sliceId).size() == 0) {
                sliceRepository.delete(optional.get());
                log.info("Slice " + sliceId + " successfully deleted");
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<String>("Slice cannot be deleted because in use", HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity<String>("Slice with sliceId " + sliceId + " not present in DB", HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Add user to a slice")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 404, message = "Slice not found in database or user not found in Keycloak"),
        @ApiResponse(code = 400, message = "Query without parameter sliceId or userName"),
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/slices/{sliceId}/users/{userName}", method = RequestMethod.PUT)
    public ResponseEntity<?> addUserToSlice (@ApiParam(value = "", required = true)
                                              @PathVariable("sliceId") String sliceId,
                                              @ApiParam(value = "", required = true)
                                              @PathVariable("userName") String userName,
                                              @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Received request for adding User " + userName + " to slice " + sliceId);
        if (sliceId == null)
            return new ResponseEntity<String>("Query without parameter sliceId", HttpStatus.BAD_REQUEST);
        if (userName == null)
            return new ResponseEntity<String>("Query without parameter userName", HttpStatus.BAD_REQUEST);

        SliceResource sliceResource;
        Optional<SliceResource> optional = sliceRepository.findBySliceId(sliceId);
        if (optional.isPresent()) {
            sliceResource = optional.get();
            sliceResource.addUser(userName);
            sliceRepository.saveAndFlush(sliceResource);
            log.info("User " + userName + " successfully added to slice " + sliceId);
            /*
            Optional<UserResource> userResourceOptional = userRepository.findByUserName(userName);
            if (userResourceOptional.isPresent()) {
                UserResource userResource = userResourceOptional.get();
                if (userResource.getProjects().isEmpty()) {
                    userResource.setDefaultProject(sliceId);
                }
                userResource.addProject(sliceId);
                userRepository.saveAndFlush(userResource);
            } else {
                return new ResponseEntity<String>("User with userName " + userName + " not present in DB", HttpStatus.BAD_REQUEST);
            }
             */
            //TODO check user in keycloak
            return new ResponseEntity<SliceResource>(sliceResource, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("Slice with sliceId " + sliceId + " not present in DB", HttpStatus.NOT_FOUND);
        }
    }
}
