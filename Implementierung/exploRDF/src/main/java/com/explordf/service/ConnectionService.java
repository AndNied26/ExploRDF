package com.explordf.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.explordf.dao.ExploRDFDao;
import com.explordf.dto.ConnectionDto;

/**
 * 
 * Service class for triple store connection. This class manages the
 * communication between the Controller and the DAO tier concerning the triple store
 * connections. It is annotated as Spring {@link Service}, therefore it is
 * created once during the application.
 * 
 * @author Andreas Niederquell
 *
 */
@Service
public class ConnectionService {

	@Autowired
	@Qualifier("exploRDFDaoImpl")
	ExploRDFDao exploRDFDao;
	
	/**
	 * Gets the properties of the currently connected triple store.
	 * 
	 * @return ConnectionDto with the connection properties.
	 */
	public ConnectionDto getConnectionProps() {
		return exploRDFDao.getConnectionProps();
	}
	
	/**
	 * Tries to connect to the triple store that is entered by the user.
	 * 
	 * @param connectionDto Required properties to try to connect to a triple store.
	 * @return ConnectionDto with the connection properties if the connection was
	 * 	successful, otherwise return null.
	 */
	public ConnectionDto setConnectionProps(ConnectionDto connectionDto) {
		return exploRDFDao.setConnectionProps(connectionDto);
	}

	/**
	 * Get a list of all supported triple store types the application
	 * is able to connect to.
	 * 
	 * @return List with the names of supported triple store servers.
	 */
	public List<String> getSupportedServers() {
		return exploRDFDao.getSupportedServers();
	}
	
	
}
