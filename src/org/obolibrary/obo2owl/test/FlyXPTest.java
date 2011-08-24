package org.obolibrary.obo2owl.test;

import static junit.framework.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class FlyXPTest extends OboFormatTestBasics {

	@Test
	public void testConvertXPs() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		OWLOntology owlOnt = 
			convertOBOFile("http://obo.cvs.sourceforge.net/*checkout*/obo/obo/ontology/anatomy/gross_anatomy/animal_gross_anatomy/fly/fly_anatomy_XP.obo");
		assertNotNull(owlOnt);
	}
	
	private OWLOntology convertOBOFile(String fn) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		return convert(parseOBOURL(fn), "flyxp");
	}

}
