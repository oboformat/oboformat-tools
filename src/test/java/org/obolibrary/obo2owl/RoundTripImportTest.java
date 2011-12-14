package org.obolibrary.obo2owl;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class RoundTripImportTest extends RoundTripTest {

	@Test
	public void testRoundTrip() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		roundTripOBOFile("import_test_main.obo", true);
	}
	
}
