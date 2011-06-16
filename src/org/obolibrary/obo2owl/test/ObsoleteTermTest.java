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
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import junit.framework.TestCase;

public class ObsoleteTermTest extends TestCase {
	
	public ObsoleteTermTest() {
		super();
	}

	public ObsoleteTermTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public static void testConvert() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		Logger.getRootLogger().setLevel(Level.ERROR);
		Obo2Owl obo2owl = new Obo2Owl();
		
		// PARSE TEST FILE
		OWLOntology ontology = obo2owl.convert("test_resources/obsolete_term_test.obo");
	
		
		// TEST CONTENTS OF OWL ONTOLOGY
		OWLAnnotationSubject subj = IRI.create("http://purl.obolibrary.org/obo/XX_0000034");
		Set<OWLAnnotationAssertionAxiom> aas = ontology.getAnnotationAssertionAxioms(subj );
		boolean okDeprecated = false;
		for (OWLAnnotationAssertionAxiom aa : aas) {
			System.out.println(aa);
			if (aa.getProperty().getIRI().equals(OWLRDFVocabulary.OWL_DEPRECATED.getIRI())) {
				OWLLiteral v = (OWLLiteral) aa.getValue();
				System.out.println("  dep="+v);
				if (v.isBoolean()) {
					if (v.parseBoolean()) {
						okDeprecated = true;
					}
				}
				
			}
		}
		assertTrue(okDeprecated);

		// CONVERT TO OWL FILE
		IRI outputStream = IRI.create("file:///tmp/obsolete_term_test.owl");
		System.out.println("saving to "+outputStream);
		OWLOntologyFormat format = new RDFXMLOntologyFormat();
		OWLOntologyManager manager = obo2owl.getManager();
		manager.saveOntology(ontology, format, outputStream);

		// CONVERT BACK TO OBO
		Owl2Obo owl2obo = new Owl2Obo();
		OBODoc obodoc = owl2obo.convert(ontology);
		Frame tf = obodoc.getTermFrame("XX:0000034");
		Clause c = tf.getClause("is_obsolete");
		Object v = c.getValue();
		System.out.println("V="+v);
		assertTrue(v.equals("true")); // should this be a Boolean object? TODO
	}
	
	public static OBODoc parseOBOFile(String fn) throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parse("test_resources/"+fn);
		return obodoc;
	}


}
