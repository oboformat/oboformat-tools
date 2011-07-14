package org.obolibrary.obo2owl.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
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
 * unmappable expressions should be handled gracefully.
 * 
 * in particular, there should be no single intersection_of tags
 * 
 * See http://code.google.com/p/oboformat/issues/detail?id=13
 *
 */
public class UnmappableExpressionsTest extends TestCase {

	public UnmappableExpressionsTest() {
		super();
	}

	public UnmappableExpressionsTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public static void testConvert() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException, URISyntaxException {
		Logger.getRootLogger().setLevel(Level.ERROR);
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager(); // persist?
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create("file:test_resources/nesting.owl"));
				
		Owl2Obo bridge = new Owl2Obo();
		OBODoc obodoc = bridge.convert(ontology);
//		checkOBODoc(obodoc);

		// ROUNDTRIP AND TEST AGAIN
		String fn = "/tmp/nesting.obo";
		OBOFormatWriter w = new OBOFormatWriter();
		FileOutputStream os = new FileOutputStream(new File(fn));
		OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		w.write(obodoc, bw);
		bw.close();
		OBOFormatParser p = new OBOFormatParser();
		obodoc = p.parse(fn);
		checkOBODoc(obodoc);
		




	}
	
	public static void checkOBODoc(OBODoc obodoc) {
		// OBODoc tests
		
		 		
		if (true) {
			Frame tf = obodoc.getTermFrame("x1"); // TODO - may change
			Collection<Clause> cs = tf.getClauses("intersection_of");
			assertTrue(cs.size() != 1); // there should NEVER be a situation with single intersection tags
			// TODO - add validation step prior to saving
		}

	}

	public static OBODoc parseOBOFile(String fn) throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parse("test_resources/"+fn);
		return obodoc;
	}


}
