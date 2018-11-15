package com.explordf.dao;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.AbstractValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleLiteral;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class MyValueFactory extends AbstractValueFactory {
	
	private static final MyValueFactory sharedInstance = new MyValueFactory();
	
	ValueFactory factory = SimpleValueFactory.getInstance();
	
	public static MyValueFactory getInstance() {
		return sharedInstance;
	}

	@Override
	public Literal createLiteral(String value, IRI datatype) {
		return factory.createLiteral(value);
	}
	
	/**
	 * Hidden constructor to enforce singleton pattern.
	 */
	protected MyValueFactory() {
	}

}
