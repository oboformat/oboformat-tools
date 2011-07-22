package org.obolibrary.obo2owl.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.obolibrary.obo2owl.Obo2Owl;
import org.obolibrary.obo2owl.Owl2Obo;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.writer.OBOFormatWriter;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import junit.framework.TestCase;

public class Owl2OboDefTest extends TestCase {

	public static void testConversion() throws Exception{

		Logger.getRootLogger().setLevel(Level.ERROR);
		Obo2Owl obo2owl = new Obo2Owl();
	
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager(); // persist?
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create("file:test_resources/deftest.owl"));

		Owl2Obo bridge = new Owl2Obo();
		
		OBODoc doc = bridge.convert(ontology);
		
		Frame f = doc.getTermFrame("PR:000005307");
		System.out.println("F="+f);
		Clause rc = f.getClause("name");
		assertTrue(rc.getValue().equals("CCAAT/enhancer-binding protein alpha"));
		Collection<Clause> defs = f.getClauses("def");
		assertTrue(defs.size() == 1);
		Collection<Xref> xrefs = defs.iterator().next().getXrefs();
		assertTrue(xrefs.size() == 2);
		
		OBOFormatWriter oboWriter = new OBOFormatWriter();
		
		oboWriter.write(doc, "test_resources/deftest.owl.obo");
		
	}
}
