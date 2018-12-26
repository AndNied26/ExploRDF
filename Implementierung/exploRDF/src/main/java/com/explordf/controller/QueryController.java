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
import com.explordf.dto.VisualizationNodesDto;
import com.explordf.service.QueryService;


@RestController
public class QueryController {
	
	@Autowired
	QueryService queryService;

//	@RequestMapping(value="/simpleSearch/{term}/{broaderSearch}", method = RequestMethod.GET)
//	public List<TripleDto> simpleSearch(@PathVariable(name="term") String term, @PathVariable(name="broaderSearch") char broaderSearch) {
//		System.out.println("first Method simpleSearch() entered");
//		return exploRDFService.simpleSearch(term, broaderSearch == '1');
//	}
	
	@RequestMapping(value="/savePredicatesList/{listName}", method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String savePredicatesList(@RequestBody List<PredicateDto> predicateDtoList, @PathVariable(name="listName") String listName) {
		return queryService.savePredicatesList(predicateDtoList, listName);
	}
	
	@RequestMapping(value="/getPredicates/{listName}", method=RequestMethod.GET)
	public List<PredicateDto> getPredicatesList(@PathVariable(name="listName") String listName) {
		return queryService.getPredicatesList(listName);
	}
	
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
	
//	@RequestMapping(value="/getSubject", method = RequestMethod.POST)
//	public List<TripleDto> getSubject(@RequestBody String subject) {
//		System.out.println("Entered Controller");
//		return exploRDFService.getSubject(subject);
//	}
	
	@RequestMapping(value="/getSubject/**", method = RequestMethod.GET)
	public List<TripleDto> getSubject(HttpServletRequest request) {
		System.out.println("Entered Controller");
		String subject = request.getRequestURI().split("/getSubject/")[1];
		return queryService.getSubject(subject);
	}
	
//	@RequestMapping(value="/getNodeData/**", method = RequestMethod.GET)
//	public VisualizationNodesDto getNodeData(HttpServletRequest request) {
//		System.out.println("Entered Controller");
//		String subject = request.getRequestURI().split("/getNodeData/")[1];
//		return queryService.getNode(subject);
//	}
	
	@RequestMapping(value="/getNode/**/{predicatesList}", method = RequestMethod.GET)
	public VisualizationNodesDto getNode(HttpServletRequest request, @PathVariable(name = "predicatesList") String predicatesList){
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
//		return null;
	}
	
	@RequestMapping(value="/getNodeData/**/{predicatesList}", method = RequestMethod.GET)
	public VisualizationNodesDto getNodeData(HttpServletRequest request, @PathVariable(name = "predicatesList") String predicatesList){
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
//		return null;
	}
	
	
}
