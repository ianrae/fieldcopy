package org.dnal.fieldcopy.codegeneration;

import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.group.GroupCodeGenerator;
import org.dnal.fieldcopy.group.ObjectConverterSpec;
import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.log.SimpleLog;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyJsonParser;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.parser.fieldcopyjson.ParserResults;
import org.dnal.fieldcopy.util.ResourceTextFileReader;
import org.dnal.fieldcopy.util.StringUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeGenerationTests {

    public static class AdditionalConverterBuilder {
        private List<ObjectConverterSpec> list = new ArrayList<>();

        public AdditionalConverterBuilder addNamedConverter(String converterName, String converterClassName) {
            ObjectConverterSpec converterSpec = new ObjectConverterSpec(converterName, converterClassName);
            list.add(converterSpec);
            return this;
        }
        public AdditionalConverterBuilder addNamedConverter(String converterName, Class<?> converterClass) {
            ObjectConverterSpec converterSpec = new ObjectConverterSpec(converterName, converterClass.getName());
            list.add(converterSpec);
            return this;
        }

        public AdditionalConverterBuilder addConverter(String converterClassName) {
            ObjectConverterSpec converterSpec = new ObjectConverterSpec(null, converterClassName);
            list.add(converterSpec);
            return this;
        }
        public AdditionalConverterBuilder addConverter(Class<?> converterClass) {
            ObjectConverterSpec converterSpec = new ObjectConverterSpec(null, converterClass.getName());
            list.add(converterSpec);
            return this;
        }
    }

    public static class CodeGenerationBuilder1 {
        private String json;
        private FieldCopyOptions options;
        private String converterPackageName;
        private String outputDir;
        private boolean dryRunFlag;
        private AdditionalConverterBuilder additionalConverterBuilder;

        public CodeGenerationBuilder1(String json) {
            this.json = json;
        }

        public CodeGenerationBuilder1 options(FieldCopyOptions options) {
            this.options = options;
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
        public CodeGenerationBuilder1 additionalConverters(AdditionalConverterBuilder additionalConverterBuilder) {
            this.additionalConverterBuilder = additionalConverterBuilder;
            return this;
        }

        public FieldCopyCodeGenerator build() {

        }
    }

    public static class FieldCopyCodeGenerator {

        public boolean generateSourceFiles() {
            return false;
        }
    }

    public static class CodeGenerationBuilder {
        private String json;

        public CodeGenerationBuilder(String json) {
            this.json = json;
        }

        public static CodeGenerationBuilder1 json(String json) {
            CodeGenerationBuilder1 builder = new CodeGenerationBuilder1(json);
            return builder;
        }
    }

    @Test
    public void testPlain() {
        String json = readJsonFile("codegeneration/sample1.json");

        FieldCopyLog log = new SimpleLog();
        FieldCopyJsonParser parser = new FieldCopyJsonParser(log);

        FieldCopyOptions options = new FieldCopyOptions();
        ParserResults parseRes = parser.parse(json, options);
        String outDir = "C:/tmp/fieldcopy2/gen";

        GroupCodeGenerator groupCodeGenerator = new GroupCodeGenerator();
//        String outputPath = "src/main/java/org/delia/gip/slugs/bigcommerce/gen";
        groupCodeGenerator.setPackageName("org.delia.gip.slugs.bigcommerce.gen");
//        groupCodeGenerator.setAdditionalConverterPackageName(ADDITIONAL_CONVERTER_PACKAGE);
        groupCodeGenerator.setOutDir(outDir);
        groupCodeGenerator.setOptions(parseRes.options);
//        groupCodeGenerator.setDryRunFlag(true);
        boolean ok = groupCodeGenerator.generateJavaFiles(parseRes);


        assertEquals(2, 2);
    }


    @Test
    public void testFluent() {
        String json = readJsonFile("codegeneration/sample1.json");
        FieldCopyOptions options = new FieldCopyOptions();
        String outDir = "C:/tmp/fieldcopy2/gen";

        FieldCopyCodeGenerator gen = CodeGenerationBuilder.json(json).dryRunFlag(false).options(options).outputDir(outDir)
                .converterPackageName("org.delia.gip.slugs.bigcommerce.gen").build();

        boolean ok = gen.generateSourceFiles();

        assertEquals(true, ok);
    }

    protected String buildConverterName(CopySpec spec) {
        return buildConverterName(spec, null, null);
    }

    protected String buildConverterName(CopySpec spec, String prefix, String suffix) {
        String srcName = StringUtil.uppify(spec.srcClass.getSimpleName());
        String destName = StringUtil.uppify(spec.destClass.getSimpleName());

        String name = String.format("%sTo%sConverter", srcName, destName);
        if (prefix != null) {
            name = prefix + name;
        }
        if (suffix != null) {
            name = name + suffix;
        }
        spec.converterName = Optional.ofNullable(name);
        return name;
    }


    //---
    public String readJsonFile(String resourcePath) {
        ResourceTextFileReader r = new ResourceTextFileReader();
        String src = r.readAsSingleString(resourcePath);
        return src;
    }

}
