package com.explordf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ExploRDFController {
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index() {
		return "index";
	}
	
	@RequestMapping(value="/query", method=RequestMethod.GET)
	public String query() {
		return "query";
	}
	
	@RequestMapping(value="/connect", method=RequestMethod.GET)
	public String connect() {
		return "connect";
	}
	
	@RequestMapping(value="/help", method=RequestMethod.GET)
	public String help() {
		return "help";
	}
}
