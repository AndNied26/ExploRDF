package com.explordf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.explordf.dto.ConnectionFormDto;

@Controller
public class PageController {
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index() {
		return "index";
	}
	
	@RequestMapping(value="/query", method=RequestMethod.GET)
	public String query() {
		return "query";
	}
	
	@RequestMapping(value="/connect", method=RequestMethod.GET)
	public String connect(Model model) {
		model.addAttribute("connectionFormDto", new ConnectionFormDto());
		return "connect";
	}
	
	@RequestMapping(value="/help", method=RequestMethod.GET)
	public String help() {
		return "help";
	}
	
}
