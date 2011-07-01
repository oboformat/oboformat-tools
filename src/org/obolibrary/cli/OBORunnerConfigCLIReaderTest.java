package org.obolibrary.cli;

import java.util.Collection;

import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyFormat;

import junit.framework.TestCase;

public class OBORunnerConfigCLIReaderTest extends TestCase {

	public void testReadConfig() {
		OBORunnerConfiguration config = OBORunnerConfigCLIReader.readConfig(new String[]{"-o","obi","-x","http://some.url"});
		assertEquals(new Boolean(true), config.isOboToOwl.getValue());
		assertEquals("obi", config.outFile.getValue());
		Collection<String> value = config.paths.getValue();
		assertEquals(1, value.size());
		assertEquals("http://some.url", value.toArray()[0]);
		assertEquals(new Boolean(true), config.isExpandMacros.getValue());
	}

	
	public void testReadConfig2() {
		OBORunnerConfiguration config = OBORunnerConfigCLIReader.readConfig(new String[]{"-o","obi","--to","RDF"});
		assertEquals(new Boolean(true), config.isOboToOwl.getValue());
		assertEquals("obi", config.outFile.getValue());
		OWLOntologyFormat format = config.format.getValue();
		assertEquals(RDFXMLOntologyFormat.class.getName(), format.getClass().getName());
		assertEquals(new Boolean(false), config.isExpandMacros.getValue());
	}
}
