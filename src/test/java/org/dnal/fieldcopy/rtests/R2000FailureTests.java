package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.group.GroupCodeGenerator;
import org.dnal.fieldcopy.log.SimpleLog;
import org.dnal.fieldcopy.parser.fieldcopyjson.FileLoader;
import org.dnal.fieldcopy.parser.fieldcopyjson.ParserResults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 R2000  failure tests
 -bad json
 -bad field -> field syntax
 -can't parse value
 -can't find class
 -can't find value
 -no converter for X
 -conversion not supported
 -path not found //codegen
 -null-not-allowed
 -not-impl-yet
 */
public class R2000FailureTests extends RTestBase {

    @Test
    public void testBadField() {
        ParserResults res = loadAndParse("parser/field-bad1.json");
        GroupCodeGenerator groupCodeGenerator = createGroupCodeGenerator();

        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
            boolean b = groupCodeGenerator.generateJavaFiles(res);
        });
        chkException(thrown, "Can't find field 'nosuchfield'");
    }

    @Test
    public void testBadValue() {
        ParserResults res = loadAndParse("parser/field-bad2.json");
        GroupCodeGenerator groupCodeGenerator = createGroupCodeGenerator();

        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
            boolean b = groupCodeGenerator.generateJavaFiles(res);
        });
        chkException(thrown, "syntax error in converter for 'TestClass1'");
    }

    @Test
    public void testBadClass() {
        ParserResults res = loadAndParse("parser/field-bad3.json");
        GroupCodeGenerator groupCodeGenerator = createGroupCodeGenerator();

        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
            boolean b = groupCodeGenerator.generateJavaFiles(res);
        });
        chkException(thrown, "unknown class");
    }


    //----helpers---
    private ParserResults loadAndParse(String pathStr) {
        String path = buildPath(pathStr);
        FileLoader fileLoader = new FileLoader();
        ParserResults res = fileLoader.loadAndParseFile(path);
        dumpObj("actions:", res.converters);
        assertEquals(true, res.ok);
        return res;
    }
    private GroupCodeGenerator createGroupCodeGenerator() {
        String outputPath = "src/test/java/org/dnal/fieldcopy/group/gen";
        GroupCodeGenerator groupCodeGenerator = new GroupCodeGenerator(new SimpleLog());
        groupCodeGenerator.setPackageName("org.dnal.fieldcopy.group.gen");
        groupCodeGenerator.setOutDir(outputPath);
        return groupCodeGenerator;
    }


}
