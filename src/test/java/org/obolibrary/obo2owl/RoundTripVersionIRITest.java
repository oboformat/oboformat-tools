package org.obolibrary.obo2owl;

import static junit.framework.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class RoundTripVersionIRITest extends RoundTripTest {
	
	@Test
	public void testRoundTrip() throws Exception {
		roundTripOBOFile("version_iri_test.obo", true);
	}
	
	@Test
	public void testConvert() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		OWLOntology owlOnt = convertOBOFile("version_iri_test.obo");
		assertNotNull(owlOnt);
		IRI v = owlOnt.getOntologyID().getVersionIRI();
		assertEquals("http://purl.obolibrary.org/obo/go/2012-01-01/go.owl", v.toString());
	}
	
	private OWLOntology convertOBOFile(String fn) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		return convert(parseOBOFile(fn), fn);
	}

}
