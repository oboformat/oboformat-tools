package org.obolibrary.obo2owl.test;

import org.junit.Test;

public class RoundTripXrefTest extends RoundTripTest {
	
	@Test
	public void testRoundTrip() throws Exception {
		roundTripOBOFile("xref_annotation.obo", true);		
	}
	
}
