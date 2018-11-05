package dto;

import javax.validation.constraints.NotNull;


public class ConnectionFormDto {

	private String name;
	
	@NotNull
	private String tripleStoreServer;
	
	private String tripleStoreRepo;
	
	private String tripleStoreUsername;
	
	private String tripleStorePassword;


	public ConnectionFormDto() {
	}

	public ConnectionFormDto(String name, String tripleStoreServer, String tripleStoreRepo, String tripleStoreUsername,
			String tripleStorePassword) {
		this.name = name;
		this.tripleStoreServer = tripleStoreServer;
		this.tripleStoreRepo = tripleStoreRepo;
		this.tripleStoreUsername = tripleStoreUsername;
		this.tripleStorePassword = tripleStorePassword;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getTripleStoreUsername() {
		return tripleStoreUsername;
	}

	public void setTripleStoreUsername(String tripleStoreUsername) {
		this.tripleStoreUsername = tripleStoreUsername;
	}

	public String getTripleStorePassword() {
		return tripleStorePassword;
	}

	public void setTripleStorePassword(String tripleStorePassword) {
		this.tripleStorePassword = tripleStorePassword;
	}
	
	
	
}
