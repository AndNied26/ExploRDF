package com.explordf.dao.impl_rdf4j;

import java.util.Collections;
import java.util.Map;

import org.eclipse.rdf4j.http.client.HttpClientSessionManager;
import org.eclipse.rdf4j.http.client.SPARQLProtocolSession;
import org.eclipse.rdf4j.http.client.SharedHttpClientSessionManager;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.repository.sparql.SPARQLConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

public class MyRepository extends SPARQLRepository {
	

//	private boolean quadMode = false;

	/**
	 * The HTTP client that takes care of the client-server communication.
	 */
//	private volatile HttpClientSessionManager client;

	/** dependent life cycle */
//	private volatile SharedHttpClientSessionManager dependentClient;

	private String username;

	private String password;

	private final String queryEndpointUrl;

	private final String updateEndpointUrl;

	private volatile Map<String, String> additionalHttpHeaders = Collections.emptyMap();

	public MyRepository(String endpointUrl) {
		super(endpointUrl);
		this.queryEndpointUrl = endpointUrl;
		this.updateEndpointUrl = endpointUrl;
	}

	
	protected SPARQLProtocolSession createHTTPClient() {
		// initialize HTTP client
		SPARQLProtocolSession httpClient = getHttpClientSessionManager().createSPARQLProtocolSession(queryEndpointUrl, updateEndpointUrl);
		httpClient.setValueFactory(MyValueFactory.getInstance());
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
