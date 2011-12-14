package org.obolibrary.obo2owl;

import static junit.framework.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.obolibrary.obo2owl.Owl2Obo;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.obolibrary.oboformat.writer.OBOFormatWriter;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class DanglingRoundTripTest  extends OboFormatTestBasics {

	@Test
	public void testConvertXPs() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException, URISyntaxException {
		OWLOntology owlOnt = convertOBOFile("dangling_roundtrip_test.obo");
		
		Owl2Obo revbridge = new Owl2Obo();
		OBODoc d2 = revbridge.convert(owlOnt);
				
		Frame f = d2.getTermFrame("UBERON:0000020");
		System.out.println("F="+f);
		Clause rc = f.getClause(OboFormatTag.TAG_NAME);
		assertTrue(rc.getValue().equals("sense organ"));
		
		OBOFormatWriter w = new OBOFormatWriter();
		w.write(d2, "/tmp/z.obo");

	}
	
	private OWLOntology convertOBOFile(String fn) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		return convert(parseOBOFile(fn), fn);
	}
}
