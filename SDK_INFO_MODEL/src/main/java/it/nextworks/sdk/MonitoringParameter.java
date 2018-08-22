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

import it.nextworks.sdk.enums.DirectionType;
import it.nextworks.sdk.enums.MonitoringParameterType;

/**
 * 
 * A MonitoringParameter class defines a parameter to be monitored for different purposes. 
 * In case of scaling purposes, the object must have a threshold greater than 0 and a direction type defined.
 * The monitoring parameter object may be part of different entities (only one of them)
 * 		# Functions
 * 		# Services
 * 		# ScalingAspects
 * 
 * @version v0.1
 *
 */
@Entity
public class MonitoringParameter {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * Unique identifier of the entity
	 */
	private UUID uuid;
	
	/**
	 * Parameter Type
	 */
	private MonitoringParameterType name;

	/**
	 * Comparing operand between current value and threshold.
	 */
	private DirectionType direction;
	
	/**
	 * Threshold for the parameter
	 */
	private float threshold;

	
	@JsonIgnore
	@ManyToOne
	private SDKFunction function;
	
	@JsonIgnore
	@ManyToOne
	private SDKFunctionInstance functionInstance;
	
	@JsonIgnore
	@ManyToOne
	private SDKService service;
	
	@JsonIgnore
	@ManyToOne
	private ScalingAspect scalingAspect;
	
	
	/**
	 * Contructor used by JPA
	 */
	public MonitoringParameter() {
		//JPA Purpose
	}
	
	/**
	 * Constructor to be used when the monitoring parameter is combined with a SDKFunction
	 * @param name Parameter type to be monitored
	 * @param function Function where the parameter is used
	 */
	public MonitoringParameter(MonitoringParameterType name, SDKFunction function) {
		this.uuid = UUID.randomUUID();
		this.name = name;
		this.function = function;
		this.functionInstance = null;
		this.threshold = 0;
		this.service = null;
		this.scalingAspect = null;
		this.direction = null;
	}
	
	/**
	 * Constructor to be used when the monitoring parameter is combined with a SDKService
	 * @param name Parameter type to be monitored
	 * @param service Service where the parameter is used
	 */
	public MonitoringParameter(MonitoringParameterType name, SDKService service) {
		this.uuid = UUID.randomUUID();
		this.name = name;
		this.service = service;

		this.function = null;
		this.threshold = 0;
		this.scalingAspect = null;
		this.direction = null;
	}
	
	/**
	 * Constructor to be used when the monitoring parameter is combined with a ScalingAspect
	 * @param name Parameter type to be monitored
	 * @param threshold Threshold of the parameter
	 * @param direction Operand to be used with the threshold
	 * @param scalingAspect ScalingAspect where the object is associated
	 */
	public MonitoringParameter(MonitoringParameterType name, float threshold, DirectionType direction, ScalingAspect scalingAspect) {
		this.uuid = UUID.randomUUID();
		this.name = name;
		this.threshold = threshold;
		this.direction = direction;
		this.scalingAspect = scalingAspect;

		this.function = null;
		this.service = null;
	}

	
	/**
	 * Constructor to be used when the monitoring parameter is combined with a SDKFunction
	 * @param name Parameter type to be monitored
	 * @param function Function where the parameter is used
	 */
	public MonitoringParameter(MonitoringParameterType name, SDKFunctionInstance functionInstance) {
		this.uuid = UUID.randomUUID();
		this.name = name;
		this.function = null;
		this.functionInstance = functionInstance;
		this.functionInstance = null;
		this.threshold = 0;
		this.service = null;
		this.scalingAspect = null;
		this.direction = null;
	}
	
	


	@JsonProperty("name")
	public MonitoringParameterType getName() {
		return name;
	}

	public void setName(MonitoringParameterType name) {
		this.name = name;
	}

	@JsonProperty("threshold")
	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	@JsonProperty("id")
	public Long getId() {
		return id;
	}


	public SDKFunction getFunction() {
		return function;
	}


	public void setFunction(SDKFunction function) {
		this.function = function;
	}


	public SDKService getService() {
		return service;
	}


	public void setService(SDKService service) {
		this.service = service;
	}
	
	public SDKFunctionInstance getFunctionInstance() {
		return functionInstance;
	}

	public void setFunctionInstance(SDKFunctionInstance functionInstance) {
		this.functionInstance = functionInstance;
	}


	public ScalingAspect getScalingAspect() {
		return scalingAspect;
	}


	public void setScalingAspect(ScalingAspect scalingAspect) {
		this.scalingAspect = scalingAspect;
	}

	@JsonProperty("uuid")
	public UUID getUuid() {
		return uuid;
	}


	@JsonProperty("direction")
	public DirectionType getDirection() {
		return direction;
	}


	public void setDirection(DirectionType direction) {
		this.direction = direction;
	}
	
	/**
	 * The method checks if the object is valid for a ScalignAspect.
	 * @return true Positive response in case of threshold greater than zero and direction not null \
	 *         false otherwise
	 */
	public boolean isValidForScalingPurpose() {
		if(this.threshold > 0 && this.direction != null)
			return true;
		return false;
	}
	
}
