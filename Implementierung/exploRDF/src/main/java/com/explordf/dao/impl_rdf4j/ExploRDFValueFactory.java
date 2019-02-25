package com.explordf.dao.impl_rdf4j;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.impl.AbstractValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension class of RDF4J´s AbstractValueFactory.
 * Class is needed for continuing the request even if 
 * a malformed Literal is returned.
 * 
 * @author Andreas Niederquell
 *
 */
public class ExploRDFValueFactory extends AbstractValueFactory {
	
	private static final ExploRDFValueFactory sharedInstance = new ExploRDFValueFactory();
	
	private static final Logger logger = LoggerFactory.getLogger(ExploRDFValueFactory.class);
	
	public static ExploRDFValueFactory getInstance() {
		return sharedInstance;
	}

	@Override
	public Literal createLiteral(String value, IRI datatype) {
		Literal literal;
		try {
			literal = SimpleValueFactory.getInstance().createLiteral(value, datatype);
		} catch(IllegalArgumentException e) {
			logger.warn("Error at createLiteral(); Value: " + value 
					+ ", Datatype: " + datatype);
			literal = SimpleValueFactory.getInstance().createLiteral(value);
		}
		
		return literal;
	}
	
	@Override
	public IRI createIRI(String iri) {
		IRI iriVal;
		try {
			iriVal = SimpleValueFactory.getInstance().createIRI(iri);
		} catch(IllegalArgumentException e) {
			logger.warn("Error at createIRI(); IRI: " + iri);
			iriVal = SimpleValueFactory.getInstance().createIRI("http://www.example.org/errorAtCreateIRI/" + iri);
		}
		return iriVal;
	}
	
	/**
	 * Hidden constructor to enforce singleton pattern.
	 */
	protected ExploRDFValueFactory() {
	}

}
