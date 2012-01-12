package org.obolibrary.obo2owl;

import static junit.framework.Assert.*;

import java.io.File;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

public class GetLoadedOntologyTest {

	@Test
	public void testConvert() throws Exception {

		IRI ontIRI = IRI.create("http://example.org/");
		IRI docIRI = IRI.create(new File("src/test/resources/simple.owl"));
		OWLOntologyIRIMapper iriMapper = 
			new SimpleIRIMapper(ontIRI, docIRI);

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		manager.addIRIMapper(iriMapper);
		
		OWLOntology origOnt = manager.loadOntology(ontIRI);
		System.out.println(origOnt.getOntologyID().getOntologyIRI());
		assertNotNull(origOnt);
		assertEquals(1, manager.getOntologies().size());
		assertNull(origOnt.getOntologyID().getVersionIRI());
		assertTrue(origOnt.getAxiomCount() > 0);

		OWLOntology newOnt = manager.getOntology(ontIRI);
		assertNotNull(newOnt); // SUCCEEDS
		
	}

	
}
