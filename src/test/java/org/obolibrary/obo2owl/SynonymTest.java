package org.obolibrary.obo2owl;

import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author cjm
 */
public class SynonymTest extends OboFormatTestBasics {

    @BeforeClass
    public static void beforeClass() {
        Logger log = LogManager.getLogManager().getLogger("");
        for (Handler h : log.getHandlers()) {
            h.setLevel(Level.SEVERE);
        }
    }

    @Test
    public void testConvert() throws Exception {
        // PARSE TEST FILE
        OWLOntology ontology = convert(parseOBOFile("synonym_test.obo"));
        Set<OWLAnnotation> anns = ontology.getAnnotations();
        for (OWLAnnotation ann : anns) {
            // TODO
            System.out.println("Ann=" + ann);
        }
        Set<OWLAnnotationProperty> aps = ontology
                .getAnnotationPropertiesInSignature();
        for (OWLAnnotationProperty ap : aps) {
            System.out.println("ap=" + ap);
        }
        // TODO
    }
}
