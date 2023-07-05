package org.dnal.fieldcopy.codegeneration;

import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.log.SimpleLog;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;

/***
 * Fluent API helper class
 */
public class CodeGenerationBuilder1 {
    private String json;
    private FieldCopyOptions options;
    private String converterPackageName;
    private String outputDir;
    private boolean dryRunFlag;
    private FieldCopyLog log = new SimpleLog();

    public CodeGenerationBuilder1(String json) {
        this.json = json;
    }

    public CodeGenerationBuilder1 options(FieldCopyOptions options) {
        this.options = options;
        return this;
    }

    public CodeGenerationBuilder1 log(FieldCopyLog log) {
        this.log = log;
        return this;
    }

    public CodeGenerationBuilder1 converterPackageName(String converterPackageName) {
        this.converterPackageName = converterPackageName;
        return this;
    }

    public CodeGenerationBuilder1 outputDir(String outputDir) {
        this.outputDir = outputDir;
        return this;
    }

    public CodeGenerationBuilder1 dryRunFlag(boolean dryRunFlag) {
        this.dryRunFlag = dryRunFlag;
        return this;
    }

    public FieldCopyCodeGenerator build() {
        return new FieldCopyCodeGenerator(json, options, converterPackageName, outputDir, dryRunFlag, log);
    }
}
