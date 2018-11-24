package com.explordf.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.explordf.dto.ConnectionDto;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;

/**
 * Interface for a DAO (data access object).
 *
 * The whole communication with the triple store is transacted by the 
 * implementation of this class.
 */
@Repository
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
	 */
	List<PredicateDto> getPredicatesList(String listName);
	
	/**
	 * Save a customized list of predicates that defines which predicates to visualize.
	 * @param predicateDtoList List with the chosen predicates.
	 * @param listName Name of the list.
	 * @return Name of this list only if the predicate list was successfully saved.
	 */
	String savePredicatesList(List<PredicateDto> predicateDtoList, String listName);
	
	/**
	 * Get a list with the names of all existing customized predicates lists.
	 * @return List with the names of the predicates lists.
	 */
	List<String> getAllPredicatesLists();

	String getType();
	
	boolean getConnected(ConnectionDto connDto);
	
	void shutDown();
	
}
