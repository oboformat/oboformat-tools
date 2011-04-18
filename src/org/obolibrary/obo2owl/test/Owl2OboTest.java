package org.obolibrary.obo2owl.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.obolibrary.obo2owl.Obo2Owl;
import org.obolibrary.obo2owl.Owl2Obo;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.writer.OBOFormatWriter;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import junit.framework.TestCase;

public class Owl2OboTest extends TestCase {

	public static void testConversion() throws Exception{

		Logger.getRootLogger().setLevel(Level.ERROR);
		Obo2Owl obo2owl = new Obo2Owl();
		
		OWLOntology ontology = obo2owl.convert("test_resources/caro.obo");
		
		Owl2Obo bridge = new Owl2Obo();
		
		OBODoc doc = bridge.convert(ontology);
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("test_resources/caro_converted.obo")));
		
		OBOFormatWriter oboWriter = new OBOFormatWriter();
		
		oboWriter.write(doc, writer);
		
		writer.close();
		
	}
	
	
	
}
