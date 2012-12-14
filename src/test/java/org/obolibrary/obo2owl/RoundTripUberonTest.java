package org.obolibrary.obo2owl;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class RoundTripUberonTest extends RoundTripTest {

	/**
	 * This test uses the editors version of Uberon.
	 * 
	 * Note that this version sometimes contains syntax errors (these are always checked fully before public release)
	 * 
	 * Note also that uberon sometimes 'pushes the boundaries' of what is possible in obo-format, so this test will
	 * be especially sensitive
	 * 
	 * This test should never ben in allproductiontests for this reason
	 * 
	 * @throws IOException
	 * @throws OWLOntologyCreationException
	 * @throws OWLOntologyStorageException
	 */
	@Test
	public void testRoundTripUberonEditVersion() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		roundTripOBOURL("https://github.com/cmungall/uberon/blob/master/uberon_edit.obo?raw=true", true);		
	}
	
	
}
