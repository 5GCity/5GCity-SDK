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
 * @version v0.4
 *
 */
@Entity
public class ConnectionPoint {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	@JsonProperty("id")
	private Long id;

	
	
	/**
	 * Connection point type: {internal/external}
	 */
	@JsonProperty("type")
	private ConnectionPointType type;
	
	@JsonIgnore
	private boolean valid;
	
	@JsonIgnore
	@ManyToOne
	private SDKFunction function;
	
	@JsonIgnore
	@ManyToOne
	private Link link;
	
	@JsonProperty
	private String name;
	/**
	 * Constructor used by JPA
	 */
	public ConnectionPoint() {
		//JPA Purpose
	}
	

	/** 
	 * Creates a new Connection point. A random UUID is generated on creation.
	 * 	
	 * @param type Type of the connection point
	 */
	public ConnectionPoint(ConnectionPointType type, String name, SDKFunction function, Link link) {
		this.type = type;
		this.function = function;
		this.link = link;
		this.name = name;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
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
	public Long getId() {
		return id;
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
	public ConnectionPointType getType() {
		return type;
	}

	
	public void setType(ConnectionPointType type) {
		this.type = type;
	}

	
	public boolean isValid() {
		if(this.type == null)
			return false;
		if(this.name == null || this.name.length() < 2)
			return false;
		return true;
	}

	
}
