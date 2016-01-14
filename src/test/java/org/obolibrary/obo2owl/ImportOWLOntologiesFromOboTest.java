package org.obolibrary.obo2owl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Collection;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

/**
 * This class tests the scenario where an obo file includes import directives to
 * external OWL files
 * 
 * @author cjm
 */
@SuppressWarnings("javadoc")
public class ImportOWLOntologiesFromOboTest extends OboFormatTestBasics {

    private static final boolean useSystemOut = false;

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
        OBODoc obodoc = parseOBOFile("import_test_main.obo");
        OWLOntologyIRIMapper iriMapper = new SimpleIRIMapper(
                IRI.create("http://purl.obolibrary.org/obo/tests/import_test_imported.owl"),
                IRI.create(new File(
                        "src/test/resources/import_test_imported.owl")));
        Obo2Owl bridge = new Obo2Owl();
        if (useSystemOut) {
            System.out.println("M=" + bridge.getManager());
        }
        bridge.getManager().getIRIMappers().add(iriMapper);
        Collection<Clause> importClauses = obodoc.getHeaderFrame().getClauses(
                OboFormatTag.TAG_IMPORT);
        assertEquals(1, importClauses.size());
        assertEquals(0, obodoc.getImportedOBODocs().size());
        OWLOntology ontology = bridge.convert(obodoc);
        assertEquals(1, ontology.getImportsDeclarations().size());
        if (useSystemOut) {
            for (OWLSubClassOfAxiom a : ontology.getAxioms(
                    AxiomType.SUBCLASS_OF, Imports.INCLUDED)) {
                System.out.println("A=" + a);
            }
        }
        assertEquals(2, ontology.getAxioms(AxiomType.SUBCLASS_OF, Imports.INCLUDED).size());
    }
}
