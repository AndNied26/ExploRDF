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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.ResourceUtils;

import com.explordf.dao.ExploRDFDao;
import com.explordf.dao.RDF4JDaoImpl;
import com.explordf.dto.ConnectionDto;

@Component
@PropertySource("classpath:explordf.properties")
public class DaoServer {

	private static final Logger logger = LoggerFactory.getLogger(DaoServer.class);
	
	@Autowired
	Environment env;
	
	@Autowired
	private List<ExploRDFDao> daos;
	
	@Value("${triplestore.server}")
	private String tripleStoreServer;
	
	@Value("${triplestore.url}")
	private String tripleStoreUrl;
	
	@Value("${triplestore.repository}")
	private String tripleStoreRepo;
	
	@Value("${triplestore.username}")
	private String tripleStoreUserName;
	
	@Value("${triplestore.password}")
	private String tripleStorePassword;
	
	private final Map<String, ExploRDFDao> daoCache = new HashMap<>();
	
	
	
	@PostConstruct
	public void initDaoCache() {
		logger.info("DaoServer post construct");
		logger.info("Autowired daoImpls: ");
		String s = "";
		for(ExploRDFDao dao: daos) {
			s += dao.getType() + ", ";
			daoCache.put(dao.getType(), dao);
		}
		logger.info(s.substring(0, s.length()-2));
		
		showConnProps();
		
		saveConnProps();
		
		showConnProps();
		
	}
	
	public ExploRDFDao getDao() {
		logger.info("DaoServer getDao(): " + daoCache.get(tripleStoreServer).getType());
		ExploRDFDao dao = daoCache.get(tripleStoreServer);
		return dao;
	}
	
	public void changeDaoImpl(ConnectionDto connectionFormDto) {
		tripleStoreUrl = connectionFormDto.getTripleStoreUrl();
		tripleStoreServer = connectionFormDto.getTripleStoreServer();
		tripleStoreRepo = connectionFormDto.getTripleStoreRepo() != null ? 
				connectionFormDto.getTripleStoreRepo() : "";
		tripleStoreUserName = connectionFormDto.getTripleStoreUserName() != null ? 
				connectionFormDto.getTripleStoreUserName() : "";
		tripleStorePassword = connectionFormDto.getTripleStorePassword() != null ? 
				connectionFormDto.getTripleStorePassword() : "";
				
		showConnProps();
	}

	public ConnectionDto getConnectionProps() {
		ConnectionDto connDto = new ConnectionDto();
		connDto.setTripleStoreUrl(tripleStoreUrl);
		connDto.setTripleStoreRepo(tripleStoreRepo);
		return connDto;
	}
	
	
	private void saveConnProps() {
		DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
		
		Properties props = new Properties();
		File f = new File("classpath:explordf.properties");
		
		props.setProperty("triplestore.server", tripleStoreServer);
		props.setProperty("triplestore.url", tripleStoreUrl);
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
		logger.info("Connection properties:");
		logger.info("Server: " + tripleStoreServer + " Env: " + env.getProperty("triplestore.server"));
		logger.info("Name: " + tripleStoreUrl + " Env: " + env.getProperty("triplestore.url"));
		logger.info("Repo: " + tripleStoreRepo + " Env: " + env.getProperty("triplestore.repository"));
		logger.info("Username: " + tripleStoreUserName + " Env: " + env.getProperty("triplestore.username"));
		logger.info("Password: " + tripleStorePassword + " Env: " + env.getProperty("triplestore.password"));
		System.out.println();
	}
	
	@PreDestroy
	private void close() {
		DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
		String filePath = "classpath:explordf.properties";
		Properties props = new Properties();
		
		
		props.setProperty("triplestore.server", tripleStoreServer);
		props.setProperty("triplestore.url", tripleStoreUrl);
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

