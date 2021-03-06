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

	@RequestMapping(value="/connect", method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ConnectionDto setConnectionProps(@RequestBody ConnectionDto connectionDto) {
		System.out.println(connectionDto.getTripleStoreServer());
		return connectionService.setConnectionProps(connectionDto);
	}
	
	@RequestMapping(value="/getConnectionProps", method=RequestMethod.GET)
	public ConnectionDto getConnectionProps() {
		return connectionService.getConnectionProps();
	}
	
	@RequestMapping(value="/getSupportedServers", method=RequestMethod.GET)
	public List<String> getSupportedServers(){
		return connectionService.getSupportedServers();
	}
}
