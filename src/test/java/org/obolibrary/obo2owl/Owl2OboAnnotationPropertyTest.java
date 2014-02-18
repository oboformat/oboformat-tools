package org.obolibrary.obo2owl;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.semanticweb.owlapi.model.OWLOntology;


public class Owl2OboAnnotationPropertyTest extends OboFormatTestBasics {

	@Test
	public void testOwl2Obo() throws Exception {
		OWLOntology owl = parseOWLFile("subAnnotProp.owl");
		
		OBODoc obo = convert(owl);
		
		Collection<Frame> relations = obo.getTypedefFrames();
		assertEquals(1, relations.size());
		final Frame frame = relations.iterator().next();
		Collection<Clause> clauses = frame.getClauses();
		assertEquals(2, clauses.size());
		for (Clause clause : clauses) {
			if (OboFormatTag.TAG_ID.getTag().equals(clause.getTag())) {
				assertEquals("OP:001", clause.getValue());
			}
			else if (OboFormatTag.TAG_PROPERTY_VALUE.getTag().equals(clause.getTag())) {
				Collection<Object> values = clause.getValues();
				assertEquals(3, values.size());
				assertArrayEquals(new String[]{"AP:002","foo","xsd:string"}, values.toArray());
			}
			else {
				fail("Unexpected clause: "+clause);
			}
		}
	}
	
}
