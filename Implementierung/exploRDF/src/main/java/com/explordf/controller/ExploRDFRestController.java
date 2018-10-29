package com.explordf.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.explordf.service.ExploRDFService;

import dto.PredicateDto;
import dto.TripleDto;


@RestController
public class ExploRDFRestController {

	@Autowired
	ResourceLoader loader;
	
	@Autowired
	ExploRDFService exploRDFService;
	
	@Value("classpath:static/persistent/predicates.txt")
	Resource resourceFile;
	
	String filePath = "classpath:static/persistent/predicates.txt";	
	
	@RequestMapping(value = "/writePredicates", method = RequestMethod.POST, consumes = MediaType.TEXT_PLAIN_VALUE)
	public void writePredicates(@RequestBody String text){
		System.out.println(text);
		
		String fileName="persistent/predicates.txt";
		
		try {
			File file = ResourceUtils.getFile(filePath);
			if(file.exists()) {
				System.out.println("exists");
				BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			    writer.append(' ');
			    writer.append(text);
			     
			    writer.close();
			    
			    
			    List<String> lines = Files.readAllLines(Paths.get(resourceFile.getURI()),
		                StandardCharsets.UTF_8);

		        for (String line : lines) {

		            System.out.println(line);

		        }
			} else {
				System.out.println("doesn´t exist");
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			
		}
		
	}

	@RequestMapping(value="/simpleSearch/{term}", method = RequestMethod.GET)
	public List<TripleDto> simpleSearch(@PathVariable String term) {
		return exploRDFService.simpleSearch(term);
	}
	
	@RequestMapping(value="/getPredicates", method=RequestMethod.GET)
	public List<PredicateDto> getPredicates() throws JSONException {
		return exploRDFService.getPredicates();
	}
	
	@RequestMapping(value="/getSubject", method = RequestMethod.POST)
	public List<TripleDto> getSubject(@RequestBody String subject) {
		System.out.println("Entered Controller");
		return exploRDFService.getSubject(subject);
	}
	
}
