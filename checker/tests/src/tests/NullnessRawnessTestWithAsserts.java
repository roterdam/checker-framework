package tests;

import java.io.File;
import java.util.Collection;

import org.checkerframework.checker.nullness.AbstractNullnessChecker;
import org.checkerframework.framework.test.ParameterizedCheckerTest;
import org.junit.runners.Parameterized.Parameters;

/**
 * JUnit tests for the Nullness checker (that uses the rawness type system for
 * initialization).
 */
public class NullnessRawnessTestWithAsserts extends ParameterizedCheckerTest {

    public NullnessRawnessTestWithAsserts(File testFile) {
        // TODO: remove arrays:forbidnonnullcomponents option once it's no
        // longer needed.  See issues 154 and 322.
        super(testFile,
                org.checkerframework.checker.nullness.NullnessRawnessChecker.class,
                "nullness",
                "-AcheckPurityAnnotations",
                "-AassumeAssertionsAreEnabled",
                "-Anomsgtext", "-Xlint:deprecation",
                "-Alint=arrays:forbidnonnullcomponents,"
                        + AbstractNullnessChecker.LINT_REDUNDANTNULLCOMPARISON);
    }

    @Parameters
    public static Collection<Object[]> data() {
        return testFiles("nullness-asserts");
    }

}
