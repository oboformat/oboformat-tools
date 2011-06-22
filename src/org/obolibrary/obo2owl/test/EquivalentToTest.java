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
 * see 5.9.3 and 8.2.2 of spec
 *
 */
public class EquivalentToTest extends TestCase {

	public EquivalentToTest() {
		super();
	}

	public EquivalentToTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public static void testConvert() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		Logger.getRootLogger().setLevel(Level.ERROR);
		Obo2Owl obo2owl = new Obo2Owl();

		// PARSE TEST FILE
		OWLOntology ontology = obo2owl.convert("test_resources/equivtest.obo");


		// TEST CONTENTS OF OWL ONTOLOGY
		if (true) {
			Set<OWLEquivalentClassesAxiom> ecas = ontology.getAxioms(AxiomType.EQUIVALENT_CLASSES);
			boolean ok = false;
			for (OWLEquivalentClassesAxiom eca : ecas) {
				System.out.println(eca);
			}
			assertTrue(ecas.size() == 3);
		}

		// CONVERT BACK TO OBO
		Owl2Obo owl2obo = new Owl2Obo();
		OBODoc obodoc = owl2obo.convert(ontology);

		// OBODoc tests
		
		// test ECA between named classes is persisted using correct tag
		if (true) {
			Frame tf = obodoc.getTermFrame("X:1");
			Collection<Clause> cs = tf.getClauses("equivalent_to");
			assertTrue(cs.size() == 1);
			Object v = cs.iterator().next().getValue();
			System.out.println("V="+v);
			assertTrue(v.equals("X:2")); 
		}
		// test ECA between named class and anon class is persisted as genus-differentia intersection_of tags
		if (true) {
			Frame tf = obodoc.getTermFrame("X:1");
			Collection<Clause> cs = tf.getClauses("intersection_of");
			assertTrue(cs.size() == 2);
			boolean okGenus = false;
			boolean okDifferentia = false;
			for (Clause c : cs) {
				Collection<Object> vs = c.getValues();
				if (vs.size() == 2) {
					if (c.getValue().equals("R:1") && c.getValue2().equals("Z:1")) {
						okDifferentia = true;
					}
					
				}
				else if (vs.size() == 1) {
					if (c.getValue().equals("Y:1")) {
						okGenus = true;
					}
				}
				else {
					assertTrue(false);
				}
			}
			assertTrue(okGenus);
			assertTrue(okDifferentia);
		}
		// check reciprocal direction
		if (true) {
			Frame tf = obodoc.getTermFrame("X:2");
			Collection<Clause> cs = tf.getClauses("equivalent_to");
			assertTrue(cs.size() == 1);
			Object v = cs.iterator().next().getValue();
			System.out.println("V="+v);
			assertTrue(v.equals("X:1")); 
		}


	}

	public static OBODoc parseOBOFile(String fn) throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parse("test_resources/"+fn);
		return obodoc;
	}


}
