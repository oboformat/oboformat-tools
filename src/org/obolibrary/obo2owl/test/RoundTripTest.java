package org.obolibrary.obo2owl.test;

import static junit.framework.Assert.*;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.obolibrary.oboformat.diff.Diff;
import org.obolibrary.oboformat.diff.OBODocDiffer;
import org.obolibrary.oboformat.model.FrameStructureException;
import org.obolibrary.oboformat.model.OBODoc;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class RoundTripTest extends OboFormatTestBasics {

	@BeforeClass
	public static void beforeClass() {
		Logger.getRootLogger().setLevel(Level.ERROR);
	}
	
	public List<Diff> roundTripOBOURL(String fn, boolean isExpectRoundtrip) throws IOException, OWLOntologyCreationException {
		OBODoc obodoc = parseOBOURL(fn);
		return roundTripOBODoc(obodoc, isExpectRoundtrip);
	}
	
	public List<Diff> roundTripOBOFile(String fn, boolean isExpectRoundtrip) throws IOException, OWLOntologyCreationException {
		OBODoc obodoc = parseOBOFile(fn);
		return roundTripOBODoc(obodoc, isExpectRoundtrip);
	}
	
	public List<Diff> roundTripOBODoc(OBODoc obodoc, boolean isExpectRoundtrip) throws OWLOntologyCreationException {

		OWLOntology oo = convert(obodoc);
		
		OBODoc obodoc2 = convert(oo);
		
		try {
			obodoc2.check();
		} catch (FrameStructureException exception) {
			exception.printStackTrace();
			fail("No syntax errors allowed");
		}
		
		try {
			writeOBO(obodoc2, "roundtrip.obo");
		} catch (IOException e) {
			e.printStackTrace();
			fail("No IOExceptions allowed");
		} 
		
		OBODocDiffer dd = new OBODocDiffer();
		List<Diff> diffs = dd.getDiffs(obodoc, obodoc2);
		if (isExpectRoundtrip) {
			for (Diff diff : diffs) {
				System.out.println(diff);
			}
			assertEquals("Expected no diffs", 0, diffs.size()); 
		}
		return diffs;
	}
	
}
