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

import java.util.List;
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
 * @version v0.5
 */
@Entity
public class SDKFunctionInstance {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty("id")
	private Long id;
	

	
	
	/**
	 * Flavor of the SDKFunction. It defines the amount of resources necessary to run it. It must chosen from the Flavours list in SDKFunction
	 */
	@JsonProperty("flavour")
	private Flavour flavour;
	
	
	@JsonIgnore
	private boolean valid;
	
	
	/**
	 * Must be a list of the one defined in the related SDKFunction
	 */
	@OneToMany(mappedBy = "functionInstance", cascade=CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonProperty("monitoring_parameters")
	private List<MonitoringParameter> monitoringParameters;
	
	
	@ManyToOne
	@JsonIgnore
	private SDKService service;

/*
	@ManyToOne
	@JsonIgnore
	private SDKFunction sdkFunction;
*/	
	
	@JsonProperty("function_id")
	private Long functionId;
	
	
	


	public SDKFunctionInstance() {
		//JPA purpose
	}
	
	
	public SDKFunctionInstance(Flavour flavour, SDKFunction function, SDKService service) {
		this.flavour = flavour;
		for(MonitoringParameter monitoringParameter : monitoringParameters)
			if(monitoringParameter.isValid()) {
				this.monitoringParameters.add(monitoringParameter);
			}
//		this.sdkFunction = function;
		this.functionId = function.getId();
		this.service = service;
	}

	
/*
	public SDKFunction getSdkFunction() {
		return sdkFunction;
	}


	public void setSdkFunction(SDKFunction sdkFunction) {
		this.sdkFunction = sdkFunction;
	}
*/

	/**
	 * 
	 * @return Flavor chose for the SDK Function Instance
	 */
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
	public List<MonitoringParameter> getMonitoringParameters() {
		return monitoringParameters;
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

	
	
	
	public boolean isValid() {
	    if(this.flavour == null)
	    	return false;
		return true;
	}
	

	public void deleteMonitoringParameter(MonitoringParameter monitoringParameter) {
		if(monitoringParameter.isValid()) {
			for(MonitoringParameter param : this.monitoringParameters) {
				if(param.getId().toString().equalsIgnoreCase(monitoringParameter.getId().toString())) {
					this.monitoringParameters.remove(param);
				}
			}
				
		}
	}
	
	public Long getFunctionId() {
		return functionId;
	}


	public void setFunctionId(Long functionId) {
		this.functionId = functionId;
	}
	
	/**
	 * 
	 * @param monitoringParameters
	 */
	public void setMonitoringParameters(List<MonitoringParameter> monitoringParameters) {
		for(MonitoringParameter monitoringParameter : monitoringParameters)
			this.monitoringParameters.add(monitoringParameter);
	}
}
