package org.obolibrary.macro.test;

import static junit.framework.Assert.assertTrue;

import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.obolibrary.macro.MacroExpansionGCIVisitor;
import org.obolibrary.obo2owl.test.OboFormatTestBasics;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

public class ExpandSynapsedToTest extends OboFormatTestBasics {

	@BeforeClass
	public static void beforeClass() {
		Logger.getRootLogger().setLevel(Level.ALL);
	}
	
	@Test
	public void testExpand() throws Exception {
		OWLOntology ontology = convert(parseOBOFile("synapsed_to.obo"));

		MacroExpansionGCIVisitor mev = new MacroExpansionGCIVisitor(ontology);
		OWLOntology gciOntology = mev.createGCIOntology();
		int axiomCount = gciOntology.getAxiomCount();
		assertTrue(axiomCount > 0);
		
		Set<OWLAxiom> axioms = gciOntology.getAxioms();
		for (OWLAxiom axiom : axioms) {
			System.out.println(axiom);
			// TODO - do actual tests
		}
	}
}
