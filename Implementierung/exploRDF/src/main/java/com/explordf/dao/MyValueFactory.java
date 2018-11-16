package com.explordf.dao;

import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.AbstractValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleLiteral;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class MyValueFactory extends AbstractValueFactory {
	
	private static final MyValueFactory sharedInstance = new MyValueFactory();
	
//	ValueFactory factory = SimpleValueFactory.getInstance();
	
	public static MyValueFactory getInstance() {
		return sharedInstance;
	}

	@Override
	public Literal createLiteral(String value, IRI datatype) {
		Literal literal;
		try {
			literal = SimpleValueFactory.getInstance().createLiteral(value, datatype);
		} catch(IllegalArgumentException e) {
			System.out.println("Error at creatLiteral: ");
			System.out.println("Value: " + value + " Datatype: " + datatype);
			literal = SimpleValueFactory.getInstance().createLiteral(value);
		}
		
		return literal;
//		return factory.createLiteral(value);
	}
	
	/**
	 * Hidden constructor to enforce singleton pattern.
	 */
	protected MyValueFactory() {
	}

}
