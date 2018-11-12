package com.explordf.dao;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;

import com.explordf.dto.ConnectionDto;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;

/**
 * 
 * @author Andreas Used to access any SPARQL-Endpoint.
 */
@org.springframework.stereotype.Repository
@Lazy
@Qualifier("sparqlEndpointRepo")
public class SparqlEndpointDaoImpl implements ExploRDFDao {

	private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointDaoImpl.class);

	private final String tripleStoreServer = "sparqlEndpoint";

	private Repository repo;

	@Override
	public List<TripleDto> simpleSearch(String term, boolean broaderSearch) {
		double start = new Date().getTime();
		logger.info("Method simpleSearch() in SparqlEndpointDaoImpl entered.");

		List<TripleDto> resultDto = new LinkedList<>();

		try (RepositoryConnection con = repo.getConnection()) {

			String queryString;

			if (broaderSearch) {
				queryString = "select ?s ?p ?o where {filter(regex(?o, \"" + term + "\", \"i\")).?s ?p ?o} order by ?s";
			} else {
				queryString = "SELECT ?s ?p ?o WHERE {?s ?p \"" + term + "\". ?s ?p ?o}";
			}

			List<BindingSet> resultList;
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);

			try (TupleQueryResult result = tupleQuery.evaluate()) {
				resultList = QueryResults.asList(result);
			}

			for (BindingSet bindingSet : resultList) {

				Value subject = bindingSet.getValue("s");
				Value predicate = bindingSet.getValue("p");
				Value object = bindingSet.getValue("o");

				TripleDto dto = new TripleDto(subject.toString(), predicate.toString(), object.toString());
				resultDto.add(dto);
			}

		}
		double end = new Date().getTime();
		System.out.println((end - start) / 1000);

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
	public String getType() {
		return tripleStoreServer;
	}

	@Override
	public boolean getConnected(ConnectionDto connDto) {

		logger.info("Method getConnected() in SparqlEndpointDaoImpl entered.");

		boolean connected = false;

		System.out.println("server: " + connDto.getTripleStoreServer() + ", url: " + connDto.getTripleStoreUrl()
				+ ", repo: " + connDto.getTripleStoreRepo() + ", username: " + connDto.getTripleStoreUserName()
				+ ", password: " + connDto.getTripleStorePassword());
		Repository repo = new SPARQLRepository(connDto.getTripleStoreUrl());

		if (connDto.getTripleStoreUserName() != "" && connDto.getTripleStorePassword() != "") {
			((SPARQLRepository) repo).setUsernameAndPassword(connDto.getTripleStoreUserName(),
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
			logger.error("An RDF4JException occured while trying to connect to " 
					+ connDto.getTripleStoreUrl() + ".");
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
