package com.explordf.dao.impl_rdf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.annotation.SessionScope;

import com.explordf.dao.ExploRDFDao;
import com.explordf.dto.ConnectionDto;
import com.explordf.dto.EdgeDto;
import com.explordf.dto.NodeDto;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;
import com.explordf.dto.VisualizationDto;

/**
 * Implementation class of the ExploRDFDao interface.
 * The whole communication with the triple store is transacted by this class.
 * 
 * This class has to be created once in every user session. Therefore it is
 * annotated with Spring´s @SessionScope and @Component.  
 * 
 * @author Andreas Niederquell
 *
 */
@SessionScope
@Component
@Qualifier(value = "exploRDFDaoImpl")
@PropertySource({"classpath:connection.properties", "classpath:query.properties", "classpath:explordf.properties"})
public class ExploRDFDaoImpl implements ExploRDFDao {

	// muss dann weg
	String endpoint = "http://localhost:5820/sachbegriffeDB/query";
	String endpointDBPedia = "http://dbpedia.org/sparql"; // graph: http://dbpedia.org
	String endpointRdf4j = "http://localhost:8080/rdf4j-server/repositories/test";
	String stardogServer = "http://localhost:5820";
	String username = "admin", password = "admin";
	String db = "geograficumDB";

	private static final Logger logger = LoggerFactory.getLogger(ExploRDFDao.class);

	@Autowired
	Environment env;

	@Value("${triplestore.server}")
	private String tripleStoreServer;

	@Value("${triplestore.url}")
	private String tripleStoreUrl;

	@Value("${triplestore.repository}")
	private String tripleStoreRepo;

	@Value("${triplestore.graph}")
	private String tripleStoreGraph;

	@Value("${triplestore.username}")
	private String tripleStoreUserName;

	@Value("${triplestore.password}")
	private String tripleStorePassword;
	
	@Value("${query.searchTerm.simple}")
	private String simpleSearchQuery;
	
	@Value("${query.searchTerm.simple.trema}")
	private String simpleSearchTremaQuery;
	
	@Value("${query.searchTerm.broad}")
	private String broadSearchQuery;
	
	@Value("${query.getSubject}")
	private String getSubjectQuery;
	
	@Value("${query.getPredicates}")
	private String getPredicatesQuery;

	@Value("${query.getLabel}")
	private String getLabelQuery;
	
	@Value("${query.getTriples.subject}")
	private String getTriplesSubject;
	
	@Value("${query.getTriples.object}")
	private String getTriplesObject;
	
	@Value("${query.getTriples.subAndObject}")
	private String getTriplesSubAndObject;
	
		
	private final String predicatesRootDir = "temp/exploRDF/predicates/";
	private final String predicateLabelIRI = "http://example.org/label";
	private final String predicateEdgeIRI = "http://example.org/edge";
	
	@Value("${predicates.load.gnd}")
	private boolean loadGNDPredicateList;
	
	@Value("${predicates.load.skos}")
	private boolean loadSKOSPredicateList;
	
	private String predicatesDir;

	private Repository repo;
	private String queryGraph;

	private String currPredicatesListName;
	private List<PredicateDto> currPredicatesList;
	private String vizLabel;
	private List<String> vizEdges;
	
	/**
	 * Method triggered after the creation of this class. Setting connection properties
	 * that are saved in the connection.properties file.
	 */
	@PostConstruct
	private void init() {
		logger.info("PostConstruct init()");
		showConnProps();

		setConnectionProps(new ConnectionDto(tripleStoreUrl, tripleStoreServer, tripleStoreRepo, tripleStoreGraph,
				tripleStoreUserName, tripleStorePassword));
	}
	
