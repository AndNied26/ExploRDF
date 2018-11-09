package com.explordf.dao;

import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.explordf.dto.ConnectionDto;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;


/**
 * 
 * @author Andreas
 * Used to access any SPARQL-Endpoint.
 */
@Repository
@Lazy
@Qualifier("sparqlEndpointRepo")
public class SparqlEndpointDaoImpl implements ExploRDFDao {

	private final String tripleStoreServer = "sparqlEndpoint";
	
	@Override
	public List<TripleDto> simpleSearch(String term, boolean broaderSearch) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TripleDto> getSubject(String subject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PredicateDto> getPredicates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PredicateDto> getPredicatesList(String listName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String savePredicatesList(List<PredicateDto> predicateDtoList, String listName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllPredicatesLists() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		return tripleStoreServer;
	}

	@Override
	public boolean getConnected(ConnectionDto connDto) {
		// TODO Auto-generated method stub
		return false;
	}

}
