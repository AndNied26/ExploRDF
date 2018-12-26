package com.explordf.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;

import com.explordf.dao.ExploRDFDao;
import com.explordf.dto.ConnectionDto;

@Service
public class ConnectionService {

	@Autowired
	@Qualifier("exploRDFDaoImpl")
	ExploRDFDao exploRDFDao;
	
	public ConnectionDto getConnectionProps() {
		return exploRDFDao.getConnectionProps();
	}
	
	public ConnectionDto setConnectionProps(ConnectionDto connectionDto) {
		return exploRDFDao.setConnectionProps(connectionDto);
	}

	public List<String> getSupportedServers() {
		return exploRDFDao.getSupportedServers();
	}
	
	
}
