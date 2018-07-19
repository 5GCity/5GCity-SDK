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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.nextworks.fivegcity.sdk.enums.Flavour;


/**
 * The class SDKFunction defines a function entity. Functions are created by special users (admin or editor permission)
 * A function is part of the final service created by a normal user via the composer module
 *  
 * @version v0.1
 */
@Entity
public class SDKFunction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * Unique identifier for the SDKFunction
	 */
	private UUID uuid;
	
	
	/**
	 * Human readable identifier of the SDKFunction 
	 */
	private String name;
	
	/**
	 * List of connection points exposed by the SDKFunction
	 */
	@OneToMany(mappedBy = "function", cascade=CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonProperty("connection_point")
	private List<ConnectionPoint> connectionPoints = new ArrayList<ConnectionPoint>();
	
	/**
	 * Flavor of the SDKFunction. It defines the ammount of resources necessary to run it
	 */
	private Flavour flavour;
	
	/**
	 * Current version of the SDKFunction
	 */
	private String version;
	
	/**
	 * Identifier of the vendor for the given SDKFunction
	 */
	private String vendor;
	
	@OneToMany(mappedBy = "function", cascade=CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonProperty("monitoring_parameters")
	private List<MonitoringParameter> monitoringParameters = new ArrayList<MonitoringParameter>();
	
	/**
	 * Short description of the SDKFunction
	 */
	private String description;
	
	
	@JsonIgnore
	@ManyToOne
	private SDKService service;
	
	
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
	public SDKFunction(String name, List<ConnectionPoint> connectionPoints, Flavour flavour, String version, 
				String vendor, List<MonitoringParameter> monitoringParameters, String description, SDKService service) {
		
		this.uuid = UUID.randomUUID();
		this.name = name;
		if(connectionPoints != null) {
			for(ConnectionPoint cp : connectionPoints)
				this.connectionPoints.add(cp);
		}
		this.flavour = flavour;
		this.version = version;
		if(monitoringParameters != null) {
			for(MonitoringParameter mon : monitoringParameters)
				this.monitoringParameters.add(mon);
		}
		this.service = service;
	}

	public SDKService getService() {
		return service;
	}


	public void setService(SDKService service) {
		this.service = service;
	}


	@JsonProperty("name")
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("connection_point")
	public List<ConnectionPoint> getConnectionPoints() {
		return connectionPoints;
	}


	public void setConnectionPoints(List<ConnectionPoint> connectionPoints) {
		this.connectionPoints = connectionPoints;
	}


	@JsonProperty("flavour")
	public Flavour getFlavour() {
		return flavour;
	}


	public void setFlavour(Flavour flavour) {
		this.flavour = flavour;
	}

	@JsonProperty("version")
	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	@JsonProperty("vendor")
	public String getVendor() {
		return vendor;
	}


	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	@JsonProperty("monitoring_parameters")
	public List<MonitoringParameter> getMonitoringParameters() {
		return monitoringParameters;
	}


	public void setMonitoringParameters(List<MonitoringParameter> monitoringParameters) {
		this.monitoringParameters = monitoringParameters;
	}


	@JsonProperty("description")
	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}

	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	@JsonProperty("uuid")
	public UUID getUuid() {
		return uuid;
	}
	
}
