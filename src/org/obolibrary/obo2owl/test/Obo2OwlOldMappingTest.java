package org.obolibrary.obo2owl.test;

import org.junit.Test;
import org.obolibrary.obo2owl.Obo2OWLOldMapping;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntology;


public class Obo2OwlOldMappingTest extends OboFormatTestBasics {

	@Test
	public void testMapping() throws Exception{
		Obo2OWLOldMapping obo2Owl = new Obo2OWLOldMapping();
		
		OWLOntology ontology = convert(parseOBOFile("temp.obo"));
		writeOWL(ontology, "temp.owl", new OWLFunctionalSyntaxOntologyFormat());
		
//		writeOWL(ontology, "temp.owl", new RDFXMLOntologyFormat());
//		writeOWL(ontology, "temp.owl", new OWLXMLOntologyFormat());
	}
	
}
