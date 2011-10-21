package org.obolibrary.obo2owl.test;

import static junit.framework.Assert.*;

import java.util.Collection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;

public class Owl2OboDefTest extends OboFormatTestBasics {

	@BeforeClass
	public static void beforeClass() {
		Logger.getRootLogger().setLevel(Level.ERROR);
	}
	
	@Test
	public void testConversion() throws Exception{
		OBODoc doc = convert(parseOWLFile("deftest.owl"));
		
		Frame f = doc.getTermFrame("PR:000005307");
		System.out.println("F="+f);
		Clause rc = f.getClause(OboFormatTag.TAG_NAME);
		assertTrue(rc.getValue().equals("CCAAT/enhancer-binding protein alpha"));
		Collection<Clause> defs = f.getClauses(OboFormatTag.TAG_DEF);
		assertTrue(defs.size() == 1);
		Collection<Xref> xrefs = defs.iterator().next().getXrefs();
		assertTrue(xrefs.size() == 2);
		
		writeOBO(doc, "deftest.owl.obo");
	}
}
