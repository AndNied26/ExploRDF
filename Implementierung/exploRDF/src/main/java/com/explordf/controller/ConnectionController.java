package com.explordf.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.explordf.dto.ConnectionDto;
import com.explordf.service.ConnectionService;

/**
 * Spring RestController for managing user´s REST calls concerning the
 * connection to a triple store. It is annotated as Spring {@link RestController}, 
 * therefore it is created once during the application.
 * 
 * @author Andreas Niederquell
 *
 */
@RestController
public class ConnectionController {

	@Autowired
	private ConnectionService connectionService;

	/**
	 * Tries to connect to the triple store that is entered by the user.
	 * 
	 * @param connectionDto Required properties to try to connect to a triple store.
	 * @return ConnectionDto with the connection properties if the connection was
	 * 	successful, otherwise return null.
	 */
	@RequestMapping(value="/connect", method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ConnectionDto setConnectionProps(@RequestBody ConnectionDto connectionDto) {
		System.out.println(connectionDto.getTripleStoreServer());
		return connectionService.setConnectionProps(connectionDto);
	}
	
	/**
	 * Gets the properties of the currently connected triple store.
	 * 
	 * @return ConnectionDto with the connection properties.
	 */
	@RequestMapping(value="/getConnectionProps", method=RequestMethod.GET)
	public ConnectionDto getConnectionProps() {
		return connectionService.getConnectionProps();
	}
	
	/**
	 * Get a list of all supported triple store types the application
	 * is able to connect to.
	 * 
	 * @return List with the names of supported triple store servers.
	 */
	@RequestMapping(value="/getSupportedServers", method=RequestMethod.GET)
	public List<String> getSupportedServers(){
		return connectionService.getSupportedServers();
	}
}
