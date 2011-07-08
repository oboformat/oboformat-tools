package org.obolibrary.obo2owl.test;

import org.obolibrary.obo2owl.Obo2Owl;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.Frame.FrameType;
import org.obolibrary.oboformat.model.OBODoc;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import junit.framework.TestCase;

public class IDsTest extends TestCase {

	public static void testIDs() throws OWLOntologyCreationException{
		OBODoc doc = new OBODoc();
		doc.setHeaderFrame(new Frame(FrameType.HEADER));
		
		Obo2Owl bridge = new Obo2Owl();
		bridge.convert(doc);
	
		IRI iri= bridge.oboIdToIRI("GO:001");
		System.out.println(iri);
		assertTrue("http://purl.obolibrary.org/obo/GO_001".equals(iri.toString()));

		iri= bridge.oboIdToIRI("My_Ont:002");
		System.out.println(iri);

		assertTrue("http://purl.obolibrary.org/obo/My_Ont#_002".equals(iri.toString()));
		
		iri= bridge.oboIdToIRI("003");
		System.out.println(iri);
		assertTrue("http://purl.obolibrary.org/obo/TODO#003".equals(iri.toString()));

		iri= bridge.oboIdToIRI("part_of");
		System.out.println(iri);
		assertTrue("http://purl.obolibrary.org/obo/TODO#_part_of".equals(iri.toString()));

		
		iri= bridge.oboIdToIRI("OBO_REL:part_of");
		System.out.println(iri);
		assertTrue("http://purl.obolibrary.org/obo/OBO_REL#_part_of".equals(iri.toString()));

		iri= bridge.oboIdToIRI("http://purl.obolibrary.org/testont");
		System.out.println(iri);
		
		
	}
	
}
