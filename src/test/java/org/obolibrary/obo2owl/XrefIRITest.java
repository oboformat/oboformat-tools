package org.obolibrary.obo2owl;

import static junit.framework.Assert.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.obolibrary.obo2owl.Owl2Obo;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class XrefIRITest extends OboFormatTestBasics {

	@BeforeClass
	public static void beforeClass() {
		Logger.getRootLogger().setLevel(Level.ERROR);
	}
	
	@Test
	public void testConversion() throws Exception{

		OWLOntology ontology = parseOWLFile("xrefIRItest.owl");
	
		OBODoc doc = convert(ontology);
		
		String oboFile = "xrefIRIroundtrip.owl";
		
		Frame tf = doc.getTermFrame("FOO:1");
		//System.out.println(tf);
		writeOBO(doc, oboFile);		
		
		
	}

	
}
