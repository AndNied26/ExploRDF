package com.explordf.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.explordf.dao.ExploRDFDao;

@Service
public class ExploRDFService {

	@Autowired
	@Qualifier("rdf4jRepo")
	ExploRDFDao exploRDFDao;
	
	
	public Collection<String> simpleSearch(String term) {
		return exploRDFDao.simpleSearch(term);
	}
}
