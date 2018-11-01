package com.explordf.dao;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import dto.PredicateDto;
import dto.TripleDto;

@org.springframework.stereotype.Repository
@Qualifier("dummyRepo")
public class TestDummyDaoImpl implements ExploRDFDao {

	private static final Logger logger = LoggerFactory.getLogger(TestDummyDaoImpl.class);
	
	String rdf4jServer = "http://localhost:8080/rdf4j-server";
	String repoName = "test";
	RepositoryManager manager;
	
	@PostConstruct
	private void init() {
		manager = RepositoryProvider.getRepositoryManager(rdf4jServer);
		logger.info("TestDummy Repository created.");
	}
	
	@Override
	public List<TripleDto> simpleSearch(String term, boolean broaderSearch) {
		double start = new Date().getTime();
		System.out.println(start);
		Repository repo = manager.getRepository(repoName);
		logger.info("Method simpleSearch() in TestDummyDaoImpl entered.");
		List<TripleDto> resultDto = new LinkedList<>();
		
		repo.initialize();
		
		try(RepositoryConnection con = repo.getConnection()){
			
			String queryString;
			
			if(broaderSearch) {
				queryString = "select ?s ?p ?o where {filter(regex(?o, \""+ term + "\", \"i\")).?s ?p ?o} order by ?s";
			} else {
				queryString = "SELECT ?s ?p ?o WHERE {?s ?p \"" + term + "\". ?s ?p ?o}";
			}		
			
			List<BindingSet> resultList;
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			
			try(TupleQueryResult result = tupleQuery.evaluate()){
				resultList = QueryResults.asList(result);
			}
			
			for (BindingSet bindingSet : resultList) {
				
				Value subject = bindingSet.getValue("s");
				Value predicate = bindingSet.getValue("p");
				Value object = bindingSet.getValue("o");
				
				TripleDto dto = new TripleDto(subject.toString(), predicate.toString(), object.toString());
				resultDto.add(dto);
			}
			
			
		}finally {
			repo.shutDown();
		}
		double end = new Date().getTime();
		System.out.println((end-start)/1000);
		return resultDto;
	}

	@Override
	public List<PredicateDto> getPredicates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TripleDto> getSubject(String subject) {
		// TODO Auto-generated method stub
		return null;
	}

}
