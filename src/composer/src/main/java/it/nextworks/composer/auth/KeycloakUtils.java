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
package it.nextworks.composer.auth;

import it.nextworks.composer.controller.elements.SliceResource;
import it.nextworks.composer.executor.repositories.SliceRepository;
import it.nextworks.nfvmano.libs.common.exceptions.NotAuthorizedOperationException;
import org.apache.commons.codec.binary.Base64;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


import javax.ws.rs.ProcessingException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class KeycloakUtils {

    private static final Logger log = LoggerFactory.getLogger(KeycloakUtils.class);

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Value("${admin.user.name:admin}")
    private String userName;

    @Value("${admin.password:admin}")
    private String password;

    private Keycloak keycloak;

    @Autowired
    private SliceRepository sliceRepository;

    public Keycloak getInstance() {
        if (keycloak == null) {
            keycloak = Keycloak.getInstance(serverUrl, realm, userName, password, clientId, clientSecret);
        }
        return keycloak;
    }

    public AccessTokenResponse getAccessToken() throws ProcessingException {
        log.debug("Going to request an access token");
        return getInstance().tokenManager().getAccessToken();
    }

    public List<UserRepresentation> getUsers() {
        log.debug("Going to retrieve users from realm " + realm + "...");
        List<UserRepresentation> users = getInstance().realm(realm).users().list();
        for (UserRepresentation userRepresentation : users) {
            log.debug("Keycloak user: " + userRepresentation.getUsername());
        }
        return users;
    }

    public void decodeJWT(String jwtToken) {
        log.debug("Going to decode JWT...");
        String[] split_string = jwtToken.split("\\.");
        String base64EncodedHeader = split_string[0];
        String base64EncodedBody = split_string[1];
        String base64EncodedSignature = split_string[2];

        Base64 base64Url = new Base64(true);

        String header = new String(base64Url.decode(base64EncodedHeader));
        log.debug("JWT Header: " + header);

        String body = new String(base64Url.decode(base64EncodedBody));
        log.debug("JWT Body: " + body);

        String signature = new String(base64Url.decode(base64EncodedSignature));
        log.debug("JWT Signature: " + signature);
    }

    public String getUserNameFromJWT() {

        String userName = null;

        log.debug("Going to validate received TOKEN for getting user infos...");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Authenticated user: " + authentication.getName()
            + " | Role: " + authentication.getAuthorities().toString()
            + " | Credentials: " + authentication.getCredentials().toString()
            + " | Details: " + authentication.getDetails().toString()
            + " | Principal: " + authentication.getPrincipal().toString());

        if (authentication.getPrincipal() instanceof KeycloakPrincipal) {
            KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) authentication.getPrincipal();
            // retrieving username here
            userName = kp.getKeycloakSecurityContext().getToken().getPreferredUsername();
        }

        return userName;
    }

    /*
    public List<String> getGroupsFromJWT() {

        List<String> groups = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof KeycloakPrincipal) {
            KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) authentication.getPrincipal();
            // retrieving username here
            Map<String, Object> otherClaims = kp.getKeycloakSecurityContext().getToken().getOtherClaims();
            groups = (List<String>)otherClaims.get("groups");
        }

        if(groups != null)
            log.debug("User groups : " + groups.toString());
        return groups;
    }
    */

    public Integer getAccessLevelFromJWT() {

        Integer accessLevel = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof KeycloakPrincipal) {
            KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) authentication.getPrincipal();
            // retrieving username here
            Map<String, Object> otherClaims = kp.getKeycloakSecurityContext().getToken().getOtherClaims();
            accessLevel = (Integer)otherClaims.get("accessLevel");
        }

        if(accessLevel != null)
            log.debug("User accessLevel : " + accessLevel);
        return accessLevel;
    }

    public void checkUserSlices(String userName, String resourceSliceId) throws NotAuthorizedOperationException {
        //if(resourceSliceId == null)
        //    return;
        Optional<SliceResource> optionalSlice = sliceRepository.findBySliceId(resourceSliceId);
        if(optionalSlice.isPresent()) {
            List<String> users = optionalSlice.get().getUsers();
            for (String user : users) {
                if (user.equals(userName))
                    return;
            }
        }
        log.error("Current user cannot access the specified slice");
        throw new NotAuthorizedOperationException("Current user cannot access the specified slice");
    }

    public void checkUserAccessLevel(Integer userAccessLevel, Integer resourceAccessLevel) throws NotAuthorizedOperationException {
        if(userAccessLevel.compareTo(resourceAccessLevel) <= 0)
            return;
        log.error("Current user cannot access to the specified resource: accessLevel mismatch");
        throw new NotAuthorizedOperationException("Current user cannot access to the specified resource: accessLevel mismatch");
    }

    /*
    public void checkUserGroups(List<String> userGroups, String resourceGroupId) throws NotAuthorizedOperationException {
       if(userGroups.contains(resourceGroupId))
           return;
        log.error("Current user cannot access to the specified resource: group mismatch");
        throw new NotAuthorizedOperationException("Current user cannot access to the specified resource: group mismatch");
    }
    */

    public void checkUserId(String userName, String resourceOwnerId) throws NotAuthorizedOperationException {
        if(userName.equals(resourceOwnerId))
            return;
        log.error("Current user cannot access to the specified resource: owner mismatch");
        throw new NotAuthorizedOperationException("Current user cannot access to the specified resource: owner mismatch");
    }
}