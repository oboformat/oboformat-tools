package org.obolibrary.oboformat.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatParser;

import junit.framework.TestCase;

public class CAROTest extends TestCase {
	
	public CAROTest() {
		super();
	}



	public CAROTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public static void testParseCARO() throws IOException {
		OBODoc obodoc = parseOBOFile("caro.obo");
		System.out.println("F:"+obodoc);
		assertTrue(obodoc.getTermFrames().size() > 2);
		Frame cc = obodoc.getTermFrame("CARO:0000014");
		assertTrue(cc.getTagValue("name").equals("cell component"));
		assertTrue(cc.getTagValue("def").equals("Anatomical structure that is a direct part of the cell."));
		
		Clause dc = cc.getClause("def");
		System.out.println("dc="+dc);
		Collection<Xref> dcxs = dc.getXrefs();
		System.out.println("dcxs="+dcxs);
		assertTrue(dcxs.iterator().next().toString().equals("CARO:MAH"));
		/*
		Collection<Xref> defxrefs = cc.getTagXrefs("def");
		System.out.println("def xrefs = "+defxrefs);
		assertTrue(defxrefs.iterator().next().getIdref().equals("CARO:MAH"));
		*/
		//assertTrue(frame.getClause("name").getValue().equals("x1"));
	}
	
	public static OBODoc parseOBOFile(String fn) throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parse("test_resources/"+fn);
		return obodoc;
	}


}
