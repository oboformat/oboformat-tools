package org.obolibrary.obo2owl;

import static junit.framework.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.obolibrary.obo2owl.Owl2Obo;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.obolibrary.oboformat.writer.OBOFormatWriter;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLProperty;

/**
 * @author cjm
 * 
 * see 5.9.3 and 8.2.2 of spec
 * 
 * See http://code.google.com/p/oboformat/issues/detail?id=13
 *
 */
public class LogicalDefinitionPropertyViewTest extends OboFormatTestBasics {

	@BeforeClass
	public static void beforeClass() {
		Logger.getRootLogger().setLevel(Level.ERROR);		
	}

	@Test
	public void testConvert() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException, URISyntaxException {

		// PARSE TEST FILE

		OWLOntology owlOntology = convert(parseOBOFile("logical-definition-view-relation-test.obo"));

		for (OWLAnnotation ann : owlOntology.getAnnotations()) {
			//System.out.println("Ann: "+ann);
		}

		boolean ok = false;
		for (OWLEquivalentClassesAxiom eca : owlOntology.getAxioms(AxiomType.EQUIVALENT_CLASSES)) {
			System.out.println(eca);
			for (OWLClassExpression x : eca.getClassExpressions()) {
				System.out.println("  "+x);
				if (x instanceof OWLObjectSomeValuesFrom) {
					// fairly weak test - just ensure it's done _something_ here
					OWLObjectProperty p =   (OWLObjectProperty) ((OWLObjectSomeValuesFrom)x).getProperty();
					System.out.println("    "+p);
					
					if (p.getIRI().toString().equals("http://purl.obolibrary.org/obo/BFO_0000050")) {
						ok = true;
					}
				}
			}
		}	
		assertTrue(ok);
		
		// reverse translation
		OBODoc obodoc = this.convert(owlOntology);
		Frame fr = obodoc.getTermFrame("X:1");
		System.out.println(fr);
		Collection<Clause> clauses = fr.getClauses(OboFormatTag.TAG_INTERSECTION_OF);
		assertTrue(clauses.size() == 2);
		for (Clause c : clauses) {
			System.out.println(" c="+c);
		}
	}

}
