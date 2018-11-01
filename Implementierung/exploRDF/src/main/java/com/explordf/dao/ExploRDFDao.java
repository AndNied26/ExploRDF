package com.explordf.dao;

import java.util.List;

import org.json.JSONException;

import dto.PredicateDto;
import dto.TripleDto;

public interface ExploRDFDao {

	List<TripleDto> simpleSearch(String term, boolean broaderSearch);
	
	List<PredicateDto> getPredicates();

	List<TripleDto> getSubject(String subject);
	
}
