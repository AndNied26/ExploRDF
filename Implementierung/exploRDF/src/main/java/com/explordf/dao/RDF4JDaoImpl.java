package com.explordf.dao;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;

import com.explordf.dto.ConnectionDto;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;


/**
 * 
 * @author Andreas Niederquell
 * 
 * HTTPRepository is, as the name implies, a Repository implementation that acts as a 
 * proxy to a repository available on a remote RDF4J Server, accessible through HTTP.
 */
//@org.springframework.stereotype.Repository
public class RDF4JDaoImpl {

	private static final Logger logger = LoggerFactory.getLogger(RDF4JDaoImpl.class);
	
	private final String tripleStoreServer = "RDF4J-Server";
	
	//TODO: die unteren beiden weg
	String rdf4jServer = "http://localhost:8080/rdf4j-server";
	String repoName = "test";
	private Repository repo;
	
	@PreDestroy
	private void close() {
		logger.info("Method predestroy entered");
		shutDown();
	}
	
	
	
	
	public List<TripleDto> simpleSearch(String term, boolean broaderSearch) {
		double start = new Date().getTime();
		logger.info("Method simpleSearch() in RDF4JDaoImpl entered.");
		
		List<TripleDto> resultDto = new LinkedList<>();
		
		try(RepositoryConnection con = repo.getConnection()){
			
			String queryString;
			
			if(broaderSearch) {
				queryString = "select ?s ?p ?o where {filter(regex(?o, \""+ term + "\", \"i\")).?s ?p ?o} order by ?s";
			} else {
//				queryString = "SELECT ?s ?p ?o WHERE {filter(?o = \"" + term + "\"). ?s ?p ?o}";
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

	
	public List<PredicateDto> getPredicatesList(String listName) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String savePredicatesList(List<PredicateDto> predicateDtoList, String listName) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<String> getAllPredicatesLists() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String getType() {
		return tripleStoreServer;
	}

	
	public boolean getConnected(ConnectionDto connDto) {
		
		logger.info("Method getConnected() in RDF4JDaoImpl entered.");

		boolean connected = false;

		System.out.println("server: " + connDto.getTripleStoreServer() + ", url: " + connDto.getTripleStoreUrl()
		+ ", repo: " + connDto.getTripleStoreRepo() + ", username: " + connDto.getTripleStoreUserName()
		+ ", password: " + connDto.getTripleStorePassword());
		
		if(connDto.getTripleStoreRepo() == "") {
			return false;
		}
		Repository repo = new HTTPRepository(connDto.getTripleStoreUrl(), connDto.getTripleStoreRepo());
		
		if (connDto.getTripleStoreUserName() != "" && connDto.getTripleStorePassword() != "") {
			((HTTPRepository) repo).setUsernameAndPassword(connDto.getTripleStoreUserName(),
					connDto.getTripleStorePassword());
		}
		
		repo.initialize();
		try (RepositoryConnection conn = repo.getConnection()) {
			String queryString = "SELECT * WHERE {?s ?p ?o} LIMIT 1";
			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			try (TupleQueryResult result = tupleQuery.evaluate()) {
				System.out.println("TupleQueryResult");
				connected = true;
			}
		} catch (RDF4JException e) {
			logger.error("An RDF4JException occured while trying to connect to " 
					+ connDto.getTripleStoreUrl() + ".");
		}

		if (connected) {
			if (this.repo != null && this.repo.isInitialized()) {
				this.repo.shutDown();
			}
			this.repo = repo;
		}
		return connected;
	}



	
	public void shutDown() {
		if (this.repo != null && this.repo.isInitialized()) {
			this.repo.shutDown();
		}
	}
}
