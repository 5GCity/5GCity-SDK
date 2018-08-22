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

import it.nextworks.sdk.enums.Flavour;

/**
 * The class SDKFunction defines a function entity. Functions are created by special users (admin or editor permission)
 * A function is part of the final service created by a normal user via the composer module
 *  
 * @version v0.1
 */
@Entity
public class SDKFunctionInstance {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/**
	 * Unique identifier for the SDKFunction
	 */
	private UUID uuid;
	
	
	/**
	 * Function descriptor for the function instance
	 */
	@JsonIgnore
	@ManyToOne
	private SDKFunction function;
	
	
	/**
	 * Flavor of the SDKFunction. It defines the amount of resources necessary to run it. It must chosen from the Flavours list in SDKFunction
	 */
	private Flavour flavour;
	
	
	/**
	 * Must be a list of the one defined in the related SDKFunction
	 */
	@OneToMany(mappedBy = "functionInstance", cascade=CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonProperty("monitoring_parameters")
	private List<MonitoringParameter> monitoringParameters = new ArrayList<MonitoringParameter>();
	
	
	
	
	@JsonIgnore
	@ManyToOne
	private SDKService service;


	
	public SDKFunctionInstance() {
		//JPA purpose
	}
	
	
	public SDKFunctionInstance(SDKFunction function, Flavour flavour, List<MonitoringParameter> monitoringParameters, SDKService service) {
		this.uuid = UUID.randomUUID();
		this.flavour = flavour;
		for(MonitoringParameter monitoringParameter : monitoringParameters)
			this.monitoringParameters.add(monitoringParameter);
		this.service = service;
		this.function = function;
	}

	

	/**
	 * 
	 * @return function associated to the SDK Function Instance
	 */
	public SDKFunction getFunction() {
		return function;
	}


	/**
	 * Function associated to the SDK Function Instance
	 * @param functionId 
	 */
	public void setFunctionId(SDKFunction function) {
		this.function = function;
	}



	/**
	 * 
	 * @return Flavor chose for the SDK Function Instance
	 */
	@JsonProperty("flavour")
	public Flavour getFlavour() {
		return flavour;
	}



	/**
	 * 
	 * @param flavour
	 */
	public void setFlavour(Flavour flavour) {
		this.flavour = flavour;
	}



	/**
	 * List of monitoring parameters to be checked in the SDKFunction Instance
	 * @return List of monitoring parameters
	 */
	@JsonProperty("monitoring_parameters")
	public List<MonitoringParameter> getMonitoringParameters() {
		return monitoringParameters;
	}


	/**
	 * 
	 * @param monitoringParameters
	 */
	public void setMonitoringParameters(List<MonitoringParameter> monitoringParameters) {
		for(MonitoringParameter monitoringParameter : monitoringParameters)
			this.monitoringParameters.add(monitoringParameter);
	}



	/**
	 * 
	 * @return service associated to the SDKFunction Instance
	 */
	public SDKService getService() {
		return service;
	}




	public void setService(SDKService service) {
		this.service = service;
	}




	public Long getId() {
		return id;
	}




	public UUID getUuid() {
		return uuid;
	}
	
	
	
	
	
	
}
