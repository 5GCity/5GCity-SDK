package it.nextworks.composer.plugins.catalogue.api.nsd;

import it.nextworks.composer.plugins.catalogue.Catalogue;
import it.nextworks.composer.plugins.catalogue.elements.nsd.CreateNsdInfoRequest;
import it.nextworks.composer.plugins.catalogue.elements.nsd.NsdInfo;
import it.nextworks.composer.plugins.catalogue.elements.nsd.NsdInfoModifications;
import it.nextworks.composer.plugins.catalogue.elements.nsd.NsdmSubscription;
import it.nextworks.composer.plugins.catalogue.elements.nsd.NsdmSubscriptionRequest;
import it.nextworks.composer.plugins.catalogue.elements.nsd.PnfdInfo;
import it.nextworks.composer.plugins.catalogue.elements.nsd.PnfdInfoModifications;
import it.nextworks.composer.plugins.catalogue.invoker.nsd.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-11-21T15:01:43.121+01:00")
@Component("it.nextworks.composer.plugins.catalogue.api.nsd.DefaultApi")
public class DefaultApi {

    private ApiClient apiClient;

    public DefaultApi(Catalogue catalogue) {
        this(new ApiClient(catalogue));
    }

    @Autowired
    public DefaultApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Create NSD Info
     * The POST method is used to create a new NS descriptor resource. This method shall follow the provisions specified in the Tables 5.4.2.3.1-1 and 5.4.2.3.1-2 of GS NFV-SOL 005 for URI query parameters, request and response data structures, and response codes.
     * <p><b>201</b> - Status 201
     * <p><b>400</b> - Status 400
     * <p><b>500</b> - Status 500
     *
     * @param body The body parameter
     * @return NsdInfo
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public NsdInfo createNsdInfo(CreateNsdInfoRequest body) throws RestClientException {
        Object postBody = body;

        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling createNsdInfo");
        }

