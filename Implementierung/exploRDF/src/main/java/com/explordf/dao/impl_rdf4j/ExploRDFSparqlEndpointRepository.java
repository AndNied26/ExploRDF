package com.explordf.dao.impl_rdf4j;

import java.util.Collections;
import java.util.Map;

import org.eclipse.rdf4j.http.client.SPARQLProtocolSession;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

/**
 * Extension class of RDF4J´s SPARQLRepository.
 * Class is needed for continuing the request even if 
 * a malformed Literal is returned.
 * 
 * @author Andreas Niederquell
 *
 */
public class ExploRDFSparqlEndpointRepository extends SPARQLRepository {

	private String username;

	private String password;

	private final String queryEndpointUrl;

	private final String updateEndpointUrl;

	private volatile Map<String, String> additionalHttpHeaders = Collections.emptyMap();

	public ExploRDFSparqlEndpointRepository(String endpointUrl) {
		super(endpointUrl);
		this.queryEndpointUrl = endpointUrl;
		this.updateEndpointUrl = endpointUrl;
	}

	
	protected SPARQLProtocolSession createHTTPClient() {
		// initialize HTTP client
		SPARQLProtocolSession httpClient = getHttpClientSessionManager().createSPARQLProtocolSession(queryEndpointUrl, updateEndpointUrl);
		httpClient.setValueFactory(ExploRDFValueFactory.getInstance());
		httpClient.setPreferredTupleQueryResultFormat(TupleQueryResultFormat.SPARQL);
		httpClient.setAdditionalHttpHeaders(additionalHttpHeaders);
		if (username != null) {
			httpClient.setUsernameAndPassword(username, password);
		}
		return httpClient;
	}
	

	public void setUsernameAndPassword(final String username, final String password) {
		this.username = username;
		this.password = password;
	}
	
	

}
