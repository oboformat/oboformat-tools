package org.obolibrary.obo2owl.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.obolibrary.obo2owl.Obo2Owl;
import org.obolibrary.obo2owl.Owl2Obo;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.obolibrary.oboformat.writer.OBOFormatWriter;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import junit.framework.TestCase;

/**
 * @author cjm
 * 
 *
 */
public class SubsetTest extends TestCase {

	public SubsetTest() {
		super();
	}

	public SubsetTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public static void testConvert() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		Logger.getRootLogger().setLevel(Level.ERROR);
		Obo2Owl obo2owl = new Obo2Owl();

		// PARSE TEST FILE
		OWLOntology ontology = obo2owl.convert("test_resources/subset_test.obo");
		
		Set<OWLAnnotation> anns = ontology.getAnnotations();
		for (OWLAnnotation ann : anns) {
			// TODO
			System.out.println("Ann="+ann);
		}
		OWLAnnotationSubject subj = IRI.create("http://purl.obolibrary.org/obo/GO_0000003");
		Set<OWLAnnotationAssertionAxiom> aas = ontology.getAnnotationAssertionAxioms(subj );
		boolean ok = false;
		for (OWLAnnotationAssertionAxiom aa : aas) {
			System.out.println(aa);
			if (aa.getProperty().getIRI().toString().equals("http://www.geneontology.org/formats/oboInOwl#inSubset")) {
				 OWLAnnotationValue v = aa.getValue();
				System.out.println("  dep="+v);
				ok = true;
			}
		}
		assertTrue(ok);



	}


}
