package it.nextworks.composer.plugins.catalogue.api.management;

import it.nextworks.composer.plugins.catalogue.Catalogue;
import it.nextworks.composer.plugins.catalogue.invoker.management.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultApi {

    private static final Logger log = LoggerFactory.getLogger(DefaultApi.class);

    private ApiClient apiClient;

    public DefaultApi(Catalogue catalogue) {
        this(new ApiClient(catalogue));
    }

    public DefaultApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ProjectResource createProject(ProjectResource project, String authorization) throws RestClientException {
        Object postBody = project;

        // verify the required parameter 'body' is set
        if (postBody == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'project' when calling createProject");
        }

        String path = UriComponentsBuilder.fromPath("/catalogue/projectManagement/projects").build().toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        headerParams.add("Authorization", authorization);
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {"application/json", "application/yaml"};
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {"application/json"};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<ProjectResource> returnType = new ParameterizedTypeReference<ProjectResource>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }

    public ProjectResource deleteProject(String projectId, String authorization) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'nsdInfoId' is set
        if (projectId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'projectId' when calling deleteProject");
        }

        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("projectId", projectId);
        String path = UriComponentsBuilder.fromPath("/catalogue/projectManagement/projects/{projectId}").buildAndExpand(uriVariables)
            .toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        headerParams.add("Authorization", authorization);
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {"application/json", "application/yaml"};
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<ProjectResource> returnType = new ParameterizedTypeReference<ProjectResource>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.DELETE, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }

    public ProjectResource getProject(String projectId, String authorization) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'projectId' is set
        if (projectId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'projectId' when calling getProject");
        }

        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("projectId", projectId);
        String path = UriComponentsBuilder.fromPath("/catalogue/projectManagement/projects/{projectId}").buildAndExpand(uriVariables).toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        headerParams.add("Authorization", authorization);
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {"application/json", "application/yaml"};
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<ProjectResource> returnType = new ParameterizedTypeReference<ProjectResource>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }

    public ProjectResource addUserToProject(String projectId, String username, String authorization) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'nsdInfoId' is set
        if (projectId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'projectId' when calling addUserToProject");
        }
        if (username == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'username' when calling addUserToProject");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("projectId", projectId);
        uriVariables.put("userName", username);
        String path = UriComponentsBuilder.fromPath("/catalogue/userManagement/projects/{projectId}/users/{userName}").buildAndExpand(uriVariables)
            .toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        headerParams.add("Authorization", authorization);
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {"application/json", "application/yaml"};
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<ProjectResource> returnType = new ParameterizedTypeReference<ProjectResource>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.PUT, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }

    public ProjectResource delUserFromProject(String projectId, String username, String authorization) throws RestClientException {
        Object postBody = null;

        // verify the required parameter 'nsdInfoId' is set
        if (projectId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'projectId' when calling addUserToProject");
        }
        if (username == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Missing the required parameter 'username' when calling addUserToProject");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("projectId", projectId);
        uriVariables.put("userName", username);
        String path = UriComponentsBuilder.fromPath("/catalogue/userManagement/projects/{projectId}/users/{userName}").buildAndExpand(uriVariables)
            .toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        headerParams.add("Authorization", authorization);
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {"application/json", "application/yaml"};
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<ProjectResource> returnType = new ParameterizedTypeReference<ProjectResource>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.DELETE, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }

    public List<UserResource> getUsers(String authorization) throws RestClientException {
        Object postBody = null;
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        String path = UriComponentsBuilder.fromPath("/catalogue/userManagement/users").build().toUriString();

        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        headerParams.add("Authorization", authorization);
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {"application/json", "application/yaml"};
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {};
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[]{};

        ParameterizedTypeReference<List<UserResource>> returnType = new ParameterizedTypeReference<List<UserResource>>() {
        };
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept,
            contentType, authNames, returnType);
    }
}
