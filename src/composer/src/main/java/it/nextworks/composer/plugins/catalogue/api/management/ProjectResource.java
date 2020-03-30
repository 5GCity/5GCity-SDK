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
package it.nextworks.composer.plugins.catalogue.api.management;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.nextworks.nfvmano.libs.common.DescriptorInformationElement;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class ProjectResource implements DescriptorInformationElement {

    String projectId;
    String projectDescription;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    List<String> users = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    List<String> nsds = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    List<String> pnfds = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    List<String> vnfPackages = new ArrayList<>();
    @Id
    @GeneratedValue
    private UUID id;

    public ProjectResource() {
    }

    public ProjectResource(String projectId) {
        this.projectId = projectId;
    }

    public ProjectResource(String projectId, String projectDescription) {
        this.projectId = projectId;
        this.projectDescription = projectDescription;
    }

    public ProjectResource(String projectId, String projectDescription, List<String> users) {
        this.projectId = projectId;
        this.projectDescription = projectDescription;
        this.users = users;
    }

    public UUID getId() {
        return id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public List<String> getNsds() {
        return nsds;
    }

    public void setNsds(List<String> nsds) {
        this.nsds = nsds;
    }

    public List<String> getPnfds() {
        return pnfds;
    }

    public void setPnfds(List<String> pnfds) {
        this.pnfds = pnfds;
    }

    public List<String> getVnfPackages() {
        return vnfPackages;
    }

    public void setVnfPackages(List<String> vnfPackages) {
        this.vnfPackages = vnfPackages;
    }

    public void addUser(String userName) {
        this.users.add(userName);
    }

    public boolean isDeletable() {
        if (!users.isEmpty() || !nsds.isEmpty() || !pnfds.isEmpty() || !vnfPackages.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public void isValid() throws MalformattedElementException {
        if (this.projectId == null)
            throw new MalformattedElementException("ProjectResource without projectId");
    }
}
