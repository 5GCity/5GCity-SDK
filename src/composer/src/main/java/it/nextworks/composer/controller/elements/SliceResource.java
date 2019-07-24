package it.nextworks.composer.controller.elements;

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
public class SliceResource implements DescriptorInformationElement {

    String sliceId;
    String sliceDescription;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    List<String> users = new ArrayList<>();
    /*
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
    */
    @Id
    @GeneratedValue
    private long id;

    public SliceResource() {
    }

    public SliceResource(String sliceId) {
        this.sliceId = sliceId;
    }

    public SliceResource(String sliceId, String sliceDescription) {
        this.sliceId = sliceId;
        this.sliceDescription = sliceDescription;
    }

    public SliceResource(String sliceId, String sliceDescription, List<String> users) {
        this.sliceId = sliceId;
        this.sliceDescription = sliceDescription;
        this.users = users;
    }

    public long getId() {
        return id;
    }

    public String getSliceId() {
        return sliceId;
    }

    public void setSliceId(String sliceId) {
        this.sliceId = sliceId;
    }

    public String getSliceDescription() {
        return sliceDescription;
    }

    public void setSliceDescription(String sliceDescription) {
        this.sliceDescription = sliceDescription;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }



    /*
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

     */

    public void addUser(String userName) {
        this.users.add(userName);
    }

    /*
    public boolean isDeletable() {
        if (!users.isEmpty() || !nsds.isEmpty() || !pnfds.isEmpty() || !vnfPackages.isEmpty()) {
            return false;
        }
        return true;
    }
    */

    @Override
    public void isValid() throws MalformattedElementException {
        if (this.sliceId == null)
            throw new MalformattedElementException("SliceResource without sliceId");
    }
}
