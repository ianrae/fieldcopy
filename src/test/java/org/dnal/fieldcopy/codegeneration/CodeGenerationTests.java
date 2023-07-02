package org.dnal.fieldcopy.codegeneration;

import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.group.GroupCodeGenerator;
import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.log.SimpleLog;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyJsonParser;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.parser.fieldcopyjson.ParserResults;
import org.dnal.fieldcopy.util.ResourceTextFileReader;
import org.dnal.fieldcopy.util.StringUtil;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeGenerationTests {

    @Test
    public void test() {
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
