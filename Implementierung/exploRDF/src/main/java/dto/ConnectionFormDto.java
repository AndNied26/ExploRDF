package dto;

import javax.validation.constraints.NotNull;


public class ConnectionFormDto {

	private String tripleStoreServer;
	
	
//	@NotNull
	private String tripleStoreName;
	
	private String tripleStoreRepo;
	
	private String tripleStoreUserName;
	
	private String tripleStorePassword;


	public ConnectionFormDto() {
	}

	public ConnectionFormDto(String tripleStoreName, String tripleStoreServer, String tripleStoreRepo, String tripleStoreUsername,
			String tripleStorePassword) {
		this.tripleStoreName = tripleStoreName;
		this.tripleStoreServer = tripleStoreServer;
		this.tripleStoreRepo = tripleStoreRepo;
		this.tripleStoreUserName = tripleStoreUsername;
		this.tripleStorePassword = tripleStorePassword;
	}

	public String getTripleStoreName() {
		return tripleStoreName;
	}

	public void setTripleStoreName(String name) {
		this.tripleStoreName = name;
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
