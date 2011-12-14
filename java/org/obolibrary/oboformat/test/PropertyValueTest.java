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

public class PropertyValueTest extends OboFormatTestBasics {

	@Test
	public void testExpand() throws IOException, URISyntaxException, InvalidXrefMapException {
		OBODoc obodoc = parseOBOFile("property_value_test.obo");

		for (Frame f : obodoc.getTermFrames()) {
			System.out.println(f);
			for (Clause c : f.getClauses()) {
				System.out.println(" C:"+c);
			}
		}
	
		//assertTrue(tdoc.getTermFrame("ZFA:0001689").getClauses(OboFormatTag.TAG_INTERSECTION_OF).size() == 2);
		//assertTrue(tdoc.getTermFrame("EHDAA:571").getClause(OboFormatTag.TAG_IS_A).getValue().equals("UBERON:0002539"));
		//assertTrue(tdoc.getTermFrame("UBERON:0006800").getClause(OboFormatTag.TAG_IS_A).getValue().equals("CARO:0000008"));
		//assertTrue(frame.getClause("name").getValue().equals("x1"));
	}


}
