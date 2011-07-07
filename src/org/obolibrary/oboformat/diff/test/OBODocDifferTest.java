package org.obolibrary.oboformat.diff.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import org.obolibrary.oboformat.diff.Diff;
import org.obolibrary.oboformat.diff.OBODocDiffer;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatParser;

import junit.framework.TestCase;

public class OBODocDifferTest extends TestCase {
	
	public OBODocDifferTest() {
		super();
	}



	public OBODocDifferTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public static void testIdentical() throws IOException {
		OBODoc obodoc1 = parseOBOFile("caro.obo");
		OBODoc obodoc2 = parseOBOFile("caro.obo");
		OBODocDiffer dd = new OBODocDiffer();
		List<Diff> diffs = dd.getDiffs(obodoc1, obodoc2);
		for (Diff diff : diffs) {
			System.out.println("Diff="+diff);
		}
		assertTrue(diffs.size() == 0);
	}
	public static void testDiff() throws IOException {
		OBODoc obodoc1 = parseOBOFile("caro.obo");
		OBODoc obodoc2 = parseOBOFile("caro_modified.obo");
		OBODocDiffer dd = new OBODocDiffer();
		List<Diff> diffs = dd.getDiffs(obodoc1, obodoc2);
		for (Diff diff : diffs) {
			System.out.println("MDiff="+diff);
		}
		System.out.println(diffs.size());
		assertTrue(diffs.size() == 18);
	}
	
	public static OBODoc parseOBOFile(String fn) throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parse("test_resources/"+fn);
		return obodoc;
	}


}
