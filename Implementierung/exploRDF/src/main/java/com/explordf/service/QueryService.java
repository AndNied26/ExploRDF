package com.explordf.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.explordf.dao.ExploRDFDao;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;
import com.explordf.dto.VisualizationNodesDto;

@Service
public class QueryService {
	
	@Autowired
	@Qualifier("exploRDFDaoImpl")
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


	public List<String> getAllPredicatesLists() {
		return exploRDFDao.getAllPredicatesLists();
	}


	public String savePredicatesList(List<PredicateDto> predicateDtoList, String listName) {
		String result = null;
		try {
			result = exploRDFDao.savePredicatesList(predicateDtoList, listName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}


	public List<PredicateDto> getPredicatesList(String listName) {
		List<PredicateDto> result = null;
		try {
			result = exploRDFDao.getPredicatesList(listName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}


	public VisualizationNodesDto getNode(String subject, String predicatesList) {
		return exploRDFDao.getNode(subject, predicatesList);
	}


	public VisualizationNodesDto getNodeData(String subject, String predicatesList) {
		return exploRDFDao.getNodeData(subject, predicatesList);
	}
	
}
