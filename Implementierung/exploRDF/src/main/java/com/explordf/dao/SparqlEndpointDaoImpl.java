package com.explordf.dao;

import java.util.Collection;
import java.util.List;

import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
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
 * @author Andreas
 * Used to access any SPARQL-Endpoint.
 */
@org.springframework.stereotype.Repository
@Lazy
@Qualifier("sparqlEndpointRepo")
public class SparqlEndpointDaoImpl implements ExploRDFDao {

	private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointDaoImpl.class);
	
	private final String tripleStoreServer = "sparqlEndpoint";
	private String tripleStoreUrl;
	private String tripleStoreRepo;
	private String tripleStoreUserName;
	private String tripleStorePassword;
	
	private Repository repo;
	
	@Override
	public List<TripleDto> simpleSearch(String term, boolean broaderSearch) {
		
		
		
		return null;
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
		Repository repo = new SPARQLRepository(connDto.getTripleStoreUrl());
		((SPARQLRepository) repo).setUsernameAndPassword(connDto.getTripleStoreUserName(), connDto.getTripleStorePassword());
		repo.initialize();
		RepositoryConnection conn = repo.getConnection();
		
		if(conn.isOpen()) {
			conn.close();
			this.tripleStoreUrl = connDto.getTripleStoreUrl();
			this.tripleStoreRepo = connDto.getTripleStoreRepo();
			this.tripleStoreUserName = connDto.getTripleStoreUserName();
			this.tripleStorePassword = connDto.getTripleStorePassword();
			
			if(this.repo.isInitialized()) {
				this.repo.shutDown();
			}
			this.repo = repo;
			
			return true;
		} else {
			repo.shutDown();
			conn.close();
			return false;
		}
	}

}
