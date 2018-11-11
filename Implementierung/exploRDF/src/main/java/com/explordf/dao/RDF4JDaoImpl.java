package com.explordf.dao;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;

import com.explordf.dto.ConnectionDto;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;


/**
 * 
 * @author Andreas
 * HTTPRepository is, as the name implies, a Repository implementation that acts as a 
 * proxy to a repository available on a remote RDF4J Server, accessible through HTTP.
 */
@org.springframework.stereotype.Repository
@Lazy
public class RDF4JDaoImpl implements ExploRDFDao {

	private static final Logger logger = LoggerFactory.getLogger(RDF4JDaoImpl.class);
	
	private final String tripleStoreServer = "rdf4jServer";
	
	//TODO Hier muss HTTPRepository von rdf4j verwendet werden
	
	String rdf4jServer = "http://localhost:8080/rdf4j-server";
	String repoName = "test";
	Repository repo;
	
	@PostConstruct
	private void init() {
		repo = new HTTPRepository(rdf4jServer, repoName);
		repo.initialize();
		logger.info("Repository created.");
	}
	
	@PreDestroy
	private void destroy() {
		repo.shutDown();
	}
	
	
	
	@Override
	public List<TripleDto> simpleSearch(String term, boolean broaderSearch) {
		double start = new Date().getTime();
		logger.info("Method simpleSearch() in RDF4JDaoImpl entered.");
		List<TripleDto> resultDto = new LinkedList<>();
		
//		repo.initialize();
		
		try(RepositoryConnection con = repo.getConnection()){
			
			String queryString;
			
			if(broaderSearch) {
				queryString = "select ?s ?p ?o where {filter(regex(?o, \""+ term + "\", \"i\")).?s ?p ?o} order by ?s";
			} else {
				queryString = "SELECT ?s ?p ?o WHERE {?s ?p \"" + term + "\". ?s ?p ?o}";
			}		
			
			List<BindingSet> resultList;
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			
			try(TupleQueryResult result = tupleQuery.evaluate()){
				resultList = QueryResults.asList(result);
			}
			
			for (BindingSet bindingSet : resultList) {
				
				Value subject = bindingSet.getValue("s");
				Value predicate = bindingSet.getValue("p");
				Value object = bindingSet.getValue("o");
				
				TripleDto dto = new TripleDto(subject.toString(), predicate.toString(), object.toString());
				resultDto.add(dto);
			}
			
//		}finally {
//			repo.shutDown();
		}
		double end = new Date().getTime();
		System.out.println((end-start)/1000);
		return resultDto;
	}
	
	
	public List<PredicateDto> getPredicates() {
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
			
			for (BindingSet bindingSet : resultList) {
				
				Value value = bindingSet.getValue("p");
				System.out.println(value.toString());
				PredicateDto dto = new PredicateDto(value.toString(), false, false);
				resultDto.add(dto);
				
			}
			
		}finally {
			repo.shutDown();
		}
		return resultDto;	
	}



	@Override
	public List<TripleDto> getSubject(String subject) {
		logger.info("Method getSubject() entered.");
		List<TripleDto> resultDto = new LinkedList<>();
		
		repo.initialize();
		
		try(RepositoryConnection con = repo.getConnection()){
			String subjt = "<"+subject+">";
			System.out.println(subjt);
//			String queryString = "SELECT ?s ?p ?o WHERE {<" + subject + "> ?p ?o. ?s ?p ?o}";
			
//			String queryString = "SELECT ( "+ subjt +" as ?s) ?p ?o ?g { "
//					+ "{ "+subjt+" ?p ?o } union { graph ?g { " + subjt + " ?p ?o } } }";
			
			String queryString = "SELECT ( "+ subjt +" as ?s) ?p ?o WHERE { "
					+subjt+" ?p ?o }";
			
			
			List<BindingSet> resultList;
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			
			try(TupleQueryResult result = tupleQuery.evaluate()){
				resultList = QueryResults.asList(result);
			}
			
			for (BindingSet bindingSet : resultList) {
				
				Value subj = bindingSet.getValue("s");
				Value pred = bindingSet.getValue("p");
				Value obj = bindingSet.getValue("o");
				
				TripleDto dto = new TripleDto(subj.toString(), pred.toString(), obj.toString());
				resultDto.add(dto);
			}
			
		}finally {
			repo.shutDown();
		}
		
		return resultDto;
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
		return true;
	}
}
