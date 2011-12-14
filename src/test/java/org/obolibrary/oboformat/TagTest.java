package org.obolibrary.oboformat;

import static junit.framework.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;
import org.obolibrary.obo2owl.OboFormatTestBasics;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;

public class TagTest extends OboFormatTestBasics {
	
	@Test
	public void testParseOBOFile() throws IOException {
		OBODoc obodoc = parseOBOFile("test.obo");
		System.out.println("F:"+obodoc);
		assertTrue(obodoc.getTermFrames().size() == 4);
		assertTrue(obodoc.getTypedefFrames().size() == 1);
		Frame frame = obodoc.getTermFrames().iterator().next();
		assertNotNull(frame);
		//assertTrue(frame.getClause("name").getValue().equals("x1"));
	}
	
	@Test
	public void testParseOBOFile2() throws IOException {
		OBODoc obodoc = parseOBOFile("testqvs.obo");
		System.out.println("F:"+obodoc);
		assertTrue(obodoc.getTermFrames().size() == 4);
		assertTrue(obodoc.getTypedefFrames().size() == 1);
		for (Frame f : obodoc.getTermFrames()) {
			System.out.println(f);
		}
	}

	@Test
	public void testParseOBODoc() {
		OBODoc obodoc = parseOBODoc("[Term]\nid: x\nname: foo\n\n\n[Term]\nid: y\nname: y");
		System.out.println(obodoc);
		assertTrue(obodoc.getTermFrames().size() == 2);
		Frame frame = obodoc.getTermFrame("x");
		System.out.println(frame);
		assertTrue(frame.getClause(OboFormatTag.TAG_NAME).getValue().equals("foo"));
	}

	@Test
	public void testParseFrames() {
		OBODoc obodoc = parseFrames("[Term]\nid: x\nname: foo");
		assertTrue(obodoc.getTermFrames().size() == 1);
		Frame frame = obodoc.getTermFrames().iterator().next();
		for (Clause cl : frame.getClauses()) {
			System.out.println(cl);
		}
		assertTrue(frame.getClause(OboFormatTag.TAG_NAME).getValue().equals("foo"));
	}
	
	@Test
	public void testParseDefTag() {
		Clause cl = parseLine("def: \"a b c\" [foo:1, bar:2]");
		System.out.println("DEF:"+cl);
		assertTrue(cl.getTag().equals(OboFormatTag.TAG_DEF.getTag()));
		assertTrue(cl.getValue().equals("a b c"));
		assertTrue(cl.getValues().size() == 1);
	}
	
	@Test
	public void testParseDefTag2() {
		Clause cl = parseLine("def: \"a b c\" [foo:1 \"blah blah\", bar:2]");
		System.out.println("DEF2:"+cl);
		assertTrue(cl.getTag().equals(OboFormatTag.TAG_DEF.getTag()));
		assertTrue(cl.getValue().equals("a b c"));
	}

	@Test
	public void testParseCreationDateTag() {
		Clause cl = parseLine("creation_date: 2009-04-28T10:29:37Z");
		assertTrue(cl.getTag().equals(OboFormatTag.TAG_CREATION_DATE.getTag()));
		System.out.println("cr:"+cl.getValue());
	}

	@Test
	public void testParseNameTag() {
		Clause cl = parseLine("name: a b c");
		assertTrue(cl.getTag().equals(OboFormatTag.TAG_NAME.getTag()));
		assertTrue(cl.getValue().equals("a b c"));
	}
	
	@Test
	public void testParseNameTag2() {
		Clause cl = parseLine("name:    a b c");
		assertTrue(cl.getTag().equals(OboFormatTag.TAG_NAME.getTag()));
		System.out.println("n2 ='"+cl.getValue()+"'");
		assertTrue(cl.getValue().equals("a b c"));
	}
	
	@Test
	public void testParseNamespaceTag() {
		Clause cl = parseLine("namespace: foo");
		assertTrue(cl.getTag().equals(OboFormatTag.TAG_NAMESPACE.getTag()));
		assertTrue(cl.getValue().equals("foo"));
	}
	
	@Test
	public void testParseIsATag() {
		Clause cl = parseLine("is_a: x ! foo");
		assertTrue(cl.getTag().equals(OboFormatTag.TAG_IS_A.getTag()));
		assertTrue(cl.getValue().equals("x"));
	}
	
	private Clause parseLine(String line) {
		StringReader sr = new StringReader(line);
		OBOFormatParser p = new OBOFormatParser();
		BufferedReader br = new BufferedReader(sr);
		p.setReader(br);
		
		Clause cl = new Clause();
		if (p.parseTermFrameClause(cl)) {
			return cl;
		}
		return null;
	}
	
	private OBODoc parseFrames(String s) {
		StringReader sr = new StringReader(s);
		OBOFormatParser p = new OBOFormatParser();
		BufferedReader br = new BufferedReader(sr);
		p.setReader(br);
		
		OBODoc obodoc = new OBODoc();
		p.parseTermFrame(obodoc);
		return obodoc;
	}
	
	private OBODoc parseOBODoc(String s) {
		StringReader sr = new StringReader(s);
		OBOFormatParser p = new OBOFormatParser();
		BufferedReader br = new BufferedReader(sr);
		p.setReader(br);
		
		OBODoc obodoc = new OBODoc();
		p.parseOBODoc(obodoc);
		return obodoc;
	}
}
