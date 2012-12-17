package org.obolibrary.obo2owl;

import org.junit.Test;

public class RoundTripFlyAnatomyTest extends RoundTripTest {

	@Test
	public void testRoundTrip() throws Exception {
		roundTripOBOURL("http://obo.cvs.sourceforge.net/*checkout*/obo/obo/ontology/anatomy/gross_anatomy/animal_gross_anatomy/fly/fly_anatomy_XP.obo", true);
	}
	
}
