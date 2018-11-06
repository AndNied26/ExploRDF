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
	
	@Autowired
	ConnectionService connectionService;
	
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
		connectionService.changeDaoImpl(connectionFormDto);
		
	}
	
}
