package org.obolibrary.obo2owl;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class RoundTripPropertyValueTest extends RoundTripTest {

	@Test
	public void testRoundTrip() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		roundTripOBOFile("property_value_test.obo", true);
	}
	
	@Test
	public void testRoundTripWithQualifiers() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		//uncomment this when http://code.google.com/p/oboformat/issues/detail?id=69 is fixed:
		//roundTripOBOFile("property_value_qualifiers_test.obo", true);
	}

	
	
	/*
	@Test
	public void testDefinitions() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		OWLOntology owlOnt = convertOBOFile("multiple_def_xref_test.obo");
		int n = 0;
		for (OWLAxiom ax : owlOnt.getAxioms()) {
			//System.out.println(ax);
			for (OWLAnnotation ann : ax.getAnnotations()) {
				System.out.println(" NESTED:"+ann);
				OWLAnnotationProperty p = ann.getProperty();
				System.out.println(" P:"+p);
				if (p.getIRI().equals(IRI.create("http://www.geneontology.org/formats/oboInOwl#hasDbXref"))) {
					OWLLiteral v = (OWLLiteral)ann.getValue();
					System.out.println(" V:"+v);
					// expect this twice, as we have annotations on synonyms
					if (v.getLiteral().equals("BTO:0001750"))
						n++;
					if (v.getLiteral().equals("Wikipedia:Mandibular_condyle"))
						n++;
							
				}
				
			}
		}
		assertEquals(3, n);
	}
	*/
	
	private OWLOntology convertOBOFile(String fn) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		return convert(parseOBOFile(fn), fn);
	}

}
