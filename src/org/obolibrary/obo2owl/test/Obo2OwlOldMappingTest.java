package org.obolibrary.obo2owl.test;

import org.obolibrary.obo2owl.Obo2OWLOldMapping;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import junit.framework.TestCase;

public class Obo2OwlOldMappingTest extends TestCase {

	
	public static void testMapping() throws Exception{
		Obo2OWLOldMapping obo2Owl = new Obo2OWLOldMapping();
		
		OWLOntology ontology = obo2Owl.convert("test_resources/temp.obo");
		IRI outputStream = IRI.create("file:///tmp/temp.owl");

	//	obo2Owl.getManager().saveOntology(ontology, new RDFXMLOntologyFormat(), outputStream);
		obo2Owl.getManager().saveOntology(ontology, new OWLFunctionalSyntaxOntologyFormat(), outputStream);
		
	//	obo2Owl.getManager().saveOntology(ontology, new OWLXMLOntologyFormat(), outputStream);
		
		
	}
	
}
