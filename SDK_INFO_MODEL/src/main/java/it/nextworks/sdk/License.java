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

import it.nextworks.sdk.enums.LicenseType;

/**
 * 
 * The class License defines the license related to the Service.
 * 
 * @version v0.1
 *
 */
@Entity
public class License {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/**
	 * Unique identifier of the object
	 */
	private UUID uuid;
	
	/**
	 * Type of the license
	 */
	private LicenseType type;

	/**
	 * URL related to the license
	 */
	private String URL;
	
	@JsonIgnore
	@ManyToOne
	private SDKService service;
	
	
	/**
	 * Constructor used by JPA
	 */
	public License() {
		//JPA Purpose
	}
	
	/**
	 * 
	 * @param type Type of the license
	 * @param URL URL of the license
	 */
	public License(LicenseType type, String URL) {
		this.uuid = UUID.randomUUID();
		this.type = type;
		this.URL = URL;
	}

	@JsonProperty("type")
	public LicenseType getType() {
		return type;
	}

	public void setType(LicenseType type) {
		this.type = type;
	}

	@JsonProperty("URL")
	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}
	
	@JsonProperty("id")
	public Long getId() {
		return this.id;
	}
	
	
	@JsonProperty("uuid")
	public UUID getUuid() {
		return uuid;
	}
	
	public SDKService getService() {
		return service;
	}

	public void setService(SDKService service) {
		this.service = service;
	}



	
}
