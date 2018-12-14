/*
 * DRAFT - SOL005 - NSD Management Interface
 * DRAFT - SOL005 - NSD Management Interface IMPORTANT: Please note that this file might be not aligned to the current version of the ETSI Group Specification it refers to and has not been approved by the ETSI NFV ISG. In case of discrepancies the published ETSI Group Specification takes precedence. Please report bugs to https://forge.etsi.org/bugzilla/buglist.cgi?component=Nfv-Openapis
 *
 * OpenAPI spec version: 2.4.1
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package it.nextworks.composer.plugins.catalogue.elements.nsd;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * This type represents a subscription filter related to notifications about NSD management. It shall comply with the provisions defined in Table 5.5.3.2-1 of GS NFV-SOL 005. At a particular nesting level in the filter structure, the following applies: All attributes shall match in order for the filter to match (logical \&quot;and\&quot; between different filter attributes). If an attribute is an array, the attribute shall match if at least one of the values in the array matches (logical \&quot;or\&quot; between the values of one filter attribute).
 */
@ApiModel(description = "This type represents a subscription filter related to notifications about NSD management. It shall comply with the provisions defined in Table 5.5.3.2-1 of GS NFV-SOL 005. At a particular nesting level in the filter structure, the following applies: All attributes shall match in order for the filter to match (logical \"and\" between different filter attributes). If an attribute is an array, the attribute shall match if at least one of the values in the array matches (logical \"or\" between the values of one filter attribute).")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-11-21T15:01:43.121+01:00")
public class NsdmNotificationsFilter {
    @JsonProperty("notificationTypes")
    private List<NotificationType> notificationTypes = null;

    @JsonProperty("nsdInfoId")
    private UUID nsdInfoId = null;

    @JsonProperty("nsdId")
    private UUID nsdId = null;

    @JsonProperty("nsdName")
    private String nsdName = null;

    @JsonProperty("nsdVersion")
    private String nsdVersion = null;

    @JsonProperty("nsdDesigner")
    private String nsdDesigner = null;

    @JsonProperty("nsdInvariantId")
    private UUID nsdInvariantId = null;

    @JsonProperty("vnfPkgIds")
    private List<UUID> vnfPkgIds = null;

    @JsonProperty("pnfdInfoIds")
    private List<UUID> pnfdInfoIds = null;

    @JsonProperty("nestedNsdInfoIds")
    private List<UUID> nestedNsdInfoIds = null;

    @JsonProperty("nsdOnboardingState")
    private NsdOnboardingStateType nsdOnboardingState = null;

    @JsonProperty("nsdOperationalState")
    private NsdOperationalStateType nsdOperationalState = null;

    @JsonProperty("nsdUsageState")
    private NsdUsageStateType nsdUsageState = null;

    @JsonProperty("pnfdId")
    private UUID pnfdId = null;

    @JsonProperty("pnfdName")
    private String pnfdName = null;

    @JsonProperty("pnfdVersion")
    private String pnfdVersion = null;

    @JsonProperty("pnfdProvider")
    private String pnfdProvider = null;

    @JsonProperty("pnfdInvariantId")
    private UUID pnfdInvariantId = null;

    @JsonProperty("pnfdOnboardingState")
    private PnfdOnboardingStateType pnfdOnboardingState = null;

    @JsonProperty("pnfdUsageState")
    private PnfdUsageStateType pnfdUsageState = null;

    public NsdmNotificationsFilter notificationTypes(List<NotificationType> notificationTypes) {
        this.notificationTypes = notificationTypes;
        return this;
    }

    public NsdmNotificationsFilter addNotificationTypesItem(NotificationType notificationTypesItem) {
        if (this.notificationTypes == null) {
            this.notificationTypes = new ArrayList<NotificationType>();
        }
        this.notificationTypes.add(notificationTypesItem);
        return this;
    }

    /**
     * Get notificationTypes
     *
     * @return notificationTypes
     **/
    @ApiModelProperty(value = "")
    public List<NotificationType> getNotificationTypes() {
        return notificationTypes;
    }

