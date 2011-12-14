package org.obolibrary.oboformat.test;

import static junit.framework.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.obolibrary.obo2owl.test.OboFormatTestBasics;
import org.obolibrary.oboformat.model.OBODoc;

public class ChebiXRefTest extends OboFormatTestBasics {

	@Test
	public void testExpand() throws IOException {
		OBODoc obodoc = parseOBOFile("chebi_problematic_xref.obo");
		assertNotNull(obodoc);
	}
}