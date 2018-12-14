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

import java.util.Objects;

/**
 * This type represents a subscription request related to notifications about NSD management.
 */
@ApiModel(description = "This type represents a subscription request related to notifications about NSD management.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-11-21T15:01:43.121+01:00")
public class NsdmSubscriptionRequest {
    @JsonProperty("filter")
    private NsdmNotificationsFilter filter = null;

    @JsonProperty("callbackUri")
    private String callbackUri = null;

    @JsonProperty("authentication")
    private SubscriptionAuthentication authentication = null;

    public NsdmSubscriptionRequest filter(NsdmNotificationsFilter filter) {
        this.filter = filter;
        return this;
    }

    /**
     * Get filter
     *
     * @return filter
     **/
    @ApiModelProperty(value = "")
    public NsdmNotificationsFilter getFilter() {
        return filter;
    }

    public void setFilter(NsdmNotificationsFilter filter) {
        this.filter = filter;
    }

    public NsdmSubscriptionRequest callbackUri(String callbackUri) {
        this.callbackUri = callbackUri;
        return this;
    }

    /**
     * The URI of the endpoint to send the notification to.
     *
     * @return callbackUri
     **/
    @ApiModelProperty(required = true, value = "The URI of the endpoint to send the notification to.")
    public String getCallbackUri() {
        return callbackUri;
    }

    public void setCallbackUri(String callbackUri) {
        this.callbackUri = callbackUri;
    }

    public NsdmSubscriptionRequest authentication(SubscriptionAuthentication authentication) {
        this.authentication = authentication;
        return this;
    }

    /**
     * Get authentication
     *
     * @return authentication
     **/
    @ApiModelProperty(value = "")
    public SubscriptionAuthentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(SubscriptionAuthentication authentication) {
        this.authentication = authentication;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NsdmSubscriptionRequest nsdmSubscriptionRequest = (NsdmSubscriptionRequest) o;
        return Objects.equals(this.filter, nsdmSubscriptionRequest.filter) &&
            Objects.equals(this.callbackUri, nsdmSubscriptionRequest.callbackUri) &&
            Objects.equals(this.authentication, nsdmSubscriptionRequest.authentication);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filter, callbackUri, authentication);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class NsdmSubscriptionRequest {\n");

        sb.append("    filter: ").append(toIndentedString(filter)).append("\n");
        sb.append("    callbackUri: ").append(toIndentedString(callbackUri)).append("\n");
        sb.append("    authentication: ").append(toIndentedString(authentication)).append("\n");
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