    public void setNotificationTypes(List<NotificationType> notificationTypes) {
        this.notificationTypes = notificationTypes;
    }

    public NsdmNotificationsFilter nsdInfoId(UUID nsdInfoId) {
        this.nsdInfoId = nsdInfoId;
        return this;
    }

    /**
     * Get nsdInfoId
     *
     * @return nsdInfoId
     **/
    @ApiModelProperty(value = "")
    public UUID getNsdInfoId() {
        return nsdInfoId;
    }

    public void setNsdInfoId(UUID nsdInfoId) {
        this.nsdInfoId = nsdInfoId;
    }

    public NsdmNotificationsFilter nsdId(UUID nsdId) {
        this.nsdId = nsdId;
        return this;
    }

    /**
     * Get nsdId
     *
     * @return nsdId
     **/
    @ApiModelProperty(value = "")
    public UUID getNsdId() {
        return nsdId;
    }

    public void setNsdId(UUID nsdId) {
        this.nsdId = nsdId;
    }

    public NsdmNotificationsFilter nsdName(String nsdName) {
        this.nsdName = nsdName;
        return this;
    }

    /**
     * Match the name of the onboarded NSD.
     *
     * @return nsdName
     **/
    @ApiModelProperty(value = "Match the name of the onboarded NSD.")
    public String getNsdName() {
        return nsdName;
    }

    public void setNsdName(String nsdName) {
        this.nsdName = nsdName;
    }

    public NsdmNotificationsFilter nsdVersion(String nsdVersion) {
        this.nsdVersion = nsdVersion;
        return this;
    }

    /**
     * Get nsdVersion
     *
     * @return nsdVersion
     **/
    @ApiModelProperty(value = "")
    public String getNsdVersion() {
        return nsdVersion;
    }

    public void setNsdVersion(String nsdVersion) {
        this.nsdVersion = nsdVersion;
    }

    public NsdmNotificationsFilter nsdDesigner(String nsdDesigner) {
        this.nsdDesigner = nsdDesigner;
        return this;
    }

    /**
     * Match the NSD designer of the on-boarded NSD.
     *
     * @return nsdDesigner
     **/
    @ApiModelProperty(value = "Match the NSD designer of the on-boarded NSD.")
    public String getNsdDesigner() {
        return nsdDesigner;
    }

    public void setNsdDesigner(String nsdDesigner) {
        this.nsdDesigner = nsdDesigner;
    }

    public NsdmNotificationsFilter nsdInvariantId(UUID nsdInvariantId) {
        this.nsdInvariantId = nsdInvariantId;
        return this;
    }

    /**
     * Get nsdInvariantId
     *
     * @return nsdInvariantId
     **/
    @ApiModelProperty(value = "")
    public UUID getNsdInvariantId() {
        return nsdInvariantId;
    }

    public void setNsdInvariantId(UUID nsdInvariantId) {
        this.nsdInvariantId = nsdInvariantId;
    }

    public NsdmNotificationsFilter vnfPkgIds(List<UUID> vnfPkgIds) {
        this.vnfPkgIds = vnfPkgIds;
        return this;
    }

    public NsdmNotificationsFilter addVnfPkgIdsItem(UUID vnfPkgIdsItem) {
        if (this.vnfPkgIds == null) {
            this.vnfPkgIds = new ArrayList<UUID>();
        }
        this.vnfPkgIds.add(vnfPkgIdsItem);
        return this;
    }

    /**
     * Get vnfPkgIds
     *
     * @return vnfPkgIds
     **/
    @ApiModelProperty(value = "")
    public List<UUID> getVnfPkgIds() {
        return vnfPkgIds;
    }

    public void setVnfPkgIds(List<UUID> vnfPkgIds) {
        this.vnfPkgIds = vnfPkgIds;
    }

    public NsdmNotificationsFilter pnfdInfoIds(List<UUID> pnfdInfoIds) {
        this.pnfdInfoIds = pnfdInfoIds;
        return this;
    }

