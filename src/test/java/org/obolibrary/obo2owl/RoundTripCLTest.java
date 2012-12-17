package org.obolibrary.obo2owl;

import org.junit.Test;

public class RoundTripCLTest extends RoundTripTest {

	@Test
	public void testRoundTrip() throws Exception {
		roundTripOBOURL("http://obo.cvs.sourceforge.net/*checkout*/obo/obo/ontology/anatomy/cell_type/cell.obo", true);		
	}
	
	
}
