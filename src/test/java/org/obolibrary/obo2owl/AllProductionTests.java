package org.obolibrary.obo2owl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.obolibrary.macro.ExpandExpressionGCITest;
import org.obolibrary.macro.ExpandExpressionTest;
import org.obolibrary.macro.ExpandSynapsedToTest;
import org.obolibrary.macro.ManchesterSyntaxToolTest;
import org.obolibrary.oboformat.CAROTest;
import org.obolibrary.oboformat.ChebiXRefTest;
import org.obolibrary.oboformat.CurlyBracesInCommentsTest;
import org.obolibrary.oboformat.EmptyLinesTest;
import org.obolibrary.oboformat.IdSpaceTest;
import org.obolibrary.oboformat.IgnoreImportAnnotationsTest;
import org.obolibrary.oboformat.MultipleCommentsTest;
import org.obolibrary.oboformat.OboEscapeCharsTest;
import org.obolibrary.oboformat.PropertyValueTest;
import org.obolibrary.oboformat.SimpleGOTest;
import org.obolibrary.oboformat.SingleIntersectionOfTagTest;
import org.obolibrary.oboformat.TagTest;
import org.obolibrary.oboformat.TrailingQualifierTest;
import org.obolibrary.oboformat.XrefExpanderTest;
import org.obolibrary.oboformat.writer.OBOFormatWriterTest;
import org.obolibrary.oboformat.writer.TypeDefCommentsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	//obo2owl tests
	BFOROXrefTest.class,
	DanglingOwl2OboTest.class,
	DanglingRoundTripTest.class,
	DanglingRestrictionOwl2OboTest.class,
	EquivalentToTest.class,
	//FlyXPTest.class,
	HeaderLostBugTest.class,
	IDsTest.class,
	ImportOWLOntologiesFromOboTest.class,
	Obo2OwlTest.class,
	ObsoleteTermTest.class,
	Owl2OboTest.class,
	PropertyChainTest.class,
	TransitiveOverTest.class,
	RelationShorthandTest.class,
	RoundTripSimpleGoTest.class,
	RoundTripImportTest.class,
	RoundTripMultipleDefXrefTest.class,
	RoundTripSynonymTest.class,
	RoundTripXrefTest.class,
	RoundTripMultiLineDefTest.class,
	RoundTripCAROTest.class,
	RoundTripCardinalityTest.class,
	RoundTripOWLROTest.class,
	RoundTripNamespaceIdRule.class,
	RoundTripNonStandardSynonyms.class,
	RoundTripEquivalentToChain.class,
	RoundTripTrailingQualifiers.class,
	RoundTripCardinality.class,
	RoundTripOBITest.class,
	//SBOTest.class,
	SubsetTest.class,
	SynonymTest.class,
	UnionOfTest.class,
	UnmappableExpressionsTest.class,
	XPBridgeFileTest.class,
	DuplicateTagsTest.class,
	CommentRemarkConversionTest.class,
	
	// owl2obo
	UntranslatableAxiomsInHeaderTest.class,
	Owl2OboAnnotationPropertyTest.class,
	
	// oboformat tests
	CAROTest.class,
	SimpleGOTest.class,
	SingleIntersectionOfTagTest.class,
	TagTest.class,
	XrefExpanderTest.class,
	OboEscapeCharsTest.class,
	ChebiXRefTest.class,
	EmptyLinesTest.class,
	PropertyValueTest.class,
	OBOFormatWriterTest.class,
	MultipleCommentsTest.class,
	IgnoreImportAnnotationsTest.class,
	IdSpaceTest.class,
	TrailingQualifierTest.class,
	CurlyBracesInCommentsTest.class,
	TypeDefCommentsTest.class,
	
	// macro expansion
	ExpandSynapsedToTest.class,
	ExpandExpressionTest.class,
	ExpandExpressionGCITest.class,
	ManchesterSyntaxToolTest.class
})
public class AllProductionTests {
	// intentionally empty, uses annotations to define test suite
}
