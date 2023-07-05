package org.dnal.fieldcopy.codegeneration;

import org.dnal.fieldcopy.group.GroupCodeGenerator;
import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.log.SimpleLog;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyJsonParser;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.parser.fieldcopyjson.ParserResults;

/**
 * Performs code generation.
 */
public class FieldCopyCodeGenerator {
    private final FieldCopyLog log;
    private String json;
    private FieldCopyOptions options;
    private String converterPackageName;
    private String outputDir;
    private boolean dryRunFlag;

    public FieldCopyCodeGenerator(String json, FieldCopyOptions options, String converterPackageName, String outputDir,
                                  boolean dryRunFlag, FieldCopyLog log) {
        this.json = json;
        this.options = options;
        this.converterPackageName = converterPackageName;
        this.outputDir = outputDir;
        this.dryRunFlag = dryRunFlag;
        this.log = log;
    }

    public boolean generateSourceFiles() {
        FieldCopyJsonParser parser = new FieldCopyJsonParser(log);

        ParserResults parseRes = parser.parse(json, options);

        GroupCodeGenerator groupCodeGenerator = new GroupCodeGenerator(log);
        groupCodeGenerator.setPackageName(converterPackageName);
        groupCodeGenerator.setOutDir(outputDir);
        groupCodeGenerator.setOptions(parseRes.options);
        groupCodeGenerator.setDryRunFlag(dryRunFlag);
        boolean ok = groupCodeGenerator.generateJavaFiles(parseRes);
        return ok;
    }
}