    public NsdmNotificationsFilter addPnfdInfoIdsItem(UUID pnfdInfoIdsItem) {
        if (this.pnfdInfoIds == null) {
            this.pnfdInfoIds = new ArrayList<UUID>();
        }
        this.pnfdInfoIds.add(pnfdInfoIdsItem);
        return this;
    }

    /**
     * Get pnfdInfoIds
     *
     * @return pnfdInfoIds
     **/
    @ApiModelProperty(value = "")
    public List<UUID> getPnfdInfoIds() {
        return pnfdInfoIds;
    }

    public void setPnfdInfoIds(List<UUID> pnfdInfoIds) {
        this.pnfdInfoIds = pnfdInfoIds;
    }

    public NsdmNotificationsFilter nestedNsdInfoIds(List<UUID> nestedNsdInfoIds) {
        this.nestedNsdInfoIds = nestedNsdInfoIds;
        return this;
    }

    public NsdmNotificationsFilter addNestedNsdInfoIdsItem(UUID nestedNsdInfoIdsItem) {
        if (this.nestedNsdInfoIds == null) {
            this.nestedNsdInfoIds = new ArrayList<UUID>();
        }
        this.nestedNsdInfoIds.add(nestedNsdInfoIdsItem);
        return this;
    }

    /**
     * Get nestedNsdInfoIds
     *
     * @return nestedNsdInfoIds
     **/
    @ApiModelProperty(value = "")
    public List<UUID> getNestedNsdInfoIds() {
        return nestedNsdInfoIds;
    }

    public void setNestedNsdInfoIds(List<UUID> nestedNsdInfoIds) {
        this.nestedNsdInfoIds = nestedNsdInfoIds;
    }

    public NsdmNotificationsFilter nsdOnboardingState(NsdOnboardingStateType nsdOnboardingState) {
        this.nsdOnboardingState = nsdOnboardingState;
        return this;
    }

    /**
     * Get nsdOnboardingState
     *
     * @return nsdOnboardingState
     **/
    @ApiModelProperty(value = "")
    public NsdOnboardingStateType getNsdOnboardingState() {
        return nsdOnboardingState;
    }

    public void setNsdOnboardingState(NsdOnboardingStateType nsdOnboardingState) {
        this.nsdOnboardingState = nsdOnboardingState;
    }

    public NsdmNotificationsFilter nsdOperationalState(NsdOperationalStateType nsdOperationalState) {
        this.nsdOperationalState = nsdOperationalState;
        return this;
    }

    /**
     * Get nsdOperationalState
     *
     * @return nsdOperationalState
     **/
    @ApiModelProperty(value = "")
    public NsdOperationalStateType getNsdOperationalState() {
        return nsdOperationalState;
    }

    public void setNsdOperationalState(NsdOperationalStateType nsdOperationalState) {
        this.nsdOperationalState = nsdOperationalState;
    }

    public NsdmNotificationsFilter nsdUsageState(NsdUsageStateType nsdUsageState) {
        this.nsdUsageState = nsdUsageState;
        return this;
    }

    /**
     * Get nsdUsageState
     *
     * @return nsdUsageState
     **/
    @ApiModelProperty(value = "")
    public NsdUsageStateType getNsdUsageState() {
        return nsdUsageState;
    }

    public void setNsdUsageState(NsdUsageStateType nsdUsageState) {
        this.nsdUsageState = nsdUsageState;
    }

    public NsdmNotificationsFilter pnfdId(UUID pnfdId) {
        this.pnfdId = pnfdId;
        return this;
    }

    /**
     * Get pnfdId
     *
     * @return pnfdId
     **/
    @ApiModelProperty(value = "")
    public UUID getPnfdId() {
        return pnfdId;
    }

    public void setPnfdId(UUID pnfdId) {
        this.pnfdId = pnfdId;
    }

