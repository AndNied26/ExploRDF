package com.explordf.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.util.ResourceUtils;

import com.explordf.dao.ExploRDFDao;
import com.explordf.dto.ConnectionFormDto;

@Component
@PropertySource("classpath:explordf.properties")
public class DaoServer {

	
	@Autowired
	Environment env;
	
	@Autowired
	private List<ExploRDFDao> daos;
	
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
	
	private final Map<String, ExploRDFDao> daoCache = new HashMap<>();
	
	
	
	@PostConstruct
	public void initDaoCache() {
		System.out.println("DaoServer post construct");
		System.out.println("Autowired daoImpls: ");
		for(ExploRDFDao dao: daos) {
			System.out.print(dao.getType() + ", ");
			daoCache.put(dao.getType(), dao);
		}
		System.out.println();
		
		showConnProps();
		
		saveConnProps();
		
		showConnProps();
		
	}
	
	public ExploRDFDao getDao() {
		System.out.println("DaoServer getDao(): " + daoCache.get(tripleStoreServer).getType());
		ExploRDFDao dao = daoCache.get(tripleStoreServer);
		return dao;
	}
	
	public void changeDaoImpl(ConnectionFormDto connectionFormDto) {
		tripleStoreName = connectionFormDto.getTripleStoreName();
		tripleStoreServer = connectionFormDto.getTripleStoreServer();
		tripleStoreRepo = connectionFormDto.getTripleStoreRepo() != null ? 
				connectionFormDto.getTripleStoreRepo() : "";
		tripleStoreUserName = connectionFormDto.getTripleStoreUserName() != null ? 
				connectionFormDto.getTripleStoreUserName() : "";
		tripleStorePassword = connectionFormDto.getTripleStorePassword() != null ? 
				connectionFormDto.getTripleStorePassword() : "";
				
		showConnProps();
	}

	private void saveConnProps() {
		DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
		
		Properties props = new Properties();
		File f = new File("classpath:explordf.properties");
		
		props.setProperty("triplestore.server", tripleStoreServer);
		props.setProperty("triplestore.name", tripleStoreName);
		props.setProperty("triplestore.repository", tripleStoreRepo != null ? 
				tripleStoreRepo : "");
		props.setProperty("triplestore.username", tripleStoreUserName != null ? 
				tripleStoreUserName : "");
		props.setProperty("triplestore.password", tripleStorePassword != null ? 
				tripleStorePassword : "");
		
		try {
			FileOutputStream out = new FileOutputStream(f);
			persister.store(props, out, "db");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void showConnProps() {
		System.out.println("Connection properties:");
		System.out.println("Server: " + tripleStoreServer + " Env: " + env.getProperty("triplestore.server"));
		System.out.println("Name: " + tripleStoreName + " Env: " + env.getProperty("triplestore.name"));
		System.out.println("Repo: " + tripleStoreRepo + " Env: " + env.getProperty("triplestore.repository"));
		System.out.println("Username: " + tripleStoreUserName + " Env: " + env.getProperty("triplestore.username"));
		System.out.println("Password: " + tripleStorePassword + " Env: " + env.getProperty("triplestore.password"));
		System.out.println();
	}
	
	@PreDestroy
	private void close() {
		DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
		String filePath = "classpath:explordf.properties";
		Properties props = new Properties();
		
		
		props.setProperty("triplestore.server", tripleStoreServer);
		props.setProperty("triplestore.name", tripleStoreName);
		props.setProperty("triplestore.repository", tripleStoreRepo != null ? 
				tripleStoreRepo : "");
		props.setProperty("triplestore.username", tripleStoreUserName != null ? 
				tripleStoreUserName : "");
		props.setProperty("triplestore.password", tripleStorePassword != null ? 
				tripleStorePassword : "");
		
		try {
			File f = ResourceUtils.getFile(filePath);
			FileOutputStream out = new FileOutputStream(f);
			persister.store(props, out, "db");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showConnProps();		
	}
	
	
}

