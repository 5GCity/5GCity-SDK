package it.nextworks.composer.plugins.catalogue;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Catalogue {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String catalogueId;
	private CatalogueType type;
	private String url;
	private boolean authentication;
	private String username;
	private String password;
	
	
	public Catalogue() {
		//JPA Purpose
	}


	/**
	 * Creating a Catalogue entry in database
	 * @param catalogueId Catalogue ID
	 * @param type Type of catalogue
	 * @param url Url to reach the catalogue
	 * @param username Identify the user
	 * @param password Password o the username provided
	 */
	public Catalogue(String catalogueId, CatalogueType type, String url, 
					 boolean authentication, String username, String password) {
		this.catalogueId = catalogueId;
		this.type = type;
		this.url = url;
		this.authentication = authentication;
		this.username = username;
		this.password = password;
	}


	public CatalogueType getType() {
		return type;
	}


	public void setType(CatalogueType type) {
		this.type = type;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getCatalogueId() {
		return catalogueId;
	}


	public Long getId() {
		return id;
	}


	public boolean isAuthentication() {
		return authentication;
	}


	public void setCatalogueId(String catalogueId) {
		this.catalogueId = catalogueId;
	}
	
	
	
}
