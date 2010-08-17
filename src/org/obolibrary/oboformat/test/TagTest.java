package org.obolibrary.oboformat.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatParser;

import junit.framework.TestCase;

public class TagTest extends TestCase {
	
	


	public TagTest() {
		super();
	}



	public TagTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public static void testParseOBOFile() throws IOException {
		OBODoc obodoc = parseOBOFile("test.obo");
		System.out.println("F:"+obodoc);
		assertTrue(obodoc.getTermFrames().size() == 3);
		assertTrue(obodoc.getTypedefFrames().size() == 1);
		Frame frame = obodoc.getTermFrames().iterator().next();
		//assertTrue(frame.getClause("name").getValue().equals("x1"));
	}
	
	public static void testParseOBOFile2() throws IOException {
		OBODoc obodoc = parseOBOFile("testqvs.obo");
		System.out.println("F:"+obodoc);
		assertTrue(obodoc.getTermFrames().size() == 4);
		assertTrue(obodoc.getTypedefFrames().size() == 1);
		for (Frame f : obodoc.getTermFrames()) {
			System.out.println(f);
		}
	}

	public static void testParseOBODoc() {
		OBODoc obodoc = parseOBODoc("[Term]\nid: x\nname: foo\n\n\n[Term]\nid: y\nname: y");
		System.out.println(obodoc);
		assertTrue(obodoc.getTermFrames().size() == 2);
		Frame frame = obodoc.getTermFrame("x");
		System.out.println(frame);
		assertTrue(frame.getClause("name").getValue().equals("foo"));
	}

	public static void testParseFrames() {
		OBODoc obodoc = parseFrames("[Term]\nid: x\nname: foo");
		assertTrue(obodoc.getTermFrames().size() == 1);
		Frame frame = obodoc.getTermFrames().iterator().next();
		for (Clause cl : frame.getClauses()) {
			System.out.println(cl);
		}
		assertTrue(frame.getClause("name").getValue().equals("foo"));
	}

	public static void testParseDefTag() {
		Clause cl = parseLine("def: \"a b c\" [foo:1, bar:2]");
		System.out.println("DEF:"+cl);
		assertTrue(cl.getTag().equals("def"));
		assertTrue(cl.getValue().equals("a b c"));
		assertTrue(cl.getValues().size() == 3);
	}
	public static void testParseDefTag2() {
		Clause cl = parseLine("def: \"a b c\" [foo:1 \"blah blah\", bar:2]");
		System.out.println("DEF2:"+cl);
		assertTrue(cl.getTag().equals("def"));
		assertTrue(cl.getValue().equals("a b c"));
	}

	public static void testParseCreationDateTag() {
		Clause cl = parseLine("creation_date: 2009-04-28T10:29:37Z");
		assertTrue(cl.getTag().equals("creation_date"));
		System.out.println("cr:"+cl.getValue());
	}

	public static void testParseNameTag() {
		Clause cl = parseLine("name: a b c");
		assertTrue(cl.getTag().equals("name"));
		assertTrue(cl.getValue().equals("a b c"));
	}
	
	public static void testParseNameTag2() {
		Clause cl = parseLine("name:    a b c");
		assertTrue(cl.getTag().equals("name"));
		System.out.println("n2 ='"+cl.getValue()+"'");
		assertTrue(cl.getValue().equals("a b c"));
	}
	
	public static void testParseNamespaceTag() {
		Clause cl = parseLine("namespace: foo");
		assertTrue(cl.getTag().equals("namespace"));
		assertTrue(cl.getValue().equals("foo"));
	}
	
	public static void testParseIsATag() {
		Clause cl = parseLine("is_a: x ! foo");
		assertTrue(cl.getTag().equals("is_a"));
		assertTrue(cl.getValue().equals("x"));
	}
	
	public static Clause parseLine(String line) {
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
	
	public static OBODoc parseFrames(String s) {
		StringReader sr = new StringReader(s);
		OBOFormatParser p = new OBOFormatParser();
		BufferedReader br = new BufferedReader(sr);
		p.setReader(br);
		
		OBODoc obodoc = new OBODoc();
		p.parseTermFrame(obodoc);
		return obodoc;
	}
	
	public static OBODoc parseOBODoc(String s) {
		StringReader sr = new StringReader(s);
		OBOFormatParser p = new OBOFormatParser();
		BufferedReader br = new BufferedReader(sr);
		p.setReader(br);
		
		OBODoc obodoc = new OBODoc();
		p.parseOBODoc(obodoc);
		return obodoc;
	}
	
	public static OBODoc parseOBOFile(String fn) throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parse("test_resources/"+fn);
		return obodoc;
	}


}
