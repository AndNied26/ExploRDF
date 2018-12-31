package com.explordf.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.explordf.dto.ConnectionDto;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;
import com.explordf.dto.VisualizationNodesDto;

/**
 * Interface for a DAO (data access object).
 *
 * The whole communication with the triple store is transacted by the 
 * implementation of this class.
 */
public interface ExploRDFDao {

	/**
	 * Searching for a certain term in the Triple Store.
	 * @param term to search for.
	 * @param broaderSearch defines whether an exact word matching is mandatory or
	 * 		  a broader search (e.g. objects contain the term) is preferred. 
	 * @return A list of all found triples in the Triple Store.
	 */
	List<TripleDto> simpleSearch(String term, boolean broaderSearch);

	/**
	 * Get all triples that contain a certain subject.
	 * @param subject URI as String to search for.
	 * @return A list of all found triples in the Triple Store.
	 */
	List<TripleDto> getSubject(String subject);
	
	/**
	 * Get all existing predicates in the triple store.
	 * @return List of all predicates in the triple store.
	 */
	List<PredicateDto> getPredicates();
	
	/**
	 * Get a customized list of all predicates in the triple store which defines
	 * which predicates to visualize.
	 * @param listName Name of the saved predicates list.
	 * @return List of all predicates in the requested predicate list.
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	List<PredicateDto> getPredicatesList(String listName) throws FileNotFoundException, IOException;
	
	/**
	 * Save a customized list of predicates that defines which predicates to visualize.
	 * @param predicateDtoList List with the chosen predicates.
	 * @param listName Name of the list.
	 * @return Name of this list only if the predicate list was successfully saved.
	 * @throws IOException 
	 */
	String savePredicatesList(List<PredicateDto> predicateDtoList, String listName) throws IOException;
	
	/**
	 * Get a list with the names of all existing customized predicates lists.
	 * @return List with the names of the predicates lists.
	 */
	List<String> getAllPredicatesLists();

	ConnectionDto getConnectionProps();
	
	ConnectionDto setConnectionProps(ConnectionDto connDto);
	
	List<String> getSupportedServers();

	VisualizationNodesDto getNode(String subject, String predicatesList);

	VisualizationNodesDto getNodeData(String subject, String predicatesList);
	
}