    public NsdmNotificationsFilter pnfdName(String pnfdName) {
        this.pnfdName = pnfdName;
        return this;
    }

    /**
     * Match the name of the on-boarded PNFD.
     *
     * @return pnfdName
     **/
    @ApiModelProperty(value = "Match the name of the on-boarded PNFD.")
    public String getPnfdName() {
        return pnfdName;
    }

    public void setPnfdName(String pnfdName) {
        this.pnfdName = pnfdName;
    }

    public NsdmNotificationsFilter pnfdVersion(String pnfdVersion) {
        this.pnfdVersion = pnfdVersion;
        return this;
    }

    /**
     * Get pnfdVersion
     *
     * @return pnfdVersion
     **/
    @ApiModelProperty(value = "")
    public String getPnfdVersion() {
        return pnfdVersion;
    }

    public void setPnfdVersion(String pnfdVersion) {
        this.pnfdVersion = pnfdVersion;
    }

    public NsdmNotificationsFilter pnfdProvider(String pnfdProvider) {
        this.pnfdProvider = pnfdProvider;
        return this;
    }

    /**
     * Match the provider of the on-boarded PNFD.
     *
     * @return pnfdProvider
     **/
    @ApiModelProperty(value = "Match the provider of the on-boarded PNFD.")
    public String getPnfdProvider() {
        return pnfdProvider;
    }

    public void setPnfdProvider(String pnfdProvider) {
        this.pnfdProvider = pnfdProvider;
    }

    public NsdmNotificationsFilter pnfdInvariantId(UUID pnfdInvariantId) {
        this.pnfdInvariantId = pnfdInvariantId;
        return this;
    }

    /**
     * Get pnfdInvariantId
     *
     * @return pnfdInvariantId
     **/
    @ApiModelProperty(value = "")
    public UUID getPnfdInvariantId() {
        return pnfdInvariantId;
    }

    public void setPnfdInvariantId(UUID pnfdInvariantId) {
        this.pnfdInvariantId = pnfdInvariantId;
    }

    public NsdmNotificationsFilter pnfdOnboardingState(PnfdOnboardingStateType pnfdOnboardingState) {
        this.pnfdOnboardingState = pnfdOnboardingState;
        return this;
    }

    /**
     * Get pnfdOnboardingState
     *
     * @return pnfdOnboardingState
     **/
    @ApiModelProperty(value = "")
    public PnfdOnboardingStateType getPnfdOnboardingState() {
        return pnfdOnboardingState;
    }

    public void setPnfdOnboardingState(PnfdOnboardingStateType pnfdOnboardingState) {
        this.pnfdOnboardingState = pnfdOnboardingState;
    }

    public NsdmNotificationsFilter pnfdUsageState(PnfdUsageStateType pnfdUsageState) {
        this.pnfdUsageState = pnfdUsageState;
        return this;
    }

    /**
     * Get pnfdUsageState
     *
     * @return pnfdUsageState
     **/
    @ApiModelProperty(value = "")
    public PnfdUsageStateType getPnfdUsageState() {
        return pnfdUsageState;
    }

