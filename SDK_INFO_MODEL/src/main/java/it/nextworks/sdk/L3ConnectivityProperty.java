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
package it.nextworks.sdk;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.nextworks.sdk.enums.Protocol;

/**
 * 
 * The class defines a Layer3 connectivity rule, to be associated to a Link
 * 
 * @version v0.4
 *
 */
@Entity
public class L3ConnectivityProperty {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	@JsonProperty("id")
	private Long id;

	/**
	 * Unique ID of the L3 connectivity property
	 */
	@JsonProperty("uuid")
	private String uuid = UUID.randomUUID().toString();

	/**
	 * Source IP of the L3 connectivity property
	 */
	@JsonProperty("src_ip")
	private String src_ip;

	/**
	 * Destination IP of the L3 connectivity property
	 */
	@JsonProperty("dst_ip")
	private String dst_ip;

	/**
	 * Source port of the L3 connectivity property
	 */
	@JsonProperty("src_port")
	private int src_port;

	/**
	 * Destination port of the L3 connectivity property
	 */
	@JsonProperty("dst_port")
	private int dst_port;
	
	/**
	 * Protocol of the L3 connectivity property
	 */
	@JsonProperty("protocol")
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
	public L3ConnectivityProperty(String src_ip, String dst_ip, int src_port, int dst_port, Protocol proto) {
		this.protocol = proto;
		this.src_ip = src_ip;
		this.dst_ip = dst_ip;
		if(src_port > 0 && src_port < 65535) {this.src_port = src_port;}
		if(dst_port > 0 && dst_port < 65535) {this.src_port = src_port;}
	}
	
	
	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public String getSrc_ip() {
		return src_ip;
	}

	public void setSrc_ip(String src_ip) {
		this.src_ip = src_ip;
	}

	public String getDst_ip() {
		return dst_ip;
	}

	public void setDst_ip(String dst_ip) {
		this.dst_ip = dst_ip;
	}

	public int getSrc_port() {
		return src_port;
	}

	public void setSrc_port(int src_port) {
		this.src_port = src_port;
	}

	public int getDst_port() {
		return dst_port;
	}

	public void setDst_port(int dst_port) {
		this.dst_port = dst_port;
	}

	public String getUuid() {
		return uuid;
	}
	
	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public Long getId() {
		return id;
	}

	public boolean isValid(){
		if(this.protocol == null)
			return false;
		return true;
	}

}
