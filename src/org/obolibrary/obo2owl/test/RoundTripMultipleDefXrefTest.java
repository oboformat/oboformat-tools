package org.obolibrary.obo2owl.test;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class RoundTripMultipleDefXrefTest extends RoundTripTest {

	@Test
	public void testRoundTrip() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		roundTripOBOFile("multiple_def_xref_test.obo", true);
	}
}
