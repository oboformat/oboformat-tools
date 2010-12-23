package org.obolibrary.obo2owl.test;

import java.io.File;

import org.obolibrary.obo2owl.Owl2Obo;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import junit.framework.TestCase;

public class Owl2OboTest extends TestCase {

	public static void testConversion() throws Exception{
		Owl2Obo bridge = new Owl2Obo();
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File("test_resources/temp.owl"));
		
		OBODoc doc = bridge.convert(ontology);
	
		System.out.println( doc.getHeaderFrame() );
		
	 	for(Frame f: doc.getTermFrames()){
	 		System.out.println(f);
	 	}
	 	
	 	for(Frame f: doc.getTypedefFrames()){
	 		System.out.println(f);
	 	}
		
	}
	
}
