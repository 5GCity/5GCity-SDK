/*
* Copyright 2018 Nextworks s.r.l.
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
package it.nextworks.fivegcity.sdk;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.nextworks.fivegcity.sdk.enums.Protocol;
import it.nextworks.fivegcity.sdk.exceptions.MalformattedElementException;

/**
 * 
 * The class defines a Layer3 connectivity rule, to be associated to a Link
 * 
 * @version v0.1
 *
 */
@Entity
public class L3ConnectivityProperty {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * Unique ID of the L3 connectivity property
	 */
	private UUID uuid;

	/**
	 * Source IP of the L3 connectivity property
	 */
	private String src_ip;

	/**
	 * Destination IP of the L3 connectivity property
	 */
	private String dst_ip;

	/**
	 * Source port of the L3 connectivity property
	 */
	private int src_port;

	/**
	 * Destination port of the L3 connectivity property
	 */
	private int dst_port;
	
	/**
	 * Protocol of the L3 connectivity property
	 */
	private Protocol protocol;
	
	
	@JsonIgnore
	@ManyToOne
	private Link link;
	
	
	/**
	 * Constructor used by JPA
	 */
	public L3ConnectivityProperty() {
		// JPA Purpose
	}
	
	/**
	 * 
	 * @param src_ip Source IP of the rule
	 * @param dst_ip Destination IP of the rule
	 * @param src_port Source port of the rule
	 * @param dst_port Destination port of the rule
	 * @param proto Protocol of the rule
	 */
	public L3ConnectivityProperty(String src_ip, String dst_ip, int src_port, int dst_port, Protocol proto, Link link) {
		this.uuid = UUID.randomUUID();
		this.protocol = proto;
		if(src_ip == null) {this.src_ip = src_ip;}
		if(dst_ip == null) {this.dst_ip = dst_ip;}
		if(src_port > 0 && src_port < 65535) {this.src_port = src_port;}
		if(dst_port > 0 && dst_port < 65535) {this.src_port = src_port;}
		this.link = link;
	}
	
	
	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	@JsonProperty("src_ip")
	public String getSrc_ip() {
		return src_ip;
	}

	public void setSrc_ip(String src_ip) {
		this.src_ip = src_ip;
	}

	@JsonProperty("dst_ip")
	public String getDst_ip() {
		return dst_ip;
	}

	public void setDst_ip(String dst_ip) {
		this.dst_ip = dst_ip;
	}

	@JsonProperty("src_port")
	public int getSrc_port() {
		return src_port;
	}

	public void setSrc_port(int src_port) {
		this.src_port = src_port;
	}

	@JsonProperty("dst_port")
	public int getDst_port() {
		return dst_port;
	}

	public void setDst_port(int dst_port) {
		this.dst_port = dst_port;
	}

	@JsonProperty("uuid")
	public UUID getUuid() {
		return uuid;
	}
	
	@JsonProperty("protocol")
	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	public void isValid() throws MalformattedElementException {
		if (src_ip == null || !isIP(src_ip)) throw new MalformattedElementException("src_ip is not a valid IPv4");
		if (dst_ip == null || !isIP(dst_ip)) throw new MalformattedElementException("dst_ip is not a valid IPv4");
	}
	
	/**
	 * Verify that a given string is a valid IPv4 address
	 * @param ip
	 * @return false in case ip is not valid, true otherwise
	 */
	private boolean isIP(String ip) {
		if (ip == null || ip.isEmpty()) return false;
	    ip = ip.trim();
	    if ((ip.length() < 6) & (ip.length() > 15)) return false;
	    try {
	        Pattern pattern = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
	        Matcher matcher = pattern.matcher(ip);
	        return matcher.matches();
	    } catch (PatternSyntaxException ex) {
	        return false;
	    }
	}
	
}
