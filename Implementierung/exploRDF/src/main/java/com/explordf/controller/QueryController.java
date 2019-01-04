package com.explordf.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;
import com.explordf.dto.VisualizationDto;
import com.explordf.service.QueryService;

/**
 * Spring RestController for managing user´s REST calls concerning the
 * visualization.
 * 
 * @author Andreas Niederquell
 *
 */
@RestController
public class QueryController {
	
	
	@Autowired
	private QueryService queryService;
	
	/**
	 * Saves the predicates that are visualized in the graph. The node label and the
	 * edges of the graph are selected by the user and transferred in a list.
	 * 
	 * @param predicateDtoList List of PredicateDto objects with the selected 
	 * predicates. 
	 * @param listName Chosen name of the list the predicates have to be saved in. 
	 * @return listName if the predicates could be successfully saved, or null if 
	 * saving the predicates failed. 
	 */
	@RequestMapping(value="/savePredicatesList/{listName}", method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String savePredicatesList(@RequestBody List<PredicateDto> predicateDtoList, @PathVariable(name="listName") String listName) {
		return queryService.savePredicatesList(predicateDtoList, listName);
	}
	
	/**
	 * Gets the customized predicates list with the specified name.
	 * 
	 * @param listName Name of the predicate list selected by user.
	 * @return List of PredicateDto objects with the customized predicates.
	 */
	@RequestMapping(value="/getPredicates/{listName}", method=RequestMethod.GET)
	public List<PredicateDto> getPredicatesList(@PathVariable(name="listName") String listName) {
		return queryService.getPredicatesList(listName);
	}
	
	/**
	 * Gets a list of all customized predicates lists concerning the current connection
	 * properties.
	 * 
	 * @return List of predicates lists.
	 */
	@RequestMapping(value="/getAllPredicatesLists", method = RequestMethod.GET)
	public List<String> getAllPredicatesLists() {
		return queryService.getAllPredicatesLists();
	}
	
	
	@RequestMapping(value="/simpleSearch/**/{broaderSearch}", method = RequestMethod.GET)
	public List<TripleDto> simpleSearch(HttpServletRequest request, @PathVariable(name = "broaderSearch") char broaderSearch){
		System.out.println("second Method simpleSearch() entered");
		
		String url = "";
		try {
			url = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new LinkedList<>();
		}
		System.out.println(url);

		String term = url.split("/simpleSearch/")[1];
		term = term.substring(0, term.length()-2);
		System.out.println("term: " + term + ", broaderSearch: " + (broaderSearch == '1'));
		return queryService.simpleSearch(term, broaderSearch == '1');
	}
	
	@RequestMapping(value="/getPredicates", method=RequestMethod.GET)
	public List<PredicateDto> getPredicates() {
		return queryService.getPredicates();
	}
	
	@RequestMapping(value="/getSubject/**", method = RequestMethod.GET)
	public List<TripleDto> getSubject(HttpServletRequest request) {
		System.out.println("Entered Controller");
		String subject = request.getRequestURI().split("/getSubject/")[1];
		return queryService.getSubject(subject);
	}
	
	@RequestMapping(value="/getNode/**/{predicatesList}", method = RequestMethod.GET)
	public VisualizationDto getNode(HttpServletRequest request, @PathVariable(name = "predicatesList") String predicatesList){
		System.out.println("Method getNode() entered");
		
		String url = "";
		try {
			url = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		System.out.println(url);
		System.out.println(predicatesList);

		String term = url.split("/getNode/")[1];
		term = term.substring(0, term.length() - predicatesList.length() - 1);
		System.out.println("term: " + term + ", predicatesList: " + predicatesList);
		return queryService.getNode(term, predicatesList);
	}
	
	@RequestMapping(value="/getNodeData/**/{predicatesList}", method = RequestMethod.GET)
	public VisualizationDto getNodeData(HttpServletRequest request, @PathVariable(name = "predicatesList") String predicatesList){
		System.out.println("Method getNodeData() entered");
		
		String url = "";
		try {
			url = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		System.out.println(url);
		System.out.println(predicatesList);

		String term = url.split("/getNodeData/")[1];
		term = term.substring(0, term.length() - predicatesList.length() - 1);
		System.out.println("term: " + term + ", predicatesList: " + predicatesList);
		return queryService.getNodeData(term, predicatesList);
	}
	
	
}
