package org.dnal.fieldcopy.bdd.core;


import org.dnal.fieldcopy.error.FCError;
import org.dnal.fieldcopy.log.FieldCopyLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public class BDDFeatureRunner {
    private final FieldCopyLog log;
    private final BDDMode mode;
    private final int rNumber;
    private final int rSubNumber;
    private Map<SnippetType, SnippetRunner> runnerMap = new HashMap<>();
    private BDDSnippetResult mostRecentRes;
    private int singleTestToRunIndex = -1;
    private BDDSnippetResult mostRecendBackgroundRes;

    public BDDFeatureRunner(BDDMode mode, FieldCopyLog log, int rNumber, int rSubNumber) {
        this.mode = mode;
        this.log = log;
        this.rNumber = rNumber;
        this.rSubNumber = rSubNumber;
    }

    public void addRunner(SnippetType type, SnippetRunner runner) {
        runnerMap.put(type, runner);
    }

    public BDDFeatureResult runTests(BDDFeature feature, String fileName) {
        BDDFeatureResult res = new BDDFeatureResult();
        String codeGenMsg = BDDMode.isCodeGen(mode) ? " ***** CODEGEN!! **** " : "";
        log.log("=============== FEATURE: %s (%s) =========%s=============", feature.name, fileName, codeGenMsg);
        if (!executeBackground(feature.backgroundsL)) {
            return res;
        }

        int index = 0;
        for (BDDTest test : feature.testsL) {
            if (singleTestToRunIndex >= 0) {
                if (index == singleTestToRunIndex) {
                    boolean b = executeTest(test, index);
                    if (b) {
                        res.numPass++;
                    } else {
                        res.numFail++;
                    }
                    break;
                }
            } else {
                if (test.skip) {
                    log.log("SKIP: %s", test.title);
                    res.numSkip++;
                } else {
                    boolean b = executeTest(test, index);
                    if (b) {
                        res.numPass++;
                    } else {
                        res.numFail++;
                    }
                }
            }
            index++;
        }

        String strFail = res.numFail == 0 ? "FAIL" : "**FAIL**";
        String strPass = res.numFail == 0 ? " ==> SUCESS!!" : "";
        int total = res.numPass + res.numFail + res.numSkip;
        log.log("PASS:%d, %s:%d, SKIPPED:%d tests (%d)%s", res.numPass, strFail, res.numFail, res.numSkip, total, strPass);
        return res;
    }

    private boolean executeBackground(List<BDDSnippet> backgroundsL) {
        for (BDDSnippet snippet : backgroundsL) {
            BDDSnippetResult tres = executeSnippet(snippet, mostRecentRes, null, 0);
            mostRecendBackgroundRes = tres;
            if (!tres.ok) {
                return false;
            }
        }
        return true;
    }

    private BDDSnippetResult executeSnippet(BDDSnippet snippet, BDDSnippetResult previousRes, BDDTest test, int index) {
        if (BDDMode.isCodeGen(mode)) {
            if (!SnippetType.CODEGEN.equals(snippet.type)) {
                return isNull(previousRes) ? new BDDSnippetResult(true) : previousRes;
            }
        }
        SnippetRunner runner = runnerMap.get(snippet.type);
        SnippetContext snippetCtx = new SnippetContext();
        snippetCtx.mode = mode;
        snippetCtx.rNumber = rNumber;
        snippetCtx.rSubNumber = rSubNumber;
        snippetCtx.testNum = index;
        snippetCtx.useConverterFromTest = isNull(test) ? null : test.useConverterFromTest;
        BDDSnippetResult res = runner.execute(snippet, previousRes, snippetCtx);

        Map<String, String> hintMap = previousRes == null ? null : previousRes.nameHintMap;
        mostRecentRes = res;
        if (mostRecentRes.nameHintMap == null || mostRecentRes.nameHintMap.isEmpty()) {
            mostRecentRes.nameHintMap = hintMap;
        }
        return res;
    }

    private boolean executeTest(BDDTest test, int index) {
        log.log("");
        log.log("--------------------------------------------------------");
        log.log(String.format("Test%d: %s...", index, test.title));
        BDDSnippetResult failingTRes = null;
        if (test.givenL.isEmpty()) {
            if (mostRecentRes.parseRes == null && mostRecendBackgroundRes.parseRes != null) {
                mostRecentRes = mostRecendBackgroundRes;
            }
        } else {
            for (BDDSnippet snippet : test.givenL) {
                BDDSnippetResult tres = executeSnippet(snippet, mostRecentRes, test, index);
                if (!tres.ok) {
                    failingTRes = tres;
                }
            }
        }

        BDDSnippetResult whenRes = null;
        if (failingTRes == null) {
            for (BDDSnippet snippet : test.whenL) {
                BDDSnippetResult tres = executeSnippet(snippet, mostRecentRes, test, index);
                whenRes = tres;
                //the when part may have the expected error so keep going
                if (!tres.ok) {
                    failingTRes = tres;
                }
            }
        }

        if (whenRes == null && failingTRes != null) {
            whenRes = new BDDSnippetResult();
            whenRes.ok = false;
            whenRes.errors = new ArrayList<>(failingTRes.errors);
        }

        for (BDDSnippet snippet : test.thenL) {
            BDDSnippetResult tres = executeSnippet(snippet, whenRes, test, index);
            if (!tres.ok) {
                if (failingTRes == null) {
                    failingTRes = tres;
                } else {
                    for (FCError err : tres.errors) {
                        if (failingTRes != null && !failingTRes.errors.contains(err)) {
                            failingTRes.errors.add(err);
                        }
                    }
                }
            } else {
                failingTRes = null; //reset
            }
        }

        if (failingTRes != null && !failingTRes.ok) {
            log.log("**Test%d: %s FAILED!** (in given) => %d errors", index, test.title, failingTRes.errors.size());
            for(FCError err: failingTRes.errors) {
                log.log(" %s: %s", err.getId(), err.getMsg());
            }
            return false;
        }

        return true;
    }

    public int getSingleTestToRunIndex() {
        return singleTestToRunIndex;
    }

    public void setSingleTestToRunIndex(int singleTestToRunIndex) {
        this.singleTestToRunIndex = singleTestToRunIndex;
    }

}
