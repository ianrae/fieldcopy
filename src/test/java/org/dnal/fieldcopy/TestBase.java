package org.dnal.fieldcopy;

import org.dnal.fieldcopy.codegen.JavaSrcSpec;
import org.dnal.fieldcopy.util.render.ObjectRendererImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBase {

    protected void dumpLines(List<String> lines) {
        for (String s : lines) {
            System.out.println(s);
        }
    }

    protected void log(String s) {
        System.out.println(s);
    }

    protected void chkLines(List<String> lines, String... ar) {
        dumpLines(lines);
        int n = ar.length;
        assertEquals(n, lines.size());
        for (int i = 0; i < n; i++) {
            String s = ar[i];
            assertEquals(s, lines.get(i));
        }
    }

    protected void chkImports(JavaSrcSpec spec, String... ar) {
        chkLines(spec.getImportLines(), ar);
    }

    protected void chkNoImports(JavaSrcSpec spec) {
        String[] ar = {};
        chkImports(spec, ar);
    }

    protected void chkException(RuntimeException thrown, String expected) {
        log("Exception: " + thrown.getMessage());
        assertEquals(true, thrown.getMessage().contains(expected));
    }

    protected void dumpObj(String s, Object obj) {
        ObjectRendererImpl renderer = new ObjectRendererImpl();
        log(renderer.render(obj));
    }
    protected String buildPath(String fileName) {
        String path = "src/test/resources/" + fileName;
        return path;
    }

}
