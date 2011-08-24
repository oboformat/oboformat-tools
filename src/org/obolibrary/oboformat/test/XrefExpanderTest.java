package org.obolibrary.oboformat.test;

import static junit.framework.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.obolibrary.obo2owl.test.OboFormatTestBasics;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.InvalidXrefMapException;
import org.obolibrary.oboformat.parser.XrefExpander;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;

public class XrefExpanderTest extends OboFormatTestBasics {
	
	@Test
	public void testExpand() throws IOException, URISyntaxException, InvalidXrefMapException {
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

	@Test
	public void testExpandIntoSeparateBridges() throws IOException, URISyntaxException, InvalidXrefMapException {
		OBODoc obodoc = parseOBOFile("treat_xrefs_test.obo");
		XrefExpander x = new XrefExpander(obodoc, "bridge");
		x.expandXrefs();
		int n = 0;
		for (OBODoc tdoc : obodoc.getImportedOBODocs()) {
			//System.out.println("IMP:"+tdoc);
			Frame hf = tdoc.getHeaderFrame();
			if (hf == null) {
				continue;
			}
			Clause impClause = hf.getClause(OboFormatTag.TAG_ONTOLOGY);
			if (impClause == null) {
				continue;
			}
			String tid = impClause.getValue().toString().replace("bridge-", "");
			System.out.println("BRIDGE: "+tid);
			if (tid.equals("zfa")) {
				assertTrue(tdoc.getTermFrame("ZFA:0001689").getClauses(OboFormatTag.TAG_INTERSECTION_OF).size() == 2);
				n++;
			}
			if (tid.equals("ehdaa")) {
				assertTrue(tdoc.getTermFrame("EHDAA:571").getClause(OboFormatTag.TAG_IS_A).getValue().equals("UBERON:0002539"));
				n++;
			}
			if (tid.equals("caro")) {
				assertTrue(tdoc.getTermFrame("UBERON:0006800").getClause(OboFormatTag.TAG_IS_A).getValue().equals("CARO:0000008"));
				n++;
			}
		}
		assertTrue(n == 3);
		//assertTrue(frame.getClause("name").getValue().equals("x1"));
	}

}
