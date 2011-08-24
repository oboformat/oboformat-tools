package org.obolibrary.obo2owl.test;

import static junit.framework.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class Obo2OwlTest extends OboFormatTestBasics {

	@Test
	public void testConvertCARO() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		OWLOntology owlOnt = convertOBOFile("caro.obo");
		assertNotNull(owlOnt);
	}
	
	@Test
	public void testConvertXPWithQV() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		OWLOntology owlOnt = convertOBOFile("testqvs.obo");
		assertNotNull(owlOnt);
	}
	
	private OWLOntology convertOBOFile(String fn) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		return convert(parseOBOFile(fn), fn);
	}

}
