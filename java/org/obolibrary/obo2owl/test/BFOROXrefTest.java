package org.obolibrary.obo2owl.test;

import static junit.framework.Assert.*;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.obolibrary.obo2owl.Owl2Obo;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class BFOROXrefTest extends OboFormatTestBasics {

	@Test
	public void testConvertXPs() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		OWLOntology owlOnt = convertOBOFile("rel_xref_test.obo");
		
		Owl2Obo revbridge = new Owl2Obo();
		OBODoc d2 = revbridge.convert(owlOnt);
		
		Frame part_of = d2.getTypedefFrame("part_of");
		Collection<Clause> xrcs = part_of.getClauses(OboFormatTag.TAG_XREF);
		boolean okBfo = false;
		boolean okOboRel = false;
		
		for (Clause c : xrcs) {
			Xref value = c.getValue(Xref.class);
			if (value.getIdref().equals("BFO:0000050")) {
				okBfo = true;
			}
			if (value.getIdref().equals("OBO_REL:part_of")) {
				okOboRel = true;
			}
		}
		assertTrue(okBfo);
		assertTrue(okOboRel);
		
		Frame a = d2.getTermFrame("TEST:a");
		Clause rc = a.getClause(OboFormatTag.TAG_RELATIONSHIP);
		assertTrue(rc.getValue().equals("part_of"));
		assertTrue(rc.getValue2().equals("TEST:b"));

	}
	
	private OWLOntology convertOBOFile(String fn) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		return convert(parseOBOFile(fn), fn);
	}
}
