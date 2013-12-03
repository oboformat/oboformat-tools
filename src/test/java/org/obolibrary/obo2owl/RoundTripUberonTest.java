package org.obolibrary.obo2owl;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.Test;

public class RoundTripUberonTest extends RoundTripTest {

	/**
	 * This test uses the editors version of Uberon.
	 * 
	 * Note that this version sometimes contains syntax errors (these are always checked fully before public release)
	 * 
	 * Note also that uberon sometimes 'pushes the boundaries' of what is possible in obo-format, so this test will
	 * be especially sensitive
	 * 
	 * This test should never be in allproductiontests for this reason
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRoundTripUberonEditVersion() throws Exception {
        Logger log = LogManager.getLogManager().getLogger("");
        for (Handler h : log.getHandlers()) {
            h.setLevel(Level.WARNING);
        }
		roundTripOBOURL("https://github.com/cmungall/uberon/blob/master/uberon_edit.obo?raw=true", true);		
	}
	
	
}
