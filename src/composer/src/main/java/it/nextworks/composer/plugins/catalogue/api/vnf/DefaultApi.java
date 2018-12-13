package it.nextworks.composer.plugins.catalogue.api.vnf;

import it.nextworks.composer.plugins.catalogue.Catalogue;
import it.nextworks.composer.plugins.catalogue.elements.vnf.CreateVnfPkgInfoRequest;
import it.nextworks.composer.plugins.catalogue.elements.vnf.PkgmSubscription;
import it.nextworks.composer.plugins.catalogue.elements.vnf.PkgmSubscriptionRequest;
import it.nextworks.composer.plugins.catalogue.elements.vnf.UploadVnfPackageFromUriRequest;
import it.nextworks.composer.plugins.catalogue.elements.vnf.VnfPkgInfo;
import it.nextworks.composer.plugins.catalogue.elements.vnf.VnfPkgInfoModifications;
import it.nextworks.composer.plugins.catalogue.invoker.vnf.ApiClient;
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

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-11-21T15:10:42.557+01:00")
@Component("it.nextworks.composer.plugins.catalogue.api.vnf.DefaultApi")
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
     * Create Subscription Information
     *
     * <p><b>201</b> - Status 201
     * <p><b>303</b> - Status 303
     * <p><b>400</b> - Status 400
     * <p><b>500</b> - Status 500
     *
     * @param body The body parameter
     * @return PkgmSubscription
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public PkgmSubscription createSubscription(PkgmSubscriptionRequest body) throws RestClientException {
        Object postBody = body;

        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling createSubscription");
        }

        String path = UriComponentsBuilder.fromPath("/vnfpkgm/v1/subscriptions").build().toUriString();

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

        ParameterizedTypeReference<PkgmSubscription> returnType = new ParameterizedTypeReference<PkgmSubscription>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Create VNF Package Info
     *
     * <p><b>201</b> - Status 201
     * <p><b>400</b> - Status 400
     * <p><b>500</b> - Status 500
     *
     * @param body The body parameter
     * @return VnfPkgInfo
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public VnfPkgInfo createVNFPkgInfo(CreateVnfPkgInfoRequest body) throws RestClientException {
        Object postBody = body;

        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling createVNFPkgInfo");
        }

        String path = UriComponentsBuilder.fromPath("/vnfpkgm/v1/vnf_packages").build().toUriString();

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

        ParameterizedTypeReference<VnfPkgInfo> returnType = new ParameterizedTypeReference<VnfPkgInfo>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Delete Subscription Information
     *
     * <p><b>204</b> - Status 204
     * <p><b>400</b> - Status 400
     * <p><b>500</b> - Status 500
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
        String path = UriComponentsBuilder.fromPath("/vnfpkgm/v1/subscriptions/{subscriptionId}").buildAndExpand(uriVariables).toUriString();

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
     * Delete a VNF Package
     *
     * <p><b>204</b> - Status 204
     * <p><b>404</b> - Status 404
     * <p><b>409</b> - Status 409
     * <p><b>500</b> - Status 500
     *
     * @param vnfPkgId The vnfPkgId parameter
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void deleteVNFPkgInfo(String vnfPkgId) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'vnfPkgId' is set
        if (vnfPkgId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'vnfPkgId' when calling deleteVNFPkgInfo");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("vnfPkgId", vnfPkgId);
        String path = UriComponentsBuilder.fromPath("/vnfpkgm/v1/vnf_packages/{vnfPkgId}").buildAndExpand(uriVariables).toUriString();

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
     * Query Subscription Information
     *
     * <p><b>200</b> - Status 200
     * <p><b>400</b> - Status 400
     * <p><b>500</b> - Status 500
     *
     * @param subscriptionId The subscriptionId parameter
     * @return PkgmSubscription
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public PkgmSubscription getSubscription(String subscriptionId) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'subscriptionId' is set
        if (subscriptionId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'subscriptionId' when calling getSubscription");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("subscriptionId", subscriptionId);
        String path = UriComponentsBuilder.fromPath("/vnfpkgm/v1/subscriptions/{subscriptionId}").buildAndExpand(uriVariables).toUriString();

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

        ParameterizedTypeReference<PkgmSubscription> returnType = new ParameterizedTypeReference<PkgmSubscription>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Query Subscription Information
     *
     * <p><b>200</b> - Status 200
     * <p><b>400</b> - Status 400
     * <p><b>500</b> - Status 500
     *
     * @return List&lt;PkgmSubscription&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<PkgmSubscription> getSubscriptions() throws RestClientException {
        Object postBody = null;

        String path = UriComponentsBuilder.fromPath("/vnfpkgm/v1/subscriptions").build().toUriString();

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

        ParameterizedTypeReference<List<PkgmSubscription>> returnType = new ParameterizedTypeReference<List<PkgmSubscription>>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Get VNF Desriptor in a VNF Package.
     *
     * <p><b>200</b> - Status 200
     * <p><b>400</b> - Status 400
     * <p><b>404</b> - Status 404
     * <p><b>406</b> - Status 404
     * <p><b>409</b> - Status 409
     * <p><b>500</b> - Status 500
     *
     * @param vnfPkgId The vnfPkgId parameter
     * @return Object
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Object getVNFD(String vnfPkgId) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'vnfPkgId' is set
        if (vnfPkgId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'vnfPkgId' when calling getVNFD");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("vnfPkgId", vnfPkgId);
        String path = UriComponentsBuilder.fromPath("/vnfpkgm/v1/vnf_packages/{vnfPkgId}/vnfd").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {
            "application/json", "application/yaml", "text/plain", "application/zip"
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
     * Get VNF Package content.
     *
     * <p><b>200</b> - Status 200
     * <p><b>206</b> - Status 206
     * <p><b>400</b> - Status 400
     * <p><b>404</b> - Status 404
     * <p><b>409</b> - Status 409
     * <p><b>416</b> - Status 416
     * <p><b>500</b> - Status 500
     *
     * @param vnfPkgId The vnfPkgId parameter
     * @param range    The range parameter
     * @return Object
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Object getVNFPkg(String vnfPkgId, String range) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'vnfPkgId' is set
        if (vnfPkgId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'vnfPkgId' when calling getVNFPkg");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("vnfPkgId", vnfPkgId);
        String path = UriComponentsBuilder.fromPath("/vnfpkgm/v1/vnf_packages/{vnfPkgId}/package_content").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (range != null)
            headerParams.add("Range", apiClient.parameterToString(range));

        final String[] accepts = {
            "application/zip", "application/json"
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
     * Query VNF Packages Info
     *
     * <p><b>200</b> - Status 200
     * <p><b>400</b> - Status 400
     * <p><b>500</b> - Status 500
     *
     * @return List&lt;VnfPkgInfo&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<VnfPkgInfo> getVNFPkgsInfo() throws RestClientException {
        Object postBody = null;

        String path = UriComponentsBuilder.fromPath("/vnfpkgm/v1/vnf_packages").build().toUriString();

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

        ParameterizedTypeReference<List<VnfPkgInfo>> returnType = new ParameterizedTypeReference<List<VnfPkgInfo>>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Query VNF Package artifact.
     *
     * <p><b>200</b> - Status 200
     * <p><b>206</b> - Status 206
     * <p><b>400</b> - Status 400
     * <p><b>404</b> - Status 404
     * <p><b>409</b> - Status 409
     * <p><b>416</b> - Status 404
     * <p><b>500</b> - Status 500
     *
     * @param vnfPkgId     The vnfPkgId parameter
     * @param artifactPath The artifactPath parameter
     * @param range        The range parameter
     * @return Object
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Object queryVNFPkgArtifact(String vnfPkgId, String artifactPath, String range) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'vnfPkgId' is set
        if (vnfPkgId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'vnfPkgId' when calling queryVNFPkgArtifact");
        }

        // verify the required parameter 'artifactPath' is set
        if (artifactPath == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'artifactPath' when calling queryVNFPkgArtifact");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("vnfPkgId", vnfPkgId);
        uriVariables.put("artifactPath", artifactPath);
        String path = UriComponentsBuilder.fromPath("/vnfpkgm/v1/vnf_packages/{vnfPkgId}/artifacts/{artifactPath}").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (range != null)
            headerParams.add("Range", apiClient.parameterToString(range));

        final String[] accepts = {
            "application/octet-stream"
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
     * Query VNF Package Info
     *
     * <p><b>200</b> - Status 200
     * <p><b>400</b> - Status 400
     * <p><b>404</b> - Status 404
     * <p><b>409</b> - Status 409
     * <p><b>500</b> - Status 500
     *
     * @param vnfPkgId The vnfPkgId parameter
     * @return VnfPkgInfo
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public VnfPkgInfo queryVNFPkgInfo(String vnfPkgId) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'vnfPkgId' is set
        if (vnfPkgId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'vnfPkgId' when calling queryVNFPkgInfo");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("vnfPkgId", vnfPkgId);
        String path = UriComponentsBuilder.fromPath("/vnfpkgm/v1/vnf_packages/{vnfPkgId}").buildAndExpand(uriVariables).toUriString();

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

        ParameterizedTypeReference<VnfPkgInfo> returnType = new ParameterizedTypeReference<VnfPkgInfo>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Update a VNF Package Info
     *
     * <p><b>200</b> - Status 200
     * <p><b>400</b> - Status 400
     * <p><b>404</b> - Status 404
     * <p><b>409</b> - Status 409
     * <p><b>412</b> - Status 412
     * <p><b>500</b> - Status 500
     *
     * @param vnfPkgId The vnfPkgId parameter
     * @param body     The body parameter
     * @return VnfPkgInfoModifications
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public VnfPkgInfoModifications updateVNFPkgInfo(String vnfPkgId, VnfPkgInfoModifications body) throws RestClientException {
        Object postBody = body;

        // verify the required parameter 'vnfPkgId' is set
        if (vnfPkgId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'vnfPkgId' when calling updateVNFPkgInfo");
        }

        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling updateVNFPkgInfo");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("vnfPkgId", vnfPkgId);
        String path = UriComponentsBuilder.fromPath("/vnfpkgm/v1/vnf_packages/{vnfPkgId}").buildAndExpand(uriVariables).toUriString();

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

        ParameterizedTypeReference<VnfPkgInfoModifications> returnType = new ParameterizedTypeReference<VnfPkgInfoModifications>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.PATCH, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    /**
     * Upload VNF Package content.
     *
     * <p><b>202</b> - Status 202
     * <p><b>400</b> - Status 400
     * <p><b>404</b> - Status 404
     * <p><b>409</b> - Status 409
     * <p><b>500</b> - Status 500
     *
     * @param vnfPkgId    The vnfPkgId parameter
     * @param body        The body parameter
     * @param contentType The payload body contains a VNF Package ZIP file. The request shall set the \&quot;Content-Type\&quot; HTTP header as defined above.
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void uploadVNFPkg(String vnfPkgId, Object body, String contentType) throws RestClientException {
        Object postBody = body;

        // verify the required parameter 'vnfPkgId' is set
        if (vnfPkgId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'vnfPkgId' when calling uploadVNFPkg");
        }

        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling uploadVNFPkg");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("vnfPkgId", vnfPkgId);
        String path = UriComponentsBuilder.fromPath("/vnfpkgm/v1/vnf_packages/{vnfPkgId}/package_content").buildAndExpand(uriVariables).toUriString();

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
            "application/zip"
        };
        final MediaType finalContentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {
        };
        apiClient.invokeAPI(path, HttpMethod.PUT, queryParams, postBody, headerParams, formParams, accept, finalContentType, authNames, returnType);
    }

    /**
     * Upload VNF Package content from URI.
     *
     * <p><b>200</b> - Status 200
     * <p><b>400</b> - Status 400
     * <p><b>404</b> - Status 404
     * <p><b>409</b> - Status 409
     * <p><b>500</b> - Status 500
     *
     * @param vnfPkgId The vnfPkgId parameter
     * @param body     The body parameter
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void uploadVNFPkgFromURI(String vnfPkgId, UploadVnfPackageFromUriRequest body) throws RestClientException {
        Object postBody = body;

        // verify the required parameter 'vnfPkgId' is set
        if (vnfPkgId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'vnfPkgId' when calling uploadVNFPkgFromURI");
        }

        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling uploadVNFPkgFromURI");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("vnfPkgId", vnfPkgId);
        String path = UriComponentsBuilder.fromPath("/vnfpkgm/v1/vnf_packages/{vnfPkgId}/package_content/upload_from_uri").buildAndExpand(uriVariables).toUriString();

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

        ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {
        };
        apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
}