        String path = UriComponentsBuilder.fromPath("/nsd/v1/ns_descriptors").build().toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json", "application/yaml"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {
            "application/json"
        };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<NsdInfo> returnType = new ParameterizedTypeReference<NsdInfo>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Create PNFD Info
     * The POST method is used to create a new PNF descriptor resource.
     * <p><b>201</b> - Status 201
     * <p><b>400</b> - Status 400
     * <p><b>500</b> - Status 500
     *
     * @param body The body parameter
     * @return PnfdInfo
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public PnfdInfo createPNFDInfo(PnfdInfo body) throws RestClientException {
        Object postBody = body;

        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling createPNFDInfo");
        }

        String path = UriComponentsBuilder.fromPath("/nsd/v1/pnf_descriptors").build().toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json", "application/yaml"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {
            "application/json"
        };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<PnfdInfo> returnType = new ParameterizedTypeReference<PnfdInfo>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Subscribe
     * The POST method creates a new subscription. This method shall support the URI query parameters, request and response data structures, and response codes, as specified in the Tables 5.4.8.3.1-1 and 5.4.8.3.1-2 of GS-NFV SOL 005. Creation of two subscription resources with the same callbackURI and the same filter can result in performance degradation and will provide duplicates of notifications to the OSS, and might make sense only in very rare use cases. Consequently, the NFVO may either allow creating a subscription resource if another subscription resource with the same filter and callbackUri already exists (in which case it shall return the \&quot;201 Created\&quot; response code), or may decide to not create a duplicate subscription resource (in which case it shall return a \&quot;303 See Other\&quot; response code referencing the existing subscription resource with the same filter and callbackUri).
     * <p><b>201</b> - Status 201
     * <p><b>303</b> - A subscription with the same callbackURI and the same filter already exits and the policy of the NFVO is to not create redundant subscriptions. The response body shall be empty.
     * <p><b>400</b> - Status 400
     * <p><b>500</b> - Status 500
     *
     * @param body The body parameter
     * @return NsdmSubscription
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public NsdmSubscription createSubscription(NsdmSubscriptionRequest body) throws RestClientException {
        Object postBody = body;

        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling createSubscription");
        }

        String path = UriComponentsBuilder.fromPath("/nsd/v1/subscriptions").build().toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json", "application/yaml"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<NsdmSubscription> returnType = new ParameterizedTypeReference<NsdmSubscription>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Delete NSD
     * The DELETE method deletes an individual NS descriptor resource. An individual NS descriptor resource can only be deleted when there is no NS instance using it (i.e. usageState &#x3D; NOT_IN_USE) and has been disabled already (i.e. operationalState &#x3D; DISABLED). Otherwise, the DELETE method shall fail. This method shall follow the provisions specified in the Tables 5.4.3.3.5-1 and 5.4.3.3.5-2 of GS NFV-SOL 005 for URI query parameters, request and response data structures, and response codes.
     * <p><b>204</b> - The operation has completed successfully. The response body shall be empty.
     * <p><b>404</b> - Status 404
     * <p><b>409</b> - Status 409
     * <p><b>500</b> - Status 500
     *
     * @param nsdInfoId The nsdInfoId parameter
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void deleteNSDInfo(String nsdInfoId) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'nsdInfoId' is set
        if (nsdInfoId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'nsdInfoId' when calling deleteNSDInfo");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("nsdInfoId", nsdInfoId);
        String path = UriComponentsBuilder.fromPath("/nsd/v1/ns_descriptors/{nsdInfoId}").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json", "application/yaml"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {
        };
        apiClient.invokeAPI(path, HttpMethod.DELETE, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Delete PNFD
     * The DELETE method deletes an individual PNF descriptor resource. An individual PNF descriptor resource can only be deleted when there is no NS instance using it or there is NSD referencing it. To delete all PNFD versions identified by a particular value of the \&quot;pnfdInvariantId\&quot; attribute, the procedure is to first use the GET method with filter \&quot;pnfdInvariantId\&quot; towards the PNF descriptors resource to find all versions of the PNFD. Then, the client uses the DELETE method described in this clause to delete each PNFD version individually. This method shall follow the provisions specified in the Tables 5.4.6.3.5-1 and 5.4.6.3.5-2 of GS NFV-SOL 005 for URI query parameters, request and response data structures, and response codes.
     * <p><b>204</b> - The operation has completed successfully. The response body shall be empty.
     * <p><b>400</b> - Status 400
     * <p><b>404</b> - Status 404
     * <p><b>500</b> - Status 500
     *
     * @param pnfdInfoId The pnfdInfoId parameter
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void deletePNFDInfo(String pnfdInfoId) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'pnfdInfoId' is set
        if (pnfdInfoId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'pnfdInfoId' when calling deletePNFDInfo");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("pnfdInfoId", pnfdInfoId);
        String path = UriComponentsBuilder.fromPath("/nsd/v1/pnf_descriptors/{pnfdInfoId}").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json", "application/yaml"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {
        };
        apiClient.invokeAPI(path, HttpMethod.DELETE, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Terminate Subscription
     * The DELETE method terminates an individual subscription. This method shall support the URI query parameters, request and response data structures, and response codes, as specified in the Tables 5.4.9.3.5-1 and 5.4.9.3.3-2 of GS NFV-SOL 005.
     * <p><b>204</b> - The subscription resource was deleted successfully. The response body shall be empty.
     * <p><b>400</b> - Status 400
     * <p><b>404</b> - Status 404
     * <p><b>500</b> - 500
     *
     * @param subscriptionId The subscriptionId parameter
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void deleteSubscription(String subscriptionId) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'subscriptionId' is set
        if (subscriptionId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'subscriptionId' when calling deleteSubscription");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("subscriptionId", subscriptionId);
        String path = UriComponentsBuilder.fromPath("/nsd/v1/subscriptions/{subscriptionId}").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json", "application/yaml"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {
        };
        apiClient.invokeAPI(path, HttpMethod.DELETE, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Get NSD Content
     * The GET method fetches the content of the NSD. The NSD can be implemented as a single file or as a collection of multiple files. If the NSD is implemented in the form of multiple files, a ZIP file embedding these files shall be returned. If the NSD is implemented as a single file, either that file or a ZIP file embedding that file shall be returned. The selection of the format is controlled by the \&quot;Accept\&quot; HTTP header passed in the GET request: • If the \&quot;Accept\&quot; header contains only \&quot;text/plain\&quot; and the NSD is implemented as a single file, the file shall be returned; otherwise, an error message shall be returned. • If the \&quot;Accept\&quot; header contains only \&quot;application/zip\&quot;, the single file or the multiple files that make up the NSD shall be returned embedded in a ZIP file. • If the \&quot;Accept\&quot; header contains both \&quot;text/plain\&quot; and \&quot;application/zip\&quot;, it is up to the NFVO to choose the format to return for a single-file NSD; for a multi-file NSD, a ZIP file shall be returned. NOTE: The structure of the NSD zip file is outside the scope of the present document.
     * <p><b>200</b> - On success, the content of the NSD is returned. The payload body shall contain a copy of the file representing the NSD or a ZIP file that contains the file or multiple files representing the NSD, as specified above. The \&quot;Content-Type\&quot; HTTP header shall be set according to the format of the returned file, i.e. to \&quot;text/plain\&quot; for a YAML file or to \&quot;application/zip\&quot; for a ZIP file.
     * <p><b>206</b> - On success, if the NFVO supports range requests, a single consecutive byte range from the content of the NSD file is returned.  The response body shall contain the requested part of the NSD file.  The \&quot;Content-Range\&quot; HTTP header shall be provided according to IETF RFC 7233 [23].  The \&quot;Content-Type\&quot; HTTP header shall be set as defined above for the \&quot;200 OK\&quot; response.
     * <p><b>404</b> - Status 404
     * <p><b>406</b> - Status 406
     * <p><b>409</b> - Status 409
     * <p><b>416</b> - Status 416
     * <p><b>500</b> - Status 500
     *
     * @param nsdInfoId The nsdInfoId parameter
     * @param range     The range parameter
     * @return Object
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Object getNSD(String nsdInfoId, String range) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'nsdInfoId' is set
        if (nsdInfoId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'nsdInfoId' when calling getNSD");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("nsdInfoId", nsdInfoId);
        String path = UriComponentsBuilder.fromPath("/nsd/v1/ns_descriptors/{nsdInfoId}/nsd_content").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (range != null)
            headerParams.add("Range", apiClient.parameterToString(range));

        final String[] accepts = {
            "application/json", "application/yaml", "application/zip"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<Object> returnType = new ParameterizedTypeReference<Object>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Query NSD Info
     * The GET method reads information about an individual NS descriptor. This method shall follow the provisions specified in GS NFV-SOL 005 Tables 5.4.3.3.2-1 and 5.4.3.3.2-2 of GS NFV-SOL 005 for URI query parameters, request and response data structures, and response codes.
     * <p><b>200</b> - Information about the individual NS descriptor. The response body shall contain a representation of the individual NS descriptor, as defined in clause 5.5.2.2 of GS NFV-SOL 005.
     * <p><b>400</b> - Status 400
     * <p><b>404</b> - Status 404
     * <p><b>409</b> - Status 409
     * <p><b>500</b> - Status 500
     *
     * @param nsdInfoId The nsdInfoId parameter
     * @return NsdInfo
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public NsdInfo getNSDInfo(String nsdInfoId) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'nsdInfoId' is set
        if (nsdInfoId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'nsdInfoId' when calling getNSDInfo");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("nsdInfoId", nsdInfoId);
        String path = UriComponentsBuilder.fromPath("/nsd/v1/ns_descriptors/{nsdInfoId}").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json", "application/yaml"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<NsdInfo> returnType = new ParameterizedTypeReference<NsdInfo>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Query NSDs Info
     * The GET method queries information about multiple NS descriptor resources. This method shall follow the provisions specified in the Tables 5.4.2.3.2-1 and 5.4.2.3.2-2 of GS NFV-SOL 005 for URI query parameters, request and response data structures, and response codes.
     * <p><b>200</b> - Information about zero or more NS descriptors. The response body shall contain a representation of zero or more NS descriptors, as defined in clause 5.5.2.2 of GS NFV-SOL 005.
     * <p><b>400</b> - There are two possible scenarios listed below. Error: Invalid attribute-based filtering parameters. The response body shall contain a ProblemDetails structure, in which the \&quot;detail\&quot; attribute should convey more information about the error. Error: Invalid attribute selector. The response body shall contain a ProblemDetails structure, in which the \&quot;detail\&quot; attribute should convey more information about the error.
     * <p><b>500</b> - Status 500
     *
     * @return List&lt;NsdInfo&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<NsdInfo> getNSDsInfo() throws RestClientException {
        Object postBody = null;

        String path = UriComponentsBuilder.fromPath("/nsd/v1/ns_descriptors").build().toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json", "application/yaml"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<List<NsdInfo>> returnType = new ParameterizedTypeReference<List<NsdInfo>>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Get PNFD Content
     * The GET method fetches the content of the PNFD.
     * <p><b>200</b> - On success, the content of the PNFD is returned. The payload body shall contain a copy of the file representing the PNFD. The \&quot;Content-Type\&quot; HTTP header shall be set to \&quot;text/plain\&quot;.
     * <p><b>400</b> - Status 400
     * <p><b>404</b> - Status 404
     * <p><b>409</b> - Status 409
     * <p><b>500</b> - Status 500
     *
     * @param pnfdInfoId The pnfdInfoId parameter
     * @return Object
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Object getPNFD(String pnfdInfoId) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'pnfdInfoId' is set
        if (pnfdInfoId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'pnfdInfoId' when calling getPNFD");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("pnfdInfoId", pnfdInfoId);
        String path = UriComponentsBuilder.fromPath("/nsd/v1/pnf_descriptors/{pnfdInfoId}/pnfd_content").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json", "application/yaml", "application/zip"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<Object> returnType = new ParameterizedTypeReference<Object>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Query PNFD Info
     * The GET method reads information about an individual PNF descriptor. This method shall follow the provisions specified in the Tables 5.4.6.3.2-1 and 5.4.6.3.2-2 of GS NFV-SOL 005 for URI query parameters, request and response data structures, and response codes.
     * <p><b>200</b> - Information about the individual PNFD descriptor. The response body shall contain a representation of the individual PNF descriptor, as defined in clause 5.5.2.5 of GS NFV-SOL 005.
     * <p><b>400</b> - Status 400
     * <p><b>404</b> - Status 404
     * <p><b>409</b> - Status 409
     * <p><b>500</b> - Status 500
     *
     * @param pnfdInfoId The pnfdInfoId parameter
     * @return PnfdInfo
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public PnfdInfo getPNFDInfo(String pnfdInfoId) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'pnfdInfoId' is set
        if (pnfdInfoId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'pnfdInfoId' when calling getPNFDInfo");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("pnfdInfoId", pnfdInfoId);
        String path = UriComponentsBuilder.fromPath("/nsd/v1/pnf_descriptors/{pnfdInfoId}").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json", "application/yaml"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<PnfdInfo> returnType = new ParameterizedTypeReference<PnfdInfo>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Query PFNDs Info
     * The GET method queries information about multiple PNF descriptor resources.
     * <p><b>200</b> - Status 200
     * <p><b>400</b> - Status 400
     * <p><b>500</b> - Status 500
     *
     * @param excludeDefault Indicates to exclude the following complex attributes from the response. See clause 4.3.3 for details. The NFVO shall support this parameter. The following attributes shall be excluded from the PnfdInfo structure in the response body if this parameter is provided, or none of the parameters \&quot;all_fields,\&quot; \&quot;fields\&quot;, \&quot;exclude_fields\&quot;, \&quot;exclude_default\&quot; are provided: userDefinedData.
     * @param allFields      Include all complex attributes in the response. See clause 4.3.3 for details. The NFVO shall support this parameter.
     * @return List&lt;PnfdInfo&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<PnfdInfo> getPNFDsInfo(String excludeDefault, String allFields) throws RestClientException {
        Object postBody = null;

        String path = UriComponentsBuilder.fromPath("/nsd/v1/pnf_descriptors").build().toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "exclude_default", excludeDefault));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "all_fields", allFields));

        final String[] accepts = {
            "application/json", "application/yaml"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<List<PnfdInfo>> returnType = new ParameterizedTypeReference<List<PnfdInfo>>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Query Subscription Information
     * The GET method retrieves information about a subscription by reading an individual subscription resource. This method shall support the URI query parameters, request and response data structures, and response codes, as specified in the Tables 5.4.9.3.2-1 and 5.4.9.3.2-2.
     * <p><b>200</b> - Status 200
     * <p><b>400</b> - Status 400
     * <p><b>404</b> - Status 404
     * <p><b>500</b> - Status 500
     *
     * @param subscriptionId The subscriptionId parameter
     * @return NsdmSubscription
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public NsdmSubscription getSubscription(String subscriptionId) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'subscriptionId' is set
        if (subscriptionId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'subscriptionId' when calling getSubscription");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("subscriptionId", subscriptionId);
        String path = UriComponentsBuilder.fromPath("/nsd/v1/subscriptions/{subscriptionId}").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json", "application/yaml"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<NsdmSubscription> returnType = new ParameterizedTypeReference<NsdmSubscription>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Query Subscription Information
     * The GET method queries the list of active subscriptions of the functional block that invokes the method. It can be used e.g. for resynchronization after error situations. This method shall support the URI query parameters, request and response data structures, and response codes, as specified in the Tables 5.4.8.3.2-1 and 5.4.8.3.2-2 of GS NFV-SOL 005.
     * <p><b>200</b> - Status 200
     * <p><b>400</b> - Status 400
     * <p><b>500</b> - Status 500
     *
     * @return List&lt;NsdmSubscription&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<NsdmSubscription> getSubscriptions() throws RestClientException {
        Object postBody = null;

        String path = UriComponentsBuilder.fromPath("/nsd/v1/subscriptions").build().toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json", "application/yaml"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<List<NsdmSubscription>> returnType = new ParameterizedTypeReference<List<NsdmSubscription>>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Update NSD Info
     * The PATCH method modifies the operational state and/or user defined data of an individual NS descriptor resource.  This method can be used to: 1) Enable a previously disabled individual NS descriptor resource, allowing again its use for instantiation of new network service with this descriptor. The usage state (i.e. \&quot;IN_USE/NOT_IN_USE\&quot;) shall not change as a result. 2) Disable a previously enabled individual NS descriptor resource, preventing any further use for instantiation of new network service(s) with this descriptor. The usage state (i.e. \&quot;IN_USE/NOT_IN_USE\&quot;) shall not change as a result. 3) Modify the user defined data of an individual NS descriptor resource. This method shall follow the provisions specified in the Tables 5.4.3.3.4-1 and 5.4.3.3.4-2 for URI query parameters, request and response data structures, and response codes.
     * <p><b>200</b> - Status 200
     * <p><b>400</b> - Status 400
     * <p><b>404</b> - Status 404
     * <p><b>409</b> - Status 409
     * <p><b>412</b> - Status 412
     * <p><b>500</b> - Status 500
     *
     * @param nsdInfoId The nsdInfoId parameter
     * @param body      The body parameter
     * @return NsdInfoModifications
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public NsdInfoModifications updateNSDInfo(String nsdInfoId, NsdInfoModifications body) throws RestClientException {
        Object postBody = body;

        // verify the required parameter 'nsdInfoId' is set
        if (nsdInfoId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'nsdInfoId' when calling updateNSDInfo");
        }

        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling updateNSDInfo");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("nsdInfoId", nsdInfoId);
        String path = UriComponentsBuilder.fromPath("/nsd/v1/ns_descriptors/{nsdInfoId}").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json", "application/yaml"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {
            "application/json"
        };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<NsdInfoModifications> returnType = new ParameterizedTypeReference<NsdInfoModifications>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.PATCH, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Update PNFD Info
     * The PATCH method modifies the user defined data of an individual PNF descriptor resource. This method shall follow the provisions specified in the Tables 5.4.6.3.4-1 and 5.4.6.3.4-2 for URI query parameters, request and response data structures, and response codes.
     * <p><b>200</b> - Status 200
     * <p><b>400</b> - Status 400
     * <p><b>404</b> - Status 404
     * <p><b>409</b> - Status 409
     * <p><b>412</b> - Status 412
     * <p><b>500</b> - Status 500
     *
     * @param pnfdInfoId The pnfdInfoId parameter
     * @param body       The body parameter
     * @return PnfdInfoModifications
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public PnfdInfoModifications updatePNFDInfo(String pnfdInfoId, PnfdInfoModifications body) throws RestClientException {
        Object postBody = body;

        // verify the required parameter 'pnfdInfoId' is set
        if (pnfdInfoId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'pnfdInfoId' when calling updatePNFDInfo");
        }

        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling updatePNFDInfo");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("pnfdInfoId", pnfdInfoId);
        String path = UriComponentsBuilder.fromPath("/nsd/v1/pnf_descriptors/{pnfdInfoId}").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json", "application/yaml"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {
            "application/json"
        };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<PnfdInfoModifications> returnType = new ParameterizedTypeReference<PnfdInfoModifications>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.PATCH, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Upload NSD
     * The PUT method is used to upload the content of a NSD. The NSD to be uploaded can be implemented as a single file or as a collection of multiple files, as defined in clause 5.4.4.3.2 of GS NFV-SOL 005. If the NSD is implemented in the form of multiple files, a ZIP file embedding these files shall be uploaded. If the NSD is implemented as a single file, either that file or a ZIP file embedding that file shall be uploaded. The \&quot;Content-Type\&quot; HTTP header in the PUT request shall be set accordingly based on the format selection of the NSD. If the NSD to be uploaded is a text file, the \&quot;Content-Type\&quot; header is set to \&quot;text/plain\&quot;. If the NSD to be uploaded is a zip file, the \&quot;Content-Type\&quot; header is set to \&quot;application/zip\&quot;. This method shall follow the provisions specified in the Tables 5.4.4.3.3-1 and 5.4.4.3.3-2 of GS-NFV-SOL 005 for URI query parameters, request and response data structures, and response codes.
     * <p><b>202</b> - Status 202
     * <p><b>204</b> - The NSD content was successfully uploaded and validated (synchronous mode). The response body shall be empty.
     * <p><b>400</b> - Status 400
     * <p><b>404</b> - Status 404
     * <p><b>409</b> - Error: The operation cannot be executed currently, due to a conflict with the state of the resource. Typically, this is due to the fact that the NsdOnboardingState has a value other than CREATED. The response body shall contain a ProblemDetails structure, in which the \&quot;detail\&quot; attribute shall convey more information about the error.
     * <p><b>500</b> - Status 500
     *
     * @param nsdInfoId   The nsdInfoId parameter
     * @param body        The body parameter
     * @param contentType The payload body contains a copy of the file representing the NSD or a ZIP file that contains the file or multiple files representing the NSD, as specified above. The request shall set the \&quot;Content-Type\&quot; HTTP header as defined above.
     * @return Object
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Object uploadNSD(String nsdInfoId, Object body, String contentType) throws RestClientException {
        Object postBody = body;

        // verify the required parameter 'nsdInfoId' is set
        if (nsdInfoId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'nsdInfoId' when calling uploadNSD");
        }

        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling uploadNSD");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("nsdInfoId", nsdInfoId);
        String path = UriComponentsBuilder.fromPath("/nsd/v1/ns_descriptors/{nsdInfoId}/nsd_content").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (contentType != null)
            headerParams.add("Content-Type", apiClient.parameterToString(contentType));

        final String[] accepts = {
            "application/json", "application/yaml"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {
            "application/json", "application/yaml", "application/zip"
        };
        final MediaType finalContentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<Object> returnType = new ParameterizedTypeReference<Object>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.PUT, queryParams, postBody, headerParams, formParams, accept, finalContentType, authNames, returnType);
    }

    /**
     * Upload PNFD
     * The PUT method is used to upload the content of a PNFD. This method shall follow the provisions specified in the Tables 5.4.7.3.3-1 and 5.4.7.3.3-2 of GS NFV-SOL 005for URI query parameters, request and response data structures, and response codes.
     * <p><b>204</b> - The PNFD content was successfully uploaded and validated. The response body shall be empty.
     * <p><b>400</b>
     * <p><b>404</b> - Status 404
     * <p><b>409</b> - Status 409
     * <p><b>500</b> - Status 500
     *
     * @param pnfdInfoId  The pnfdInfoId parameter
     * @param body        The body parameter
     * @param contentType The request shall set the \&quot;Content-Type\&quot; HTTP header to \&quot;text/plain\&quot;.
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void uploadPNFD(String pnfdInfoId, Object body, String contentType) throws RestClientException {
        Object postBody = body;

        // verify the required parameter 'pnfdInfoId' is set
        if (pnfdInfoId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'pnfdInfoId' when calling uploadPNFD");
        }

        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling uploadPNFD");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("pnfdInfoId", pnfdInfoId);
        String path = UriComponentsBuilder.fromPath("/nsd/v1/pnf_descriptors/{pnfdInfoId}/pnfd_content").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (contentType != null)
            headerParams.add("Content-Type", apiClient.parameterToString(contentType));

        final String[] accepts = {
            "application/json", "application/yaml"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {
            "application/json", "application/yaml"
        };
        final MediaType finalContentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {
        };
        apiClient.invokeAPI(path, HttpMethod.PUT, queryParams, postBody, headerParams, formParams, accept, finalContentType, authNames, returnType);
    }

}
