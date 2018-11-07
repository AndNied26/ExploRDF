package com.explordf.dto;

import javax.validation.constraints.NotNull;


public class ConnectionDto {

	private String tripleStoreServer;
	
	
	@NotNull
	private String tripleStoreUrl;
	
	private String tripleStoreRepo;
	
	private String tripleStoreUserName;
	
	private String tripleStorePassword;


	public ConnectionDto() {
	}

	public ConnectionDto(String tripleStoreUrl, String tripleStoreServer, String tripleStoreRepo, String tripleStoreUsername,
			String tripleStorePassword) {
		this.tripleStoreUrl = tripleStoreUrl;
		this.tripleStoreServer = tripleStoreServer;
		this.tripleStoreRepo = tripleStoreRepo;
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
