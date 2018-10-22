package com.explordf.dao;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("stardogRepo")
public class StardogDaoImpl {

	public Collection<String> simpleSearch(String term) {
		
		return null;
	}

	public JSONArray getPredicates() throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

}
