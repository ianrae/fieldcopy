package org.dnal.fieldcopy.codegen;

import java.util.ArrayList;
import java.util.List;

public class JavaSrcSpec {
    public String className;
    public List<String> lines;
    private List<String> importLines = new ArrayList<>();

    public JavaSrcSpec(String className) {
        this.className = className;
    }

    //avoid duplicates
    public void addImportIfNotAlreadyPresent(String importStr) {
        if (!importLines.contains(importStr)) {
            importLines.add(importStr);
        }
    }

    public List<String> getImportLines() {
        return importLines;
    }
}
