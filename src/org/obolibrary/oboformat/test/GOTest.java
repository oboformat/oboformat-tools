package org.obolibrary.oboformat.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatParser;

import junit.framework.TestCase;

public class GOTest extends TestCase {

	public GOTest() {
		super();
	}

	public GOTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public static void testParseGO() throws IOException {
		OBODoc obodoc = parseOBOURL("http://geneontology.org/ontology/obo_format_1_2/gene_ontology_ext.obo");
		//System.out.println("F:"+obodoc);
		System.out.println("term frames:"+obodoc.getTermFrames().size());
		System.out.println("typedef frames:"+obodoc.getTermFrames().size());
		assertTrue(obodoc.getTermFrames().size() > 3000);
		//assertTrue(frame.getClause("name").getValue().equals("x1"));
	}
	
	public static OBODoc parseOBOURL(String url) throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parseURL(url);
		return obodoc;
	}


}
