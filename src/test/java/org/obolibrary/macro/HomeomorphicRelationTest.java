package org.obolibrary.macro;

import static junit.framework.Assert.*;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.junit.BeforeClass;
import org.junit.Test;
import org.obolibrary.macro.MacroExpansionVisitor;
import org.obolibrary.obo2owl.OboFormatTestBasics;
import org.obolibrary.oboformat.model.OBODoc;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class HomeomorphicRelationTest extends OboFormatTestBasics {

	@BeforeClass
	public static void beforeClass() {
		Logger.getRootLogger().setLevel(Level.ALL);
	}
	
	@Test
	public void testExpand() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		OWLOntology owlOnt = convertOBOFile("homrel.obo");
		assertNotNull(owlOnt);
	}
	
	private OWLOntology convertOBOFile(String fn) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
		return convert(parseOBOFile(fn), fn);
	}

	protected OWLOntology convert(OBODoc obodoc, String fn) throws OWLOntologyCreationException, OWLOntologyStorageException {
		OWLOntology ontology = convert(obodoc);

		MacroExpansionVisitor mev = 
			new MacroExpansionVisitor(ontology);
		OWLOntology outputOntology = mev.expandAll();
		
		writeOWL(ontology, fn, new ManchesterOWLSyntaxOntologyFormat());
		return outputOntology;
	}

}
