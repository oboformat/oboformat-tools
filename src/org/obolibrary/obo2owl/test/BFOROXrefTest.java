package org.obolibrary.obo2owl.test;

import java.io.IOException;
import java.util.Collection;

import org.obolibrary.obo2owl.Obo2Owl;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import junit.framework.TestCase;

public class BFOROXrefTest extends TestCase {

	public static void testConvertXPs() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		OWLOntology owlOnt = convertOBOFile("rel_xref_test.obo");
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
		OWLOntology ontology = bridge.convert(obodoc);
		IRI outputStream = IRI.create("file:///tmp/"+fn+".owl");
		System.out.println("saving to "+outputStream);
		OWLOntologyFormat format = new RDFXMLOntologyFormat();
		manager.saveOntology(ontology, format, outputStream);
		return ontology;
	}

}
