package com.explordf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.annotation.SessionScope;

import com.explordf.dto.ConnectionDto;

/**
 * 
 * Spring Controller that returns the appropriate HTML page after a user´s 
 * request.
 * 
 * @author Andreas Niederquell
 * 
 */
@Controller
@SessionScope
public class PageController {
	
	/**
	 * Returns the loading HTML page.
	 * 
	 * @return "index" HTML page.
	 */
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index() {
		return "index";
	}
	
	/**
	 * Returns a HTML page on which queries can be done.
	 * 
	 * @return "query" HTML page.
	 */
	@RequestMapping(value="/query", method=RequestMethod.GET)
	public String query() {
		return "query";
	}
	
	/**
	 * Returns a HTML page on which the user can connect to a RDF triplestore.
	 * 
	 * @return "connect" HTML page.
	 */
	@RequestMapping(value="/connect", method=RequestMethod.GET)
	public String connect() {
		return "connect";
	}
	
	/**
	 * Returns a HTML page on which further information and description of
	 * the application can be found.
	 * 
	 * @return "help" HTML page.
	 */
	@RequestMapping(value="/help", method=RequestMethod.GET)
	public String help() {
		return "help";
	}
	
}
