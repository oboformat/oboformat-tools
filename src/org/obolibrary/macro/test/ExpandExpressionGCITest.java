package org.obolibrary.macro.test;

import static junit.framework.Assert.*;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.obolibrary.macro.MacroExpansionGCIVisitor;
import org.obolibrary.obo2owl.test.OboFormatTestBasics;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class ExpandExpressionGCITest extends OboFormatTestBasics {

	@BeforeClass
	public static void beforeClass() {
		Logger.getRootLogger().setLevel(Level.ALL);
	}
	
	@Test
	public void testExpand() throws Exception {
		OWLOntology ontology = convert(parseOBOFile("no_overlap.obo"));

		OWLOntologyManager manager = ontology.getOWLOntologyManager();
		OWLDataFactory df = manager.getOWLDataFactory();
		
		MacroExpansionGCIVisitor mev = new MacroExpansionGCIVisitor(df,ontology, manager);
		OWLOntology gciOntology = mev.createGCIOntology();
		for (OWLAxiom ax : gciOntology.getAxioms()) {
			System.out.println(ax);
		}
		List<String> gciList = mev.getGCIList();
		assertEquals(3, gciList.size());
		
		int axiomCount = gciOntology.getAxiomCount();
		assertTrue(axiomCount > 0);

		OWLClass cls = df.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/TEST_2"));
		Set<OWLDisjointClassesAxiom> dcas = gciOntology.getDisjointClassesAxioms(cls);
		System.out.println(dcas);
		assertTrue(dcas.size() == 1);
	}
}
