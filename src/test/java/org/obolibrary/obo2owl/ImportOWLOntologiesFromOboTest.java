package org.obolibrary.obo2owl;

import static junit.framework.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

/**
 * 
 * This class tests the scenario where an obo file includes import directives to external OWL files
 * 
 * @author cjm
 *
 */
public class ImportOWLOntologiesFromOboTest extends OboFormatTestBasics {
	
	private static final boolean useSystemOut = false;

	@BeforeClass
	public static void beforeClass() {
		Logger.getRootLogger().setLevel(Level.ERROR);		
	}
	
	@Test
	public void testConvert() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException, URISyntaxException {

		// PARSE TEST FILE
		OBODoc obodoc = parseOBOFile("import_test_main.obo");
		
		OWLOntologyIRIMapper iriMapper = 
			new SimpleIRIMapper(IRI.create("http://purl.obolibrary.org/obo/tests/import_test_imported.owl"),
					IRI.create(new File("src/test/resources/import_test_imported.owl")));

		Obo2Owl bridge = new Obo2Owl();
		if (useSystemOut) {
			System.out.println("M=" + bridge.getManager());
		}
		bridge.getManager().addIRIMapper(iriMapper);
		
		Collection<Clause> importClauses = obodoc.getHeaderFrame().getClauses(OboFormatTag.TAG_IMPORT);
		assertEquals(1, importClauses.size());

		assertEquals(0, obodoc.getImportedOBODocs().size());
		
		OWLOntology ontology = bridge.convert(obodoc);

		assertEquals(1, ontology.getImportsDeclarations().size());
		
		if (useSystemOut) {
			for (OWLSubClassOfAxiom a : ontology.getAxioms(AxiomType.SUBCLASS_OF, true)) {
				System.out.println("A="+a);
			}
		}
		assertEquals(2, ontology.getAxioms(AxiomType.SUBCLASS_OF, true).size());

	}

	
}
