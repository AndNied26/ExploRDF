package com.explordf.dao.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.rdf4j.StardogRepository;
import com.explordf.dto.ConnectionDto;

public class RepositoryServer {
	
	private static final Logger logger = LoggerFactory.getLogger(RepositoryServer.class);
	
	private final static List<String> supportedServers = new LinkedList<String>(
			Arrays.asList("RDF4J-Server", "Stardog-Server", "SPARQL-Endpoint"));
	
	private RepositoryServer() {
		
	}
	
	
	public static Repository getRepository(ConnectionDto connDto) {
		switch (connDto.getTripleStoreServer()) {
		case "RDF4J-Server":
			return getHTTPRepository(connDto);	
		case "Stardog-Server":
			return getStardogRepository(connDto);
		case "SPARQL-Endpoint":
			return getMyRepository(connDto);
		default:
			return null;
		}
	}
	
	/**
	 * Tries to connect to SPARQL-Endpoint via an instance of MyRepository.
	 * 
	 * @param connDto All needed properties for the connection to the SPARQL-Endpoint.
	 * @return Initialized instance of MyRepository or null if connection failed. 
	 */
	private static Repository getMyRepository(ConnectionDto connDto) {
		logger.info("Method getMyRepository() entered.");
		Repository repo = new MyRepository(connDto.getTripleStoreUrl());
		
		if (connDto.getTripleStoreUserName() != "" && connDto.getTripleStorePassword() != "") {
			((MyRepository) repo).setUsernameAndPassword(connDto.getTripleStoreUserName(),
					connDto.getTripleStorePassword());
		}
		
		
		return getConnectedRepository(repo, connDto);
	}
	
	private static Repository getHTTPRepository(ConnectionDto connDto) {
		logger.info("Method getHTTPRepository() entered.");
		
		Repository repo = new HTTPRepository(connDto.getTripleStoreUrl(), connDto.getTripleStoreRepo());
		
		if (connDto.getTripleStoreUserName() != "" && connDto.getTripleStorePassword() != "") {
			((HTTPRepository) repo).setUsernameAndPassword(connDto.getTripleStoreUserName(),
					connDto.getTripleStorePassword());
		}
		
		return getConnectedRepository(repo, connDto);
	}
	
	private static Repository getStardogRepository(ConnectionDto connDto) {
		logger.info("Method getStardogRepository() entered.");
		
		Repository repo = new StardogRepository(ConnectionConfiguration
				.to(connDto.getTripleStoreRepo()).server(connDto.getTripleStoreUrl())
				.credentials(connDto.getTripleStoreUserName(), connDto.getTripleStorePassword()));
		
		return getConnectedRepository(repo, connDto);
	}
	
	private static Repository getConnectedRepository(Repository repo, ConnectionDto connDto) {
		boolean connected = false;
		
		System.out.println("server: " + connDto.getTripleStoreServer() + ", url: " + connDto.getTripleStoreUrl()
		+ ", repo: " + connDto.getTripleStoreRepo() + ", graph: " + connDto.getTripleStoreGraph() 
		+ ", username: " + connDto.getTripleStoreUserName()
		+ ", password: " + connDto.getTripleStorePassword());
		
		
		String graph = !connDto.getTripleStoreGraph().isEmpty() 
				? "from <" + connDto.getTripleStoreGraph() + ">" : "";
		
		repo.initialize();
		
		try (RepositoryConnection conn = repo.getConnection()) {
			String queryString = "SELECT * " + graph  + " WHERE {?s ?p ?o} LIMIT 1";
			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			System.out.println(queryString);
			
			try (TupleQueryResult result = tupleQuery.evaluate()) {
				System.out.println("TupleQueryResult");
				connected = true;
			}
		} catch (RDF4JException e) {
			logger.warn("Could not connect to Endpoint: " + connDto.getTripleStoreUrl() 
				+ ", Repository: " + connDto.getTripleStoreRepo()
				+ ", Graph: " + connDto.getTripleStoreGraph() + ".");
			e.printStackTrace();
		}
		
		return connected ? repo : null;
	}
	
	public static List<String> getSupportedServers() {
		return supportedServers;
	}
}
