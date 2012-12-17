package org.obolibrary.obo2owl;

import static junit.framework.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.obolibrary.oboformat.diff.Diff;
import org.obolibrary.oboformat.diff.OBODocDiffer;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.FrameStructureException;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

public class RoundTripImportTest extends RoundTripTest {

	@Test
	public void testRoundTrip() throws Exception {
		OBODoc obodoc = parseOBOFile("import_test_main.obo", true);

		OWLOntologyIRIMapper iriMapper = 
			new SimpleIRIMapper(IRI.create("http://purl.obolibrary.org/obo/tests/import_test_imported.owl"),
					IRI.create(new File("src/test/resources/import_test_imported.owl")));

		Obo2Owl bridge = new Obo2Owl();
		System.out.println("M="+bridge.getManager());
		bridge.getManager().addIRIMapper(iriMapper);

		Collection<Clause> importClauses = obodoc.getHeaderFrame().getClauses(OboFormatTag.TAG_IMPORT);
		assertEquals(1, importClauses.size());

		assertEquals(0, obodoc.getImportedOBODocs().size());

		OWLOntology ontology = bridge.convert(obodoc);

		assertEquals(1, ontology.getImportsDeclarations().size());

		for (OWLSubClassOfAxiom a : ontology.getAxioms(AxiomType.SUBCLASS_OF, true)) {
			System.out.println("A="+a);
		}
		assertEquals(2, ontology.getAxioms(AxiomType.SUBCLASS_OF, true).size());

		// Convert back to obo
		OBODoc obodoc2 = convert(ontology);

		Collection<Clause> importClauses2 = obodoc2.getHeaderFrame().getClauses(OboFormatTag.TAG_IMPORT);
		System.out.println("TESTING IMPORTS..");
		for (Clause cl : importClauses2) {
			System.out.println(cl);
		}

		try {
			obodoc2.check();
		} catch (FrameStructureException exception) {
			exception.printStackTrace();
			fail("No syntax errors allowed");
		}

		try {
			writeOBO(obodoc2, "roundtrip.obo");
		} catch (IOException e) {
			e.printStackTrace();
			fail("No IOExceptions allowed");
		} 

		OBODocDiffer dd = new OBODocDiffer();
		List<Diff> diffs = dd.getDiffs(obodoc, obodoc2);
		for (Diff diff : diffs) {
			System.out.println(diff);
		}
		assertEquals("Expected no diffs", 0, diffs.size()); 

	}

}
