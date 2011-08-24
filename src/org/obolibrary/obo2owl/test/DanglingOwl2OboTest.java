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

public class DanglingOwl2OboTest extends OboFormatTestBasics {

	@BeforeClass
	public static void beforeClass() {
		Logger.getRootLogger().setLevel(Level.ERROR);		
	}
	
	@Test
	public void testConversion() throws Exception{
		OBODoc doc = convert(parseOWLFile("dangling_owl2_obo_test.owl"));
		
		Frame f = doc.getTermFrame("UBERON:0000020");
		System.out.println("F="+f);
		Clause rc = f.getClause("name");
		assertTrue(rc.getValue().equals("sense organ"));
		Collection<Clause> ics = f.getClauses("intersection_of");
		assertTrue(ics.size() == 2);
		
		writeOBO(doc, "dangling_owl2_obo_test.owl.obo");
	}
}
