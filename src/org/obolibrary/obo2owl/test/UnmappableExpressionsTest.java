package org.obolibrary.obo2owl.test;

import static junit.framework.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 * @author cjm
 * 
 * unmappable expressions should be handled gracefully.
 * 
 * in particular, there should be no single intersection_of tags
 * 
 * See http://code.google.com/p/oboformat/issues/detail?id=13
 *
 */
public class UnmappableExpressionsTest extends OboFormatTestBasics {

	@BeforeClass
	public static void beforeClass(){
		Logger.getRootLogger().setLevel(Level.ERROR);
	}
	
	@Test
	public void testConvert() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException, URISyntaxException {
		OBODoc obodoc = convert(parseOWLFile("nesting.owl"));
		for (Frame f : obodoc.getTermFrames()) {
			System.out.println(f);
		}
//		checkOBODoc(obodoc);

		// ROUNDTRIP AND TEST AGAIN
		File file = writeOBO(obodoc, "nesting.obo");
		
		obodoc = parseOBOFile(file);
		checkOBODoc(obodoc);
	}
	
	private void checkOBODoc(OBODoc obodoc) {
		// OBODoc tests
		 		
		if (true) {
			Frame tf = obodoc.getTermFrame("x1"); // TODO - may change
			Collection<Clause> cs = tf.getClauses("intersection_of");
			assertTrue(cs.size() != 1); // there should NEVER be a situation with single intersection tags
			// TODO - add validation step prior to saving
		}

	}
	
}
