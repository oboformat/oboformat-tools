package org.obolibrary.obo2owl.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.obolibrary.macro.test.ExpandExpressionGCITest;
import org.obolibrary.macro.test.ExpandExpressionTest;
import org.obolibrary.oboformat.test.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	//obo2owl tests
	BFOROXrefTest.class,
	DanglingOwl2OboTest.class,
	DanglingRoundTripTest.class,
	//TODO
	//DanglingRestrictionOwl2OboTest.class,
	EquivalentToTest.class,
	//FlyXPTest.class,
	HeaderLostBug.class,
	IDsTest.class,
	Obo2OwlTest.class,
	ObsoleteTermTest.class,
	Owl2OboTest.class,
	PropertyChainTest.class,
	RelationShorthandTest.class,
	RoundTripTestSimpleGo.class,
	//RoundTripImportTest.class,
	RoundTripMultipleDefXrefTest.class,
	RoundTripSynonymTest.class,
	RoundTripXrefTest.class,
	//SBOTest.class,
	SubsetTest.class,
	SynonymTest.class,
	UnionOfTest.class,
	UnmappableExpressionsTest.class,
	XPBridgeFileTest.class,
	
	// oboformat tests
	CAROTest.class,
	SimpleGOTest.class,
	SingleIntersectionOfTagTest.class,
	TagTest.class,
	XrefExpanderTest.class,
	OboEscapeCharsTest.class,
	ChebiXRefTest.class,
	EmptyLinesTest.class,
	
	// macro expansion
	ExpandExpressionTest.class,
	ExpandExpressionGCITest.class
})
public class AllProductionTests {
	// intentionally empty, uses annotations to define test suite
}
