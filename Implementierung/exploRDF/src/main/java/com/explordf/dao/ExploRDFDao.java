package com.explordf.dao;

import java.util.List;

import org.json.JSONException;

import dto.PredicateDto;
import dto.TripleDto;

public interface ExploRDFDao {

	List<TripleDto> simpleSearch(String term);
	
	List<PredicateDto> getPredicates()  throws JSONException;

	List<TripleDto> getSubject(String subject);
	
}
