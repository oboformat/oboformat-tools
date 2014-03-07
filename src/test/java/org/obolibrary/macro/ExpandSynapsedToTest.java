package org.obolibrary.macro;

import static junit.framework.Assert.assertTrue;

import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;
import org.obolibrary.obo2owl.OboFormatTestBasics;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

public class ExpandSynapsedToTest extends OboFormatTestBasics {

    @BeforeClass
    public static void beforeClass() {
        Logger log = LogManager.getLogManager().getLogger("");
        for (Handler h : log.getHandlers()) {
            h.setLevel(Level.ALL);
        }
    }

    @Test
    public void testExpand() throws Exception {
        OWLOntology ontology = convert(parseOBOFile("synapsed_to.obo"));
        MacroExpansionGCIVisitor mev = new MacroExpansionGCIVisitor(ontology,
                OWLManager.createOWLOntologyManager());
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
