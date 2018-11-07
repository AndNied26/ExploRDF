package com.explordf.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;

import com.explordf.dto.ConnectionDto;

@Service
public class ConnectionService {

	@Autowired
	DaoServer daoServer;
	
	public ConnectionDto getConnectionProps() {
		return daoServer.getConnectionProps();
	}
	
//	@Autowired
//	Environment env;
//	
//	@Value("${triplestore.server}")
//	private String tripleStoreServer;
//	
//	@Value("${triplestore.name}")
//	private String tripleStoreName;
//	
//	@Value("${triplestore.repository}")
//	private String tripleStoreRepo;
//	
//	@Value("${triplestore.username}")
//	private String tripleStoreUserName;
//	
//	@Value("${triplestore.password}")
//	private String tripleStorePassword;
//	
//	
//	@PostConstruct
//	private void init( ) {
//		System.out.println("PostConstuct method in ConnectionService entered.");
//		System.out.println("Connection properties:");
//		System.out.println("Server: " + tripleStoreServer + " Env: " + env.getProperty("triplestore.server"));
//		System.out.println("Name: " + tripleStoreName + " Env: " + env.getProperty("triplestore.name"));
//		System.out.println("Repo: " + tripleStoreRepo + " Env: " + env.getProperty("triplestore.repository"));
//		System.out.println("Username: " + tripleStoreUserName + " Env: " + env.getProperty("triplestore.username"));
//		System.out.println("Password: " + tripleStorePassword + " Env: " + env.getProperty("triplestore.password"));
//		System.out.println();
//	}

	
	
//	@PreDestroy
//	public void close() {
//		DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
//		Properties props = new Properties();
//		props.setProperty("triplestore.name", "test1999Db");
//		File f = new File("src/main/resources/explordf.properties");
//		File f2 = new File("classpath:explordf.properties");
//		System.out.println("f2: " + f2.getAbsolutePath());
//		System.out.println("f: " + f.getAbsolutePath() + " " + f.canWrite());
//		try {
//			FileOutputStream out = new FileOutputStream(f2);
//			persister.store(props, out, "db");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//}
	
//	@PreDestroy
//	private void close() {
//		DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
//		
//		Properties props = new Properties();
//		File f = new File("classpath:explordf.properties");
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
//		System.out.println("Server: " + tripleStoreServer + " Env: " + env.getProperty("triplestore.server"));
//		System.out.println("Name: " + tripleStoreName + " Env: " + env.getProperty("triplestore.name"));
//		System.out.println("Repo: " + tripleStoreRepo + " Env: " + env.getProperty("triplestore.repository"));
//		System.out.println("Username: " + tripleStoreUserName + " Env: " + env.getProperty("triplestore.username"));
//		System.out.println("Password: " + tripleStorePassword + " Env: " + env.getProperty("triplestore.password"));
//		System.out.println();
//		
//	}
//
//	public void changeDaoImpl(ConnectionFormDto connectionFormDto) {
//		tripleStoreName = connectionFormDto.getTripleStoreName();
//		tripleStoreServer = connectionFormDto.getTripleStoreServer();
//		tripleStoreRepo = connectionFormDto.getTripleStoreRepo() != null ? 
//				connectionFormDto.getTripleStoreRepo() : "";
//		tripleStoreUserName = connectionFormDto.getTripleStoreUserName() != null ? 
//				connectionFormDto.getTripleStoreUserName() : "";
//		tripleStorePassword = connectionFormDto.getTripleStorePassword() != null ? 
//				connectionFormDto.getTripleStorePassword() : "";
//	}
	
	public void changeDaoImpl(ConnectionDto connectionDto) {
		daoServer.changeDaoImpl(connectionDto);
	}
	
	
}
