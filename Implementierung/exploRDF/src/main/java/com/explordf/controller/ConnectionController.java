package com.explordf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.explordf.dto.ConnectionDto;
import com.explordf.service.ConnectionService;
import com.explordf.service.QueryService;

@Controller
public class ConnectionController {

	@Autowired
	ConnectionService connectionService;
	
	@Autowired
	QueryService queryService;
	
	@RequestMapping(value="/connect", method=RequestMethod.POST)
	public @ResponseBody ConnectionDto setConnectionProps(@ModelAttribute("connectionFormDto") ConnectionDto connectionDto) {
		ConnectionDto connDto = connectionService.setConnectionProps(connectionDto);
		if(connDto != null) {
			queryService.setDao();
		}	
		return connDto;
	}
	
	@RequestMapping(value="/getConnectionProps", method=RequestMethod.GET)
	public @ResponseBody ConnectionDto getConnectionProps() {
		return connectionService.getConnectionProps();
	}
}
