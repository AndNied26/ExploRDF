package com.explordf.dao.impl_rdf4j;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPQueryEvaluationException;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.rdf4j.StardogRepository;
import com.explordf.dto.ConnectionDto;

/**
 * Static class for creating a Repository instance and first attempt to connecting to a
 * triple store.
 * 
 * @author Andreas Niederquell
 *
 */
public class ExploRDFRepositoryServer {
	
	private static final Logger logger = LoggerFactory.getLogger(ExploRDFRepositoryServer.class);
	
	private final static List<String> supportedServers = new LinkedList<String>(
			Arrays.asList("RDF4J-Server", "Stardog-Server", "SPARQL-Endpoint"));
	
	private ExploRDFRepositoryServer() {
		
	}
	
	/**
	 * Gets the Repository instance according to the connection data in the ConnectionDto.
	 * 
	 * @param connDto All needed properties for the connection to a triple store.
	 * @return Repository object if the connection attempt was successful.
	 * 		   Return null if attempt failed.
	 */
	public static Repository getRepository(ConnectionDto connDto) {
		switch (connDto.getTripleStoreServer()) {
		case "RDF4J-Server":
			return getHTTPRepository(connDto);	
		case "Stardog-Server":
			return getStardogRepository(connDto);
		case "SPARQL-Endpoint":
			return ExploRDFSparqlEndpointRepository(connDto);
		default:
			return null;
		}
	}
	
	/**
	 * Creates a ExploRDFSparqlEndpointRepository for querying a SPARQL-Endpoint.
	 * 
	 * @param connDto All needed properties for the connection to the SPARQL-Endpoint.
	 * @return Initialized instance of ExploRDFSparqlEndpointRepository or null if connection failed. 
	 */
	private static Repository ExploRDFSparqlEndpointRepository(ConnectionDto connDto) {
		logger.info("Method ExploRDFSparqlEndpointRepository() entered.");
		Repository repo = new ExploRDFSparqlEndpointRepository(connDto.getTripleStoreUrl());
		
		if (connDto.getTripleStoreUserName() != "" && connDto.getTripleStorePassword() != "") {
			((ExploRDFSparqlEndpointRepository) repo).setUsernameAndPassword(connDto.getTripleStoreUserName(),
					connDto.getTripleStorePassword());
		}
		
		
		return getConnectedRepository(repo, connDto);
	}
	
	/**
	 * Creates a HTTPRepository for querying a RDF4J-server. 
	 * 
	 * @param connDto All needed properties for the connection to the RDF4J-server.
	 * @return Initialized instance of HTTPRepository or null if connection failed. 
	 */
	private static Repository getHTTPRepository(ConnectionDto connDto) {
		logger.info("Method getHTTPRepository() entered.");
		
		Repository repo = new HTTPRepository(connDto.getTripleStoreUrl(), connDto.getTripleStoreRepo());
		
		if (connDto.getTripleStoreUserName() != "" && connDto.getTripleStorePassword() != "") {
			((HTTPRepository) repo).setUsernameAndPassword(connDto.getTripleStoreUserName(),
					connDto.getTripleStorePassword());
		}
		
		return getConnectedRepository(repo, connDto);
	}
	
	/**
	 * Creates a StardogRepository for querying a Stardog-server. 
	 * 
	 * @param connDto All needed properties for the connection to the Stardog-server.
	 * @return Initialized instance of StardogRepository or null if connection failed. 
	 */
	private static Repository getStardogRepository(ConnectionDto connDto) {
		logger.info("Method getStardogRepository() entered.");
		
		Repository repo = new StardogRepository(ConnectionConfiguration
				.to(connDto.getTripleStoreRepo()).server(connDto.getTripleStoreUrl())
				.credentials(connDto.getTripleStoreUserName(), connDto.getTripleStorePassword()));
		
		return getConnectedRepository(repo, connDto);
	}
	
	/**
	 * Tries to connect to the triple store according to the connection properties in the
	 * ConnectionDto object.
	 * 
	 * @param repo Repository instance with set credentials.
	 * @param connDto ConnectionDto with the connection properties.
	 * @return return The same Repository object if the connection attempt was successful.
	 * 		   Return null if attempt failed.
	 */
	private static Repository getConnectedRepository(Repository repo, ConnectionDto connDto) {
		boolean connected = false;
		
		String graph = !connDto.getTripleStoreGraph().isEmpty() 
				? "from <" + connDto.getTripleStoreGraph() + ">" : "";
		
		repo.initialize();
		
		try (RepositoryConnection conn = repo.getConnection()) {
			String queryString = "SELECT * " + graph  + " WHERE {?s ?p ?o} LIMIT 1";
			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			System.out.println(queryString);
			
			try (TupleQueryResult result = tupleQuery.evaluate()) {
				connected = true;
			} catch(HTTPQueryEvaluationException e) {
				logger.warn("Could not evaluate tuple query.");
			}
		} catch (RDF4JException e) {
			logger.warn("Could not connect to Endpoint: " + connDto.getTripleStoreUrl() 
				+ ", Repository: " + connDto.getTripleStoreRepo()
				+ ", Graph: " + connDto.getTripleStoreGraph() + ".");
			e.printStackTrace();
		}
		
		return connected ? repo : null;
	}
	
	/**
	 * Gets all supported triple-store types.
	 * 
	 * @return List with the supported triple-stores.
	 */
	public static List<String> getSupportedServers() {
		return supportedServers;
	}
}
