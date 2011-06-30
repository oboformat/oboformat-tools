package org.obolibrary.cli;

import java.util.Collection;

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

}
