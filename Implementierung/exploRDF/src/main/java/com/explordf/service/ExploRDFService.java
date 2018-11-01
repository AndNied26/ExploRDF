package com.explordf.service;

import java.util.List;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.explordf.dao.ExploRDFDao;

import dto.PredicateDto;
import dto.TripleDto;

@Service
public class ExploRDFService {

	@Autowired
	@Qualifier("dummyRepo")
	ExploRDFDao exploRDFDao;
	
	
	public List<TripleDto> simpleSearch(String term, boolean broaderSearch) {
		return exploRDFDao.simpleSearch(term, broaderSearch);
	}


	public List<PredicateDto> getPredicates() {
		return exploRDFDao.getPredicates();
	}


	public List<TripleDto> getSubject(String subject) {
		System.out.println("Entered sevice");
		return exploRDFDao.getSubject(subject);
	}
}
