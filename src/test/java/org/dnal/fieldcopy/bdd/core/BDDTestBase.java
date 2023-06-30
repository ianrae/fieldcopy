package org.dnal.fieldcopy.bdd.core;

import org.dnal.fieldcopy.TestBase;
import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.log.SimpleLog;
import org.dnal.fieldcopy.util.TextFileReader;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public abstract class BDDTestBase extends TestBase {

    public static final String BASE_DIR = "src/test/resources/bdd/";
    protected int singleTestToRunIndex = -1;

    protected FieldCopyLog log; //a custom log just for Seede execution
    protected boolean stopOnFirstParseError;
    protected String onlyDoCSVFile;
    protected boolean scanCSVMode;
    protected BDDMode mode = BDDMode.RUNTIME;

    public BDDTestBase() {
        this.log = new SimpleLog();
    }

    protected void runBBBTest(int rNumber, int rSubNumber, String baseDir, String bddFileName, int numTests) {
        runBBBTest(rNumber, rSubNumber, baseDir, bddFileName, numTests, 0);
    }

    protected void runBBBTest(int rNumber, int rSubNumber, String baseDir, String bddFileName, int numTests, int numSkip) {
        BDDFeature feature = readTest(baseDir + bddFileName);

        BDDFeatureRunner runner = new BDDFeatureRunner(mode, log, rNumber, rSubNumber);
        runner.addRunner(SnippetType.CODEGEN, new ConvLangSnippetRunner(log));
        runner.addRunner(SnippetType.CONVERTER, new ConverterRuntimeSnippetRunner(log));
        runner.addRunner(SnippetType.VALUES, new ValuesSnippetRunner(log));
        runner.addRunner(SnippetType.NOTHING, new NoOpSnippetRunner(log));
        if (singleTestToRunIndex >= 0) {
            runner.setSingleTestToRunIndex(singleTestToRunIndex);
            numTests = 1;
        }

        BDDFeatureResult res = runner.runTests(feature, bddFileName);
        String codeGenMsg = BDDMode.isCodeGen(mode) ? " ************* CODEGEN! *************": "";
        log.log("finished: %s%s", bddFileName, codeGenMsg);
        assertEquals(numTests, res.numPass);
        assertEquals(0, res.numFail);
        assertEquals(numSkip, res.numSkip);
    }

    protected void deleteGeneratedJavaFiles() {
        String genDir = "src/test/java/org/dnal/fieldcopy/bdd/gen/";
        Set<String> list = Stream.of(new File(genDir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());

        for(String filename: list) {
            String path = genDir + filename;
            log(String.format("DELETING %s..", path));
            File f = new File(path);
            try {
                f.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //---

    protected BDDFeature readTest(String path) {
        TextFileReader r = new TextFileReader();
        List<String> lines = r.readFile(path);
        BDDFileParser parser = new BDDFileParser();
        return parser.parse(lines);
    }

    protected void runR100(int rSubNumber, String fileName, int numTests) {
        doRunBBBTest(100, rSubNumber, fileName, numTests);
    }
    protected void runR700(String fileName, int numTests) {
        doRunBBBTest(700, fileName, numTests);
    }
    protected void runR200(int rSubNumber, String fileName, int numTests) {
        doRunBBBTest(200, rSubNumber, fileName, numTests);
    }
    protected void runR250(String fileName, int numTests) {
        doRunBBBTest(250, fileName, numTests);
    }
    protected void runR250(int rSubNumber, String fileName, int numTests) {
        doRunBBBTest(250, rSubNumber, fileName, numTests);
    }

    protected void runR500(String fileName, int numTests) {
        doRunBBBTest(500, fileName, numTests);
    }
    protected void runR500(int rSubNumber, String fileName, int numTests) {
        doRunBBBTest(500, rSubNumber, fileName, numTests);
    }

    protected void runR900(String fileName, int numTests) {
        doRunBBBTest(900, fileName, numTests);
    }
    protected void runR900(int rSubNumber, String fileName, int numTests) {
        doRunBBBTest(900, rSubNumber, fileName, numTests);
    }

    protected void runR1000(String fileName, int numTests) {
        doRunBBBTest(1000, fileName, numTests);
    }
    protected void runR1000(int rSubNumber, String fileName, int numTests) {
        doRunBBBTest(1000, rSubNumber, fileName, numTests);
    }


    protected void runR1100(String fileName, int numTests) {
        doRunBBBTest(1100, fileName, numTests);
    }
    protected void runR1100(int rSubNumber, String fileName, int numTests) {
        doRunBBBTest(1100, rSubNumber, fileName, numTests);
    }
    protected void runR1300(String fileName, int numTests) {
        runR1300(1300, fileName, numTests);
    }
    protected void runR1300(int rSubNumber, String fileName, int numTests) {
        doRunBBBTest(1300, rSubNumber, fileName, numTests);
    }

    protected void runR2000(String fileName, int numTests) {
        runR1300(2000, fileName, numTests);
    }
    protected void runR2000(int rSubNumber, String fileName, int numTests) {
        doRunBBBTest(2000, rSubNumber, fileName, numTests);
    }


    // --- helpers ---
    protected void doRunBBBTest(int rNumber, String fileName, int numTests) {
        doRunBBBTest(rNumber, rNumber, fileName, numTests);
    }
    protected void doRunBBBTest(int rNumber, int rSubNumber, String fileName, int numTests) {
        String baseDir = String.format(BASE_DIR + "R%s/", rNumber);
        runBBBTest(rNumber, rSubNumber, baseDir, fileName, numTests);
    }

}
