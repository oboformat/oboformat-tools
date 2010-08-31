package org.obolibrary.oboformat.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatParser;

import junit.framework.TestCase;

public class SimpleGOTest extends TestCase {
	
	public SimpleGOTest() {
		super();
	}


	public SimpleGOTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public static void testParseOBOFile() throws IOException {
		OBODoc obodoc = parseOBOFile("simplego.obo");
		System.out.println("F:"+obodoc);
		assertTrue(obodoc.getTermFrames().size() == 3);
		Frame frame = obodoc.getTermFrames().iterator().next();
		//assertTrue(frame.getClause("name").getValue().equals("x1"));
	}

	
	public static OBODoc parseOBOFile(String fn) throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parse("test_resources/"+fn);
		return obodoc;
	}


}
