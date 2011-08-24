package org.obolibrary.oboformat.test;

import static junit.framework.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.obolibrary.obo2owl.test.OboFormatTestBasics;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;

public class SimpleGOTest extends OboFormatTestBasics {
	
	@Test
	public void testParseOBOFile() throws IOException {
		OBODoc obodoc = parseOBOFile("simplego.obo");
		System.out.println("F:"+obodoc);
		assertTrue(obodoc.getTermFrames().size() == 3);
		Frame frame = obodoc.getTermFrames().iterator().next();
		assertNotNull(frame);
		//assertTrue(frame.getClause("name").getValue().equals("x1"));
	}

}
