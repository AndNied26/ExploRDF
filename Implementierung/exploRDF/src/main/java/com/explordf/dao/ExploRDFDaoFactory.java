package com.explordf.dao;

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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;

@Component
public class ExploRDFDaoFactory {

	@Autowired
	private List<ExploRDFDao> daos;
	
	private final Map<String, ExploRDFDao> daoCache = new HashMap<>();
	
	@Value("${dao.name}")
	private String daoName;
	
	@PostConstruct
	public void initDaoCache() {
		System.out.println("DaoFactory post construct");
		for(ExploRDFDao dao: daos) {
			System.out.println("Factory: " + dao.getType());
			daoCache.put(dao.getType(), dao);
		}
	}
	
	public ExploRDFDao getDao(String type) {
		ExploRDFDao dao = daoCache.get(type);
		return dao;
	}
	
	public String getDaoName() {
		return daoName;
	}
	
	public void setDaoName(String daoName) {
		this.daoName = daoName;
	}
	
	@PreDestroy
	public void close() {
		DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
		Properties props = new Properties();
		props.setProperty("dao.name", "montesoryDB");
		File f = new File("classpath/:explordf.properties");
		try {
			FileOutputStream out = new FileOutputStream(f);
			persister.store(props, out, "db");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
