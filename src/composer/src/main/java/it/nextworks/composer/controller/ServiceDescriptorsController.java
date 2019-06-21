package it.nextworks.composer.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.nextworks.composer.executor.interfaces.ServiceManagerProviderInterface;
import it.nextworks.nfvmano.libs.common.exceptions.NotPermittedOperationException;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;
import it.nextworks.sdk.SdkServiceDescriptor;
import it.nextworks.sdk.exceptions.AlreadyPublishedServiceException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.NotPublishedServiceException;
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

import java.util.List;

/**
 * Created by Marco Capitani on 18/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */

@RestController
@CrossOrigin
@RequestMapping("/sdk/service_descriptor")
@Api(value = "Sdk descriptor NBI", description = "Operations on SDK Composer Module - SDK Service Descriptor APIs")
public class ServiceDescriptorsController {

    private static final Logger log = LoggerFactory.getLogger(ServiceDescriptorsController.class);

    @Autowired
    ServiceManagerProviderInterface serviceManager;

    @ApiOperation(value = "Get all SDK Service Descriptors", response = SdkServiceDescriptor.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<?> getAllDescriptor() {
        log.info("Request GET all descriptor");
        List<SdkServiceDescriptor> allDescriptors = serviceManager.getAllDescriptors();
        return new ResponseEntity<>(allDescriptors, HttpStatus.OK);
    }

    @ApiOperation(value = "Get SDK Service Descriptor with ID", response = SdkServiceDescriptor.class)
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Query without parameter serviceDescriptorId"),
        @ApiResponse(code = 404, message = "SDK Service Descriptor not present in database"),
        @ApiResponse(code = 200, message = "")})
    @RequestMapping(value = "/{serviceDescriptorId}", method = RequestMethod.GET)
    public ResponseEntity<?> getDescriptor(@PathVariable Long serviceDescriptorId) {
        log.info("Request GET descriptor wit ID" + serviceDescriptorId);
        if (serviceDescriptorId == null) {
            log.error("GET descriptor request without parameter serviceDescriptorId");
            return new ResponseEntity<>("GET descriptor request without parameter serviceDescriptorId", HttpStatus.BAD_REQUEST);
        }
        try {
            SdkServiceDescriptor descriptor = serviceManager.getServiceDescriptor(serviceDescriptorId);
            return new ResponseEntity<>(descriptor, HttpStatus.OK);
        } catch (NotExistingEntityException e) {
            log.debug(e.toString());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Delete SDK Service Descriptor from database")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = ""),
        @ApiResponse(code = 404, message = "SDK Service Descriptor not present in database"),
        @ApiResponse(code = 403, message = "SDK Service Descriptor cannot be deleted"),
        @ApiResponse(code = 400, message = "Deletion request without parameter serviceDescriptorId")})
    @RequestMapping(value = "/{serviceDescriptorId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteDescriptor(@PathVariable Long serviceDescriptorId) {
        log.info("Request DELETE descriptor with ID " + serviceDescriptorId);
        if (serviceDescriptorId == null) {
            log.error("DELETE descriptor request without parameter serviceDescriptorId");
            return new ResponseEntity<>("DELETE descriptor request without parameter serviceDescriptorId", HttpStatus.BAD_REQUEST);
        }
        // else:
        try {
            serviceManager.deleteServiceDescriptor(serviceDescriptorId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NotExistingEntityException e) {
            log.error(e.toString());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (NotPermittedOperationException e){
            log.error(e.toString());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(value = "Publish SDK Service to Public Catalogue" )
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "Network Service descriptor created. The descriptor will be published to the public catalogue"),
        @ApiResponse(code = 404, message = "SDK Service Descriptor not present in database"),
        @ApiResponse(code = 400, message = "Publish request without parameter serviceDescriptorId or SDK Service already published")})
    @RequestMapping(value = "/{serviceDescriptorId}/publish", method = RequestMethod.POST)
    public ResponseEntity<?> publishService(@PathVariable Long serviceDescriptorId) {
        log.info("Request to publish the service, using the descriptor with ID " + serviceDescriptorId + ", to the public catalogue");
        if (serviceDescriptorId == null) {
            log.error("Publishing request without parameter serviceDescriptorId");
            return new ResponseEntity<String>("Publishing request without parameter serviceDescriptorId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                serviceManager.publishService(serviceDescriptorId);
                return new ResponseEntity<>(HttpStatus.ACCEPTED);
            } catch (NotExistingEntityException e) {
                log.error(e.toString());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }catch (AlreadyPublishedServiceException e) {
                log.error(e.toString());
                return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    @ApiOperation(value = "Unpublish SDK Service from Public Catalogue")
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "The SDK Service will be removed from the public catalogue"),
        @ApiResponse(code = 404, message = "SDK Service Descriptor not present in database"),
        @ApiResponse(code = 400, message = "Publish request without parameter serviceDescriptorId or SDK Service not yet published")})
    @RequestMapping(value = "/{serviceDescriptorId}/unpublish", method = RequestMethod.POST)
    public ResponseEntity<?> unPublishService(@PathVariable Long serviceDescriptorId) {
        log.info("Request to unpublish the service, using the descriptor with ID" + serviceDescriptorId + ", from the public catalogue");
        if (serviceDescriptorId == null) {
            log.error("Request without parameter serviceDescriptorId");
            return new ResponseEntity<String>("Request without parameter serviceDescriptorId", HttpStatus.BAD_REQUEST);
        } else {
            try {
                serviceManager.unPublishService(serviceDescriptorId);
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

    @ApiOperation(value = "Get NSD from SDK Service Descriptor")
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "NSD content"),
        @ApiResponse(code = 400, message = "SDK Service Descriptor not present in database")})
    @RequestMapping(value = "/{serviceDescriptorId}/nsd", method = RequestMethod.GET)
    public ResponseEntity<?> getDescriptorNsd(@PathVariable Long serviceDescriptorId) throws NotExistingEntityException {
        log.info("Request GET Nsd for descriptor with ID " + serviceDescriptorId);
        if (serviceDescriptorId == null) {
            log.error("GET descriptor Nsd request without parameter serviceDescriptorId");
            return new ResponseEntity<String>("GET descriptor Nsd request without parameter serviceDescriptorId", HttpStatus.NOT_FOUND);
        } else {
            DescriptorTemplate descriptorTemplate = serviceManager.generateTemplate(serviceDescriptorId);
            return new ResponseEntity<>(descriptorTemplate, HttpStatus.OK);
        }
    }
}
