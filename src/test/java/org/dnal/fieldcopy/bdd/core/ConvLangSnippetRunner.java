package org.dnal.fieldcopy.bdd.core;


import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.group.GroupCodeGenerator;
import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.log.SimpleLog;
import org.dnal.fieldcopy.parser.fieldcopyjson.*;
import org.dnal.fieldcopy.util.StringUtil;

import java.util.Optional;

import static java.util.Objects.isNull;


public class ConvLangSnippetRunner implements SnippetRunner {
    public static final String GEN_PACKAGE = "org.dnal.fieldcopy.bdd.gen";
    public static final String ADDITIONAL_CONVERTER_PACKAGE = "org.dnal.fieldcopy.bdd.customconverter";

    protected final FieldCopyLog log;

    public ConvLangSnippetRunner(FieldCopyLog log) {
        this.log = log;
    }

    @Override
    public BDDSnippetResult execute(BDDSnippet snippet, BDDSnippetResult previousRes, SnippetContext ctx) {
        BDDSnippetResult res = new BDDSnippetResult();

        String convLangSrc = StringUtil.convertToSingleString(snippet.lines);
        ParserResults parseRes = loadFile(convLangSrc); //parser.parse(json, options);
//        dumpObj("actions:", res.converters);
//        chkNoErrors(res);
        res.parseRes = parseRes;

        if (parseRes.hasErrors()) {
            res.errors.addAll(parseRes.errors);
            res.ok = false;
            return res;
        }

        FieldCopyJsonParser parser = createParser();
        for (ParsedConverterSpec action : parseRes.converters) {
            CopySpec spec = parser.buildSpecFromAction(action, parseRes.options);
            action.nameStr = buildConverterName(spec, ctx);
            res.specs.add(spec);
        }

        //do codegen if BDDMode.CODEGEN
        if (BDDMode.isCodeGen(ctx.mode) && isNull(ctx.useConverterFromTest)) {
            GroupCodeGenerator groupCodeGenerator = new GroupCodeGenerator();
            String outputPath = "src/test/java/org/dnal/fieldcopy/bdd/gen";
            groupCodeGenerator.setPackageName(GEN_PACKAGE);
            groupCodeGenerator.setAdditionalConverterPackageName(ADDITIONAL_CONVERTER_PACKAGE);
            groupCodeGenerator.setOutDir(outputPath);
            groupCodeGenerator.setOptions(parseRes.options);
            res.ok = groupCodeGenerator.generateJavaFiles(parseRes);
        }

        res.ok = true;
        return res;
    }

    protected String buildConverterName(CopySpec spec, SnippetContext ctx) {
        //ignore name value from json and create test-specific converter names
        String srcName = StringUtil.uppify(spec.srcClass.getSimpleName());
        String destName = StringUtil.uppify(spec.destClass.getSimpleName());
        String suffix = ctx.buildSuffix();
        String name = String.format("%s_%sTo%sConverter", suffix, srcName, destName);
        spec.converterName = Optional.ofNullable(name);
        return name;
    }


    protected FieldCopyJsonParser createParser() {
        FieldCopyLog log = new SimpleLog();
        FieldCopyJsonParser parser = new FieldCopyJsonParser(log);
        return parser;
    }

    protected ParserResults loadFile(String json) {
        FileLoader fileLoader = new FileLoader();
        FieldCopyOptions options = new FieldCopyOptions();
        ParserResults res = fileLoader.parseString(json, options);
        return res;
    }


}
