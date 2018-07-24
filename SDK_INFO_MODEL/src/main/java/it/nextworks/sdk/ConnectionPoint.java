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

import it.nextworks.sdk.enums.ConnectionPointType;

/**
 * 
 * The class defines a connection point associated to a SDKFunction or to a Link
 * 
 * @version v0.1
 *
 */
@Entity
public class ConnectionPoint {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * UUID for the connection point
	 */
	private UUID uuid;
	
	/**
	 * Connection point type: {internal/external}
	 */
	private ConnectionPointType type;
	
	
	@JsonIgnore
	@ManyToOne
	private SDKFunction function;
	
	@JsonIgnore
	@ManyToOne
	private Link link;
	
	/**
	 * Constructor used by JPA
	 */
	public ConnectionPoint() {
		//JPA Purpose
	}
	
	
	/** 
	 * Creates a new Connection point. A random UUID is generated on creation. This object is associated to a Link. 
	 * 	In this case, function parameter must be null
	 * @param type Type of the connection point
	 * @param link Link associated to the connection point
	 */
	public ConnectionPoint(ConnectionPointType type, Link link) {
		super();
		this.uuid = UUID.randomUUID();
		this.type = type;
		this.link = link;
		this.function = null;
	}
	
	/** 
	 * Creates a new Connection point. A random UUID is generated on creation. This object is associated to a SDKFunction
	 * 	In this case, link parameter must be null
	 * @param type Type of the connection point
	 */
	public ConnectionPoint(ConnectionPointType type, SDKFunction function) {
		super();
		this.uuid = UUID.randomUUID();
		this.type = type;
		this.link = null;
		this.function = function;
	}


	public Link getLink() {
		return link;
	}


	public void setLink(Link link) {
		this.link = link;
	}


	/**
	 * 
	 * @return unique identifier for the given connection point
	 */
	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	
	@JsonProperty("uuid")
	public UUID getUuid() {
		return uuid;
	}


	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}


	public SDKFunction getFunction() {
		return function;
	}


	public void setFunction(SDKFunction function) {
		this.function = function;
	}


	/**
	 * 
	 * @return type for the given connection point
	 */
	@JsonProperty("type")
	public ConnectionPointType getType() {
		return type;
	}

	
	public void setType(ConnectionPointType type) {
		this.type = type;
	}

	
}
