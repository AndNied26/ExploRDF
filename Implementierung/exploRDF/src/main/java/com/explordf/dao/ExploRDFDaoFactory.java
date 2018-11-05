package com.explordf.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class ExploRDFDaoFactory {

	@Autowired
	private List<ExploRDFDao> daos;
	
	private final Map<String, ExploRDFDao> daoCache = new HashMap<>();
	
	@PostConstruct
	public void initDaoCache() {
		System.out.println("DaoFactory post construct");
		for(ExploRDFDao dao: daos) {
			System.out.println("Factory: " + dao.getType());
			daoCache.put(dao.getType(), dao);
		}
	}
	
	public ExploRDFDao getDao(String type) {
		ExploRDFDao dao = daoCache.get(type);
		return dao;
	}
}
