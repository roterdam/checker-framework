package tests;

import org.checkerframework.framework.test.ParameterizedCheckerTest;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.util.Collection;

/**
 * JUnit tests for the Nullness checker when reflection resolution is enabled
 */
public class NullnessReflectionTest extends ParameterizedCheckerTest {

    public NullnessReflectionTest(File testFile) {
        super(testFile,
                org.checkerframework.checker.nullness.NullnessChecker.class,
                "nullness",
                "-AresolveReflection",
                "-Anomsgtext");
    }

    @Parameters
    public static Collection<Object[]> data() {
        return testFiles("nullness-reflection");
    }

}
