package org.obolibrary.obo2owl.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllProductionTests extends TestCase {

	protected AllProductionTests(String name) {
		super(name);
	}
	
	public static Test suite() {
		TestSuite out = new TestSuite();
		out.addTestSuite(BFOROXrefTest.class);
		out.addTestSuite(DanglingOwl2OboTest.class);
		out.addTestSuite(DanglingRoundTripTest.class);
		out.addTestSuite(EquivalentToTest.class);
		//out.addTestSuite(FlyXPTest.class);
		out.addTestSuite(HeaderLostBug.class);
		out.addTestSuite(IDsTest.class);
		out.addTestSuite(Obo2OwlTest.class);
		out.addTestSuite(ObsoleteTermTest.class);
		out.addTestSuite(Owl2OboTest.class);
		out.addTestSuite(PropertyChainTest.class);
		out.addTestSuite(RelationShorthandTest.class);
		out.addTestSuite(RoundTripTestSimpleGo.class);
		//out.addTestSuite(RoundTripImportTest.class);
		out.addTestSuite(RoundTripMultipleDefXrefTest.class);
		out.addTestSuite(RoundTripSynonymTest.class);
		//out.addTestSuite(SBOTest.class);
		out.addTestSuite(SubsetTest.class);
		out.addTestSuite(SynonymTest.class);
		out.addTestSuite(UnionOfTest.class);
		out.addTestSuite(UnmappableExpressionsTest.class);
		out.addTestSuite(XPBridgeFileTest.class);
		return out;

	}

}
