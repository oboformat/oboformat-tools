package org.obolibrary.obo2owl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // obo2owl tests
        // FlyXPTest.class,
        ImportOWLOntologiesFromOboTest.class,
        RoundTripImportTest.class,
        RoundTripOBITest.class,
        RoundTripRelationshipVsProperty.class,
        // SBOTest.class,
        // owl2obo
        Owl2OboAnnotationPropertyTest.class, })
        // oboformat tests
        // macro expansion
public class AllProductionTests {
    // intentionally empty, uses annotations to define test suite
}
