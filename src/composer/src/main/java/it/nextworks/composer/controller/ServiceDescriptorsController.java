package it.nextworks.composer.controller;

import io.swagger.annotations.*;
import it.nextworks.composer.executor.interfaces.ServiceManagerProviderInterface;
import it.nextworks.nfvmano.libs.common.exceptions.NotPermittedOperationException;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.sdk.SdkServiceDescriptor;
import it.nextworks.sdk.exceptions.AlreadyPublishedServiceException;
import it.nextworks.sdk.exceptions.MalformedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.NotPublishedServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Marco Capitani on 18/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */

@RestController
@CrossOrigin
@RequestMapping("/sdk/service_descriptor")
@Api(value = "Sdk descriptor NBI", description = "Operations on SDK - SDK Service Descriptor APIs")
public class ServiceDescriptorsController {

    private static final Logger log = LoggerFactory.getLogger(ServiceDescriptorsController.class);

    @Autowired
    ServiceManagerProviderInterface serviceManager;

    @ApiOperation(value = "Get all SDK Service Descriptors", response = SdkServiceDescriptor.class, responseContainer = "List")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<?> getAllDescriptor(@RequestParam(required = false) String sliceId, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        try {
            log.info("Request GET all descriptor");
            List<SdkServiceDescriptor> allDescriptors = serviceManager.getAllDescriptors(sliceId);
            log.debug("Service Descriptor entities retrived");
            return new ResponseEntity<>(allDescriptors, HttpStatus.OK);
        }catch (NotExistingEntityException e) {
            log.debug(null, e);
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (NotPermittedOperationException e){
            log.debug(null, e);
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(value = "Get SDK Service Descriptor with ID", response = SdkServiceDescriptor.class)
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 400, message = "Query without parameter serviceDescriptorId"),
        @ApiResponse(code = 404, message = "SDK Service Descriptor not present in database"),
        @ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/{serviceDescriptorId}", method = RequestMethod.GET)
    public ResponseEntity<?> getDescriptor(@PathVariable Long serviceDescriptorId, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Request GET descriptor wit ID" + serviceDescriptorId);
        if (serviceDescriptorId == null) {
            log.error("GET descriptor request without parameter serviceDescriptorId");
            return new ResponseEntity<>("GET descriptor request without parameter serviceDescriptorId", HttpStatus.BAD_REQUEST);
        }
        try {
            SdkServiceDescriptor descriptor = serviceManager.getServiceDescriptor(serviceDescriptorId);
            log.debug("Service Descriptor entity retrived");
            return new ResponseEntity<>(descriptor, HttpStatus.OK);
        } catch (NotExistingEntityException e) {
            log.debug(null, e);
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (NotPermittedOperationException e){
            log.debug(null, e);
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(value = "Delete SDK Service Descriptor from database")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 204, message = "SDK Service Descriptor deleted"),
        @ApiResponse(code = 404, message = "SDK Service Descriptor not present in database"),
        @ApiResponse(code = 409, message = "SDK Service Descriptor cannot be deleted"),
        @ApiResponse(code = 400, message = "Deletion request without parameter serviceDescriptorId")})
    @RequestMapping(value = "/{serviceDescriptorId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteDescriptor(@PathVariable Long serviceDescriptorId, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Request DELETE descriptor with ID " + serviceDescriptorId);
        if (serviceDescriptorId == null) {
            log.error("DELETE descriptor request without parameter serviceDescriptorId");
            return new ResponseEntity<>("DELETE descriptor request without parameter serviceDescriptorId", HttpStatus.BAD_REQUEST);
        }
        // else:
        try {
            serviceManager.deleteServiceDescriptor(serviceDescriptorId);
            log.debug("Service Descriptor entity deleted");
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

    @ApiOperation(value = "Publish SDK Service to Public Catalogue" )
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 202, message = "Network Service descriptor created. The descriptor will be published to the Public Catalogue"),
        @ApiResponse(code = 404, message = "SDK Service Descriptor not present in database"),
        @ApiResponse(code = 409, message = "Not all components are published to the Public Catalogue"),
        @ApiResponse(code = 400, message = "Publish request without parameter serviceDescriptorId or SDK Service already published")})
    @RequestMapping(value = "/{serviceDescriptorId}/publish", method = RequestMethod.POST)
    public ResponseEntity<?> publishService(@PathVariable Long serviceDescriptorId, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Request to publish the service, using the descriptor with ID " + serviceDescriptorId + ", to the Public Catalogue");
        if (serviceDescriptorId == null) {
            log.error("Publishing request without parameter serviceDescriptorId");
            return new ResponseEntity<String>("Publishing request without parameter serviceDescriptorId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                serviceManager.publishService(serviceDescriptorId, authorization);
                log.debug("Service entity will be published to the Public Catalogue");
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            } catch (NotExistingEntityException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }catch (AlreadyPublishedServiceException | MalformedElementException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }catch(NotPermittedOperationException e){
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
            }
        }
    }

    @ApiOperation(value = "Unpublish SDK Service from Public Catalogue")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 202, message = "The SDK Service will be removed from the Public Catalogue"),
        @ApiResponse(code = 404, message = "SDK Service Descriptor not present in database"),
        @ApiResponse(code = 400, message = "Publish request without parameter serviceDescriptorId or SDK Service not yet published")})
    @RequestMapping(value = "/{serviceDescriptorId}/unpublish", method = RequestMethod.POST)
    public ResponseEntity<?> unPublishService(@PathVariable Long serviceDescriptorId, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Request to unpublish the service, using the descriptor with ID" + serviceDescriptorId + ", from the Public Catalogue");
        if (serviceDescriptorId == null) {
            log.error("Request without parameter serviceDescriptorId");
            return new ResponseEntity<String>("Request without parameter serviceDescriptorId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                serviceManager.unPublishService(serviceDescriptorId, authorization);
                log.debug("Service entity will be unpublished from the Public Catalogue");
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            } catch (NotExistingEntityException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
            } catch (NotPublishedServiceException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }catch (NotPermittedOperationException e){
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
            }
        }
    }

    @ApiOperation(value = "Get NSD from SDK Service Descriptor")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "User not allowed to access the resource"),
        @ApiResponse(code = 401, message = "User not authenticated"),
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Network Service Descriptor cannot be retrived"),
        @ApiResponse(code = 404, message = "SDK Service Descriptor not present in database")})
    @RequestMapping(value = "/{serviceDescriptorId}/nsd", method = RequestMethod.GET)
    public ResponseEntity<?> getDescriptorNsd(@PathVariable Long serviceDescriptorId, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        log.info("Request GET Nsd for descriptor with ID " + serviceDescriptorId);
        if (serviceDescriptorId == null) {
            log.error("GET descriptor Nsd request without parameter serviceDescriptorId");
            return new ResponseEntity<String>("GET descriptor Nsd request without parameter serviceDescriptorId", HttpStatus.NOT_FOUND);
        } else {
            try {
                DescriptorTemplate descriptorTemplate = serviceManager.generateTemplate(serviceDescriptorId);
                log.debug("Nsd entity retrived");
                return new ResponseEntity<>(descriptorTemplate, HttpStatus.OK);
            } catch (NotExistingEntityException e) {
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
            }catch (MalformedElementException e){
                log.debug(null, e);
                log.error(e.getMessage());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }
}
