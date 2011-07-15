package org.obolibrary.oboformat.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;

import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.FrameStructureException;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.obolibrary.oboformat.writer.OBOFormatWriter;

import junit.framework.TestCase;

public class SingleIntersectionOfTagTest extends TestCase {
	
	public SingleIntersectionOfTagTest() {
		super();
	}


	public SingleIntersectionOfTagTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public static void testParseOBOFile() throws IOException, URISyntaxException {
		OBODoc obodoc = parseOBOFile("single_intersection_of_tag_test.obo");
		System.out.println("F:"+obodoc);
		assertTrue(obodoc.getTermFrames().size() == 2);
		Frame frame = obodoc.getTermFrames().iterator().next();
		OBOFormatWriter w = new OBOFormatWriter();
		try {
			w.write(obodoc, "/tmp/test.obo");
			assertTrue(false);
		}
		catch (FrameStructureException e) {
			assertTrue(true);
		}
		//assertTrue(frame.getClause("name").getValue().equals("x1"));
	}

	
	public static OBODoc parseOBOFile(String fn) throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parse("test_resources/"+fn);
		return obodoc;
	}


}
