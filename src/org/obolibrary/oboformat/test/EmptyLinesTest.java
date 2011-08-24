package org.obolibrary.oboformat.test;

import java.io.IOException;
import java.util.Collection;

import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatParser;

import junit.framework.TestCase;


/**
 *  This was inspired by a cross product ontology file which has no header, 
 *  but an empty line as first entry. 
 *  Original url: http://www.geneontology.org/scratch/xps/biological_process_xp_plant_anatomy.obo
 */
public class EmptyLinesTest extends TestCase {

	public void testEmptyFirstLine() throws IOException {
		OBOFormatParser p = new OBOFormatParser();
		OBODoc obodoc = p.parse("test_resources/empty_lines.obo");
		assertNotNull("The obodoc has to be created.", obodoc);
		
		Collection<Frame> frames = obodoc.getTermFrames();
		assertEquals(1, frames.size());
		assertEquals("GO:0009555", frames.iterator().next().getId());
	}
}
