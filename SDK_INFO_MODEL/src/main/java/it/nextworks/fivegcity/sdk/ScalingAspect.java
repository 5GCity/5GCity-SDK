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

import it.nextworks.fivegcity.sdk.enums.ActionType;

/**
 * 
 * The class creates a ScalingAspect entity. It is used to define a scaling strategy based on the monitoring parameters
 * 
 * @version v0.1
 *
 */
@Entity
public class ScalingAspect {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * Unique identifier of the ScalingAspect entity
	 */
	private UUID uuid;
	
	/**
	 * Human readable identifier of the ScalingAspect
	 */
	private String name;
	
	
	/**
	 * List of parameters to be monitored, in order to enable scaling
	 */
	@OneToMany(mappedBy = "scalingAspect", cascade=CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonProperty("monitoring_parameter")
	private List<MonitoringParameter> monitoringParameters = new ArrayList<MonitoringParameter>();
	
	/**
	 * Action to be taken in case thresholds are reached
	 */
	private ActionType action;
	
	@JsonIgnore
	@ManyToOne
	private SDKService service;
	
	/**
	 * Constructor used by JPA
	 */
	public ScalingAspect() {
		//JPA Purpose
	}
	
	/**
	 * Construction of a ScalingAspect entity
	 * @param name Human readable identifier of the scaling policy
	 * @param monitoringParameters List of parameters to be monitored for scaling purposes. 
	 *                All the elements of this list must have a threshold grater that 0 and a DirectionType defined (not null)
	 * @param action Scaling type
	 * @param service
	 */
	public ScalingAspect(String name, ArrayList<MonitoringParameter> monitoringParameters, ActionType action, SDKService service) {
		this.uuid = UUID.randomUUID();
		this.name = name;
		for(MonitoringParameter monitoringParameter : monitoringParameters)
			if(monitoringParameter.isValidForScalingPurpose()) {
				this.monitoringParameters.add(monitoringParameter);
			}
		this.service = service;
		this.action = action;
	}


	@JsonProperty("name")
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@JsonProperty("monitoring_parameter")
	public List<MonitoringParameter> getMonitoringParameters() {
		return monitoringParameters;
	}


	public void setMonitoringParameters(List<MonitoringParameter> monitoringParameters) {
		this.monitoringParameters = monitoringParameters;
	}


	@JsonProperty("action")
	public ActionType getAction() {
		return action;
	}


	public void setAction(ActionType action) {
		this.action = action;
	}


	@JsonProperty("id")
	public Long getId() {
		return id;
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
