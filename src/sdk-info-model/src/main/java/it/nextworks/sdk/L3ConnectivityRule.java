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
package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.nextworks.sdk.enums.Protocol;

import javax.persistence.Embeddable;


/**
 * L3ConnectivityRule
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "protocol",
    "srcIp",
    "srcPort",
    "dstIp",
    "dstPort"
})
@Embeddable
public class L3ConnectivityRule {

    private String dstIp;

    private Integer dstPort;

    private Protocol protocol;

    private String srcIp;

    private Integer srcPort;

    private static boolean validateIp(String ip) {
        return ip.matches("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9]?[0-9])\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9]?[0-9])$");
    }

    private static boolean validatePort(Integer port) {
        return 0 <= port && port <= 65535;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("dstIp")
    public String getDstIp() {
        return dstIp;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("dstIp")
    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("dstPort")
    public Integer getDstPort() {
        return dstPort;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("dstPort")
    public void setDstPort(Integer dstPort) {
        this.dstPort = dstPort;
    }

    @JsonProperty("protocol")
    public Protocol getProtocol() {
        return protocol;
    }

    @JsonProperty("protocol")
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("srcIp")
    public String getSrcIp() {
        return srcIp;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("srcIp")
    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("srcPort")
    public Integer getSrcPort() {
        return srcPort;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("srcPort")
    public void setSrcPort(Integer srcPort) {
        this.srcPort = srcPort;
    }

    @JsonIgnore
    public boolean isValid() {
        return this.protocol != null
            && (srcIp == null || validateIp(srcIp))
            && (dstIp == null || validateIp(dstIp))
            && (srcPort == null || validatePort(srcPort))
            && (dstPort == null || validatePort(dstPort));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(L3ConnectivityRule.class.getName())
            .append('[');
        sb.append("dstIp");
        sb.append('=');
        sb.append(((this.dstIp == null) ? "<null>" : this.dstIp));
        sb.append(',');
        sb.append("dstPort");
        sb.append('=');
        sb.append(((this.dstPort == null) ? "<null>" : this.dstPort));
        sb.append(',');
        sb.append("protocol");
        sb.append('=');
        sb.append(((this.protocol == null) ? "<null>" : this.protocol));
        sb.append(',');
        sb.append("srcIp");
        sb.append('=');
        sb.append(((this.srcIp == null) ? "<null>" : this.srcIp));
        sb.append(',');
        sb.append("srcPort");
        sb.append('=');
        sb.append(((this.srcPort == null) ? "<null>" : this.srcPort));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result * 31) + ((this.protocol == null) ? 0 : this.protocol.hashCode()));
        result = ((result * 31) + ((this.srcIp == null) ? 0 : this.srcIp.hashCode()));
        result = ((result * 31) + ((this.dstPort == null) ? 0 : this.dstPort.hashCode()));
        result = ((result * 31) + ((this.dstIp == null) ? 0 : this.dstIp.hashCode()));
        result = ((result * 31) + ((this.srcPort == null) ? 0 : this.srcPort.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof L3ConnectivityRule)) {
            return false;
        }
        L3ConnectivityRule rhs = ((L3ConnectivityRule) other);
        return (((((((this.protocol == rhs.protocol) || ((this.protocol != null) && this.protocol.equals(rhs.protocol))))
            && ((this.srcPort == rhs.srcPort) || ((this.srcPort != null) && this.srcPort.equals(rhs.srcPort))))
            && ((this.srcIp == rhs.srcIp) || ((this.srcIp != null) && this.srcIp.equals(rhs.srcIp))))
            && ((this.dstPort == rhs.dstPort) || ((this.dstPort != null) && this.dstPort.equals(rhs.dstPort))))
            && ((this.dstIp == rhs.dstIp) || ((this.dstIp != null) && this.dstIp.equals(rhs.dstIp))));
    }
}
