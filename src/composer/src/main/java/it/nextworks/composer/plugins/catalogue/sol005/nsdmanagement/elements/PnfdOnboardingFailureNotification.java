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
package it.nextworks.composer.plugins.catalogue.sol005.nsdmanagement.elements;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import org.threeten.bp.OffsetDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

/**
 * This type represents a PNFD management notification, which informs the
 * receiver of the failure of on-boarding a PNFD. It shall comply with the
 * provisions defined in Table 5.5.2.14-1. The support of this notification is
 * mandatory. The notification is triggered when the on-boarding of a PNFD
 * fails.
 */
@ApiModel(description = "This type represents a PNFD management notification, which informs the receiver of the failure of on-boarding a  PNFD. It shall comply with the provisions defined in Table 5.5.2.14-1. The support of this notification is mandatory. The notification is triggered when the on-boarding of a PNFD fails.")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-23T16:31:35.952+02:00")

public class PnfdOnboardingFailureNotification {
    @JsonProperty("id")
    private UUID id = null;

    @JsonProperty("notificationType")
    private NotificationType notificationType = null;

    @JsonProperty("subscriptionId")
    private UUID subscriptionId = null;

    @JsonProperty("timeStamp")
    private OffsetDateTime timeStamp = null;

    @JsonProperty("pnfdInfoId")
    private UUID pnfdInfoId = null;

    @JsonProperty("pnfdId")
    private UUID pnfdId = null;

    @JsonProperty("onboardingFailureDetails")
    private ProblemDetails onboardingFailureDetails = null;

    @JsonProperty("_links")
    private PnfdmLinks links = null;

    public PnfdOnboardingFailureNotification id(UUID id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     **/
    @ApiModelProperty(required = true, value = "")
    @NotNull

    @Valid

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PnfdOnboardingFailureNotification notificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
        return this;
    }

    /**
     * Discriminator for the different notification types. Shall be set to
     * \"PnfdOnboardingFailureNotification\" for this notification type.
     *
     * @return notificationType
     **/
    @ApiModelProperty(required = true, value = "Discriminator for the different notification types. Shall be set to \"PnfdOnboardingFailureNotification\" for this notification type.")
    @NotNull

    @Valid

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public PnfdOnboardingFailureNotification subscriptionId(UUID subscriptionId) {
        this.subscriptionId = subscriptionId;
        return this;
    }

    /**
     * Get subscriptionId
     *
     * @return subscriptionId
     **/
    @ApiModelProperty(value = "")

    @Valid

    public UUID getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(UUID subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public PnfdOnboardingFailureNotification timeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    /**
     * Date-time of the generation of the notification.
     *
     * @return timeStamp
     **/
    @ApiModelProperty(required = true, value = "Date-time of the generation of the notification.")
    @NotNull

    @Valid

    public OffsetDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public PnfdOnboardingFailureNotification pnfdInfoId(UUID pnfdInfoId) {
        this.pnfdInfoId = pnfdInfoId;
        return this;
    }

    /**
     * Get pnfdInfoId
     *
     * @return pnfdInfoId
     **/
    @ApiModelProperty(required = true, value = "")
    @NotNull

    @Valid

    public UUID getPnfdInfoId() {
        return pnfdInfoId;
    }

    public void setPnfdInfoId(UUID pnfdInfoId) {
        this.pnfdInfoId = pnfdInfoId;
    }

    public PnfdOnboardingFailureNotification pnfdId(UUID pnfdId) {
        this.pnfdId = pnfdId;
        return this;
    }

    /**
     * Get pnfdId
     *
     * @return pnfdId
     **/
    @ApiModelProperty(value = "")

    @Valid

    public UUID getPnfdId() {
        return pnfdId;
    }

    public void setPnfdId(UUID pnfdId) {
        this.pnfdId = pnfdId;
    }

    public PnfdOnboardingFailureNotification onboardingFailureDetails(ProblemDetails onboardingFailureDetails) {
        this.onboardingFailureDetails = onboardingFailureDetails;
        return this;
    }

    /**
     * Get onboardingFailureDetails
     *
     * @return onboardingFailureDetails
     **/
    @ApiModelProperty(required = true, value = "")
    @NotNull

    @Valid

    public ProblemDetails getOnboardingFailureDetails() {
        return onboardingFailureDetails;
    }

    public void setOnboardingFailureDetails(ProblemDetails onboardingFailureDetails) {
        this.onboardingFailureDetails = onboardingFailureDetails;
    }

    public PnfdOnboardingFailureNotification links(PnfdmLinks links) {
        this.links = links;
        return this;
    }

    /**
     * This type represents the links to resources that a PNFD management
     * notification can contain.
     *
     * @return links
     **/
    @ApiModelProperty(required = true, value = "This type represents the links to resources that a PNFD management notification can contain.")
    @NotNull

    @Valid

    public PnfdmLinks getLinks() {
        return links;
    }

    public void setLinks(PnfdmLinks links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PnfdOnboardingFailureNotification pnfdOnboardingFailureNotification = (PnfdOnboardingFailureNotification) o;
        return Objects.equals(this.id, pnfdOnboardingFailureNotification.id)
                && Objects.equals(this.notificationType, pnfdOnboardingFailureNotification.notificationType)
                && Objects.equals(this.subscriptionId, pnfdOnboardingFailureNotification.subscriptionId)
                && Objects.equals(this.timeStamp, pnfdOnboardingFailureNotification.timeStamp)
                && Objects.equals(this.pnfdInfoId, pnfdOnboardingFailureNotification.pnfdInfoId)
                && Objects.equals(this.pnfdId, pnfdOnboardingFailureNotification.pnfdId)
                && Objects.equals(this.onboardingFailureDetails,
                pnfdOnboardingFailureNotification.onboardingFailureDetails)
                && Objects.equals(this.links, pnfdOnboardingFailureNotification.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, notificationType, subscriptionId, timeStamp, pnfdInfoId, pnfdId,
                onboardingFailureDetails, links);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PnfdOnboardingFailureNotification {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    notificationType: ").append(toIndentedString(notificationType)).append("\n");
        sb.append("    subscriptionId: ").append(toIndentedString(subscriptionId)).append("\n");
        sb.append("    timeStamp: ").append(toIndentedString(timeStamp)).append("\n");
        sb.append("    pnfdInfoId: ").append(toIndentedString(pnfdInfoId)).append("\n");
        sb.append("    pnfdId: ").append(toIndentedString(pnfdId)).append("\n");
        sb.append("    onboardingFailureDetails: ").append(toIndentedString(onboardingFailureDetails)).append("\n");
        sb.append("    links: ").append(toIndentedString(links)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
