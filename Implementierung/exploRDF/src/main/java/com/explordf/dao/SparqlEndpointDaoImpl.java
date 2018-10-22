package com.explordf.dao;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;


/**
 * 
 * @author Andreas
 * Used to access any SPARQL-Endpoint.
 */
@Repository
@Qualifier("sparqlEndpointRepo")
public class SparqlEndpointDaoImpl {

	
	//TODO Hier muss SPARQLRepository von rdf4j verwendet werden
	

	public Collection<String> simpleSearch(String term) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public JSONArray getPredicates() throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

}
