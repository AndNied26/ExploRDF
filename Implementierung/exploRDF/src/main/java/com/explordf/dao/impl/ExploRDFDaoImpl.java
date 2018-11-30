package com.explordf.dao.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.ResourceUtils;

import com.explordf.dao.ExploRDFDao;
import com.explordf.dto.ConnectionDto;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;

@org.springframework.stereotype.Repository
@PropertySource("classpath:explordf.properties")
@Qualifier(value="exploRDFDaoImpl")
public class ExploRDFDaoImpl implements ExploRDFDao {
	
	// muss dann weg
		String endpoint = "http://localhost:5820/sachbegriffeDB/query";
		String endpointDBPedia = "http://dbpedia.org/sparql"; // graph: http://dbpedia.org
		String endpointRdf4j = "http://localhost:8080/rdf4j-server/repositories/test";
		String stardogServer = "http://localhost:5820";
		String username = "admin", password = "admin";
		String db = "geograficumDB";
	
	private static final Logger logger = LoggerFactory.getLogger(ExploRDFDao.class);

	@Autowired
	Environment env;
	
	@Value("${triplestore.server}")
	private String tripleStoreServer;
	
	@Value("${triplestore.url}")
	private String tripleStoreUrl;
	
	@Value("${triplestore.repository}")
	private String tripleStoreRepo;
	
	@Value("${triplestore.graph}")
	private String tripleStoreGraph;
	
	@Value("${triplestore.username}")
	private String tripleStoreUserName;
	
	@Value("${triplestore.password}")
	private String tripleStorePassword;
	
	
	private Repository repo;
	private String queryGraph;
	
	@PostConstruct
	private void init() {
		logger.info("PostConstruct init()");
		showConnProps();
		
		setConnectionProps(new ConnectionDto(tripleStoreUrl, tripleStoreServer,
				tripleStoreRepo, tripleStoreGraph, tripleStoreUserName, 
				tripleStorePassword));
	}

	@Override
	public List<TripleDto> simpleSearch(String term, boolean broaderSearch) {
		logger.info("Method simpleSearch() in entered.");
		double start = new Date().getTime();
	
		List<TripleDto> resultDto = new LinkedList<>();
		
		try(RepositoryConnection con = repo.getConnection()){
			
			String queryString;
			
			if(broaderSearch) {
				queryString = "select ?s ?p ?o "+ queryGraph +" where {filter(regex(?o, \""+ term 
						+ "\", \"i\")).?s ?p ?o} order by ?s";
			} else {
//				queryString = "select ?s ?p ?o "+this.tripleStoreGraph+" where {filter(?o = \"" 
//						+ term +"\"). {SELECT ?s ?p ?o WHERE {?s ?p \"" 
//						+ term + "\". ?s ?p ?o}}}";
//				queryString = "SELECT ?s ?p ?o "+ queryGraph +" WHERE {filter(?o = \"" + term + "\"). ?s ?p ?o}";
				queryString = "SELECT ?s ?p ?o WHERE {FILTER(?o = \"" + term + "\"). {SELECT ?s ?p ?o " + queryGraph +" WHERE {?s ?p \"" + term + "\". ?s ?p ?o}}}";
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

	@Override
	public List<TripleDto> getSubject(String subject) {
		double start = new Date().getTime();
		List<TripleDto> resultDto = new LinkedList<>();
		
		try(RepositoryConnection con = repo.getConnection()) {
			String queryString = "SELECT (<" + subject + "> as ?s) ?p ?o " + queryGraph + " WHERE {<" + subject + "> ?p ?o. "
					+ "FILTER(!isLiteral(?o) || langMatches(lang(?o), \"EN\") || langMatches(lang(?o), \"DE\") || langMatches(lang(?o), \"\"))}";
			
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
		logger.info("Method getPredicates() entered.");
		double start = new Date().getTime();
		List<PredicateDto> resultDto = new LinkedList<>();

		boolean gotAllPredicates = false;

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

				String queryString = "select distinct ?p where {select ?p "+queryGraph+" where {?s ?p ?o} limit "+limit+" offset " + offset + "}";
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
	public ConnectionDto getConnectionProps() {
		if(repo != null) {
			ConnectionDto connDto = new ConnectionDto();
			connDto.setTripleStoreUrl(tripleStoreUrl);
			connDto.setTripleStoreRepo(tripleStoreRepo);
			return connDto;
		} else {
			return null;
			
		}
	}

	@Override
	public ConnectionDto setConnectionProps(ConnectionDto connDto) {
		logger.info("Method setConnectionProps() entered.");
		Repository repo = RepositoryServer.getRepository(connDto);
		if(repo != null) {
			shutDown();
			this.repo = repo;
			
			tripleStoreUrl = connDto.getTripleStoreUrl();
			tripleStoreServer = connDto.getTripleStoreServer();
			tripleStoreRepo = connDto.getTripleStoreRepo() != null ? 
					connDto.getTripleStoreRepo() : "";
			tripleStoreGraph = connDto.getTripleStoreGraph() != null ? 
							connDto.getTripleStoreGraph() : "";
			tripleStoreUserName = connDto.getTripleStoreUserName() != null ? 
					connDto.getTripleStoreUserName() : "";
			tripleStorePassword = connDto.getTripleStorePassword() != null ? 
					connDto.getTripleStorePassword() : "";
			
			saveConnProps();
			
			queryGraph = !connDto.getTripleStoreGraph().isEmpty() 
					? "from <" + connDto.getTripleStoreGraph() + ">" : "";
					
			return getConnectionProps();
		} else {
			return null;
		}
		
	}

	@Override
	public List<String> getSupportedServers() {
		return RepositoryServer.getSupportedServers();
	}
	
	private void saveConnProps() {
		DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
		String filePath = "classpath:explordf.properties";
		Properties props = new Properties();

		props.setProperty("triplestore.server", tripleStoreServer);
		props.setProperty("triplestore.url", tripleStoreUrl);
		props.setProperty("triplestore.repository", tripleStoreRepo != null ? tripleStoreRepo : "");
		props.setProperty("triplestore.graph", tripleStoreGraph != null ? tripleStoreGraph : "");
		props.setProperty("triplestore.username", tripleStoreUserName != null ? tripleStoreUserName : "");
		props.setProperty("triplestore.password", tripleStorePassword != null ? tripleStorePassword : "");

		try {
			File f = ResourceUtils.getFile(filePath);
			FileOutputStream out = new FileOutputStream(f);
			persister.store(props, out, "db");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showConnProps();
	}
	
	private void shutDown() {
		logger.info("Shut down");
		if (this.repo != null && this.repo.isInitialized()) {
			logger.info("Shut down the repo");
			this.repo.shutDown();
		}
	}
	
	@PreDestroy
	private void close() {
		logger.info("Method predestroy entered");
		shutDown();
	}

	private void showConnProps() {
		logger.info("Connection properties:");
		logger.info("Server: " + tripleStoreServer + " Env: " + env.getProperty("triplestore.server"));
		logger.info("Name: " + tripleStoreUrl + " Env: " + env.getProperty("triplestore.url"));
		logger.info("Repo: " + tripleStoreRepo + " Env: " + env.getProperty("triplestore.repository"));
		logger.info("Graph: " + tripleStoreGraph + " Env: " + env.getProperty("triplestore.graph"));
		logger.info("Username: " + tripleStoreUserName + " Env: " + env.getProperty("triplestore.username"));
		logger.info("Password: " + tripleStorePassword + " Env: " + env.getProperty("triplestore.password"));
		System.out.println();
	}

}
