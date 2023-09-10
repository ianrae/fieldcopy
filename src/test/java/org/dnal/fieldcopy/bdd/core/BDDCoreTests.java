package org.dnal.fieldcopy.bdd.core;

import org.dnal.fieldcopy.TestBase;
import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.log.SimpleLog;
import org.dnal.fieldcopy.util.TextFileReader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BDDCoreTests extends TestBase  {
    public static class MockSnippetRunner implements SnippetRunner {
        @Override
        public BDDSnippetResult execute(BDDSnippet snippet, BDDSnippetResult previousRes, SnippetContext ctx) {
            BDDSnippetResult res = new BDDSnippetResult();
            res.ok = true;
            return res;
        }
    }

    @Test
    public void testParse() {
        BDDFeature feature = readTest("sample1.txt");
        assertEquals("sample1", feature.name);
        assertEquals(2, feature.testsL.size());
        BDDTest test = feature.testsL.get(0);
        assertEquals("test1", test.title);
        test = feature.testsL.get(1);
        assertEquals("test2", test.title);
    }

    @Test
    public void testRun() {
        BDDFeature feature = readTest("sample1.txt");

        BDDFeatureRunner runner = new BDDFeatureRunner(BDDMode.RUNTIME, log,  100, 100);
        runner.addRunner(SnippetType.CODEGEN, new MockSnippetRunner());
        runner.addRunner(SnippetType.VALUES, new MockSnippetRunner());
        runner.addRunner(SnippetType.CONVERTER, new MockSnippetRunner());

        BDDFeatureResult res = runner.runTests(feature, "sample1.txt");
        assertEquals(2, res.numPass);
        assertEquals(0, res.numFail);
        assertEquals(0, res.numSkip);
    }

    @Test
    public void testRun2() {
        BDDFeature feature = readTest("sample2.txt");

        BDDFeatureRunner runner = new BDDFeatureRunner(BDDMode.RUNTIME, log,  100, 100);
        runner.addRunner(SnippetType.CODEGEN, new MockSnippetRunner());
        runner.addRunner(SnippetType.VALUES, new MockSnippetRunner());
        runner.addRunner(SnippetType.CONVERTER, new MockSnippetRunner());

        BDDFeatureResult res = runner.runTests(feature, "sample2.txt");
        assertEquals(2, res.numPass);
        assertEquals(0, res.numFail);
        assertEquals(0, res.numSkip);
    }
    //---
//    @Before
//    public void init() {
//        UnitTestLog.disableLogging = false;
//        super.init();
//    }

    private BDDFeature readTest(String filename) {
        String dir = "src/test/resources/bdd/";
        String path = dir + filename;
        TextFileReader r = new TextFileReader();
        List<String> lines = r.readFile(path);
        BDDFileParser parser = new BDDFileParser();
        return parser.parse(lines);
    }

}
