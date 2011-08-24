package org.obolibrary.oboformat.test;

import static junit.framework.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.obolibrary.obo2owl.test.OboFormatTestBasics;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.FrameStructureException;
import org.obolibrary.oboformat.model.OBODoc;

public class SingleIntersectionOfTagTest extends OboFormatTestBasics {
	
	@Test(expected=FrameStructureException.class)
	public void testParseOBOFile() throws IOException, URISyntaxException {
		OBODoc obodoc = parseOBOFile("single_intersection_of_tag_test.obo");
		System.out.println("F:"+obodoc);
		assertTrue(obodoc.getTermFrames().size() == 2);
		Frame frame = obodoc.getTermFrames().iterator().next();
		assertNotNull(frame);
		
		writeOBO(obodoc, "test.obo"); // throws FrameStructureException
		
		//assertTrue(frame.getClause("name").getValue().equals("x1"));
	}

}
