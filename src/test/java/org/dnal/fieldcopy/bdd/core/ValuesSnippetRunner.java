package org.dnal.fieldcopy.bdd.core;


import org.dnal.fieldcopy.error.FCError;
import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.util.StringUtil;
import org.dnal.fieldcopy.util.render.ObjectRendererImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ValuesSnippetRunner implements SnippetRunner {
    private final FieldCopyLog log;

    public ValuesSnippetRunner(FieldCopyLog log) {
        this.log = log;
    }

    @Override
    public BDDSnippetResult execute(BDDSnippet snippet, BDDSnippetResult previousRes, SnippetContext ctx) {
        BDDSnippetResult res = new BDDSnippetResult();

        String expectedJson = StringUtil.convertToSingleString(snippet.lines);

        ObjectRendererImpl renderer = new ObjectRendererImpl(true);
        String outputJson =  renderer.render(previousRes.convDestObj);

        if (areTheSame(expectedJson, outputJson)) {
            res.ok = true;
        } else {
            log.log("EXPECTED: %s", expectedJson);
            log.log("ACTUAL:  %s", outputJson);
            res.errors.add(new FCError("sdfd", "sdfsdf"));
        }

        return res;
    }

    private boolean areTheSame(String expectedJson, String outputJson) {
        String[] ar1 = expectedJson.split("\n");
        String[] ar2 = outputJson.split("\n");

        for(int i = 0; i < ar1.length; i++) {
            if (i >= ar2.length) {
                log.log("output too short");
                return false;
            }

            String s1 = ar1[i].trim();
            String s2 = ar2[i].trim();
            if (! s1.equals(s2)) {
                log.log("[%d] expected: %s", i, s1);
                log.log("[%d] actual:   %s", i, s2);
                return false;
            }
        }

        if (ar1.length < ar2.length) {
            log.log("output too long");
            return false;
        }


        return true;
    }
}
