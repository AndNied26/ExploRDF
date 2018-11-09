package com.explordf.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.explordf.dao.ExploRDFDao;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;

@Service
public class QueryService {
	
	@Autowired
	DaoServer daoServer;
	ExploRDFDao exploRDFDao;
	
	@PostConstruct
	private void init() {
		setDao();
	}
	
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
	
	
	public void setDao() {
		exploRDFDao = daoServer.getDao();
		if(exploRDFDao != null) {
			System.out.println("QueryService new Dao: " + exploRDFDao.getType());
		}
	}
}
