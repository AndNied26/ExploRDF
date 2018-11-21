package com.explordf.dao;

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
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryProvider;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import com.explordf.dto.ConnectionDto;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;

/**
 * 
 * @author Andreas Niederquell
 *
 */
@org.springframework.stereotype.Repository
public class TestDummyDaoImpl implements ExploRDFDao {

	private static final Logger logger = LoggerFactory.getLogger(TestDummyDaoImpl.class);
	
	private final String tripleStoreServer = "testDummyServer";
	
	
	Repository repo;
	
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
		
		logger.info("Method simpleSearch() in TestDummyDaoImpl entered.");
		List<TripleDto> resultDto = new LinkedList<>();
		
		try(RepositoryConnection con = repo.getConnection()){
			
			String queryString;
			
			if(broaderSearch) {
				queryString = "select ?s ?p ?o where {filter(regex(?o, \""+ term 
						+ "\", \"i\")).?s ?p ?o} order by ?s";
			} else {
				queryString = "select ?s ?p ?o where {filter(?o = \"" 
						+ term +"\"). {SELECT ?s ?p ?o WHERE {?s ?p \"" 
						+ term + "\". ?s ?p ?o}}}";
			}		
			
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			
			try(TupleQueryResult result = tupleQuery.evaluate()){
				while (result.hasNext()) {
					BindingSet bindingSet = result.next();
					resultDto.add(new TripleDto(
							bindingSet.getValue("s").toString(), 
							bindingSet.getValue("p").toString(), 
							bindingSet.getValue("o").toString()));
				}
			}
			
			
		}
		
		double end = new Date().getTime();
		System.out.println((end-start)/1000);
		return resultDto;
	}
	
	@Override
	public List<TripleDto> getSubject(String subject) {
		double start = new Date().getTime();
		logger.info("Method getSubject() entered.");
		List<TripleDto> resultDto = new LinkedList<>();
		
		try(RepositoryConnection con = repo.getConnection()) {
			String queryString = "SELECT (<" + subject + "> as ?s) ?p ?o WHERE {<" + subject + "> ?p ?o. "
					+ "FILTER(!isLiteral(?o) || langMatches(lang(?o), \"EN\") || langMatches(lang(?o), \"\"))}";
			
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);

			try (TupleQueryResult result = tupleQuery.evaluate()) {

				while (result.hasNext()) {
					BindingSet bindingSet = result.next();
					resultDto.add(new TripleDto(bindingSet.getValue("s").toString(),
							bindingSet.getValue("p").toString(), bindingSet.getValue("o").toString()));
				}
			}
		
		}
		
		double end = new Date().getTime();
		System.out.println("Query time: " + (end-start)/1000);
		return resultDto;
	}

	@Override
	public List<PredicateDto> getPredicates() {
		double start = new Date().getTime();
		logger.info("Method getPredicates() entered.");
		List<PredicateDto> resultDto = new LinkedList<>();

		boolean gotAllPredicates = false;

		// DBPedia 438336000 triple geht noch
//		int offset = 438336000;
		int offset = 990336000;
		int limit = 500;
//		while (!gotAllPredicates) {
			int resultNum = 0;
			try (RepositoryConnection con = repo.getConnection()) {

				String queryString = "select ?p where {?s ?p ?o} limit "+limit+" offset " + offset;
				System.out.println(queryString);
				
				
				TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
				
				try (TupleQueryResult result = tupleQuery.evaluate()) {
					while (result.hasNext()) {
						BindingSet bindingSet = result.next();
						resultNum += 1;
						resultDto.add(new PredicateDto(bindingSet.getValue("p").toString(), false, false));
						System.out.println(resultNum + " " + bindingSet.getValue("p").toString());
					}
				}
				
			}
			System.out.println("resultNum : " + resultNum);
			if (resultNum < limit) {
				gotAllPredicates = true;
			}
			offset += resultNum;
			double meanTime = new Date().getTime();
			System.out.println("Offset: " + offset + " Mean time : " + (meanTime-start)/1000);
//		}
		double end = new Date().getTime();
		System.out.println("Query time: " + (end-start)/1000);
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
		
		logger.info("Method getConnected() in TestDummyDaoImpl entered.");
		
		boolean connected = false;

		System.out.println("server: " + connDto.getTripleStoreServer() + ", url: " + connDto.getTripleStoreUrl()
				+ ", repo: " + connDto.getTripleStoreRepo() + ", username: " + connDto.getTripleStoreUserName()
				+ ", password: " + connDto.getTripleStorePassword());
		
		Repository repo = new MyRepository(connDto.getTripleStoreUrl());

		if (connDto.getTripleStoreUserName() != "" && connDto.getTripleStorePassword() != "") {
			((MyRepository) repo).setUsernameAndPassword(connDto.getTripleStoreUserName(),
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
			logger.error("An RDF4JException occured while trying to connect to " + connDto.getTripleStoreUrl() + ".");
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
		logger.info("Shut down the repo");
		if (this.repo != null && this.repo.isInitialized()) {
			this.repo.shutDown();
		}
	}

}
