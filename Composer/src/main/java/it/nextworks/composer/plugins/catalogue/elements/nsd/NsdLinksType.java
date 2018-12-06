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
 * Links to resources related to this resource.
 */
@ApiModel(description = "Links to resources related to this resource.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-11-21T15:01:43.121+01:00")
public class NsdLinksType {
    @JsonProperty("self")
    private String self = null;

    @JsonProperty("nsd_content")
    private String nsdContent = null;

    public NsdLinksType self(String self) {
        this.self = self;
        return this;
    }

    /**
     * Get self
     *
     * @return self
     **/
    @ApiModelProperty(value = "")
    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public NsdLinksType nsdContent(String nsdContent) {
        this.nsdContent = nsdContent;
        return this;
    }

    /**
     * Get nsdContent
     *
     * @return nsdContent
     **/
    @ApiModelProperty(value = "")
    public String getNsdContent() {
        return nsdContent;
    }

    public void setNsdContent(String nsdContent) {
        this.nsdContent = nsdContent;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NsdLinksType nsdLinksType = (NsdLinksType) o;
        return Objects.equals(this.self, nsdLinksType.self) &&
            Objects.equals(this.nsdContent, nsdLinksType.nsdContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(self, nsdContent);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class NsdLinksType {\n");

        sb.append("    self: ").append(toIndentedString(self)).append("\n");
        sb.append("    nsdContent: ").append(toIndentedString(nsdContent)).append("\n");
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

