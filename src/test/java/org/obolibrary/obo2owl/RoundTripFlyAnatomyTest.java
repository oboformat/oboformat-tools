package org.obolibrary.obo2owl;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class RoundTripFlyAnatomyTest extends RoundTripTest {

	@Test
	public void testRoundTrip() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		roundTripOBOURL("http://obo.cvs.sourceforge.net/*checkout*/obo/obo/ontology/anatomy/gross_anatomy/animal_gross_anatomy/fly/fly_anatomy_XP.obo", true);
	}
	
}
