package org.obolibrary.obo2owl;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class RoundTripPropertyValueTest extends RoundTripTest {

	@Test
	public void testRoundTrip() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		roundTripOBOFile("property_value_test.obo", true);
	}
	
	@Test
	public void testRoundTripWithQualifiers() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		roundTripOBOFile("property_value_qualifier_test.obo", true);
	}

	@Test
	public void testRoundTripHeader() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		roundTripOBOFile("dc_header_test.obo", true);
	}

}
