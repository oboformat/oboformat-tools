package org.obolibrary.obo2owl.test;

import java.io.IOException;
import java.util.Collection;

import org.obolibrary.obo2owl.Obo2Owl;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.model.Xref;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import junit.framework.TestCase;

public class UnionOfTest extends TestCase {

	static OWLOntologyManager manager;
	static OWLDataFactory df;
	static OWLOntology ontology;
	
	public static void testUnion() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		OWLOntology owlOnt = convertOBOFile("taxon_union_terms.obo");	
		IRI iri = IRI.create("http://purl.obolibrary.org/obo/NCBITaxon_Union_0000000");
		OWLClass cls = df.getOWLClass(iri);
		boolean ok = false;
		for (OWLObject ec : cls.getEquivalentClasses(ontology)) {
			System.out.println(cls + " = " + ec);
			if (ec instanceof OWLObjectUnionOf) {
				ok = true;
			}
		}
		assertTrue(ok);
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
		manager = bridge.getManager();
		df = manager.getOWLDataFactory();
		 ontology = bridge.convert(obodoc);
		IRI outputStream = IRI.create("file:///tmp/"+fn+".owl");
		System.out.println("saving to "+outputStream);
		OWLOntologyFormat format = new OWLXMLOntologyFormat();
		manager.saveOntology(ontology, format, outputStream);
		return ontology;
	}

}
