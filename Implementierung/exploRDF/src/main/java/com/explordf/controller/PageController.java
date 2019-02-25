package com.explordf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.annotation.SessionScope;

/**
 * 
 * Spring Controller that returns the appropriate HTML page after a user´s 
 * request. It is annotated as Spring {@link Controller} to catch the request
 * from the front end of the application. It is annotated as Spring 
 * {@link SessionScope}, meaning that this class is created in
 * every session. Therefore it overwrites the singleton property of the Spring 
 * {@link Controller}.
 * 
 * @author Andreas Niederquell
 * 
 */
@SessionScope
@Controller
public class PageController {
	
	/**
	 * Returns the loading HTML page.
	 * 
	 * @return "index" HTML page.
	 */
	@RequestMapping(value= {"/", "/index", "/start"}, method=RequestMethod.GET)
	public String index() {
		return "index";
	}
	
	/**
	 * Returns a HTML page on which queries can be done.
	 * 
	 * @return "explore" HTML page.
	 */
	@RequestMapping(value="/explore", method=RequestMethod.GET)
	public String explore() {
		return "explore";
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
