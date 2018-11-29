package com.explordf.dao.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryProvider;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import com.explordf.dao.ExploRDFDao;
import com.explordf.dto.ConnectionDto;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;

/**
 * 
 * @author Andreas Niederquell
 *
 */
//@org.springframework.stereotype.Repository
public class TestDummyDaoImpl {

	private static final Logger logger = LoggerFactory.getLogger(TestDummyDaoImpl.class);
	
	private final String tripleStoreServer = "testDummyServer";
	
	private String tripleStoreGraph;
	
	Repository repo;
	
	@PreDestroy
	private void close() {
		logger.info("Method predestroy entered");
		if (this.repo != null && this.repo.isInitialized()) {
			this.repo.shutDown();
		}
	}
	
	
	public List<TripleDto> simpleSearch(String term, boolean broaderSearch) {
		
		logger.info("Method simpleSearch() in TestDummyDaoImpl entered.");
		List<TripleDto> resultDto = simpleSearchWithTupleQuery(term, broaderSearch);
//		List<TripleDto> resultDto = simpleSearchWithStatement(term, broaderSearch);
		
		return resultDto;
	}
	
	private List<TripleDto> simpleSearchWithTupleQuery(String term, boolean broaderSearch) {
		double start = new Date().getTime();
		
		logger.info("Method simpleSearchWithTupleQuery() in TestDummyDaoImpl entered.");
		List<TripleDto> resultDto = new LinkedList<>();
		
		try(RepositoryConnection con = repo.getConnection()){
			
			String queryString;
//			String graph = "from <http://dbpedia.org>";
			
			if(broaderSearch) {
				queryString = "select ?s ?p ?o "+this.tripleStoreGraph+" where {filter(regex(?o, \""+ term 
						+ "\", \"i\")).?s ?p ?o} order by ?s";
			} else {
//				queryString = "select ?s ?p ?o "+this.tripleStoreGraph+" where {filter(?o = \"" 
//						+ term +"\"). {SELECT ?s ?p ?o WHERE {?s ?p \"" 
//						+ term + "\". ?s ?p ?o}}}";
				queryString = "SELECT ?s ?p ?o "+this.tripleStoreGraph+" WHERE {filter(?o = \"" + term + "\"). ?s ?p ?o}";
			}		
			
			System.out.println();
			System.out.println(queryString);
			System.out.println();
			
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
	
	private List<TripleDto> simpleSearchWithStatement(String term, boolean broaderSearch) {
		double start = new Date().getTime();
		
		logger.info("Method simpleSearchWithStatement() in TestDummyDaoImpl entered.");
		List<TripleDto> resultDto = new LinkedList<>();
		
		ValueFactory factory = repo.getValueFactory();
		
		Literal obj = factory.createLiteral(term);
		
		// For dbpedia we need to query only the dbpedia graph not the default graph !!!!!!
		IRI context = factory.createIRI("http://dbpedia.org");
		
		try(RepositoryConnection con = repo.getConnection()){
			
			
			try(RepositoryResult<Statement> result = con.getStatements(null, null, obj, context)){
				while (result.hasNext()) {
					Statement st = result.next();
					resultDto.add(new TripleDto(st.getSubject().toString(), st.getPredicate().toString(),
							st.getObject().toString()));
				}
			}
			
			
		}
		
		double end = new Date().getTime();
		System.out.println((end-start)/1000);
		return resultDto;
	}
	
	public List<TripleDto> getSubject(String subject) {

		logger.info("Method getSubject() entered.");
		List<TripleDto> resultDto = getSubjectWithTupleQuery(subject);
//		List<TripleDto> resultDto = getSubjectWithStatement(subject);
		return resultDto;
	}
	
	private List<TripleDto> getSubjectWithTupleQuery(String subject){
		double start = new Date().getTime();
		List<TripleDto> resultDto = new LinkedList<>();
		
		try(RepositoryConnection con = repo.getConnection()) {
			String queryString = "SELECT (<" + subject + "> as ?s) ?p ?o " + this.tripleStoreGraph + " WHERE {<" + subject + "> ?p ?o. "
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
	
	private List<TripleDto> getSubjectWithStatement(String subject) {
		double start = new Date().getTime();
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

	public List<PredicateDto> getPredicates() {
		logger.info("Method getPredicates() entered.");
		List<PredicateDto> resultDto = getPredicatesWithTupleQuery();

		return resultDto;
		
	}
	
	private List<PredicateDto> getPredicatesWithTupleQuery() {
		double start = new Date().getTime();
		List<PredicateDto> resultDto = new LinkedList<>();

		boolean gotAllPredicates = false;
		String graph = "from <http://dbpedia.org>";

		// DBPedia 438336000 triple geht noch
//		int offset = 438300000;
		//maximum results in dbpedia 10000
		int maxDbPediaResultNum = 9900;
		int maxLimit = 100000000;
		int offset = 0;
		int limit = 10000000;
		
		List<String> predicatesAsString = new LinkedList<>();
		
		while (!gotAllPredicates) {
			int resultNum = 0;
			List<String> queryResult = new LinkedList<>();
//			List<BindingSet> bindingSetResult = new LinkedList<>();
			try (RepositoryConnection con = repo.getConnection()) {

				String queryString = "select distinct ?p where {select ?p "+this.tripleStoreGraph+" where {?s ?p ?o} limit "+limit+" offset " + offset + "}";
				System.out.println(queryString);
				
				
				TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
				
				try (TupleQueryResult result = tupleQuery.evaluate()) {
					while (result.hasNext()) {
						BindingSet bindingSet = result.next();
						resultNum += 1;
						queryResult.add(bindingSet.getValue("p").toString());
//						bindingSetResult.add(bindingSet);
//						resultDto.add(new PredicateDto(bindingSet.getValue("p").toString(), false, false));
//						System.out.println(resultNum + " " + bindingSet.getValue("p").toString());
					}
				}
				
			}
			System.out.println("resultNum : " + resultNum);
			if (resultNum == 0) {
				gotAllPredicates = true;
			}
			if(resultNum > maxDbPediaResultNum) {
				limit = maxDbPediaResultNum;
			} else {
				for (String qResult: queryResult) {
					if(!predicatesAsString.contains(qResult)) {
						predicatesAsString.add(qResult);
					}
				}
//				for (BindingSet bindingSet : bindingSetResult) {
//					
//					resultDto.add(new PredicateDto(bindingSet.getValue("p").toString(), false, false));
//				}
				offset += limit;
				
				limit = limit * 2 > maxLimit ? maxLimit : limit * 2;
			}
			
			double meanTime = new Date().getTime();
			System.out.println("Offset: " + offset + " Mean time : " + (meanTime-start)/1000);
		}
		double end = new Date().getTime();
		System.out.println("Query time: " + (end-start)/1000);
		
		System.out.println("Predicates number: " + predicatesAsString.size());
		for (String string : predicatesAsString) {
			resultDto.add(new PredicateDto(string, false, false));
		}
		
		return resultDto;
	}

	private List<PredicateDto> getPredicatesWithStatement() {
		double start = new Date().getTime();
		List<PredicateDto> resultDto = new LinkedList<>();

		boolean gotAllPredicates = false;
		
		ValueFactory factory = repo.getValueFactory();

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
		boolean connected = false;
		logger.info("Method getConnected() in TestDummyDaoImpl entered.");
		
//		this.tripleStoreGraph = connDto.getTripleStoreGraph() != "" 
//				? "from <" + connDto.getTripleStoreGraph() + ">" : "";
//		
//		Repository repo = RepositoryServer.getMyRepository(connDto);
//		if(repo != null) {
//			connected = true;
//			shutDown();
//			this.repo = repo;
//			
//		}
		
		return connected;
	}
	
//	@Override
	public boolean getConnected2(ConnectionDto connDto) {
		
		logger.info("Method getConnected() in TestDummyDaoImpl entered.");
		
		boolean connected = false;

		System.out.println("server: " + connDto.getTripleStoreServer() + ", url: " + connDto.getTripleStoreUrl()
				+ ", repo: " + connDto.getTripleStoreRepo() + ", graph: " + connDto.getTripleStoreGraph() 
				+ ", username: " + connDto.getTripleStoreUserName()
				+ ", password: " + connDto.getTripleStorePassword());
		
		Repository repo = new MyRepository(connDto.getTripleStoreUrl());
		
		this.tripleStoreGraph = connDto.getTripleStoreGraph() != "" 
				? "from <" + connDto.getTripleStoreGraph() + ">" : "";

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
			logger.warn("Could not connect to Endpoint: " + connDto.getTripleStoreUrl() + " Graph: " + this.tripleStoreGraph + ".");
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
		logger.info("Shut down the repo");
		if (this.repo != null && this.repo.isInitialized()) {
			this.repo.shutDown();
		}
	}

}
