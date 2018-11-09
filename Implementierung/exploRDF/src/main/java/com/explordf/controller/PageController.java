package com.explordf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.explordf.dto.ConnectionDto;

@Controller
public class PageController {
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index() {
		return "main";
	}
	
	@RequestMapping(value="/query", method=RequestMethod.GET)
	public String query() {
		return "index";
	}
	
	@RequestMapping(value="/connect", method=RequestMethod.GET)
	public String connect(Model model) {
		model.addAttribute("connectionDto", new ConnectionDto());
		return "connect";
	}
	
	@RequestMapping(value="/help", method=RequestMethod.GET)
	public String help() {
		return "help";
	}
	
}
