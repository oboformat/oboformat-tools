package org.obolibrary.obo2owl;

import static junit.framework.Assert.*;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntology;

public class CHEBITest extends OboFormatTestBasics {

	@Test
	public void testConvertXPs() throws Exception {
		OWLOntology owlOnt = 
			convertOBOFile("ftp://ftp.ebi.ac.uk/pub/databases/chebi/ontology/chebi.obo");
		assertNotNull(owlOnt);
	}
	
	private OWLOntology convertOBOFile(String fn) throws Exception {
		return convert(parseOBOURL(fn), "chebi");
	}

}
