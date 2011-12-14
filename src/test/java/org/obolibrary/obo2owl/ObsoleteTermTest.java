package org.obolibrary.obo2owl;

import static junit.framework.Assert.*;

import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class ObsoleteTermTest extends OboFormatTestBasics {
	
	@BeforeClass
	public static void beforeClass() {
		Logger.getRootLogger().setLevel(Level.ERROR);		
	}
	
	@Test
	public void testConvert() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		// PARSE TEST FILE
		OWLOntology ontology = convert(parseOBOFile("obsolete_term_test.obo"));
		
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
		writeOWL(ontology, "obsolete_term_test.owl", new RDFXMLOntologyFormat());

		// CONVERT BACK TO OBO
		OBODoc obodoc = convert(ontology);
		Frame tf = obodoc.getTermFrame("XX:0000034");
		Clause c = tf.getClause(OboFormatTag.TAG_IS_OBSELETE);
		Object v = c.getValue();
		System.out.println("V="+v);
		assertTrue(v.equals("true")); // should this be a Boolean object? TODO
	}
	
}
