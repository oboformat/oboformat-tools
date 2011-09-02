package org.obolibrary.oboformat.test;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.obolibrary.obo2owl.test.OboFormatTestBasics;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;

public class OboEscapeCharsTest extends OboFormatTestBasics {

	@Test
	public void testEscapeChars() throws IOException {
		OBODoc obodoc = parseOBOFile("escape_chars_test.obo");
		
		Collection<Frame> frames = obodoc.getTermFrames();
		assertEquals(2, frames.size());
		Iterator<Frame> iterator = frames.iterator();
		Frame f1 = iterator.next();
		assertEquals("GO:0033942", f1.getId());
		Clause nameClause = f1.getClause(OboFormatTag.TAG_NAME);
		assertEquals("4-alpha-D-{(1->4)-alpha-D-glucano}trehalose trehalohydrolase activity", nameClause.getValue());
		
		Frame f2 = iterator.next();
		assertEquals("CL:0000096", f2.getId());
		Clause defClause = f2.getClause(OboFormatTag.TAG_DEF);
		assertEquals("bla bla .\"", defClause.getValue());
		
		Clause commentClause = f2.getClause(OboFormatTag.TAG_COMMENT);
		assertEquals("bla bla bla.\nbla bla (bla).", commentClause.getValue());
	}
}
