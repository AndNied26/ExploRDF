package com.explordf.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;

import com.explordf.dao.ExploRDFDao;
import com.explordf.dao.ExploRDFDaoFactory;

import dto.ConnectionFormDto;
import dto.PredicateDto;
import dto.TripleDto;

@Service
@PropertySource("classpath:explordf.properties")
public class ExploRDFService {
	
	@Autowired
	ExploRDFDaoFactory daoFactory;
	
	ExploRDFDao exploRDFDao;
	
//	@Autowired
//	private Environment env;
	
	@PostConstruct
	private void postConstruct() {
		System.out.println("Service postconstruct method entered");
//		String daoName = env.getProperty("dao.name");
		
		Properties props = new Properties();
		File f = new File("classpath:explordf.properties");
		String daoName = "";
		try {
//			OutputStream out = new FileOutputStream(f);
			InputStream in = new FileInputStream(f);
			DefaultPropertiesPersister p = new DefaultPropertiesPersister();
			p.load(props, in);
			daoName = props.getProperty("dao.name");
//			p.store(props, out, "Triple Store Connection");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println(daoName);
		exploRDFDao = daoFactory.getDao(daoName);
	}
	
	public List<TripleDto> simpleSearch(String term, boolean broaderSearch) {
		return exploRDFDao.simpleSearch(term, broaderSearch);
	}


	public List<PredicateDto> getPredicates() {
		return exploRDFDao.getPredicates();
	}


	public List<TripleDto> getSubject(String subject) {
		System.out.println("Entered sevice");
		return exploRDFDao.getSubject(subject);
	}
	
	public void changeDaoImpl(ConnectionFormDto connectionFormDto) {
		Properties props = new Properties();
		props.setProperty("dao.name", connectionFormDto.getName());
		props.setProperty("triplestore.server", connectionFormDto.getTripleStoreServer());
		props.setProperty("triplestore.repo", connectionFormDto.getTripleStoreRepo() != null ? connectionFormDto.getTripleStoreRepo() : "");
		props.setProperty("triplestore.username", connectionFormDto.getTripleStoreUsername() != null ? connectionFormDto.getTripleStoreUsername() : "");
		props.setProperty("triplestore.password", connectionFormDto.getTripleStorePassword() != null ? connectionFormDto.getTripleStorePassword() : "");
		
		File f = new File("classpath:explordf.properties");
		try {
			OutputStream out = new FileOutputStream(f);
			DefaultPropertiesPersister p = new DefaultPropertiesPersister();
			p.store(props, out, "Triple Store Connection");
			
			InputStream in = new FileInputStream(f);
			props = new Properties();
			p.load(props, in);
			System.out.println("Folgender Server: " + props.getProperty("triplestore.server"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(connectionFormDto.getName());
		exploRDFDao = daoFactory.getDao(connectionFormDto.getName());
//		System.out.println(env.getProperty("dao.name"));
//		System.out.println(env.getProperty("triplestore.server"));
		System.out.println(exploRDFDao.getType());
		
	}
}
