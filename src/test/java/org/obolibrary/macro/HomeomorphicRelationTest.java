package org.obolibrary.macro;

import static org.junit.Assert.assertNotNull;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.junit.BeforeClass;
import org.junit.Test;
import org.obolibrary.obo2owl.OboFormatTestBasics;
import org.obolibrary.oboformat.model.OBODoc;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class HomeomorphicRelationTest extends OboFormatTestBasics {

    @BeforeClass
    public static void beforeClass() {
        Logger log = LogManager.getLogManager().getLogger("");
        for (Handler h : log.getHandlers()) {
            h.setLevel(Level.ALL);
        }
    }

    @Test
    public void testExpand() throws Exception {
        OWLOntology owlOnt = convertOBOFile("homrel.obo");
        assertNotNull(owlOnt);
    }

    private OWLOntology convertOBOFile(String fn) throws Exception {
        return convert(parseOBOFile(fn), fn);
    }

    protected OWLOntology convert(OBODoc obodoc, String fn)
            throws OWLOntologyCreationException, OWLOntologyStorageException {
        OWLOntology ontology = convert(obodoc);
        MacroExpansionVisitor mev = new MacroExpansionVisitor(ontology);
        OWLOntology outputOntology = mev.expandAll();
        writeOWL(ontology, fn, new ManchesterOWLSyntaxOntologyFormat());
        return outputOntology;
    }
}
