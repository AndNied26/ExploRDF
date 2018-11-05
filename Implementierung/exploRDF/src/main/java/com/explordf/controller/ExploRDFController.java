package com.explordf.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.explordf.service.ExploRDFService;

import dto.ConnectionFormDto;

@Controller
public class ExploRDFController {
	
	@Autowired
	ExploRDFService exploRDFService;
	
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
	
	@RequestMapping(value="/connect", method=RequestMethod.POST)
	public String changeConnection(@ModelAttribute("connectionFormDto") ConnectionFormDto connectionFormDto) {
		exploRDFService.changeDaoImpl(connectionFormDto);
		return "index";
	}
}