	/**
	 * Sets the properties of the visualization, namely the
	 * chosen predicates list and node label.
	 */
	private void setVisualizationProps() {
		vizEdges = new LinkedList<>();
		for (PredicateDto predicateDto : currPredicatesList) {
			if(predicateDto.isLabel()) {
				vizLabel = predicateDto.getPredicate();
			}
			else if(predicateDto.isEdge()) {
				vizEdges.add(predicateDto.getPredicate());
			}
		}
	}

	
	@Override
	public VisualizationDto getNodeRelations(String resource, String predicatesList, int edgeViz, int edgeOffset, int limit) {
				
		if(!predicatesList.equals(currPredicatesListName) || currPredicatesList == null) {
			System.out.println("currPredicatesListName has changed or currPredicatesList is null");
			currPredicatesListName = predicatesList;
			try {
				currPredicatesList = getPredicatesList(predicatesList);
				
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			setVisualizationProps();
		}
		
		
		if(edgeViz == 0) {
			return getOutgoingNodes(resource, edgeOffset, limit);
		} else if (edgeViz == 1) {
			return getIncomingNodes(resource, edgeOffset, limit);
		} else {
			return getInAndOutgoingNodes(resource, edgeOffset, limit);
		}

	}
	
	/**
	 * Gets a VisualizationDto object containing the outgoing nodes and edges.
	 * 
	 * @param subject IRI of the requested node.
	 * @param edgeOffset Offset to begin the search at.
	 * @param limit Limit of the requested triples.
	 * @return List of VisualizationDto objects containing the nodes and edges.
	 */
	private VisualizationDto getOutgoingNodes(String subject, int edgeOffset, int limit) {
		VisualizationDto viz = new VisualizationDto();
		
		int localOffset = 0;
		int localLimit = 9900; // Maximum results in DBpedia 10000.
		int reachedLimit = limit;
		int reachedOffset = edgeOffset;
		boolean searchDone = false;
		
		while(reachedLimit > 0 && !searchDone) {
			List<TripleDto> nodeData = getSubjectTriples(subject, localOffset * localLimit, localLimit);
			
			localOffset++;
			System.out.println("nodeData " + nodeData.size());
			
			if(nodeData.isEmpty()) {
				searchDone = true;
			}
			
			for (TripleDto tripleDto : nodeData) {
				
				String obj = tripleDto.getObject();
				String pred = tripleDto.getPredicate();
				
				ValueFactory factory = SimpleValueFactory.getInstance();
				String predIRI = factory.createIRI(pred).getLocalName();
				
				if(vizEdges.contains(pred)) {
					
					String nodeLabel = getNodeLabel(obj, vizLabel);
					
					if(nodeLabel != null) {
						if(!(reachedOffset > 0)) {
							viz.addNode(new NodeDto(obj, nodeLabel, subject));
							viz.addEdge(new EdgeDto(subject, obj, predIRI));
							reachedLimit--;
						} else {
							reachedOffset--;
						}
	
					}	
										
				}
				if(!(reachedLimit > 0)) {
					break;
				}
		
			}
		}
		
	return viz;
	}
	
	/**
	 * Gets a VisualizationDto object containing the incoming nodes and edges.
	 * 
	 * @param object IRI of the requested node.
	 * @param edgeOffset Offset to begin the search at.
	 * @param limit Limit of the requested triples.
	 * @return List of VisualizationDto objects containing the nodes and edges.
	 */
	private VisualizationDto getIncomingNodes(String object, int edgeOffset, int limit) {
		
		VisualizationDto viz = new VisualizationDto();
		
		int localOffset = 0;
		int localLimit = 9900; // Maximum results in DBpedia 10000.
		int reachedLimit = limit;
		int reachedOffset = edgeOffset;
		boolean searchDone = false;
		
		while(reachedLimit > 0 && !searchDone) {
			List<TripleDto> nodeData = getObjectTriples(object, localOffset * localLimit, localLimit);
			
			localOffset++;
			System.out.println("nodeData " + nodeData.size());
			
			if(nodeData.isEmpty()) {
				searchDone = true;
			}
			
			for (TripleDto tripleDto : nodeData) {
				
				String subj = tripleDto.getSubject();
				String pred = tripleDto.getPredicate();
				
				ValueFactory factory = SimpleValueFactory.getInstance();
				String predIRI = factory.createIRI(pred).getLocalName();
				
				if(vizEdges.contains(pred)) {
					
					String nodeLabel = getNodeLabel(subj, vizLabel);
					
					if(nodeLabel != null) {
						if(!(reachedOffset > 0)) {
							viz.addNode(new NodeDto(subj, nodeLabel, object));
							viz.addEdge(new EdgeDto(subj, object, predIRI));
							reachedLimit--;
						} else {
							reachedOffset--;
						}
	
					}	
										
				}
				if(!(reachedLimit > 0)) {
					break;
				}
		
			}
			
		}
		
	return viz;
	}
	
	
	/**
	 * Gets a VisualizationDto object containing the nodes and edges.
	 * 
	 * @param resource IRI of the requested node.
	 * @param edgeOffset Offset to begin the search at.
	 * @param limit Limit of the requested triples.
	 * @return List of VisualizationDto objects containing the nodes and edges.
	 */
	private VisualizationDto getInAndOutgoingNodes(String resource, int edgeOffset, int limit) {
		
		VisualizationDto viz = new VisualizationDto();
		
		int localOffset = 0;
		int localLimit = 9900; // Maximum results in DBpedia 10000.
		int reachedLimit = limit;
		int reachedOffset = edgeOffset;
		boolean searchDone = false;
		
		while(reachedLimit > 0 && !searchDone) {
			List<TripleDto> nodeData = getResourceTriples(resource, localOffset * localLimit, localLimit);
			localOffset++;
			System.out.println("nodeData " + nodeData.size());
			
			if(nodeData.isEmpty()) {
				searchDone = true;
			}
			
			for(TripleDto tripleDto : nodeData) {
				String subj = tripleDto.getSubject();
				String pred = tripleDto.getPredicate();
				String obj = tripleDto.getObject();
				
				ValueFactory factory = SimpleValueFactory.getInstance();
				String predIRI = factory.createIRI(pred).getLocalName();
				
				String res = null;
				
				if(resource.equals(subj)) {
					res = obj;
				} else if (resource.equals(obj)) {
					res = subj;
				} else {
					System.out.println("hier: " + resource + " " + subj + " " + pred + " " + obj);
				}
				
				if(vizEdges.contains(pred)) {
					
					String nodeLabel = getNodeLabel(res, vizLabel);
					
					if(nodeLabel != null) {
						if(!(reachedOffset > 0)) {
							viz.addNode(new NodeDto(res, nodeLabel, resource));
							viz.addEdge(new EdgeDto(subj, obj, predIRI));
							reachedLimit--;
						} else {
							reachedOffset--;
						}
						
					} 
										
				}
				if(!(reachedLimit > 0)) {
					break;
				}
			}
			
			
		}
	
		System.out.println("viz groesse: " + viz.getNodes().size());
		
	return viz;
	}
	
	/**
	 * Gets a certain amount of RDF triples with the requested node´s IRI as subject and object.
	 * 
	 * @param resource IRI of the requested node.
	 * @param edgeOffset Offset to begin the search at.
	 * @param limit Limit of the requested triples.
	 * @return List of TripleDto objects containing the RDF triples.  
	 */
	private List<TripleDto> getResourceTriples(String resource, int edgeOffset, int limit) {
		List<TripleDto> resultDto = new LinkedList<>();
		
		try (RepositoryConnection con = repo.getConnection()) {

			String queryString = String.format(getTriplesSubAndObject, resource, queryGraph, resource, resource, queryGraph, resource, limit, edgeOffset );
			
			System.out.println(queryString);
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);

			try (TupleQueryResult result = tupleQuery.evaluate()) {

				while (result.hasNext()) {
					BindingSet bindingSet = result.next();
					resultDto.add(new TripleDto(bindingSet.getValue("s").toString(),
							bindingSet.getValue("p").toString(), bindingSet.getValue("o").toString()));
				}
			}
		}
		
		return resultDto;
	}

