package org.obolibrary.obo2owl.test;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.obolibrary.obo2owl.Obo2Owl;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import junit.framework.TestCase;

public class HeaderLostBug extends TestCase {

	/**
	 * During the conversion of the rdfxml formatfile the ontology header tags are lost.
	 * The possible reason is that the RDFXMLOntologyFormat format writes the annotation assertion axioms
	 * as annotations. 
	 * @throws Exception
	 */
	public static void testHeaderLog() throws Exception{

		convertOBOFile("header_lost_bug.obo");

		IRI ontologyIRI = IRI.create("file:///tmp/header_lost_bug.obo.owl");
	
	 	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontologyIRI);
		
		Set<OWLAnnotationAssertionAxiom> axioms = ontology.getAnnotationAssertionAxioms(ontologyIRI);
		System.out.println("Annotaiton Assertions: " + axioms);
		
		//two tags in the header of the obo file are translated as annotation assertions, so the axioms
		//should have two axioms in count.
		assertTrue(axioms.size() == 2);
		
		
	}
	
	
	public static OBODoc parseOBOFile(String fn) throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parse("test_resources/"+fn);
		return obodoc;
	}

	public static OWLOntology convertOBOFile(String fn) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		return convert(parseOBOFile(fn), fn);
	}

	private static OWLOntology convert(OBODoc obodoc, String fn) throws OWLOntologyCreationException, OWLOntologyStorageException {
		Obo2Owl bridge = new Obo2Owl();
		OWLOntologyManager manager = bridge.getManager();
		OWLOntology ontology = bridge.convert(obodoc);
		IRI outputStream = IRI.create("file:///tmp/"+fn+".owl");
		System.out.println("saving to "+outputStream);
		OWLOntologyFormat format = new RDFXMLOntologyFormat();
		manager.saveOntology(ontology, format, outputStream);
		return ontology;
	}
	
	
}
