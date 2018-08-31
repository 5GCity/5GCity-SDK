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

import javax.persistence.Embeddable;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.nextworks.sdk.enums.LicenseType;

/**
 * 
 * The class License defines the license related to the Service.
 * 
 * @version v0.4
 *
 */
@Embeddable
public class License {


	/**
	 * Type of the license
	 */
	@JsonProperty("type")
	private LicenseType type;

	/**
	 * URL related to the license
	 */
	@JsonProperty("URL")
	private String URL;
	

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
		this.type = type;
		this.URL = URL;
	}

	public LicenseType getType() {
		return type;
	}

	public void setType(LicenseType type) {
		this.type = type;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}
	
	
	public boolean isValid() {
		if(this.type == null) 
			return false;		
		if(this.URL == null || this.URL.length() == 0)
			return false;
		return true;
	}

	
}
