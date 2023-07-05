package org.dnal.fieldcopy.codegeneration;

import org.dnal.fieldcopy.group.GroupCodeGenerator;
import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.log.SimpleLog;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyJsonParser;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.parser.fieldcopyjson.ParserResults;
import org.dnal.fieldcopy.util.ResourceTextFileReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeGenerationTests {

    @Test
    public void testPlain() {
        String json = readJsonFile("codegeneration/sample1.json");

        FieldCopyLog log = new SimpleLog();
        FieldCopyJsonParser parser = new FieldCopyJsonParser(log);

        FieldCopyOptions options = new FieldCopyOptions();
        ParserResults parseRes = parser.parse(json, options);
        String outDir = "C:/tmp/fieldcopy2/gen";

        GroupCodeGenerator groupCodeGenerator = new GroupCodeGenerator(log);
        groupCodeGenerator.setPackageName("org.delia.gip.slugs.bigcommerce.gen");
        groupCodeGenerator.setOutDir(outDir);
        groupCodeGenerator.setOptions(parseRes.options);
        groupCodeGenerator.setDryRunFlag(true);
        boolean ok = groupCodeGenerator.generateJavaFiles(parseRes);
        assertEquals(true, ok);
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


    //--------
    public String readJsonFile(String resourcePath) {
        ResourceTextFileReader r = new ResourceTextFileReader();
        String src = r.readAsSingleString(resourcePath);
        return src;
    }

}
