package org.obolibrary.oboformat.test;

import static junit.framework.Assert.*;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.obolibrary.obo2owl.test.OboFormatTestBasics;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatParser;

public class CAROTest extends OboFormatTestBasics {
	
	@Test
	public void testParseCARO() throws IOException {
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
	
}
