package com.explordf.dao;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;


import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.rdf4j.StardogRepository;
import com.explordf.dto.ConnectionDto;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;

@org.springframework.stereotype.Repository
public class StardogDaoImpl implements ExploRDFDao {

	private static final Logger logger = LoggerFactory.getLogger(StardogDaoImpl.class);
	
	private final String tripleStoreServer = "Stardog-Server";
	
	private Repository repo;
	
	
	//muss dann weg
	String stardogServer = "http://localhost:5820";
	String username = "admin", password = "admin";
	String db = "geograficumDB";
	
	@PreDestroy
	private void close() {
		logger.info("Method predestroy entered");
		if (this.repo != null && this.repo.isInitialized()) {
			this.repo.shutDown();
		}
	}
	
	@Override
	public List<TripleDto> simpleSearch(String term, boolean broaderSearch) {
		double start = new Date().getTime();
		logger.info("Method simpleSearch() in StardogDaoImpl entered.");
		
		List<TripleDto> resultDto = new LinkedList<>();
		
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
		}
		double end = new Date().getTime();
		System.out.println((end-start)/1000);
		return resultDto;
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

		logger.info("Method getConnected() in StardogDaoImpl entered.");
		
		boolean connected = false;
		
		System.out.println("server: " + connDto.getTripleStoreServer() + ", url: " + connDto.getTripleStoreUrl()
		+ ", repo: " + connDto.getTripleStoreRepo() + ", username: " + connDto.getTripleStoreUserName()
		+ ", password: " + connDto.getTripleStorePassword());
		
		if(connDto.getTripleStoreRepo() == "") {
			return false;
		}
		
		Repository repo = new StardogRepository(ConnectionConfiguration
				.to(connDto.getTripleStoreRepo()).server(connDto.getTripleStoreUrl())
				.credentials(connDto.getTripleStoreUserName(), connDto.getTripleStorePassword()));
		
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
					+ connDto.getTripleStoreUrl() + " " 
					+ connDto.getTripleStoreRepo() + ".");
		}

		if (connected) {
			if (this.repo != null && this.repo.isInitialized()) {
				this.repo.shutDown();
			}
			this.repo = repo;
		}
		return connected;
	}

	@Override
	public void shutDown() {
		if (this.repo != null && this.repo.isInitialized()) {
			this.repo.shutDown();
		}
	}

}
