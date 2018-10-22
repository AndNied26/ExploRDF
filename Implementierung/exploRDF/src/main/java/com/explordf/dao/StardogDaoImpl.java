package com.explordf.dao;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("stardogRepo")
public class StardogDaoImpl implements ExploRDFDao {

	@Override
	public Collection<String> simpleSearch(String term) {
		
		return null;
	}

}
