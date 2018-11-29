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
	
	
	Repository repo;
	
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
		
		String tripleStoreGraph = this.tripleStoreGraph != "" 
				? "from <" + this.tripleStoreGraph + ">" : "";
		
		try(RepositoryConnection con = repo.getConnection()){
			
			String queryString;
//			String graph = "from <http://dbpedia.org>";
			
			if(broaderSearch) {
				queryString = "select ?s ?p ?o "+tripleStoreGraph+" where {filter(regex(?o, \""+ term 
						+ "\", \"i\")).?s ?p ?o} order by ?s";
			} else {
//				queryString = "select ?s ?p ?o "+this.tripleStoreGraph+" where {filter(?o = \"" 
//						+ term +"\"). {SELECT ?s ?p ?o WHERE {?s ?p \"" 
//						+ term + "\". ?s ?p ?o}}}";
				queryString = "SELECT ?s ?p ?o "+tripleStoreGraph+" WHERE {filter(?o = \"" + term + "\"). ?s ?p ?o}";
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
		logger.info("Shut down the repo");
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
