package com.explordf.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.explordf.dto.ConnectionDto;
import com.explordf.service.ConnectionService;
import com.explordf.service.QueryService;

@RestController
public class ConnectionController {

	@Autowired
	ConnectionService connectionService;
	
	@Autowired
	QueryService queryService;
	
	@RequestMapping(value="/connect", method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ConnectionDto setConnectionProps(@RequestBody ConnectionDto connectionDto) {
		System.out.println(connectionDto.getTripleStoreServer());
//		ConnectionDto connDto = connectionService.setConnectionProps(connectionDto);
//		if(connDto != null) {
//			queryService.setDao();
//		}	
//		return connDto;
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
