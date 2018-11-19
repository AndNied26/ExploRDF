package com.explordf.dao;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

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
@Qualifier("dummyRepo")
public class TestDummyDaoImpl implements ExploRDFDao {

	private static final Logger logger = LoggerFactory.getLogger(TestDummyDaoImpl.class);
	
	private final String tripleStoreServer = "testDummyServer";
	
	String rdf4jServer = "http://localhost:8080/rdf4j-server";
	String repoName = "test";
	
	//rdf4j server
//	RepositoryManager manager;
	Repository repo;
	//stardog
	RemoteRepositoryManager manager;
	
	
	String stardogServer = "http://localhost:5820";
	String username = "admin", password = "admin";
	String db = "geograficumDB";
	
	@PostConstruct
	private void init() {
		// redf4j server
		//manager = RepositoryProvider.getRepositoryManager(rdf4jServer);
		
		// stardog server
		manager = new RemoteRepositoryManager("http://localhost:5820/sachbegriffeDB/query");
		manager.setUsernameAndPassword(username, password);

		logger.info("TestDummy Repository created.");
	}
	
	@Override
	public List<TripleDto> simpleSearch(String term, boolean broaderSearch) {
		double start = new Date().getTime();
		System.out.println(start);
		
		// rdf4j server
//		Repository repo = manager.getRepository(repoName);
		
		//stardog server
		Repository repo = new SPARQLRepository("http://localhost:5820/sachbegriffeDB/query");
		((SPARQLRepository) repo).setUsernameAndPassword(username, password);
		repo.initialize();
		
		logger.info("Method simpleSearch() in TestDummyDaoImpl entered.");
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
			
			
		}finally {
			repo.shutDown();
		}
		double end = new Date().getTime();
		System.out.println((end-start)/1000);
		return resultDto;
	}

	@Override
	public List<PredicateDto> getPredicates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TripleDto> getSubject(String subject) {
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
		boolean connected = false;

		System.out.println("server: " + connDto.getTripleStoreServer() + ", url: " + connDto.getTripleStoreUrl()
				+ ", repo: " + connDto.getTripleStoreRepo() + ", username: " + connDto.getTripleStoreUserName()
				+ ", password: " + connDto.getTripleStorePassword());
//		Repository repo = new SPARQLRepository(connDto.getTripleStoreUrl());
		
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

}
