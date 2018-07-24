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

/**
 * The Link class defines the interconnectivity between functions, over the 
 * 	connection points and connectivity rules, over L3ConnectivityProperties.
 * 
 * @version v0.1
 *
 */
@Entity
public class Link {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/** 
	 * Unique identifier of the link
	*/
	private UUID uuid;
	
	/**
	 * Human readable name that identifies the link
	 * 
	 */
	private String name;
	
	/**
	 * List of the L3 connectivity properties
	 * 
	 */
	@OneToMany(mappedBy = "link", cascade=CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonProperty("l3_property")
	private List<L3ConnectivityProperty> l3Properties = new ArrayList<L3ConnectivityProperty>();
	

	/**
	 * List of the connection points related to the link
	 */
	@OneToMany(mappedBy = "link", cascade=CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonProperty("connection_point")
	private List<ConnectionPoint> connectionPoints = new ArrayList<ConnectionPoint>();
	
	@JsonIgnore
	@ManyToOne
	private SDKService service;
	
	/**
	 * Default constructor used by JPA
	 */
	public Link() {
		//JPA purpose
	}
	
	
	/**
	 * 
	 * @param name Human readable name that identifies the link
	 * @param l3Properties List of the L3 connectivity properties
	 * @param cps List of the connection points related to the link
	 * @param service
	 */
	public Link(String name, ArrayList<L3ConnectivityProperty> l3Properties, ArrayList<ConnectionPoint> cps, SDKService service) {
		this.uuid = UUID.randomUUID();
		this.name = name;
		if(l3Properties != null) {
			for (L3ConnectivityProperty l3Property : l3Properties) {
				this.l3Properties.add(l3Property);
			}
		}
		if (cps != null) {
			for (ConnectionPoint cp : cps) {
				this.connectionPoints.add(cp);
			}
		}
		this.service = service;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@JsonProperty("l3_property")
	public List<L3ConnectivityProperty> getL3Properties() {
		return l3Properties;
	}


	public void setL3Properties(List<L3ConnectivityProperty> l3Properties) {
		this.l3Properties = l3Properties;
	}

	@JsonProperty("connection_point")
	public List<ConnectionPoint> getConnectionPoints() {
		return connectionPoints;
	}


	public void setConnectionPoints(List<ConnectionPoint> connectionPoints) {
		this.connectionPoints = connectionPoints;
	}


	@JsonProperty("id")
	public Long getId() {
		return id;
	}


	public SDKService getService() {
		return service;
	}


	public void setService(SDKService service) {
		this.service = service;
	}
	
	@JsonProperty("uuid")
	public UUID getUuid() {
		return uuid;
	}
	
	
	public boolean isValid() {
		//TODO
		return true;
	}
	
}
