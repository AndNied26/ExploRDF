package com.explordf.dao;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;


/**
 * 
 * @author Andreas
 * Used to access any SPARQL-Endpoint.
 */
@Repository
@Qualifier("sparqlEndpointRepo")
public class SparqlEndpointDaoImpl implements ExploRDFDao {

	
	//TODO Hier muss SPARQLRepository von rdf4j verwendet werden
	
	@Override
	public Collection<String> simpleSearch(String term) {
		// TODO Auto-generated method stub
		return null;
	}

}
