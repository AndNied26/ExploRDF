package com.explordf.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.explordf.dao.ExploRDFDao;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;
import com.explordf.dto.VisualizationDto;

/**
 * 
 * Service class for triple store queries. This class manages the
 * communication between the Controller and the DAO tier concerning the triple store
 * requests. It is annotated as Spring {@link Service}, therefore it is
 * created once during the application.
 * 
 * @author Andreas Niederquell
 *
 */
@Service
public class QueryService {
	
	@Autowired
	@Qualifier("exploRDFDaoImpl")
	private ExploRDFDao exploRDFDao;
	
	/**
	 * Gets a list of results of user´s request for a certain searching term. 
	 * 
	 * @param term Term requested by the user to query the connected triple store.
	 * @param broaderSearch Determines whether an exact query match is set. If 
	 * broaderSearch is set to "true", all results which contain the searching
	 * term are returned, otherwise only exact matches are returned.
	 * @return List of TripleDtos objects containing the results of user´s request.
	 */
	public List<TripleDto> simpleSearch(String term, boolean broaderSearch) {
		return exploRDFDao.simpleSearch(term, broaderSearch);
	}


	/**
	 * Gets a list of all RDF predicates (subject-predicate-object) that are used 
	 * in the connected triple store.
	 *  
	 * @return List of PredicateDtos objects containing all used predicates in 
	 * the triple store.
	 */
	public List<PredicateDto> getPredicates() {
		return exploRDFDao.getPredicates();
	}


	/**
	 * Gets a list of all RDF triples containing the IRI of the request resource as a 
	 * subject (subject-predicate-object).
	 * 
	 * @param subject IRI of the requested subject.
	 * @return List of TripleDtos objects containing the results of user´s request.
	 */
	public List<TripleDto> getSubject(String subject) {
		System.out.println("Entered sevice");
		return exploRDFDao.getSubject(subject);
	}


	/**
	 * Gets a list of all customized predicates lists concerning the current connection
	 * properties.
	 * 
	 * @return List of predicates lists.
	 */
	public List<String> getAllPredicatesLists() {
		return exploRDFDao.getAllPredicatesLists();
	}


	/**
	 * Saves the predicates that are visualized in the graph. The node label and the
	 * edges of the graph are selected by the user and transferred in a list.
	 * 
	 * @param predicateDtoList List of PredicateDto objects with the selected 
	 * predicates. 
	 * @param listName Chosen name of the list the predicates have to be saved in. 
	 * @return listName if the predicates could be successfully saved, or null if 
	 * saving the predicate failed. 
	 */
	public String savePredicatesList(List<PredicateDto> predicateDtoList, String listName) {
		String result = null;
		try {
			result = exploRDFDao.savePredicatesList(predicateDtoList, listName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}


	/**
	 * Gets the customized predicates list with the specified name.
	 * 
	 * @param listName Name of the predicate list selected by user.
	 * @return List of PredicateDtos objects with the customized predicates.
	 */
	public List<PredicateDto> getPredicatesList(String listName) {
		List<PredicateDto> result = null;
		try {
			result = exploRDFDao.getPredicatesList(listName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}


	/**
	 * Gets a DTO (Data transfer object) of the requested node (RDF subject 
	 * (subject-predicate-object)) containing the node label selected in the predicate
	 * list.
	 * 
	 * @param subject IRI of the requested subject node.
	 * @param listName Name of the predicate list selected by user.
	 * @return VisualizationDto object containing the IRI and label of the node.
	 * @return
	 */
	public VisualizationDto getNode(String subject, String listName) {
		return exploRDFDao.getNode(subject, listName);
	}


	/**
	 * Gets a DTO (Data transfer object) of each resource node (RDF object) that is 
	 * connected with the chosen node (RDF subject) via the selected predicates 
	 * (subject-predicate-object). Moreover, the VisualizationDto object contains each 
	 * link between the the subject and the object node as an own DTO.
	 * 
	 * @param subject IRI of the requested subject node.
	 * @param limit 
	 * @param edgeOffset 
	 * @param edgeViz 
	 * @param Name of the predicate list selected by user.
	 * @return VisualizationDto as a JSON object containing a NodeDto object of each 
	 * node and an EdgeDto object of every link between the subject and the object node.
	 */
	public VisualizationDto getNodeRelations(String subject, String listName, int edgeViz, int edgeOffset, int limit) {
		return exploRDFDao.getNodeRelations(subject, listName, edgeViz, edgeOffset, limit);
	}
	
}
