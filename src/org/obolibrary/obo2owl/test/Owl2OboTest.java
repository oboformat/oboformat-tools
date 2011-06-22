package org.obolibrary.obo2owl.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.obolibrary.obo2owl.Obo2Owl;
import org.obolibrary.obo2owl.Owl2Obo;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.writer.OBOFormatWriter;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import junit.framework.TestCase;

public class Owl2OboTest extends TestCase {

	public static void testConversion() throws Exception{

		Logger.getRootLogger().setLevel(Level.ERROR);
		Obo2Owl obo2owl = new Obo2Owl();
		
		OWLOntology ontology = obo2owl.convert("test_resources/caro.obo");
		
		Owl2Obo bridge = new Owl2Obo();
		
		OBODoc doc = bridge.convert(ontology);
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("test_resources/caro_converted.obo")));
		
		OBOFormatWriter oboWriter = new OBOFormatWriter();
		
		oboWriter.write(doc, writer);
		
		writer.close();
		
	}
	
	
	public static void testIRTsConversion() throws Exception{
		IRI ontologyIRI = IRI.create("http://purl.obolibrary.org/obo/test.owl");
		
	 	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
		OWLOntology ontology = manager.createOntology(ontologyIRI);
		
		Owl2Obo bridge  = new Owl2Obo();
	
		bridge.convert(ontology);
		
		String ontId = bridge.getOntologyId(ontology);
		
		assertTrue("test".equals(ontId));
		
		IRI iri = IRI.create("http://purl.obolibrary.org/obo/OBI_0000306");
		
		String id = bridge.getIdentifier(iri);
		
		assertTrue("OBI:0000306".endsWith(id));
		
		iri = 	IRI.create("http://purl.org/obo/owl/NCBITaxon#NCBITaxon_5794");
		id = bridge.getIdentifier(iri);
		assertTrue("NCBITaxon:5794".equals(id));
		

		iri = 	IRI.create("http://purl.obolibrary.org/obo/IAO_0000119");
		id = bridge.getIdentifier(iri);
		assertTrue("IAO:0000119".equals(id));

		iri = 	IRI.create("http://purl.obolibrary.org/obo/caro_part_of");
		id = bridge.getIdentifier(iri);
		assertTrue("caro:part_of".equals(id));
		

		iri = 	IRI.create("http://purl.org/obo/owl/CL#CL_0000540");
		id = bridge.getIdentifier(iri);
		assertTrue("CL:0000540".equals(id));
		

		iri = 	IRI.create("http://www.obofoundry.org/ro/ro.owl#has_part");
		id = bridge.getIdentifier(iri);
		assertTrue("OBO_REL:has_part".equals(id));
		
		iri = 	IRI.create("http://www.w3.org/2002/07/owl#topObjectProperty");
		id = bridge.getIdentifier(iri);
		assertTrue("owl:topObjectProperty".equals(id));
				
		
	}
	
	
	
}
