package org.obolibrary.macro;

import static org.junit.Assert.assertTrue;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;
import org.obolibrary.obo2owl.RoundTripTest;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/*
 * 
 * Note there is currently a bug whereby blocks of constraints are not translated. E.g
 * 
 * [Term]
id: GO:0009657
name: plastid organization
relationship: never_in_taxon NCBITaxon:33208 {id="GOTAX:0000492", source="PMID:21311032"} ! Metazoa
relationship: never_in_taxon NCBITaxon:4751 {id="GOTAX:0000502", source="PMID:21311032"} ! Fungi
relationship: never_in_taxon NCBITaxon:28009 {id="GOTAX:0000503", source="PMID:21311032"} ! Choanoflagellida
relationship: never_in_taxon NCBITaxon:554915 {id="GOTAX:0000504", source="PMID:21311032"} ! Amoebozoa
	
 * 
 */
public class ExpandTaxonConstraintsTest extends RoundTripTest {

    @BeforeClass
    public static void beforeClass() {
        Logger log = LogManager.getLogManager().getLogger("");
        for (Handler h : log.getHandlers()) {
            h.setLevel(Level.ALL);
        }
    }

    @Test
    public void testExpand() throws Exception {
        OWLOntology ontology = convert(parseOBOFile("taxon_constraints.obo"));
        OWLDataFactory df = ontology.getOWLOntologyManager()
                .getOWLDataFactory();
        MacroExpansionVisitor mev = new MacroExpansionVisitor(ontology);
        OWLOntology outputOntology = mev.expandAll();
        int n = 0;
        for (OWLDisjointClassesAxiom dca : outputOntology
                .getAxioms(AxiomType.DISJOINT_CLASSES)) {
            Logger.getAnonymousLogger().log(Level.INFO, dca.toString());
            n++;
        }
        System.out.println("Disjoint class axioms: " + n);
        assertTrue(n > 0);
        // writeOWL(ontology, "expanded-taxon-constraints.owl");
    }
}
