package org.obolibrary.oboformat.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;

import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.FrameStructureException;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.InvalidXrefMapException;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.obolibrary.oboformat.parser.XrefExpander;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.obolibrary.oboformat.writer.OBOFormatWriter;

import junit.framework.TestCase;

public class XrefExpanderTest extends TestCase {
	
	public XrefExpanderTest() {
		super();
	}


	public XrefExpanderTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public static void testExpand() throws IOException, URISyntaxException, InvalidXrefMapException {
		OBODoc obodoc = parseOBOFile("treat_xrefs_test.obo");
		XrefExpander x = new XrefExpander(obodoc);
		x.expandXrefs();
		OBODoc tdoc = obodoc.getImportedOBODocs().iterator().next();
		assertTrue(tdoc.getTermFrames().size() > 0);
		for (Frame f : tdoc.getTermFrames()) {
			System.out.println(f);
		}
		assertTrue(tdoc.getTermFrame("ZFA:0001689").getClauses(OboFormatTag.TAG_INTERSECTION_OF).size() == 2);
		assertTrue(tdoc.getTermFrame("EHDAA:571").getClause(OboFormatTag.TAG_IS_A).getValue().equals("UBERON:0002539"));
		assertTrue(tdoc.getTermFrame("UBERON:0006800").getClause(OboFormatTag.TAG_IS_A).getValue().equals("CARO:0000008"));
		//assertTrue(frame.getClause("name").getValue().equals("x1"));
	}

	
	public static OBODoc parseOBOFile(String fn) throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parse("test_resources/"+fn);
		return obodoc;
	}


}
