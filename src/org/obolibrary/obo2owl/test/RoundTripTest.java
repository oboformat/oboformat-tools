package org.obolibrary.obo2owl.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.obolibrary.obo2owl.Obo2Owl;
import org.obolibrary.obo2owl.Owl2Obo;
import org.obolibrary.oboformat.diff.Diff;
import org.obolibrary.oboformat.diff.OBODocDiffer;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.FrameStructureException;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.obolibrary.oboformat.writer.OBOFormatWriter;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import junit.framework.TestCase;

public class RoundTripTest extends TestCase {

	static OWLOntologyManager manager;
	static OWLDataFactory df;
	static OWLOntology ontology;
	
	
	public static List<Diff> roundTripOBOURL(String fn, boolean isExpectRoundtrip) throws IOException, OWLOntologyCreationException {
		OBODoc obodoc = parseOBOURL(fn);
		return roundTripOBODoc(obodoc, isExpectRoundtrip);
	}
	
	public static List<Diff> roundTripOBOFile(String fn, boolean isExpectRoundtrip) throws IOException, OWLOntologyCreationException {
		OBODoc obodoc = parseOBOFile(fn);
		return roundTripOBODoc(obodoc, isExpectRoundtrip);
	}
	
	public static List<Diff> roundTripOBODoc(OBODoc obodoc, boolean isExpectRoundtrip) throws OWLOntologyCreationException {

		Logger.getRootLogger().setLevel(Level.ERROR);

		Obo2Owl bridge = new Obo2Owl();
		OWLOntology oo = bridge.convert(obodoc);
		
		Owl2Obo revbridge = new Owl2Obo();
		OBODoc obodoc2 = revbridge.convert(oo);
		
		try {
			obodoc2.check();
		} catch (FrameStructureException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			assertTrue(false);
		}
		
		OBOFormatWriter ow = new OBOFormatWriter();
		try {
			ow.write(obodoc2, "/tmp/roundtrip.obo");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		OBODocDiffer dd = new OBODocDiffer();
		List<Diff> diffs = dd.getDiffs(obodoc, obodoc2);
		if (isExpectRoundtrip) {
			for (Diff diff : diffs) {
				System.out.println(diff);
			}
			assertTrue(diffs.size() == 0); 
		}
		return diffs;
	}
	
	public static OBODoc parseOBOURL(String fn) throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parseURL(fn);
		assertTrue(obodoc.getTermFrames().size() > 0);
		return obodoc;
	}
	public static OBODoc parseOBOFile(String fn) throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parse(fn);
		assertTrue(obodoc.getTermFrames().size() > 0);
		return obodoc;
	}


}
