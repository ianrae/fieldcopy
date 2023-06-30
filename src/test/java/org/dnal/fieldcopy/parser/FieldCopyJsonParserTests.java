package org.dnal.fieldcopy.parser;

import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyJsonParser;
import org.dnal.fieldcopy.parser.fieldcopyjson.ParsedConverterSpec;
import org.dnal.fieldcopy.parser.fieldcopyjson.ParserResults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class FieldCopyJsonParserTests extends ParserTestBase {

    @Test
    public void testPreParse() {
        ParserResults res = loadFile("parser/convlang1.json");
        dumpObj("actions:", res.converters);
        chkNoErrors(res);

        assertEquals(1, res.converters.size());
        ParsedConverterSpec action = res.converters.get(0);
        assertEquals("bob", action.nameForUsingStr);
        assertEquals("org.dnal.fieldcopy.dataclass.Inner1", action.srcClass);
        assertEquals("org.dnal.fieldcopy.dataclass.Inner1DTO", action.destClass);
        assertEquals(1, action.fieldStrings.size());
        assertEquals("s3 -> s3", action.fieldStrings.get(0));
    }

    @Test
    public void testFullParse() {
        ParserResults res = loadFile("parser/convlang1.json");
        dumpObj("actions:", res.converters);
        chkNoErrors(res);

        FieldCopyJsonParser parser = createParser();
        ParsedConverterSpec action = res.converters.get(0);
        CopySpec spec = parser.buildSpecFromAction(action, res.options);
        assertEquals("org.dnal.fieldcopy.dataclass.Inner1", spec.srcClass.getName());
        assertEquals("org.dnal.fieldcopy.dataclass.Inner1DTO", spec.destClass.getName());
        assertEquals(1, spec.fields.size());
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        assertEquals(false, nspec.srcTextIsValue);
        assertEquals("s3", nspec.srcText);
        assertEquals("s3", nspec.destText);
    }

    @Test
    public void testFullParse2() {
        ParserResults res = loadFile("parser/convlang2.json");
        dumpObj("actions:", res.converters);
        chkNoErrors(res);

        FieldCopyJsonParser parser = createParser();
        ParsedConverterSpec action = res.converters.get(0);
        CopySpec spec = parser.buildSpecFromAction(action, res.options);
        assertEquals("org.dnal.fieldcopy.dataclass.Inner1", spec.srcClass.getName());
        assertEquals("org.dnal.fieldcopy.dataclass.Inner1DTO", spec.destClass.getName());
        assertEquals(1, spec.fields.size());
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        assertEquals("s3", nspec.srcText);
        assertEquals("s3", nspec.destText);
    }

    @Test
    public void testFullParse3() {
        ParserResults res = loadFile("parser/convlang3.json");
        dumpObj("actions:", res.converters);
        chkNoErrors(res);

        FieldCopyJsonParser parser = createParser();
        ParsedConverterSpec action = res.converters.get(0);
        CopySpec spec = parser.buildSpecFromAction(action, res.options);
        assertEquals("org.dnal.fieldcopy.dataclass.Inner1", spec.srcClass.getName());
        assertEquals("org.dnal.fieldcopy.dataclass.Inner1DTO", spec.destClass.getName());
        assertEquals(1, spec.fields.size());
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        assertEquals("s3", nspec.srcText);
        assertEquals("s3", nspec.destText);
    }

    @Test
    public void testCustom1() {
        ParserResults res = loadFile("parser/convlang-custom1.json");
        dumpObj("actions:", res.converters);
        chkNoErrors(res);

        FieldCopyJsonParser parser = createParser();
        ParsedConverterSpec action = res.converters.get(0);
        CopySpec spec = parser.buildSpecFromAction(action, res.options);
        assertEquals("org.dnal.fieldcopy.dataclass.Inner1", spec.srcClass.getName());
        assertEquals("org.dnal.fieldcopy.dataclass.Inner1DTO", spec.destClass.getName());
        assertEquals(0, spec.fields.size());
        assertEquals(true, spec.autoFlag);
    }

    @Test
    public void testCustom2() {
        ParserResults res = loadFile("parser/convlang-custom2.json");
        dumpObj("actions:", res.converters);
        chkNoErrors(res);

        FieldCopyJsonParser parser = createParser();
        ParsedConverterSpec action = res.converters.get(0);
        CopySpec spec = parser.buildSpecFromAction(action, res.options);
        assertEquals("org.dnal.fieldcopy.dataclass.Src1", spec.srcClass.getName());
        assertEquals("org.dnal.fieldcopy.dataclass.Dest1", spec.destClass.getName());
        assertEquals(1, spec.fields.size());
        assertEquals(true, spec.autoFlag);
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        assertEquals("n1", nspec.srcText);
        assertEquals("n1", nspec.destText);
    }

    @Test
    public void testCustom3() {
        ParserResults res = loadFile("parser/convlang-custom3.json");
        dumpObj("actions:", res.converters);
        chkNoErrors(res);

        FieldCopyJsonParser parser = createParser();
        ParsedConverterSpec action = res.converters.get(0);
        CopySpec spec = parser.buildSpecFromAction(action, res.options);
        assertEquals("org.dnal.fieldcopy.dataclass.Src1", spec.srcClass.getName());
        assertEquals("org.dnal.fieldcopy.dataclass.Dest1", spec.destClass.getName());
        assertEquals(1, spec.fields.size());
        assertEquals(true, spec.autoFlag);
        assertEquals(2, spec.autoExcludeFields.size());
        assertEquals("s2", spec.autoExcludeFields.get(0));
        assertEquals("n1", spec.autoExcludeFields.get(1));
    }

    @Test
    public void testAdditionalConverters() {
        ParserResults res = loadFile("parser/convlang2.json");
        dumpObj("actions:", res.converters);
        chkNoErrors(res);

        FieldCopyJsonParser parser = createParser();
        ParsedConverterSpec action = res.converters.get(0);
        CopySpec spec = parser.buildSpecFromAction(action, res.options);
        assertEquals("org.dnal.fieldcopy.dataclass.Inner1", spec.srcClass.getName());
        assertEquals("org.dnal.fieldcopy.dataclass.Inner1DTO", spec.destClass.getName());
        assertEquals(1, spec.fields.size());
        assertEquals(1, action.additionalConverters.size());
        assertEquals("SomeConverter", action.additionalConverters.get(0).converterClassName);
    }

    @Test
    public void testConfig() {
        ParserResults res = loadFile("parser/convlang-config1.json");
        dumpObj("actions:", res.converters);
        chkNoErrors(res);

        assertEquals("org.dnal.fieldcopy.service", res.options.defaultSourcePackage);
        assertEquals("org.dnal.fieldcopy.other", res.options.defaultDestinationPackage);
    }

    @Test
    public void testParseBadJson() {
        ParserResults res = loadFile("parser/convlang-bad1.json");
        dumpObj("actions:", res.converters);
        assertEquals(1, res.errors.size());
    }
    @Test
    public void testParseBadJson2() {
        ParserResults res = loadFile("parser/convlang-bad2.json");
        dumpObj("actions:", res.converters);
        chkNoErrors(res);

        FieldCopyJsonParser parser = createParser();
        ParsedConverterSpec action = res.converters.get(0);

        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
            CopySpec spec = parser.buildSpecFromAction(action, res.options);
        });
        chkException(thrown, "syntax error");
    }

    //============

}
