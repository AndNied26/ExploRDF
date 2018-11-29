package com.explordf.dao.impl;

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
	
	
	
	private RepositoryServer() {
		
	}
	
	/**
	 * Tries to connect to SPARQL-Endpoint via an instance of MyRepository.
	 * 
	 * @param connDto All needed properties for the connection to the SPARQL-Endpoint.
	 * @return Initialized instance of MyRepository or null if connection failed. 
	 */
	public static Repository getMyRepository(ConnectionDto connDto) {
		logger.info("Method getMyRepository() entered.");
		Repository repo = new MyRepository(connDto.getTripleStoreUrl());
		
		if (connDto.getTripleStoreUserName() != "" && connDto.getTripleStorePassword() != "") {
			((MyRepository) repo).setUsernameAndPassword(connDto.getTripleStoreUserName(),
					connDto.getTripleStorePassword());
		}
		
		
		return getConnectedRepository(repo, connDto);
	}
	
	public static Repository getHTTPRepository(ConnectionDto connDto) {
		logger.info("Method getHTTPRepository() entered.");
		
		Repository repo = new HTTPRepository(connDto.getTripleStoreUrl(), connDto.getTripleStoreRepo());
		
		if (connDto.getTripleStoreUserName() != "" && connDto.getTripleStorePassword() != "") {
			((HTTPRepository) repo).setUsernameAndPassword(connDto.getTripleStoreUserName(),
					connDto.getTripleStorePassword());
		}
		
		return getConnectedRepository(repo, connDto);
	}
	
	public static Repository getStardogRepository(ConnectionDto connDto) {
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
		
		String graph = connDto.getTripleStoreGraph() != "" 
				? "from <" + connDto.getTripleStoreGraph() + ">" : "";
		
		repo.initialize();
		
		try (RepositoryConnection conn = repo.getConnection()) {
			String queryString = "SELECT * " + graph + " WHERE {?s ?p ?o} LIMIT 1";
			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			try (TupleQueryResult result = tupleQuery.evaluate()) {
				System.out.println("TupleQueryResult");
				connected = true;
			}
		} catch (RDF4JException e) {
			logger.warn("Could not connect to Endpoint: " + connDto.getTripleStoreUrl() 
				+ ", Repository: " + connDto.getTripleStoreRepo()
				+ ", Graph: " + connDto.getTripleStoreGraph() + ".");
		}
		
		return connected ? repo : null;
	}
}