    public void setPnfdUsageState(PnfdUsageStateType pnfdUsageState) {
        this.pnfdUsageState = pnfdUsageState;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NsdmNotificationsFilter nsdmNotificationsFilter = (NsdmNotificationsFilter) o;
        return Objects.equals(this.notificationTypes, nsdmNotificationsFilter.notificationTypes) &&
            Objects.equals(this.nsdInfoId, nsdmNotificationsFilter.nsdInfoId) &&
            Objects.equals(this.nsdId, nsdmNotificationsFilter.nsdId) &&
            Objects.equals(this.nsdName, nsdmNotificationsFilter.nsdName) &&
            Objects.equals(this.nsdVersion, nsdmNotificationsFilter.nsdVersion) &&
            Objects.equals(this.nsdDesigner, nsdmNotificationsFilter.nsdDesigner) &&
            Objects.equals(this.nsdInvariantId, nsdmNotificationsFilter.nsdInvariantId) &&
            Objects.equals(this.vnfPkgIds, nsdmNotificationsFilter.vnfPkgIds) &&
            Objects.equals(this.pnfdInfoIds, nsdmNotificationsFilter.pnfdInfoIds) &&
            Objects.equals(this.nestedNsdInfoIds, nsdmNotificationsFilter.nestedNsdInfoIds) &&
            Objects.equals(this.nsdOnboardingState, nsdmNotificationsFilter.nsdOnboardingState) &&
            Objects.equals(this.nsdOperationalState, nsdmNotificationsFilter.nsdOperationalState) &&
            Objects.equals(this.nsdUsageState, nsdmNotificationsFilter.nsdUsageState) &&
            Objects.equals(this.pnfdId, nsdmNotificationsFilter.pnfdId) &&
            Objects.equals(this.pnfdName, nsdmNotificationsFilter.pnfdName) &&
            Objects.equals(this.pnfdVersion, nsdmNotificationsFilter.pnfdVersion) &&
            Objects.equals(this.pnfdProvider, nsdmNotificationsFilter.pnfdProvider) &&
            Objects.equals(this.pnfdInvariantId, nsdmNotificationsFilter.pnfdInvariantId) &&
            Objects.equals(this.pnfdOnboardingState, nsdmNotificationsFilter.pnfdOnboardingState) &&
            Objects.equals(this.pnfdUsageState, nsdmNotificationsFilter.pnfdUsageState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationTypes, nsdInfoId, nsdId, nsdName, nsdVersion, nsdDesigner, nsdInvariantId, vnfPkgIds, pnfdInfoIds, nestedNsdInfoIds, nsdOnboardingState, nsdOperationalState, nsdUsageState, pnfdId, pnfdName, pnfdVersion, pnfdProvider, pnfdInvariantId, pnfdOnboardingState, pnfdUsageState);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class NsdmNotificationsFilter {\n");

        sb.append("    notificationTypes: ").append(toIndentedString(notificationTypes)).append("\n");
        sb.append("    nsdInfoId: ").append(toIndentedString(nsdInfoId)).append("\n");
        sb.append("    nsdId: ").append(toIndentedString(nsdId)).append("\n");
        sb.append("    nsdName: ").append(toIndentedString(nsdName)).append("\n");
        sb.append("    nsdVersion: ").append(toIndentedString(nsdVersion)).append("\n");
        sb.append("    nsdDesigner: ").append(toIndentedString(nsdDesigner)).append("\n");
        sb.append("    nsdInvariantId: ").append(toIndentedString(nsdInvariantId)).append("\n");
        sb.append("    vnfPkgIds: ").append(toIndentedString(vnfPkgIds)).append("\n");
        sb.append("    pnfdInfoIds: ").append(toIndentedString(pnfdInfoIds)).append("\n");
        sb.append("    nestedNsdInfoIds: ").append(toIndentedString(nestedNsdInfoIds)).append("\n");
        sb.append("    nsdOnboardingState: ").append(toIndentedString(nsdOnboardingState)).append("\n");
        sb.append("    nsdOperationalState: ").append(toIndentedString(nsdOperationalState)).append("\n");
        sb.append("    nsdUsageState: ").append(toIndentedString(nsdUsageState)).append("\n");
        sb.append("    pnfdId: ").append(toIndentedString(pnfdId)).append("\n");
        sb.append("    pnfdName: ").append(toIndentedString(pnfdName)).append("\n");
        sb.append("    pnfdVersion: ").append(toIndentedString(pnfdVersion)).append("\n");
        sb.append("    pnfdProvider: ").append(toIndentedString(pnfdProvider)).append("\n");
        sb.append("    pnfdInvariantId: ").append(toIndentedString(pnfdInvariantId)).append("\n");
        sb.append("    pnfdOnboardingState: ").append(toIndentedString(pnfdOnboardingState)).append("\n");
        sb.append("    pnfdUsageState: ").append(toIndentedString(pnfdUsageState)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
