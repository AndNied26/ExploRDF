package com.explordf.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
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
