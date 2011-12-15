package org.obolibrary.oboformat.writer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.obolibrary.obo2owl.OboFormatTestBasics;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;

/**
 * Tests for {@link OBOFormatWriter}.
 */
public class OBOFormatWriterTest extends OboFormatTestBasics {

	/**
	 * Test a special case of the specification. For intersections 
	 * put the genus before the differentia, instead of the default
	 * case-insensitive alphabetical ordering.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSortTermClausesIntersection_of() throws Exception {
		OBODoc oboDoc = parseOBOFile("equivtest.obo");
		Frame frame = oboDoc.getTermFrame("X:1");
		List<Clause> clauses = new ArrayList<Clause>(frame.getClauses(OboFormatTag.TAG_INTERSECTION_OF));
		OBOFormatWriter.sortTermClauses(clauses);
		assertEquals("Y:1", clauses.get(0).getValue());
		assertEquals("R:1", clauses.get(1).getValue());
		assertEquals("Z:1", clauses.get(1).getValue2());
	}
	
	/**
	 * Test for sorting clauses according to alphabetical case-insensitive 
	 * order. Prefer upper-case over lower case for equal strings. Prefer 
	 * shorter strings over longer strings.
	 */
	@Test
	public void testSortTermClausesSynonyms() {
		List<Clause> clauses = createSynonymClauses("cc","ccc","AAA","aaa","bbbb");
		OBOFormatWriter.sortTermClauses(clauses);
		assertEquals("AAA", clauses.get(0).getValue());
		assertEquals("aaa", clauses.get(1).getValue());
		assertEquals("bbbb", clauses.get(2).getValue());
		assertEquals("cc", clauses.get(3).getValue());
		assertEquals("ccc", clauses.get(4).getValue());
	}

	private List<Clause> createSynonymClauses(String...labels) {
		List<Clause> clauses = new ArrayList<Clause>(labels.length);
		for(String label : labels) {
			Clause clause = new Clause(OboFormatTag.TAG_SYNONYM, label);
			clauses.add(clause);
		}
		return clauses;
	}

}
