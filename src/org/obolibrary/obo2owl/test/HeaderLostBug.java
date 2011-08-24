package org.obolibrary.obo2owl.test;

import static junit.framework.Assert.*;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class HeaderLostBug extends OboFormatTestBasics {

	/**
	 * During the conversion of the rdfxml formatfile the ontology header tags are lost.
	 * The possible reason is that the RDFXMLOntologyFormat format writes the annotation assertion axioms
	 * as annotations. 
	 * @throws Exception
	 */
	@Test
	public void testHeaderLog() throws Exception{

		convertOBOFile("header_lost_bug.obo");

		IRI ontologyIRI = IRI.create("file:///tmp/header_lost_bug.obo.owl");
	
	 	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontologyIRI);
		
		Set<OWLAnnotation> ontAnns = ontology.getAnnotations();
		Set<OWLAnnotationAssertionAxiom> axioms = ontology.getAnnotationAssertionAxioms(ontologyIRI);

		System.out.println("ont.getAnnotation() = " + ontology.getAnnotations());
		
		//two tags in the header of the obo file are translated as annotation assertions, so the axioms
		//should have two axioms in count.
		assertTrue(ontAnns.size() == 2);
		
	}
	
	private OWLOntology convertOBOFile(String fn) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		return convert(parseOBOFile(fn), fn);
	}

}
