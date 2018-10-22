package com.explordf.dao;

import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import dto.PredicateDto;

public interface ExploRDFDao {

	Collection<String> simpleSearch(String term);
	
	List<PredicateDto> getPredicates()  throws JSONException;
	
}
