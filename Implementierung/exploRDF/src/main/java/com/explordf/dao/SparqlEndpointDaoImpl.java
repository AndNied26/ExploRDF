package com.explordf.dao;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.annotation.PreDestroy;

import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.sparql.SPARQLConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.eclipse.rdf4j.repository.util.RDFLoader;
import org.eclipse.rdf4j.rio.DatatypeHandler;
import org.eclipse.rdf4j.rio.ParserConfig;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.RioSetting;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.eclipse.rdf4j.rio.helpers.NTriplesParserSettings;
import org.eclipse.rdf4j.rio.helpers.RioSettingImpl;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.rio.helpers.TurtleParserSettings;
import org.eclipse.rdf4j.rio.turtle.TurtleParser;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.http.client.SPARQLProtocolSession;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.LiteralUtilException;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.Query;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResult;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.parser.sparql.TupleExprBuilder;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultParser;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;

import com.explordf.dto.ConnectionDto;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;

/**
 * 
 * @author Andreas Niederquell
 * 
 *         Used to access any SPARQL-Endpoint.
 */
@org.springframework.stereotype.Repository
public class SparqlEndpointDaoImpl implements ExploRDFDao {

	private static final Logger logger = LoggerFactory.getLogger(SparqlEndpointDaoImpl.class);

	private final String tripleStoreServer = "SPARQL-Endpoint";

	// muss dann weg
	String endpoint = "http://localhost:5820/sachbegriffeDB/query";

	private Repository repo;

	@PreDestroy
	private void close() {
		if (this.repo != null && this.repo.isInitialized()) {
			this.repo.shutDown();
		}
	}

	@Override
	public List<TripleDto> simpleSearch(String term, boolean broaderSearch) {
		double start = new Date().getTime();
		logger.info("Method simpleSearch() in SparqlEndpointDaoImpl entered.");

		List<TripleDto> resultDto = new LinkedList<>();

//		try (RepositoryConnection con = repo.getConnection()) {
//
//			ValueFactory factory = repo.getValueFactory();
//
//			String queryString;
//
//			String object = term;
//
//			if (broaderSearch) {
//				System.out.println("broaderSearch = true");
//				queryString = "select ?s ?p ?o where {filter(regex(?o, \"" + term + "\", \"i\")).?s ?p ?o} order by ?s";
//			} else {
//				System.out.println("broaderSearch = false");
//				queryString = "SELECT ?s ?p ?o WHERE {filter(?o = \"" + term + "\"). ?s ?p ?o}";
//			}
//
//			Literal obj = factory.createLiteral(object);
//
//			try (RepositoryResult<Statement> statements = con.getStatements(null, null, obj)) {
//				while (statements.hasNext()) {
//
//					Statement st = statements.next();
////					String s = "" + st.getSubject() + " " + st.getPredicate() + " " + st.getObject();
////					System.out.println(s);
//					resultDto.add(new TripleDto(st.getSubject().toString(), st.getPredicate().toString(),
//							st.getObject().toString()));
//				}
//			}
//
//		} 

		try (RepositoryConnection con = repo.getConnection()) {

			String queryString;

			if (broaderSearch) {
				queryString = "select ?s ?p ?o where {filter(regex(?o, \"" + term + "\", \"i\")).?s ?p ?o} order by ?s";
			} else {
				queryString = "SELECT ?s ?p ?o WHERE {filter(?o = \"" + term + "\"). ?s ?p ?o}";
			}

			List<BindingSet> resultList;
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);

			try (TupleQueryResult result = tupleQuery.evaluate()) {
				resultList = QueryResults.asList(result);
			}

			for (BindingSet bindingSet : resultList) {

				Value subject = bindingSet.getValue("s");
				Value predicate = bindingSet.getValue("p");
				Value object = bindingSet.getValue("o");

				TripleDto dto = new TripleDto(subject.toString(), predicate.toString(), object.toString());
				resultDto.add(dto);
			}

		} catch (RDF4JException e) {
			e.printStackTrace();
			logger.error("Something´s rotten in the state of Denmark");
		}
		double end = new Date().getTime();
		System.out.println((end - start) / 1000);

