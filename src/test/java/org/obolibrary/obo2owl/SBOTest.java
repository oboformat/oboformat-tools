package org.obolibrary.obo2owl;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntology;

@SuppressWarnings("javadoc")
public class SBOTest extends OboFormatTestBasics {

    @Test
    public void testConvertXPs() throws Exception {
        OWLOntology owlOnt = convertOBOFile("SBO_OBO.obo");
        assertNotNull(owlOnt);
    }

    private OWLOntology convertOBOFile(String fn) throws Exception {
        return convert(parseOBOFile(fn), "cell");
    }
}
