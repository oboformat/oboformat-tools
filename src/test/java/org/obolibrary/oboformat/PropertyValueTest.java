package org.obolibrary.oboformat;

import static junit.framework.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.obolibrary.obo2owl.OboFormatTestBasics;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.InvalidXrefMapException;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;

public class PropertyValueTest extends OboFormatTestBasics {

	@Test
	public void testExpand() throws IOException, URISyntaxException, InvalidXrefMapException {
		OBODoc obodoc = parseOBOFile("property_value_test.obo");
		Clause propertyValue = obodoc.getTermFrame("UBERON:0004657").getClause(OboFormatTag.TAG_PROPERTY_VALUE);
		assertEquals("IAO:0000412", propertyValue.getValue());
		assertEquals("http://purl.obolibrary.org/obo/uberon.owl", propertyValue.getValue2());
	}
}
