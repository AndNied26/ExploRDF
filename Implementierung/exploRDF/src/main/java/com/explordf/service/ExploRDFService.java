package com.explordf.service;

import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.explordf.dao.ExploRDFDao;

import dto.PredicateDto;

@Service
public class ExploRDFService {

	@Autowired
	@Qualifier("rdf4jRepo")
	ExploRDFDao exploRDFDao;
	
	
	public Collection<String> simpleSearch(String term) {
		return exploRDFDao.simpleSearch(term);
	}


	public List<PredicateDto> getPredicates() throws JSONException {
		return exploRDFDao.getPredicates();
	}
}
