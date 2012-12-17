package org.obolibrary.oboformat;

import static junit.framework.Assert.*;

import org.junit.Test;
import org.obolibrary.obo2owl.OboFormatTestBasics;
import org.obolibrary.oboformat.model.OBODoc;

public class GOTest extends OboFormatTestBasics {

	@Test
	public void testParseGO() throws Exception {
		OBODoc obodoc = parseOBOURL("http://geneontology.org/ontology/obo_format_1_2/gene_ontology_ext.obo");
		//System.out.println("F:"+obodoc);
		System.out.println("term frames:"+obodoc.getTermFrames().size());
		System.out.println("typedef frames:"+obodoc.getTypedefFrames().size());
		assertTrue(obodoc.getTermFrames().size() > 3000);
	}
	
}
