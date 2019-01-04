package com.explordf.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.eclipse.rdf4j.repository.RepositoryResult;
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
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.annotation.SessionScope;

import com.explordf.dao.ExploRDFDao;
import com.explordf.dto.ConnectionDto;
import com.explordf.dto.EdgeDto;
import com.explordf.dto.NodeDto;
import com.explordf.dto.PredicateDto;
import com.explordf.dto.TripleDto;
import com.explordf.dto.VisualizationDto;

//@org.springframework.stereotype.Repository
@SessionScope
@Component
@Qualifier(value = "exploRDFDaoImpl")
@PropertySource({"classpath:explordf.properties", "classpath:query.properties"})
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
	
	@Value("${query.simpleSearch.simple}")
	private String simpleSearchQuery;
	
	@Value("${query.simpleSearch.broad}")
	private String broadSearchQuery;
	
	@Value("${query.getSubject}")
	private String getSubjectQuery;
	
	@Value("${query.getPredicates}")
	private String getPredicatesQuery;

	@Value("${query.getLabel}")
	private String getLabelQuery;
	
	private final String predicatesRootDir = "temp/exploRDF/predicates/";
	private final String predicateLabelIRI = "http://example.org/label";
	private final String predicateEdgeIRI = "http://example.org/edge";
	
	
	private String predicatesDir;

	private Repository repo;
	private String queryGraph;

	private String currPredicatesListName;
	private List<PredicateDto> currPredicatesList;
	private String vizLabel;
	private List<String> vizEdges;
	
	@PostConstruct
	private void init() {
		logger.info("PostConstruct init()");
		showConnProps();

		setConnectionProps(new ConnectionDto(tripleStoreUrl, tripleStoreServer, tripleStoreRepo, tripleStoreGraph,
				tripleStoreUserName, tripleStorePassword));
	}
	
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
	public VisualizationDto getNodeData(String subject, String predicatesList) {
		
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
		
		
			List<TripleDto> nodeData = getSubject(subject);
			
			for (TripleDto tripleDto : nodeData) {
				
				String obj = tripleDto.getObject();
				String pred = tripleDto.getPredicate();
				
				ValueFactory factory = SimpleValueFactory.getInstance();
				String predIRI = factory.createIRI(pred).getLocalName();
				
				if(vizEdges.contains(pred)) {
					
					String nodeLabel = getNodeLabel(obj, vizLabel);
					
					if(nodeLabel != null) {
						viz.addNode(new NodeDto(obj, nodeLabel));
						viz.addEdge(new EdgeDto(subject, obj, predIRI));
					}	
										
				}
		
			}
			
		return viz;
	}
	
	private String getNodeLabel(String nodeId, String label) {
		if (nodeId.indexOf(':') < 0) {
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
	public List<TripleDto> simpleSearch(String term, boolean broaderSearch) {
		logger.info("Method simpleSearch() in entered.");
		double start = new Date().getTime();

		List<TripleDto> resultDto = new LinkedList<>();

		try (RepositoryConnection con = repo.getConnection()) {

			String queryString;

			if (broaderSearch) {
//				queryString = "select ?s ?p ?o " + queryGraph + " where {filter(regex(?o, \"" + term
//						+ "\", \"i\")).?s ?p ?o} order by ?s";
				queryString = String.format(broadSearchQuery, queryGraph, term);
			} else {
//				queryString = "SELECT ?s ?p ?o WHERE {FILTER(?o = \"" + term + "\"). {SELECT ?s ?p ?o " + queryGraph
//						+ " WHERE {?s ?p \"" + term + "\". ?s ?p ?o}}}";
				queryString = String.format(simpleSearchQuery, term, queryGraph, term);
			}

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
			}

		}

		double end = new Date().getTime();
		System.out.println((end - start) / 1000);
		return resultDto;
	}

	@Override
	public List<TripleDto> getSubject(String subject) {
		double start = new Date().getTime();
		List<TripleDto> resultDto = new LinkedList<>();

		try (RepositoryConnection con = repo.getConnection()) {

			String queryString = String.format(getSubjectQuery, "<" + subject + ">", queryGraph, "<" + subject + ">");
			
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

	@Override
	public List<PredicateDto> getPredicates() {
		logger.info("Method getPredicates() entered.");
		double start = new Date().getTime();
		List<PredicateDto> resultDto = new LinkedList<>();

		boolean gotAllPredicates = false;

		// maximum results in dbpedia 10000
		int maxDbPediaResultNum = 9900;
		int maxLimit = 100000000;
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
			if (resultNum > maxDbPediaResultNum) {
				limit = maxDbPediaResultNum;
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
			e.printStackTrace();
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
		Repository repo = RepositoryServer.getRepository(connDto);
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
			System.out.println("Something´s wrong.");
			return null;
		}

	}

	private void createPredicatesDir(String dirNameServer, String dirNameUrl, String dirNameRepo) {
		
		predicatesDir = dirNameServer + "/"
				+ dirNameUrl.replaceFirst("http://", "").replaceAll("/", "").replaceAll(":", "-") + "/" + dirNameRepo;
		System.out.println(predicatesRootDir + predicatesDir);

		File file = new File(predicatesRootDir + predicatesDir);
//		File file = new File("test");
		if (!file.exists()) {
			if (file.mkdirs()) {
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
		return RepositoryServer.getSupportedServers();
	}

	private void saveConnProps() {
		DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
		String filePath = "classpath:explordf.properties";
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showConnProps();
	}

	private void shutDown() {
		logger.info("Shut down");
		if (this.repo != null && this.repo.isInitialized()) {
			logger.info("Shut down the repo");
			this.repo.shutDown();
		}
	}

	@PreDestroy
	private void close() {
		logger.info("Method predestroy entered");
		shutDown();
	}

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
