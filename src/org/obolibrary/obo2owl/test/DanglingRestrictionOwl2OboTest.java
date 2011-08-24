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

public class DanglingRestrictionOwl2OboTest extends OboFormatTestBasics {

	@BeforeClass
	public static void beforeClass() {
		Logger.getRootLogger().setLevel(Level.ERROR);		
	}
	
	@Test
	public void testConversion() throws Exception{

		// this is a test ontology that has had its imports axioms removed
		OBODoc doc = convert(parseOWLFile("dangling_restriction_test.owl"));
		
		Frame f = doc.getTermFrame("FUNCARO:0000014");
		System.out.println("F="+f);
		Clause rc = f.getClause("name");
		assertTrue(rc.getValue().equals("digestive system"));
		Collection<Clause> isas = f.getClauses("is_a");
		assertTrue(isas.size() == 1);
		Collection<Clause> rs = f.getClauses("relationship");
		assertTrue(rs.size() == 1);
		
		writeOBO(doc, "dangling_restriction_test.owl.obo");
		
	}
}
