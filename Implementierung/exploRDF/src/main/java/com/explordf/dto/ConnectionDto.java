package com.explordf.dto;

import javax.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for the connection properties of a Triple Store.
 * 
 * @author Andreas Niederquell
 *
 */
public class ConnectionDto {

	@NotNull
	private String tripleStoreServer;
	
	
	@NotNull
	private String tripleStoreUrl;
	
	private String tripleStoreRepo;
	
	private String tripleStoreGraph;
	
	private String tripleStoreUserName;
	
	private String tripleStorePassword;


	public ConnectionDto() {
	}

	public ConnectionDto(String tripleStoreUrl, String tripleStoreServer, 
			String tripleStoreRepo, String tripleStoreGraph, 
			String tripleStoreUsername, String tripleStorePassword) {
		this.tripleStoreUrl = tripleStoreUrl;
		this.tripleStoreServer = tripleStoreServer;
		this.tripleStoreRepo = tripleStoreRepo;
		this.tripleStoreGraph = tripleStoreGraph;
		this.tripleStoreUserName = tripleStoreUsername;
		this.tripleStorePassword = tripleStorePassword;
	}

	public String getTripleStoreUrl() {
		return tripleStoreUrl;
	}

	public void setTripleStoreUrl(String url) {
		this.tripleStoreUrl = url;
	}

	public String getTripleStoreServer() {
		return tripleStoreServer;
	}

	public void setTripleStoreServer(String tripleStoreServer) {
		this.tripleStoreServer = tripleStoreServer;
	}

	public String getTripleStoreRepo() {
		return tripleStoreRepo;
	}

	public void setTripleStoreRepo(String tripleStoreRepo) {
		this.tripleStoreRepo = tripleStoreRepo;
	}
	
	public String getTripleStoreGraph() {
		return tripleStoreGraph;
	}
	
	public void setTripleStoreGraph(String tripleStoreGraph) {
		this.tripleStoreGraph = tripleStoreGraph;
	}

	public String getTripleStoreUserName() {
		return tripleStoreUserName;
	}

	public void setTripleStoreUserName(String tripleStoreUsername) {
		this.tripleStoreUserName = tripleStoreUsername;
	}

	public String getTripleStorePassword() {
		return tripleStorePassword;
	}

	public void setTripleStorePassword(String tripleStorePassword) {
		this.tripleStorePassword = tripleStorePassword;
	}
	
	
	
}