		return resultDto;

	}

	@Override
	public List<TripleDto> getSubject(String subject) {
		logger.info("Method getSubject() entered.");
		List<TripleDto> resultDto = new LinkedList<>();

		// resultDto = getSubWithValueFactory(subject);
		resultDto = getSubWithTupleQuery(subject);
		// resultDto = getSubTest(subject);

		return resultDto;
	}

	public List<TripleDto> getSubTest(String subject) {
		List<TripleDto> resultDto = new LinkedList<>();

		ValueFactory factory = repo.getValueFactory();

		IRI subj = factory.createIRI(subject);
		RDFParser parser = Rio.createParser(RDFFormat.NTRIPLES, factory);

		IRI resource = MyValueFactory.getInstance().createIRI(subject);
		Model retrievedStatements = new LinkedHashModel();
		RDFLoader rdfLoader = new RDFLoader(new ParserConfig(), MyValueFactory.getInstance());
		StatementCollector statementCollector = new StatementCollector(retrievedStatements);
		try {
			rdfLoader.load(new URL(resource.stringValue()), null, null, statementCollector);

		} catch (RDFParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RDFHandlerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int i = 0;
		for (Statement st : retrievedStatements) {
			System.out.println(st.getSubject() + " " + st.getPredicate() + " " + st.getObject());
			resultDto.add(
					new TripleDto(st.getSubject().toString(), st.getPredicate().toString(), st.getObject().toString()));
			i++;
		}
		System.out.println("Statements: " + i);

//		try (RepositoryConnection con = repo.getConnection()) {
////			con.setParserConfig(parser.getParserConfig());
//
////			ParserConfig config = con.getParserConfig();
//			ParserConfig config = Rio.createParser(RDFFormat.NTRIPLES, factory).getParserConfig();
//			config.addNonFatalError(NTriplesParserSettings.FAIL_ON_NTRIPLES_INVALID_LINES);
//			config.addNonFatalError(TurtleParserSettings.CASE_INSENSITIVE_DIRECTIVES);
//			config.addNonFatalError(BasicParserSettings.VERIFY_URI_SYNTAX);
//			config.addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);
//			config.addNonFatalError(BasicParserSettings.VERIFY_LANGUAGE_TAGS);
//			config.addNonFatalError(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES);
//			config.addNonFatalError(BasicParserSettings.NORMALIZE_LANGUAGE_TAGS);
//			config.addNonFatalError(BasicParserSettings.LANGUAGE_HANDLERS);
//			config.addNonFatalError(BasicParserSettings.LARGE_LITERALS_HANDLING);
//			config.addNonFatalError(BasicParserSettings.NAMESPACES);
//			config.addNonFatalError(BasicParserSettings.NORMALIZE_DATATYPE_VALUES);
//			config.addNonFatalError(BasicParserSettings.PRESERVE_BNODE_IDS);
//			config.addNonFatalError(BasicParserSettings.SKOLEMIZE_ORIGIN);
//			config.addNonFatalError(BasicParserSettings.VERIFY_RELATIVE_URIS);
//			
//
//			config.set(BasicParserSettings.VERIFY_URI_SYNTAX, false);
//			config.set(BasicParserSettings.VERIFY_LANGUAGE_TAGS, false);
//			config.set(BasicParserSettings.VERIFY_DATATYPE_VALUES, false);
//			config.set(BasicParserSettings.VERIFY_LANGUAGE_TAGS, false);
//
//			con.setParserConfig(config);
//			
//			Set<RioSetting<?>> set = con.getParserConfig().getNonFatalErrors();
//			
//			System.out.println("non fatal errors:");
//			for (RioSetting<?> object : set) {
//				System.out.println(object.getKey());
//			}
//			
//			
//			
//			try (RepositoryResult<Namespace> statements = con.getNamespaces()) {
//				
//				System.out.println("getSubTest(): ");
//				while (statements.hasNext()) {
//
//					Namespace st = statements.next();
//					String s = st.getName();
//					System.out.println(s);
//					
////					String s = "" + st.getSubject() + " " + st.getPredicate() + " " + st.getObject();
////					System.out.println(s);
////					resultDto.add(new TripleDto(st.getSubject().toString(),
////							st.getPredicate().toString(), st.getObject().toString()));
//				}
//			}

//		} 
//		catch (RDF4JException e) {
//			e.printStackTrace();
//			
//		}

		return resultDto;
	}

	public List<TripleDto> getSubTest2(String subject) {
		List<TripleDto> resultDto = new LinkedList<>();

		ValueFactory factory = repo.getValueFactory();

		try (RepositoryConnection con = repo.getConnection()) {

			Set<RioSetting<?>> set = con.getParserConfig().getNonFatalErrors();

			System.out.println("non fatal errors:");
			for (RioSetting<?> object : set) {
				System.out.println(object.getKey());
			}
			IRI subj = null;

			try (RepositoryResult<Statement> statements = con.getStatements(subj, null, null)) {
				while (statements.hasNext()) {

					Statement st = statements.next();
					String s = "" + st.getSubject() + " " + st.getPredicate() + " " + st.getObject();
					System.out.println(s);
					resultDto.add(new TripleDto(st.getSubject().toString(), st.getPredicate().toString(),
							st.getObject().toString()));
				}
			}

		} catch (RDF4JException e) {
			e.printStackTrace();

		}

		IRI subj = factory.createIRI(subject);
		RDFParser parser = Rio.createParser(RDFFormat.NTRIPLES, factory);

		IRI resource = MyValueFactory.getInstance().createIRI(subject);
		Model retrievedStatements = new LinkedHashModel();
		RDFLoader rdfLoader = new RDFLoader(new ParserConfig(), MyValueFactory.getInstance());
		StatementCollector statementCollector = new StatementCollector(retrievedStatements);
		try {
			rdfLoader.load(new URL(resource.stringValue()), null, null, statementCollector);
		} catch (RDFParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RDFHandlerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int i = 0;
		for (Statement st : retrievedStatements) {
			System.out.println(st.getSubject() + " " + st.getPredicate() + " " + st.getObject());
			resultDto.add(
					new TripleDto(st.getSubject().toString(), st.getPredicate().toString(), st.getObject().toString()));
			i++;
		}
		System.out.println("Statements: " + i);

//		try (TupleQueryResult result = tupleQuery.evaluate()) {
//			
//			while (result.hasNext()) {
//				BindingSet bindingSet = result.next();
//				resultDto.add(new TripleDto(bindingSet.getValue("s").toString(),
//						bindingSet.getValue("p").toString(), bindingSet.getValue("o").toString()));
//			}
//		}

		return resultDto;
	}

	public List<TripleDto> getSubWithValueFactory(String subject) {
		List<TripleDto> resultDto = new LinkedList<>();

		ValueFactory factory = repo.getValueFactory();

		IRI subj = factory.createIRI(subject);

		try (RepositoryConnection con = repo.getConnection()) {

			try (RepositoryResult<Statement> statements = con.getStatements(subj, null, null)) {
				while (statements.hasNext()) {

					Statement st = statements.next();
					String s = "" + st.getSubject() + " " + st.getPredicate() + " " + st.getObject();
					System.out.println(s);
					resultDto.add(new TripleDto(st.getSubject().toString(), st.getPredicate().toString(),
							st.getObject().toString()));
				}
			}

		} catch (RDF4JException e) {
			e.printStackTrace();

		}

		return resultDto;
	}

	public List<TripleDto> getSubWithTupleQuery(String subject) {
		List<TripleDto> resultDto = new LinkedList<>();

		try (RepositoryConnection con = repo.getConnection()) {
			String subjt = "<" + subject + ">";
			System.out.println(subjt);
//			String queryString = "SELECT ?s ?p ?o WHERE {<" + subject + "> ?p ?o. ?s ?p ?o}";

//			String queryString = "SELECT ( "+ subjt +" as ?s) ?p ?o ?g { "
//					+ "{ "+subjt+" ?p ?o } union { graph ?g { " + subjt + " ?p ?o } } }";

//			String queryString = "SELECT ( "+ subjt +" as ?s) ?p ?o WHERE { "
//					+subjt+" ?p ?o }";

			String queryString = "SELECT (" + subjt + " as ?s) ?p ?o WHERE { " + subjt + " ?p ?o. "
					+ "FILTER(!isLiteral(?o) || langMatches(lang(?o), \"EN\") || langMatches(lang(?o), \"\"))}";

			System.out.println("getSubWithTupleQuery() Method: ");
			List<BindingSet> resultList;
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);

			try (TupleQueryResult result = tupleQuery.evaluate()) {

				while (result.hasNext()) {
					BindingSet bindingSet = result.next();
					resultDto.add(new TripleDto(bindingSet.getValue("s").toString(),
							bindingSet.getValue("p").toString(), bindingSet.getValue("o").toString()));
				}
//				resultList = QueryResults.asList(result);
			}

//			for (BindingSet bindingSet : resultList) {
//				
//				Value subj = bindingSet.getValue("s");
//				Value pred = bindingSet.getValue("p");
//				Value obj = bindingSet.getValue("o");
//				
//				TripleDto dto = new TripleDto(subj.toString(), pred.toString(), obj.toString());
//				System.out.println(subj.toString() + " " + pred.toString() + " " + obj.toString());
//				resultDto.add(dto);
//			}

//		} catch (RDF4JException e) {
//			logger.error("Something´s rotten in the state of Denmark");
		}

		return resultDto;
	}

	@Override
	public List<PredicateDto> getPredicates() {
		logger.info("Method getPredicates() entered.");
		List<PredicateDto> resultDto = new LinkedList<>();

		boolean gotAllPredicates = false;

		int offset = 30000;
		int limit = 1000;
		while (!gotAllPredicates) {
			int resultNum = 0;
			try (RepositoryConnection con = repo.getConnection()) {

//				String queryString = "select ?p where {?s ?p ?o} limit "+limit+" offset " + offset;
				
				String queryString = "";
				
				TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
				
				try (TupleQueryResult result = tupleQuery.evaluate()) {
					while (result.hasNext()) {
						BindingSet bindingSet = result.next();
						resultNum += 1;
						resultDto.add(new PredicateDto(bindingSet.getValue("p").toString(), false, false));
					}
				}
				
			}
			if (resultNum < limit) {
				gotAllPredicates = true;
			}
			offset += resultNum;
			System.out.println("Offset: " + offset);
		}
//		return resultDto;
		return null;
	}

	@Override
	public List<PredicateDto> getPredicatesList(String listName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String savePredicatesList(List<PredicateDto> predicateDtoList, String listName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllPredicatesLists() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		return tripleStoreServer;
	}

	@Override
	public boolean getConnected(ConnectionDto connDto) {

		logger.info("Method getConnected() in SparqlEndpointDaoImpl entered.");

		boolean connected = false;

		System.out.println("server: " + connDto.getTripleStoreServer() + ", url: " + connDto.getTripleStoreUrl()
				+ ", repo: " + connDto.getTripleStoreRepo() + ", username: " + connDto.getTripleStoreUserName()
				+ ", password: " + connDto.getTripleStorePassword());
//		Repository repo = new SPARQLRepository(connDto.getTripleStoreUrl());

		Repository repo = new MyRepository(connDto.getTripleStoreUrl());

		if (connDto.getTripleStoreUserName() != "" && connDto.getTripleStorePassword() != "") {
			((MyRepository) repo).setUsernameAndPassword(connDto.getTripleStoreUserName(),
					connDto.getTripleStorePassword());
		}

		repo.initialize();
		try (RepositoryConnection conn = repo.getConnection()) {
			String queryString = "SELECT * WHERE {?s ?p ?o} LIMIT 1";
			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			try (TupleQueryResult result = tupleQuery.evaluate()) {
				System.out.println("TupleQueryResult");
				connected = true;
			}
		} catch (RDF4JException e) {
			logger.error("An RDF4JException occured while trying to connect to " + connDto.getTripleStoreUrl() + ".");
		}

		if (connected) {
			if (this.repo != null && this.repo.isInitialized()) {
				this.repo.shutDown();
			}
			this.repo = repo;
		}
		return connected;
	}

}
