package com.explordf.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;

import dto.ConnectionFormDto;

@Component
public class ConnectionService {

	@Value("${triplestore.server}")
	private String tripleStoreServer;
	
	@Value("${triplestore.name}")
	private String tripleStoreName;
	
	@Value("${triplestore.repository}")
	private String tripleStoreRepo;
	
	@Value("${triplestore.username}")
	private String tripleStoreUserName;
	
	@Value("${triplestore.password}")
	private String tripleStorePassword;
	
	
	@PostConstruct
	private void init( ) {
		System.out.println("PostConstuct method in ConnectionService entered.");
		System.out.println("Connection properties:");
		System.out.println("Server: " + tripleStoreServer);
		System.out.println("Name: " + tripleStoreName);
		System.out.println("Repo: " + tripleStoreRepo);
		System.out.println("Username: " + tripleStoreUserName);
		System.out.println("Password: " + tripleStorePassword);
		System.out.println();
	}

	public String getTripleStoreServer() {
		return tripleStoreServer;
	}

	public void setTripleStoreServer(String tripleStoreServer) {
		this.tripleStoreServer = tripleStoreServer;
	}

	public String getTripleStoreName() {
		return tripleStoreName;
	}

	public void setTripleStoreName(String tripleStoreName) {
		this.tripleStoreName = tripleStoreName;
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

	public void setTripleStoreUserName(String tripleStoreUserName) {
		this.tripleStoreUserName = tripleStoreUserName;
	}

	public String getTripleStorePassword() {
		return tripleStorePassword;
	}

	public void setTripleStorePassword(String tripleStorePassword) {
		this.tripleStorePassword = tripleStorePassword;
	}
	
	@PreDestroy
	public void close() {
		DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
		Properties props = new Properties();
		props.setProperty("triplestore.server", "montesoryDB");
		File f = new File("/explordf.properties");
		try {
			FileOutputStream out = new FileOutputStream(f);
			persister.store(props, out, "db");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
	
//	@PreDestroy
//	private void close() {
//		DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
//		
//		Properties props = new Properties();
//		File f = new File("classpath*:explordf.properties");
//		
//		props.setProperty("triplestore.server", tripleStoreServer);
//		props.setProperty("triplestore.name", tripleStoreName);
//		props.setProperty("triplestore.repository", tripleStoreRepo != null ? 
//				tripleStoreRepo : "");
//		props.setProperty("triplestore.username", tripleStoreUserName != null ? 
//				tripleStoreUserName : "");
//		props.setProperty("triplestore.password", tripleStorePassword != null ? 
//				tripleStorePassword : "");
//		
//		try {
//			FileOutputStream out = new FileOutputStream(f);
//			persister.store(props, out, "db");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("PreDestroy method in ConnectionService entered.");
//		System.out.println("Connection properties:");
//		System.out.println("Server: " + tripleStoreServer);
//		System.out.println("Name: " + tripleStoreName);
//		System.out.println("Repo: " + tripleStoreRepo);
//		System.out.println("Username: " + tripleStoreUserName);
//		System.out.println("Password: " + tripleStorePassword);
//		System.out.println();
//		
//	}

	public void changeDaoImpl(ConnectionFormDto connectionFormDto) {
		tripleStoreName = connectionFormDto.getTripleStoreName();
		tripleStoreServer = connectionFormDto.getTripleStoreServer();
		tripleStoreRepo = connectionFormDto.getTripleStoreRepo() != null ? 
				connectionFormDto.getTripleStoreRepo() : "";
		tripleStoreUserName = connectionFormDto.getTripleStoreUserName() != null ? 
				connectionFormDto.getTripleStoreUserName() : "";
		tripleStorePassword = connectionFormDto.getTripleStorePassword() != null ? 
				connectionFormDto.getTripleStorePassword() : "";
	}
	
	
	
}
