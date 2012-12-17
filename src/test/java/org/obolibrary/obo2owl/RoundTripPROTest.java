package org.obolibrary.obo2owl;

import org.junit.Test;

public class RoundTripPROTest extends RoundTripTest {

	@Test
	public void testRoundTrip() throws Exception {
		roundTripOBOURL("ftp://ftp.pir.georgetown.edu/databases/ontology/pro_obo/pro.obo", true);
		
	}
	
	
}
