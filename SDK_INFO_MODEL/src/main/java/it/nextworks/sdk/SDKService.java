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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
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

import it.nextworks.sdk.enums.ScalingRatioType;
import it.nextworks.sdk.enums.StatusType;
/**
 * 
 * @version v0.5
 *
 */
@Entity
public class SDKService {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty("id")
	private Long id;
	
	@JsonIgnore
	private boolean valid;
	
	/**
	 * Human readable name that identifies the SDKService
	 */
	@JsonProperty("name")
	private String name;
	
	
	/**
	 * Designer of the SDKService
	 */
	@JsonProperty("designer")
	private String designer;
	
	/**
	 * Current version of the SDKService
	 */
	@JsonProperty("version")
	private String version;
	
	
	/**
	 * List of SDKFunctionInstances related to the SDKService
	 */
	@OneToMany(mappedBy = "service", cascade=CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonProperty("functions")
	private List<SDKFunctionInstance> functions = new ArrayList<SDKFunctionInstance>();
	
	/**
	 * Scaling Ratio Type for the SDKService.
	 */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@JsonProperty("scaling_ratio")
	private ScalingRatioType scalingRatio;
	
	/**
	 * The Status Type of the SDKService. Determinates if the SDKService is stored in an external Catalogue or just locally stored
	 */
	@JsonProperty("status")
	private StatusType status = StatusType.SAVED;
	
	
	/**
	 * A short description of the SDKService
	 */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@JsonProperty("descriptor")
	private String description;
	
	
	/**
	 * License for the SDKService
	 */
	@Embedded
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	@JsonProperty("license")
	private License license;
	
	
	/**
	 * List of parameters to be monitored for the SDKService
	 */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@OneToMany(mappedBy = "service", cascade=CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonProperty("monitoring_parameters")
	private List<MonitoringParameter> monitoringParameters = new ArrayList<MonitoringParameter>();
	
	
	/**
	 * List of scaling aspects for the SDKService. For each of these, the service should scale. 
	 */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
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
	 * Map of metadata composed by a key and a value
	 */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	@JsonProperty("metadata")
	private Map<String, String> metadata = new HashMap<String, String>();
	
	
	@JsonIgnore
	private String nsNode;
	
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
	 * @param functions List of SDKFunctionInstancess composing the SDKService
	 * @param scalingRatio Scaling Ratio Type for the SDKService.
	 * @param description A short description of the SDKService
	 * @param license License for the SDKService
	 * @param monitoringParameters List of parameters to be monitored for the SDKService
	 * @param scalingAspects List of scaling aspects for the SDKService. For each of these, the service should scale.
	 * @param topologyList List of the links composing the SDKService
	 * @param metadata Map of data composed by key and value
	 */
	public SDKService(String name, String designer, String version, ScalingRatioType scalingRatio,
			String description, License license, Map<String, String> metadata) {
		this.name = name;
		this.designer = designer;
		this.version = version;
		this.scalingRatio = scalingRatio;
		this.description = description;
		this.license = license;
		if (metadata != null) this.metadata = metadata;
	}


	public String getName() {
		return name;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	
	public String getDesigner() {
		return designer;
	}

	
	public void setDesigner(String designer) {
		this.designer = designer;
	}

	
	public String getVersion() {
		return version;
	}

	
	public void setVersion(String version) {
		this.version = version;
	}

	
	public List<SDKFunctionInstance> getFunctions() {
		return functions;
	}

	
	public void setFunctions(List<SDKFunctionInstance> functions) {
		this.functions  = functions;
	}

	
	public ScalingRatioType getScalingRatio() {
		return scalingRatio;
	}

	
	public void setScalingRatio(ScalingRatioType scalingRatio) {
		this.scalingRatio = scalingRatio;
	}
	

	public StatusType getStatus() {
		return status;
	}

	public void setStatus(StatusType status) {
		this.status = status;
	}


	public String getDescription() {
		return description;
	}

	
	public void setDescription(String description) {
		this.description = description;
	}


	public License getLicense() {
		return license;
	}


	public void setLicense(License license) {
		this.license = license;
	}

	
	public List<MonitoringParameter> getMonitoringParameters() {
		return monitoringParameters;
	}
	

	public void setMonitoringParameters(List<MonitoringParameter> monitoringParameters) {
		this.monitoringParameters = monitoringParameters;
	}

	
	public List<ScalingAspect> getScalingAspects() {
		return scalingAspects;
	}

	
	public void setScalingAspects(List<ScalingAspect> scalingAspects) {
		this.scalingAspects = scalingAspects;
	}

	
	public List<Link> getTopologyList() {
		return topologyList;
	}

	
	public void setTopologyList(List<Link> topologyList) {
		this.topologyList = topologyList;
	}

	
	public Long getId() {
		return id;
	}

	
	/**
	 * The method checks the validity of the object 
	 * @return true in case all requirements are satisfied.
	 *         false otherwise
	 */
	public boolean isValid() {
		if(this.name == null || this.name == "")
			return false;
		if(this.designer == null || this.designer == "")
			return false;
		if(this.version == null || this.version == "")
			return false;
		return true;
	}
	
	
	
	public void deleteScalingAspect(ScalingAspect scalingAspect) {
		for(ScalingAspect scale : this.scalingAspects) {
			if(scale.getId().toString().equalsIgnoreCase(scalingAspect.getId().toString())) {
				this.scalingAspects.remove(scale);
				break;
			}
		}
	}
	
	public void deleteMonitoringParameter(MonitoringParameter parameter) {
		for(MonitoringParameter param: this.monitoringParameters) {
			if(param.getId().toString().equalsIgnoreCase(parameter.getId().toString()))
				this.monitoringParameters.remove(param);
		}
	}
	
	public Map<String, String> getMetadata() {
		return this.metadata;
	}


	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}


	public String getNsNode() {
		return nsNode;
	}


	public void setNsNode(String nsNode) {
		this.nsNode = nsNode;
	}
	
	
	
}
