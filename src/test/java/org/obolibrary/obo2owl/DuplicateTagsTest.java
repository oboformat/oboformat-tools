package org.obolibrary.obo2owl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.semanticweb.owlapi.model.OWLOntology;

public class DuplicateTagsTest extends OboFormatTestBasics {
	
	static {
		Logger.getRootLogger().setLevel(Level.INFO);
	}

	@Test
	public void test() throws Exception {
		
		OWLOntology owl = parseOWLFile("duplicate-def.ofn");
		final List<Clause> duplicates = new ArrayList<Clause>(); 
		
		Owl2Obo owl2Obo = new Owl2Obo() {

			@Override
			protected boolean handleDuplicateClause(Frame frame, Clause clause) {
				duplicates.add(clause);
				return super.handleDuplicateClause(frame, clause);
			}
			
		};
		OBODoc convert = owl2Obo.convert(owl);
		
		assertEquals(1, duplicates.size());
		
		// test that no exception is thrown during write.
		renderOboToString(convert);
	}

}
