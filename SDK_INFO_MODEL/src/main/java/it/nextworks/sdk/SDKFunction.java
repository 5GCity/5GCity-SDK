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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.nextworks.sdk.enums.Flavour;


/**
 * The class SDKFunction defines a function entity. Functions are created by special users (admin or editor permission)
 * A function is part of the final service created by a normal user via the composer module
 *  
 * @version v0.4
 */
@Entity
public class SDKFunction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	@JsonProperty("id")
	private Long id;

	/**
	 * Unique identifier for the SDKFunction
	 */
	@JsonProperty("uuid")
	private String uuid = UUID.randomUUID().toString();
	
	
	/**
	 * Human readable identifier of the SDKFunction 
	 */
	@JsonProperty("name")
	private String name;
	
	/**
	 * List of connection points exposed by the SDKFunction
	 */
	@OneToMany(mappedBy = "function", cascade=CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonProperty("connection_point")
	private List<ConnectionPoint> connectionPoints;
	
	
	@ElementCollection(fetch=FetchType.EAGER)
	@JsonProperty("flavours")
	private List<Flavour> flavour;
	
	/**
	 * Current version of the SDKFunction
	 */
	@JsonProperty("version")
	private String version;
	
	/**
	 * Identifier of the vendor for the given SDKFunction
	 */
	@JsonProperty("vendor")
	private String vendor;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@OneToMany(mappedBy = "function", cascade=CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonProperty("monitoring_parameters")
	private List<MonitoringParameter> monitoringParameters;
	
	/**
	 * Short description of the SDKFunction
	 */
	@JsonProperty("description")
	private String description;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	@JsonProperty("metadata")
	private Map<String, String> metadata = new HashMap<String, String>();
	
	
	/**
	 * Default constructor. It is used only by JPA
	 */
	public SDKFunction() {
		//JPA Purpose
	}
	
	
	/**
	 * SDKFunction constructor
	 * 
	 * @param name Name of the Function
	 * @param connectionPoints List of connectionPoints related to the function
	 * @param flavour Flavor of the function
	 * @param version Current version of the function
	 * @param vendor Vendor identifier of the function
	 * @param monitoringParameters List of parameters to be monitored in the function
	 * @param description A short description of the function.
	 * @param service 
	 */
	public SDKFunction(String name, List<ConnectionPoint> connectionPoints, List<Flavour> flavour, String version, 
				String vendor, List<MonitoringParameter> monitoringParameters, String description, Map<String, String> metadata) {
		
		this.name = name;
		if(connectionPoints != null) {
			for(ConnectionPoint cp : connectionPoints)
				this.connectionPoints.add(cp);
		}
		if(flavour != null && flavour.size() > 0)
			this.flavour = flavour;
		this.version = version;
		if(monitoringParameters != null) {
			for(MonitoringParameter mon : monitoringParameters)
				this.monitoringParameters.add(mon);
		}
		if (metadata != null) this.metadata = metadata;
	}


	public Map<String, String> getMetadata() {
		return metadata;
	}


	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}


	public void setFlavour(List<Flavour> flavour) {
		this.flavour = flavour;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<ConnectionPoint> getConnectionPoints() {
		return connectionPoints;
	}


	public void setConnectionPoints(List<ConnectionPoint> connectionPoints) {
		this.connectionPoints = connectionPoints;
	}


	public List<Flavour> getFlavour() {
		return flavour;
	}

	
	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	public String getVendor() {
		return vendor;
	}


	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public List<MonitoringParameter> getMonitoringParameters() {
		return monitoringParameters;
	}


	public void setMonitoringParameters(List<MonitoringParameter> monitoringParameters) {
		this.monitoringParameters = monitoringParameters;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}

	
	public Long getId() {
		return id;
	}


	public String getUuid() {
		return uuid;
	}
	
	public boolean isValid() {
	    if(this.name == null || this.name.length() == 0)
	    	return false;
		if(this.connectionPoints == null || this.connectionPoints.size() == 0)
			return false;
		if(this.version == null || this.version.length() == 0)
			return false;
		if(this.vendor == null || this.vendor.length() == 0)
			return false;
		if(this.flavour == null || this.flavour.size() == 0)
			return false;
		return true;
	}
	
}