	/**
	 * Gets a certain amount of RDF triples with the requested node´s IRI as object.
	 * 
	 * @param object IRI of the requested node.
	 * @param edgeOffset Offset to begin the search at.
	 * @param limit Limit of the requested triples.
	 * @return List of TripleDto objects containing the RDF triples.  
	 */
	private List<TripleDto> getObjectTriples(String object, int edgeOffset, int limit) {
		List<TripleDto> resultDto = new LinkedList<>();
		
		try (RepositoryConnection con = repo.getConnection()) {

			String queryString = String.format(getTriplesObject, object, queryGraph, object, limit, edgeOffset );
			
			System.out.println(queryString);
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);

			try (TupleQueryResult result = tupleQuery.evaluate()) {

				while (result.hasNext()) {
					BindingSet bindingSet = result.next();
					resultDto.add(new TripleDto(bindingSet.getValue("s").toString(),
							bindingSet.getValue("p").toString(), bindingSet.getValue("o").toString()));
				}
			}
		}
		
		return resultDto;
	}
	
	/**
	 * Gets a certain amount of RDF triples with the requested node´s IRI as subject.
	 * 
	 * @param subject IRI of the requested node.
	 * @param edgeOffset Offset to begin the search at.
	 * @param limit Limit of the requested triples.
	 * @return List of TripleDto objects containing the RDF triples.  
	 */
	private List<TripleDto> getSubjectTriples(String subject, int edgeOffset, int limit) {
		double start = new Date().getTime();
		List<TripleDto> resultDto = new LinkedList<>();

		try (RepositoryConnection con = repo.getConnection()) {

			String queryString = String.format(getTriplesSubject, subject, queryGraph, subject, limit, edgeOffset );
			
			System.out.println(queryString);
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);

			try (TupleQueryResult result = tupleQuery.evaluate()) {

				while (result.hasNext()) {
					BindingSet bindingSet = result.next();
					resultDto.add(new TripleDto(bindingSet.getValue("s").toString(),
							bindingSet.getValue("p").toString(), bindingSet.getValue("o").toString()));
				}
			}
		}

		double end = new Date().getTime();
		System.out.println("Query time: " + (end - start) / 1000);
		return resultDto;
	}


	/**
	 * Queries the triple store for the requested node´s label.
	 * 
	 * @param nodeId IRI of the node.
	 * @param label IRI of the predicate to be visualized as the node label.
	 * @return RDF triple object´s name if any exists.
	 */
	private String getNodeLabel(String nodeId, String label) {
		System.out.println(nodeId);
		if (nodeId.indexOf(':') < 0 || nodeId.startsWith("\"")) {
			System.out.println(nodeId + " is not an IRI");
			return null;
		}
		String resultStr = null;
		try (RepositoryConnection con = repo.getConnection()) {
			
			String queryString = String.format(getLabelQuery, "<" + nodeId + ">", "<" + label + ">", queryGraph, "<" + nodeId + ">", "<" + label + ">");
			
			System.out.println(queryString);
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);

			try (TupleQueryResult result = tupleQuery.evaluate()) {

				while (result.hasNext()) {
					BindingSet bindingSet = result.next();
					resultStr = "" + bindingSet.getValue("o");
				}
			}

		} catch (RDF4JException e) {
			logger.warn("Error occured while querying the label of the node " + nodeId);
		}
		
		return resultStr != null ? resultStr : nodeId;
		
	}
	
		
	@Override
	public VisualizationDto getNode(String subject, String predicatesList) {
		
		VisualizationDto viz = new VisualizationDto();
		
		if(!predicatesList.equals(currPredicatesListName) || currPredicatesList == null) {
			System.out.println("currPredicatesListName has changed or currPredicatesList is null");
			currPredicatesListName = predicatesList;
			try {
				currPredicatesList = getPredicatesList(predicatesList);
				
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			setVisualizationProps();
		}
		
		String label = getNodeLabel(subject, vizLabel);
		
		if(label != null) {
			viz.addNode(new NodeDto(subject, label));
		} else {
			return null;
		}
		
		return viz;
	}
	
	
	@Override
	public List<TripleDto> searchTerm(String term, boolean broaderSearch) {
		logger.info("Method searchTerm() in entered.");
		double start = new Date().getTime();

		List<TripleDto> resultDto = new LinkedList<>();
		System.out.println(term);
		term = term.replace("\"", "\\\"");
		
		if(broaderSearch) {
			resultDto = broaderSearch(term);
		} else {
			resultDto = simpleSearch(term);
		}

		double end = new Date().getTime();
		System.out.println((end - start) / 1000);
		return resultDto;
	}
	
	/**
	 * Gets the RDF triples of the simple search.
	 * 
	 * @param term Searching term.
	 * @return List of TripleDto objects containing the RDF triples. 
	 */
	private List<TripleDto> simpleSearch(String term) {
		List<TripleDto> resultDto = new LinkedList<>();
		
		// Encode umlauts (ÄÖÜäöü) as chars and '' (Needed for GND). 
		String trema = term.replace("Ä", "A"+ (char)776)
				.replace("Ö", "O" + (char)776).replace("Ü", "U" + (char)776)
				.replace("ä", "a"+ (char)776).replace("ö", "o" + (char)776)
				.replace("ü", "u" + (char)776);
		
		String queryString;
		if(trema.length() > term.length()) {
			queryString = String.format(simpleSearchTremaQuery, queryGraph, term, term, term, trema, trema, trema);
		} else {
			queryString = String.format(simpleSearchQuery, queryGraph, term, term, term);
		}
		
		try (RepositoryConnection con = repo.getConnection()) {

			System.out.println();
			System.out.println(queryString);
			System.out.println();

			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			

			try (TupleQueryResult result = tupleQuery.evaluate()) {
				while (result.hasNext()) {
					BindingSet bindingSet = result.next();
					resultDto.add(new TripleDto(bindingSet.getValue("s").toString(),
							bindingSet.getValue("p").toString(), bindingSet.getValue("o").toString()));
				}
			} catch (RDF4JException e) {
				logger.warn("An exception occured while evaluating the query.");
			}

		} catch (RDF4JException e) {
			logger.warn("An excecption occured while connecting to the repository.");
		}

		return resultDto;
		
	}
	
	/**
	 * Gets the RDF triples of the broad search.
	 * 
	 * @param term Searching term.
	 * @return List of TripleDto objects containing the RDF triples. 
	 */
	private List<TripleDto> broaderSearch(String term) {
		
		List<TripleDto> resultDto = new LinkedList<>();

		boolean gotAllResultTriples = false;
		int resultLimit = 10000;
		int offset = 0;
		final int limit = 10000000;

		while(!gotAllResultTriples && resultLimit > 0) {
			try (RepositoryConnection con = repo.getConnection()) {
				
				String queryString = String.format(broadSearchQuery, queryGraph, offset,  term, queryGraph, limit, offset);
				TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
				
				System.out.println(queryString);
				System.out.println("number of results: " + (10000 - resultLimit));

				try (TupleQueryResult result = tupleQuery.evaluate()) {
					if(result.hasNext()) {
						result.next();
					} else {
						gotAllResultTriples = true;
					}
					while (result.hasNext()) {
						BindingSet bindingSet = result.next();
						resultDto.add(new TripleDto(bindingSet.getValue("s").toString(),
								bindingSet.getValue("p").toString(), bindingSet.getValue("o").toString()));
						resultLimit--;
					}
				} catch (RDF4JException e) {
					logger.warn("An exception occured while evaluating the query.");
				}

			} catch (RDF4JException e) {
				logger.warn("An excecption occured while connecting to the repository.");
				break;
			}
			offset += limit;
		}
			
		
		return resultDto;
	}

	@Override
	public List<TripleDto> getSubject(String subject) {
		return getSubjectTriples(subject, 0, 1000);
	}

	
	@Override
	public List<PredicateDto> getPredicates() {
		logger.info("Method getPredicates() entered.");
		double start = new Date().getTime();
		List<PredicateDto> resultDto = new LinkedList<>();

		boolean gotAllPredicates = false;

		
		int dbpediaMaxLimit = 9900; // Maximum results-triples from DBPpedia 10000.
		int maxLimit = 80000000;
		int offset = 0;
		int limit = 10000000;

		List<String> predicatesAsString = new LinkedList<>();

		while (!gotAllPredicates) {
			int resultNum = 0;
			List<String> queryResult = new LinkedList<>();
			try (RepositoryConnection con = repo.getConnection()) {

				String queryString = String.format(getPredicatesQuery, queryGraph, limit, offset);
				System.out.println(queryString);

				TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);

				try (TupleQueryResult result = tupleQuery.evaluate()) {
					while (result.hasNext()) {
						BindingSet bindingSet = result.next();
						resultNum += 1;
						queryResult.add(bindingSet.getValue("p").toString());
					}
				}

			}
			System.out.println("resultNum : " + resultNum);
			if (resultNum == 0) {
				gotAllPredicates = true;
			}
			if (resultNum > dbpediaMaxLimit) {
				limit = dbpediaMaxLimit;
			} else {
				for (String qResult : queryResult) {
					if (!predicatesAsString.contains(qResult)) {
						predicatesAsString.add(qResult);
					}
				}

				offset += limit;

				limit = limit * 2 > maxLimit ? maxLimit : limit * 2;
			}

			double meanTime = new Date().getTime();
			System.out.println("Offset: " + offset + " Mean time : " + (meanTime - start) / 1000);
		}
		double end = new Date().getTime();
		System.out.println("Query time: " + (end - start) / 1000);

		System.out.println("Predicates number: " + predicatesAsString.size());
		for (String string : predicatesAsString) {
			resultDto.add(new PredicateDto(string, false, false));
		}
		
		return resultDto;
	}

	@Override
	public List<PredicateDto> getPredicatesList(String listName) throws IOException {
		logger.info("getPredicatesList() method entered.");
		double start = new Date().getTime();
		
		List<PredicateDto> resultDto = new LinkedList<>();
		
		ValueFactory factory = SimpleValueFactory.getInstance();
		Model model = new LinkedHashModel();

		File dir = new File(predicatesRootDir + predicatesDir);
		
		File tmp = new File(dir, listName + ".ttl");
		
	
		FileInputStream inputStream = new FileInputStream(tmp);
		RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
		rdfParser.setRDFHandler(new StatementCollector(model));
		try {
			rdfParser.parse(inputStream, tripleStoreUrl);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			inputStream.close();
		}
		
		
		IRI label = factory.createIRI(predicateLabelIRI);
		Literal trueLiteral = factory.createLiteral(true);
		
		IRI edge = factory.createIRI(predicateEdgeIRI);
		
		Resource labelSubj = Models.subject(model.filter(null, label, trueLiteral))
				.orElse(null);
		
		
		
		
		for (Statement statement : model.filter(null, edge, null)) {
			
			Resource edgeSubj = statement.getSubject();
			
			String predicateName = edgeSubj.stringValue();
			boolean predicateLabel = edgeSubj.equals(labelSubj);
			boolean predicateEdge = trueLiteral.equals(statement.getObject());
			
			resultDto.add(new PredicateDto(predicateName, predicateLabel, predicateEdge));
			
		}
		
		
		
		double end = new Date().getTime();
		System.out.println("Query time (getPredicatesList): " + (end - start) / 1000);
		return resultDto;
	}

	@Override
	public String savePredicatesList(List<PredicateDto> predicatesDtoList, String listName) throws IOException {
		logger.info("savePredicatesList() method entered.");

		if(listName == null || listName.isEmpty()) {
			logger.warn("listName is null.");
			return null;
		}
		
		currPredicatesList = null;
		currPredicatesListName = null;
		
		String result = null;

		File dir = new File(predicatesRootDir + predicatesDir);
		
		File tmp = new File(dir, listName + ".ttl");
		
		if (tmp.exists()) {
			logger.info("Overwriting " + listName + " in predicates directory.");
		}
		
		if(tmp.createNewFile()) {
			logger.info("File " + listName + " in predicates directory created.");
		} else {
			logger.warn("Couldn´t create " + listName + " in predicates directory.");
		}
		

		FileOutputStream out = new FileOutputStream(tmp);

		ValueFactory factory = SimpleValueFactory.getInstance();

		Model model = new LinkedHashModel();

		for (PredicateDto predicateDto : predicatesDtoList) {

			IRI pred = factory.createIRI(predicateDto.getPredicate());
			IRI label = factory.createIRI(predicateLabelIRI);
			IRI edge = factory.createIRI(predicateEdgeIRI);
			Literal labelValue = factory.createLiteral(predicateDto.isLabel());
			Literal edgeValue = factory.createLiteral(predicateDto.isEdge());

			model.add(pred, label, labelValue);
			model.add(pred, edge, edgeValue);

		}

		try {
			Rio.write(model, out, RDFFormat.TURTLE);
			result = listName;
		} catch (RDF4JException e) {
			logger.warn("Couldn´t write Predicates into file.");
		}
		finally {
			out.close();
		}

		return result;
	}

	@Override
	public List<String> getAllPredicatesLists() {
		logger.info("getAllPredicatesLists() method entered.");

		File predicatesFolder = new File(predicatesRootDir + predicatesDir);
		if (predicatesFolder.exists()) {
			List<String> predicatesList = new LinkedList<String>();
			String[] files = predicatesFolder.list();

			for (int i = 0; i < files.length; i++) {
				if (files[i].endsWith(".ttl")) {
					predicatesList.add(files[i].substring(0, files[i].length()-4));
				}
			}
			
			return predicatesList;
		} else {
			logger.info("predicates folder doesn´t exist.");
			return null;
		}
	}
	
	@Override
	public ConnectionDto getConnectionProps() {
		if (repo != null) {
			ConnectionDto connDto = new ConnectionDto();
			connDto.setTripleStoreUrl(tripleStoreUrl);
			connDto.setTripleStoreRepo(tripleStoreRepo);
			return connDto;
		} else {
			return null;

		}
	}

	@Override
	public ConnectionDto setConnectionProps(ConnectionDto connDto) {
		logger.info("Method setConnectionProps() entered.");
		
		try {
			new URL(connDto.getTripleStoreUrl());
		} catch (MalformedURLException e) {
			logger.warn("Malformed URL.");
			return null;
		}
		Repository repo = ExploRDFRepositoryServer.getRepository(connDto);
		if (repo != null) {
			System.out.println("repo != null");
			shutDown();
			this.repo = repo;
			currPredicatesList = null;
			currPredicatesListName = null;

			tripleStoreUrl = connDto.getTripleStoreUrl();
			tripleStoreServer = connDto.getTripleStoreServer();
			tripleStoreRepo = connDto.getTripleStoreRepo() != null ? connDto.getTripleStoreRepo() : "";
			tripleStoreGraph = connDto.getTripleStoreGraph() != null ? connDto.getTripleStoreGraph() : "";
			tripleStoreUserName = connDto.getTripleStoreUserName() != null ? connDto.getTripleStoreUserName() : "";
			tripleStorePassword = connDto.getTripleStorePassword() != null ? connDto.getTripleStorePassword() : "";

			saveConnProps();

			queryGraph = !connDto.getTripleStoreGraph().isEmpty() ? "from <" + connDto.getTripleStoreGraph() + ">" : "";

			createPredicatesDir(connDto.getTripleStoreServer(), connDto.getTripleStoreUrl(), connDto.getTripleStoreRepo());

			return getConnectionProps();
		} else {
			logger.warn("Could not connect to Endpoint: " + connDto.getTripleStoreUrl() 
			+ ", Repository: " + connDto.getTripleStoreRepo()
			+ ", Graph: " + connDto.getTripleStoreGraph() + ".");
			return null;
		}

	}

	/**
	 * Creates a folder to for the data set the application is currently connected to.
	 *  
	 * @param tripleStoreServer Triple Store type (RDF4J, Stardog, SPARQL-Endpoint).
	 * @param tripleStoreUrl Triple Store URL.
	 * @param tripleStoreRepo Name of the data set.
	 */
	private void createPredicatesDir(String tripleStoreServer, String tripleStoreUrl, String tripleStoreRepo) {
		
		predicatesDir = tripleStoreServer + "/"
				+ tripleStoreUrl.replaceFirst("http://", "").replaceAll("[\\/:*<>\"?|]", "-") + "/" + tripleStoreRepo;
		System.out.println(predicatesRootDir + predicatesDir);

		File file = new File(predicatesRootDir + predicatesDir);
		if (!file.exists()) {
			if (file.mkdirs()) {
				
				System.out.println("Predicates " + loadGNDPredicateList + " " + loadSKOSPredicateList);
				
				String gndPredicateListName = "gnd.ttl";
				String skosPredicateListName = "skos.ttl";
				
				File gndFile;
				File skosFile;
				try {
					if(loadGNDPredicateList) {
						gndFile = ResourceUtils.getFile("classpath:predicates/" + gndPredicateListName);
						FileCopyUtils.copy(gndFile, new File(file + "/" + gndPredicateListName));
					}
					if(loadSKOSPredicateList) {
						skosFile = ResourceUtils.getFile("classpath:predicates/" + skosPredicateListName);
						FileCopyUtils.copy(skosFile, new File(file + "/" + skosPredicateListName));
					}
					
				} catch (FileNotFoundException e) {
					logger.warn("No such files in resources folder: " + gndPredicateListName + " and " + skosPredicateListName);
				} catch (IOException e) {
					logger.warn("Files " + gndPredicateListName + " and " + skosPredicateListName 
							+ "could not be created in new predicate folder");
				}
	
				logger.info("New predicate folder created.");
			} else {
				logger.info("Couldn´t create new predicate folder.");
			}

		} else {
			logger.info("predicate folder already exists.");
		}

	}

	@Override
	public List<String> getSupportedServers() {
		return ExploRDFRepositoryServer.getSupportedServers();
	}

	/**
	 * Saves the connection properties to the application environment.
	 */
	private void saveConnProps() {
		DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
		String filePath = "classpath:connection.properties";
		Properties props = new Properties();

		props.setProperty("triplestore.server", tripleStoreServer);
		props.setProperty("triplestore.url", tripleStoreUrl);
		props.setProperty("triplestore.repository", tripleStoreRepo != null ? tripleStoreRepo : "");
		props.setProperty("triplestore.graph", tripleStoreGraph != null ? tripleStoreGraph : "");
		props.setProperty("triplestore.username", tripleStoreUserName != null ? tripleStoreUserName : "");
		props.setProperty("triplestore.password", tripleStorePassword != null ? tripleStorePassword : "");
		props.setProperty("query.search.simple", simpleSearchQuery);

		try {
			File f = ResourceUtils.getFile(filePath);
			FileOutputStream out = new FileOutputStream(f);
			persister.store(props, out, "db");
		} catch (IOException e) {
			logger.warn("Connection properties couldn´t be saved in the environment.");
		}
		showConnProps();
	}

	/**
	 * Shuts down the repository.
	 */
	private void shutDown() {
		logger.info("Shut down");
		if (this.repo != null && this.repo.isInitialized()) {
			logger.info("Shut down the repo");
			this.repo.shutDown();
		}
	}

	/**
	 * Method for triggering the shutDown()-method before the application shut down.
	 */
	@PreDestroy
	private void close() {
		logger.info("Method predestroy entered");
		shutDown();
	}

	/**
	 * Shows the connection properties of the currently connected triple store.
	 */
	private void showConnProps() {
		logger.info("Connection properties:");
		logger.info("Server: " + tripleStoreServer + " Env: " + env.getProperty("triplestore.server"));
		logger.info("Name: " + tripleStoreUrl + " Env: " + env.getProperty("triplestore.url"));
		logger.info("Repo: " + tripleStoreRepo + " Env: " + env.getProperty("triplestore.repository"));
		logger.info("Graph: " + tripleStoreGraph + " Env: " + env.getProperty("triplestore.graph"));
		logger.info("Username: " + tripleStoreUserName + " Env: " + env.getProperty("triplestore.username"));
		logger.info("Password: " + tripleStorePassword + " Env: " + env.getProperty("triplestore.password"));
		System.out.println();
	}

}
