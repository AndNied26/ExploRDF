package com.explordf.dao;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import dto.PredicateDto;


/**
 * 
 * @author Andreas
 * HTTPRepository is, as the name implies, a Repository implementation that acts as a 
 * proxy to a repository available on a remote RDF4J Server, accessible through HTTP.
 */
@org.springframework.stereotype.Repository
@Qualifier("rdf4jRepo")
public class RDF4JDaoImpl implements ExploRDFDao {

	private static final Logger logger = LoggerFactory.getLogger(RDF4JDaoImpl.class);
	
	
	//TODO Hier muss HTTPRepository von rdf4j verwendet werden
	
	String rdf4jServer = "http://localhost:8080/rdf4j-server";
	String repoName = "test";
	Repository repo;
	
	@PostConstruct
	private void init() {
		repo = new HTTPRepository(rdf4jServer, repoName);
		logger.info("Repository created.");
	}
	
	
	
	@Override
	public Collection<String> simpleSearch(String term) {
		
		repo.initialize();
		
		List<String> res = new LinkedList<>();
		
		try(RepositoryConnection con = repo.getConnection()){
			
			String queryString = "SELECT ?s ?p ?o WHERE {?s ?p ?o. ?s ?p \"" + term + "\"}";
			
			List<BindingSet> resultList;
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);		
			try(TupleQueryResult result = tupleQuery.evaluate()){
				resultList = QueryResults.asList(result);
			}
			
			// same as above
//			List<BindingSet> results = Repositories.tupleQuery(rep,
//				     "SELECT * WHERE {?s ?p ?o }", r -> QueryResults.asList(r));
			
			for (BindingSet bindingSet : resultList) {
				String s = "";
				Value valueOfS = bindingSet.getValue("s");
				Value valueOfP = bindingSet.getValue("p");
				Value valueOfO = bindingSet.getValue("o");
				s += valueOfS + " " + valueOfP + " " + valueOfO;
				res.add(s);
			}
			
		}finally {
			repo.shutDown();
		}

		return res;
	}
	
	
	public List<PredicateDto> getPredicates() throws JSONException {
		
		logger.info("Method getPredicates() entered.");
		List<PredicateDto> resultDto = new LinkedList<>();
		
		
		
		repo.initialize();
		
		
		try(RepositoryConnection con = repo.getConnection()){
			
			String queryString = "SELECT DISTINCT ?p WHERE {?s ?p ?o.}";
			
			List<BindingSet> resultList;
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);		
			try(TupleQueryResult result = tupleQuery.evaluate()){
				resultList = QueryResults.asList(result);
			}
			
			String predicate = "predicate";
			String label = "label";
			String edge = "edge";
			
			for (BindingSet bindingSet : resultList) {
				
				Value value = bindingSet.getValue("p");
				
				PredicateDto dto = new PredicateDto(value.toString(), false, false);
				resultDto.add(dto);
				System.out.println(dto);
			}
			
		}finally {
			repo.shutDown();
		}
		System.out.println(resultDto);
		return resultDto;
		
	}

}
