package tests;

import checkers.util.test.ParameterizedCheckerTest;

import java.io.File;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class I18nTest extends ParameterizedCheckerTest {

    public I18nTest(File testFile) {
        super(testFile,
                checkers.i18n.I18nChecker.class,
                "i18n",
                "-Anomsgtext");
    }

    @Parameters
    public static Collection<Object[]> data() {
        return testFiles("i18n", "all-systems");
    }
}