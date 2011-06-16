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
public class RelationShorthandTest extends TestCase {

	public RelationShorthandTest() {
		super();
	}

	public RelationShorthandTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public static void testConvert() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		Logger.getRootLogger().setLevel(Level.ERROR);
		Obo2Owl obo2owl = new Obo2Owl();

		// PARSE TEST FILE
		OWLOntology ontology = obo2owl.convert("test_resources/relation_shorthand_test.obo");


		// TEST CONTENTS OF OWL ONTOLOGY

		if (true) {
			Set<OWLSubClassOfAxiom> scas = ontology.getAxioms(AxiomType.SUBCLASS_OF);
			boolean ok = false;
			for (OWLSubClassOfAxiom sca : scas) {
				System.out.println(sca);
				OWLClassExpression sup = sca.getSuperClass();
				if (sup instanceof OWLObjectSomeValuesFrom) {
					OWLObjectProperty p = (OWLObjectProperty) ((OWLObjectSomeValuesFrom)sup).getProperty();
					OWLClass v = (OWLClass) ((OWLObjectSomeValuesFrom)sup).getFiller();
					if (p.getIRI().toString().equals("http://purl.obolibrary.org/obo/BFO_0000051") &&
							v.getIRI().toString().equals("http://purl.obolibrary.org/obo/GO_0004055")) {
						ok = true;
					}
				}
			}
			assertTrue(ok);
		}
		if (true) {
			Set<OWLSubClassOfAxiom> scas = ontology.getAxioms(AxiomType.SUBCLASS_OF);
			boolean ok = false;
			for (OWLSubClassOfAxiom sca : scas) {
				System.out.println(sca);
				OWLClassExpression sup = sca.getSuperClass();
				if (sup instanceof OWLObjectSomeValuesFrom) {
					OWLObjectProperty p = (OWLObjectProperty) ((OWLObjectSomeValuesFrom)sup).getProperty();
					OWLClass v = (OWLClass) ((OWLObjectSomeValuesFrom)sup).getFiller();
					if (p.getIRI().toString().equals("http://purl.obolibrary.org/obo/BFO_0000050") &&
							v.getIRI().toString().equals("http://purl.obolibrary.org/obo/XX_0000001")) {
						ok = true;
					}
				}
			}
			assertTrue(ok);
		}

		// CONVERT TO OWL FILE
		IRI outputStream = IRI.create("file:///tmp/x.owl");
		System.out.println("saving to "+outputStream);
		OWLOntologyFormat format = new RDFXMLOntologyFormat();
		OWLOntologyManager manager = obo2owl.getManager();
		manager.saveOntology(ontology, format, outputStream);

		// CONVERT BACK TO OBO
		Owl2Obo owl2obo = new Owl2Obo();
		OBODoc obodoc = owl2obo.convert(ontology);
		
		// test that relation IDs are converted back to symbolic form
		if (true) {
			Frame tf = obodoc.getTermFrame("GO:0000050");
			Clause c = tf.getClause("relationship");
			Object v = c.getValue();
			System.out.println("V="+v);
			assertTrue(v.equals("has_part")); // should be converted back to symbolic form
		}
		if (true) {
			Frame tf = obodoc.getTermFrame("GO:0004055");
			Clause c = tf.getClause("relationship");
			Object v = c.getValue();
			System.out.println("V="+v);
			assertTrue(v.equals("part_of")); // should be converted back to symbolic form
		}
		if (true) {
			Frame tf = obodoc.getTypedefFrame("has_part");
			Collection<Clause> cs = tf.getClauses("xref");
			assertTrue(cs.size() == 1);
			Object v = cs.iterator().next().getValue();
			System.out.println("V="+v);
			assertTrue(v.equals("BFO:0000051")); // should be converted back to symbolic form
		}


	}

	public static OBODoc parseOBOFile(String fn) throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parse("test_resources/"+fn);
		return obodoc;
	}


}
