package org.obolibrary.macro.test;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.obolibrary.macro.MacroExpansionVisitor;
import org.obolibrary.obo2owl.Obo2Owl;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import junit.framework.TestCase;

public class GCITest extends TestCase {

	public static void testExpand() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		Logger.getRootLogger().setLevel(Level.ALL);
		OWLOntology owlOnt = convertOBOFile("gcitest.obo");
		
	}
	
	public static OBODoc parseOBOFile(String fn) throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parse("test_resources/"+fn);
		return obodoc;
	}

	public static OWLOntology convertOBOFile(String fn) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		return convert(parseOBOFile(fn), fn);
	}

	private static OWLOntology convert(OBODoc obodoc, String fn) throws OWLOntologyCreationException, OWLOntologyStorageException {
		Obo2Owl bridge = new Obo2Owl();
		OWLOntologyManager manager = bridge.getManager();
		OWLDataFactory df = manager.getOWLDataFactory();
		OWLOntology ontology = bridge.convert(obodoc);
		
		MacroExpansionVisitor mev = 
			new MacroExpansionVisitor(df,ontology, manager);
		OWLOntology outputOntology = mev.expandAll();
		
		IRI outputStream = IRI.create("file:///tmp/"+fn+".owl");
		System.out.println("saving to "+outputStream);
		OWLOntologyFormat format = new RDFXMLOntologyFormat();
		manager.saveOntology(outputOntology, format, outputStream);

		return outputOntology;
	}

}
