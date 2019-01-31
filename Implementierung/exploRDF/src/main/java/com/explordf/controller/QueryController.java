package com.explordf.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
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
 * visualization. It is annotated as Spring {@link RestController}, therefore it is
 * created once during the application.
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
	 * @return List of PredicateDtos as JSON objects with the customized predicates.
	 */
	@RequestMapping(value="/getPredicates/{listName}", method=RequestMethod.GET)
	public List<PredicateDto> getPredicatesList(@PathVariable(name="listName") String listName) {
		return queryService.getPredicatesList(listName);
	}
	
	/**
	 * Gets a list of all customized predicates lists concerning the current connection
	 * properties.
	 * 
	 * @return List of predicates lists as JSON objects.
	 */
	@RequestMapping(value="/getAllPredicatesLists", method = RequestMethod.GET)
	public List<String> getAllPredicatesLists() {
		return queryService.getAllPredicatesLists();
	}
	
	/**
	 * Gets a list of results of user´s request for a certain searching term. The query 
	 * can be performed in two different ways: 1. Query for the exact match of the
	 * searching term and 2. Result that contains the searching term.
	 * 
	 * @param request Full HTTP request sent from the user interface of the application.
	 * @param broaderSearch Determines whether an exact query match is set. If 
	 * broaderSearch is set to "1", all results which contain the searching
	 * term are returned, otherwise only exact matches are returned. 
	 * @return List of TripleDtos as JSON objects containing the results of user´s 
	 * request.
	 */
	@RequestMapping(value="/simpleSearch/**/{broaderSearch}", method = RequestMethod.GET)
	public List<TripleDto> simpleSearch(HttpServletRequest request, @PathVariable(name = "broaderSearch") char broaderSearch){
		
		String url = "";
		try {
			url = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new LinkedList<>();
		}

		String term = url.split("/simpleSearch/")[1];
		term = term.substring(0, term.length()-2);
		return queryService.simpleSearch(term, broaderSearch == '1');
	}
	
	/**
	 * Gets a list of all RDF predicates (subject-predicate-object) that are used 
	 * in the connected triple store.
	 *  
	 * @return List of PredicateDtos as JSON objects containing all used predicates in 
	 * the triple store.
	 */
	@RequestMapping(value="/getPredicates", method=RequestMethod.GET)
	public List<PredicateDto> getPredicates() {
		return queryService.getPredicates();
	}
	
	/**
	 * Gets a list of all RDF triples containing the IRI of the request resource as a 
	 * subject (subject-predicate-object).
	 * 
	 * @param request Full HTTP request sent from the user interface of the application.
	 * @return List of TripleDtos as JSON objects containing the results of user´s 
	 * request.
	 */
	@RequestMapping(value="/getSubject/**", method = RequestMethod.GET)
	public List<TripleDto> getSubject(HttpServletRequest request) {
		System.out.println("Entered Controller");
		String subject = request.getRequestURI().split("/getSubject/")[1];
		return queryService.getSubject(subject);
}
	
	/**
	 * Gets a DTO (Data transfer object) of the requested node (RDF subject 
	 * (subject-predicate-object)) containing the node label selected in the predicate
	 * list. 
	 * TODO
	 * @param request Full HTTP request sent from the user interface of the application.
	 * @param listName Name of the predicate list selected by user.
	 * @return VisualizationDto as a JSON object containing the IRI and label of the 
	 * node.
	 */
	@RequestMapping(value="/getNode/**/{listName}", method = RequestMethod.GET)
	public VisualizationDto getNode(HttpServletRequest request, @PathVariable(name = "listName") String listName){
		System.out.println("Method getNode() entered");
		
		String url = "";
		try {
			url = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		System.out.println(url);
		System.out.println(listName);

		String term = url.split("/getNode/")[1];
		term = term.substring(0, term.length() - listName.length() - 1);
		System.out.println("term: " + term + ", predicatesList: " + listName);
		return queryService.getNode(term, listName);
	}
	
	/**
	 * Gets a DTO (Data transfer object) of each resource node (RDF object) that is 
	 * connected with the chosen node (RDF subject) via the selected predicates 
	 * (subject-predicate-object). Moreover, the VisualizationDto object contains each 
	 * link between the the subject and the object node as an own DTO.
	 * 
	 * @param request Full HTTP request sent from the user interface of the application.
	 * @param listName Name of the predicate list selected by user.
	 * 
	 * @return VisualizationDto as a JSON object containing a NodeDto object of each 
	 * node and an EdgeDto object of every link between the subject and the object node.
	 */
	@RequestMapping(value="/getNodeData/**/{listName}/{edgeViz}/{edgeOffset}/{limit}", method = RequestMethod.GET)
	public VisualizationDto getNodeData(HttpServletRequest request, 
			@PathVariable(name = "listName") String listName,
			@PathVariable(name = "edgeViz") int edgeViz,
			@PathVariable(name = "edgeOffset") int edgeOffset,
			@PathVariable(name = "limit") int limit){
		System.out.println("Method getNodeData() entered");
		
		String url = "";
		try {
			url = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		System.out.println(url);
		System.out.println("listname " + listName);
		System.out.println("edgeViz " + edgeViz);
		System.out.println("edgeOffset " + edgeOffset);

		String term = url.split("/getNodeData/")[1];
		term = term.substring(0, term.length() - listName.length() - String.valueOf(edgeViz).length() - String.valueOf(edgeOffset).length() - String.valueOf(limit).length() - 4);
		System.out.println("term: " + term + ", predicatesList: " + listName);
		return queryService.getNodeData(term, listName, edgeViz, edgeOffset, limit);
	}
	
	
}
