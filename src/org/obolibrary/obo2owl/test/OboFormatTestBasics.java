package org.obolibrary.obo2owl.test;

import static junit.framework.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import org.obolibrary.obo2owl.Obo2Owl;
import org.obolibrary.obo2owl.Owl2Obo;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatParser;
import org.obolibrary.oboformat.writer.OBOFormatWriter;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class OboFormatTestBasics {
	
	private final static File systemTempDir = new File(System.getProperty("java.io.tmpdir"));

	protected OBODoc parseOBOURL(String fn) throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parseURL(fn);
		assertTrue(obodoc.getTermFrames().size() > 0);
		return obodoc;
	}
	
	protected OBODoc parseOBOFile(String fn) throws IOException {
		return parseOBOFile(fn, false);
	}
	
	protected OBODoc parseOBOFile(String fn, boolean allowEmptyFrames) throws IOException {
		// TODO replace this with a mechanism not relying on the relative path
		OBODoc obodoc = parseOBOFile(new File("test_resources/"+fn));
		
		if (obodoc.getTermFrames().size() == 0 && !allowEmptyFrames) {
			fail("Term frames should not be empty.");
		}
		return obodoc;
	}
	
	protected OBODoc parseOBOFile(File file) throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parse(file.getCanonicalPath());
		return obodoc;
	}

	protected OWLOntology parseOWLFile(String fn) throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create("file:test_resources/"+fn));
		return ontology;
	}
	
	protected OWLOntology convert(OBODoc obodoc) throws OWLOntologyCreationException {
		Obo2Owl bridge = new Obo2Owl();
		OWLOntology ontology = bridge.convert(obodoc);
		return ontology;
	}
	
	protected OWLOntology convert(OBODoc obodoc, String filename) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
		OWLOntology ontology = convert(obodoc);
		
		writeOWL(ontology, filename);
		return ontology;
	}

	protected OBODoc convert(OWLOntology ontology) throws OWLOntologyCreationException {
		Owl2Obo bridge = new Owl2Obo();
		OBODoc doc = bridge.convert(ontology);
		return doc;
	}
	
	protected File writeOBO(OBODoc obodoc, String fn) throws IOException {
		if (!fn.toLowerCase().endsWith(".obo")) {
			fn += ".obo";
		}
		File file = new File(systemTempDir, fn);
		OBOFormatWriter oboWriter = new OBOFormatWriter();
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		oboWriter.write(obodoc, bw);
		bw.close();
		return file;
	}

	protected File writeOWL(OWLOntology ontology, String filename) throws OWLOntologyStorageException {
		return writeOWL(ontology, filename, new OWLXMLOntologyFormat());
	}
	
	protected File writeOWL(OWLOntology ontology, String filename, OWLOntologyFormat format) throws OWLOntologyStorageException {
		if (!filename.toLowerCase().endsWith(".owl")) {
			filename += ".owl";
		}
		File tempFile = new File(systemTempDir, filename);
		IRI iri = IRI.create(tempFile);
		
		OWLOntologyManager manager = ontology.getOWLOntologyManager();
		System.out.println("saving to "+iri);
		manager.saveOntology(ontology, format, iri);
		
		return tempFile;
	}
	
	protected static void renderOBO(OBODoc oboDoc) throws IOException {
		OBOFormatWriter writer = new OBOFormatWriter();
		writer.setCheckStructure(true);
		StringWriter out = new StringWriter();
		BufferedWriter stream = new BufferedWriter(out);
		writer.write(oboDoc, stream);
		stream.close();
		System.out.println(out.getBuffer());
	}
	
	protected IRI getIriByLabel(OWLOntology ontology, String label) {
		for (OWLAnnotationAssertionAxiom aa : ontology.getAxioms(AxiomType.ANNOTATION_ASSERTION)) {
			OWLAnnotationValue v = aa.getValue();
			OWLAnnotationProperty property = aa.getProperty();
			if (property.isLabel() && v instanceof OWLLiteral) {
				if (label.equals( ((OWLLiteral)v).getLiteral())) {
					OWLAnnotationSubject subject = aa.getSubject();
					if (subject instanceof IRI) {
						return (IRI)subject;
					}
				}
			}
		}
		return null;
	}
}
