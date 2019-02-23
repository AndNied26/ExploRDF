package com.explordf.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.explordf.dto.ConnectionDto;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;
import com.explordf.dto.VisualizationDto;

/**
 * Interface for a DAO (data access object).
 *
 * The whole communication with the triple store is transacted by an 
 * implementation of this class.
 * 
 * 
 * @author Andreas Niederquell
 *
 */
public interface ExploRDFDao {

	/**
	 * Searches for a certain term in the Triple Store.
	 * @param term to search for.
	 * @param broaderSearch defines whether an exact word matching is mandatory or
	 * 		  a broader search (e.g. objects contain the term) is preferred. 
	 * @return A list of all found triples in the Triple Store.
	 */
	List<TripleDto> searchTerm(String term, boolean broaderSearch);

	/**
	 * Gets all triples that contain a certain subject.
	 * @param subject URI as String to search for.
	 * @return A list of all found triples in the Triple Store.
	 */
	List<TripleDto> getSubject(String subject);
	
	/**
	 * Gets all existing predicates in the triple store.
	 * @return List of all predicates in the triple store.
	 */
	List<PredicateDto> getPredicates();
	
	/**
	 * Gets a customized list of all predicates in the triple store which defines
	 * which predicates to visualize.
	 * @param listName Name of the saved predicates list.
	 * @return List of all predicates in the requested predicate list.
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	List<PredicateDto> getPredicatesList(String listName) throws FileNotFoundException, IOException;
	
	/**
	 * Saves a customized list of predicates that defines which predicates to visualize.
	 * @param predicateDtoList List with the chosen predicates.
	 * @param listName Name of the list.
	 * @return Name of this list only if the predicate list was successfully saved.
	 * @throws IOException 
	 */
	String savePredicatesList(List<PredicateDto> predicateDtoList, String listName) throws IOException;
	
	/**
	 * Gets a list with the names of all existing customized predicates lists 
	 * related to the current connection properties.
	 * @return List with the names of the predicates lists.
	 */
	List<String> getAllPredicatesLists();

	/**
	 * Gets the properties of the currently connected triple store.
	 * 
	 * @return ConnectionDto object with the connection properties if connected
	 * to a triple store, or null if not connected to any triple store.
	 */
	ConnectionDto getConnectionProps();
	
	/**
	 * Tries to connect to the triple store entered by the user. 
	 * 
	 * @param connDto ConnectionDto object with the given connection
	 * properties. 
	 * @return ConnectionDto object with the connection properties if connected
	 * to a triple store, or null if not connected to any triple store.
	 */
	ConnectionDto setConnectionProps(ConnectionDto connDto);
	
	/**
	 * Gets a list with all connection types and supported triple stores servers. 
	 * 
	 * @return List with all supported triple store servers.
	 */
	List<String> getSupportedServers();

	/**
	 * Gets the label of the selected node according to the chosen
	 * predicate list.
	 * 
	 * @param subject Id of the selected node.
	 * @param predicatesList Chosen predicate list. 
	 * @return VisualizationDto object with the label of the selected node.
	 */
	VisualizationDto getNode(String subject, String predicatesList);

	
	/**
	 * Gets all connections of the selected node according to the chosen
	 * predicate list.
	 * 
	 * @param subject Id of the selected node.
	 * @param predicatesList Chosen predicate list. 
	 * @return VisualizationDto object with the nodes and edges related to
	 * the selected node.
	 */
	VisualizationDto getNodeRelations(String subject, String predicatesList, int edgeViz, int edgeOffset, int limit);
	
}
