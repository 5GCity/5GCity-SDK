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

import com.fasterxml.jackson.annotation.JsonProperty;

import it.nextworks.sdk.enums.ScalingRatioType;
import it.nextworks.sdk.enums.StatusType;
/**
 * 
 * @version v0.1
 *
 */
@Entity
public class SDKService {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * Unique identifier for the SDKService
	 */
	private UUID uuid;
	
	/**
	 * Human readable name that identifies the SDKService
	 */
	private String name;
	
	
	/**
	 * Designer of the SDKService
	 */
	private String designer;
	
	/**
	 * Current version of the SDKService
	 */
	private String version;
	
	/**
	 * List of SDKFunctions related to the SDKService
	 */
	@OneToMany(mappedBy = "service", cascade=CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonProperty("functions")
	private List<SDKFunction> functions = new ArrayList<SDKFunction>();
	
	/**
	 * Scaling Ratio Type for the SDKService.
	 */
	private ScalingRatioType scalingRatio;
	
	/**
	 * The Status Type of the SDKService. Determinates if the SDKService is stored in an external Catalogue or just locally stored
	 */
	private StatusType status;
	
	/**
	 * A short description of the SDKService
	 */
	private String description;
	
	/**
	 * License for the SDKService
	 */
	@ManyToOne
	private License license;
	
	
	/**
	 * List of parameters to be monitored for the SDKService
	 */
	@OneToMany(mappedBy = "service", cascade=CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonProperty("monitoring_parameters")
	private List<MonitoringParameter> monitoringParameters = new ArrayList<MonitoringParameter>();
	
	
	/**
	 * List of scaling aspects for the SDKService. For each of these, the service should scale. 
	 */
	@OneToMany(mappedBy = "service", cascade=CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonProperty("scaling_aspect")
	private List<ScalingAspect> scalingAspects = new ArrayList<ScalingAspect>();
	
	/**
	 * List of the links composing the SDKService
	 */
	@OneToMany(mappedBy = "service", cascade=CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonProperty("link")
	private List<Link> topologyList = new ArrayList<Link>();
	
	/**
	 * Constructor used by JPA
	 */
	public SDKService() {
		//JPA purpose
	}
	
	
	/**
	 * 
	 * @param name Human readable name that identifies the SDKService
	 * @param designer Designer of the SDKService
	 * @param version Current version of the SDKService
	 * @param functions List of SDKFunctions composing the SDKService
	 * @param scalingRatio Scaling Ratio Type for the SDKService.
	 * @param status The Status Type of the SDKService. Determinates if the SDKService is stored in an external Catalogue or just locally stored
	 * @param description A short description of the SDKService
	 * @param license License for the SDKService
	 * @param monitoringParameters List of parameters to be monitored for the SDKService
	 * @param scalingAspects List of scaling aspects for the SDKService. For each of these, the service should scale.
	 * @param topologyList List of the links composing the SDKService
	 */
	public SDKService(String name, String designer, String version, List<SDKFunction> functions, ScalingRatioType scalingRatio,
			StatusType status, String description, License license, List<MonitoringParameter> monitoringParameters, 
			List<ScalingAspect> scalingAspects, List<Link> topologyList) {
		this.uuid = UUID.randomUUID();
		this.name = name;
		this.designer = designer;
		this.version = version;
		if(functions != null) {
			for(SDKFunction function : functions)
				this.functions.add(function);
		}
		this.scalingRatio = scalingRatio;
		this.status = status;
		this.description = description;
		this.license = license;
		if(monitoringParameters !=  null) {
			for(MonitoringParameter monitoringParameter: monitoringParameters)
				this.monitoringParameters.add(monitoringParameter);
		}
		if(scalingAspects != null) {
			for(ScalingAspect scalingAspect : scalingAspects)
				this.scalingAspects.add(scalingAspect);
		}
		for(Link link : topologyList)
			this.topologyList.add(link);
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("designer")
	public String getDesigner() {
		return designer;
	}

	public void setDesigner(String designer) {
		this.designer = designer;
	}

	@JsonProperty("version")
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@JsonProperty("function")
	public List<SDKFunction> getFunctions() {
		return functions;
	}

	public void setFunctions(List<SDKFunction> functions) {
		this.functions = functions;
	}

	@JsonProperty("scaling_ratio")
	public ScalingRatioType getScalingRatio() {
		return scalingRatio;
	}

	public void setScalingRatio(ScalingRatioType scalingRatio) {
		this.scalingRatio = scalingRatio;
	}

	@JsonProperty("status")
	public StatusType getStatus() {
		return status;
	}

	public void setStatus(StatusType status) {
		this.status = status;
	}

	@JsonProperty("descriptor")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonProperty("license")
	public License getLicense() {
		return license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	@JsonProperty("monitoring_parameters")
	public List<MonitoringParameter> getMonitoringParameters() {
		return monitoringParameters;
	}

	public void setMonitoringParameters(List<MonitoringParameter> monitoringParameters) {
		this.monitoringParameters = monitoringParameters;
	}

	@JsonProperty("scaling_aspect")
	public List<ScalingAspect> getScalingAspects() {
		return scalingAspects;
	}

	public void setScalingAspects(List<ScalingAspect> scalingAspects) {
		this.scalingAspects = scalingAspects;
	}

	@JsonProperty("link")
	public List<Link> getTopologyList() {
		return topologyList;
	}

	public void setTopologyList(List<Link> topologyList) {
		this.topologyList = topologyList;
	}

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

	
	
}
