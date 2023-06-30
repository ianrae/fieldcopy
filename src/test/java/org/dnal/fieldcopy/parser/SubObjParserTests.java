package org.dnal.fieldcopy.parser;

import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyJsonParser;
import org.dnal.fieldcopy.parser.fieldcopyjson.ParsedConverterSpec;
import org.dnal.fieldcopy.parser.fieldcopyjson.ParserResults;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class SubObjParserTests extends ParserTestBase {

    //4 possibilities
    //A - addr.city -> addr.city
    //B - str -> addr.city
    //C - addr.city -> str
    //D - str -> str //already covered in other tests

    @Test
    public void testSubObjA() {
        ParserResults res = loadFile("parser/convlang4a.json");
        dumpObj("actions:", res.converters);
        chkNoErrors(res);

        FieldCopyJsonParser parser = createParser();
        ParsedConverterSpec action = res.converters.get(0);
        CopySpec spec = parser.buildSpecFromAction(action, res.options);
        assertEquals("org.dnal.fieldcopy.dataclass.Customer", spec.srcClass.getName());
        assertEquals("org.dnal.fieldcopy.dataclass.Customer", spec.destClass.getName());
        assertEquals(1, spec.fields.size());
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        assertEquals(2, nspec.dfBuilder.getMax());

        chkSegment(nspec, 0, "addr", "addr");
        chkSegment(nspec, 1, "city", "city");
    }

    @Test
    public void testSubObjB() {
        ParserResults res = loadFile("parser/convlang4b.json");
        dumpObj("actions:", res.converters);
        chkNoErrors(res);

        FieldCopyJsonParser parser = createParser();
        ParsedConverterSpec action = res.converters.get(0);
        CopySpec spec = parser.buildSpecFromAction(action, res.options);
        assertEquals("org.dnal.fieldcopy.dataclass.Customer", spec.srcClass.getName());
        assertEquals("org.dnal.fieldcopy.dataclass.Customer", spec.destClass.getName());
        assertEquals(1, spec.fields.size());
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        assertEquals(2, nspec.dfBuilder.getMax());

        chkSegment(nspec, 0, null, "addr");
        chkSegment(nspec, 1, "firstName", "city");
    }

    @Test
    public void testSubObjC() {
        ParserResults res = loadFile("parser/convlang4c.json");
        dumpObj("actions:", res.converters);
        chkNoErrors(res);

        FieldCopyJsonParser parser = createParser();
        ParsedConverterSpec action = res.converters.get(0);
        CopySpec spec = parser.buildSpecFromAction(action, res.options);
        assertEquals("org.dnal.fieldcopy.dataclass.Customer", spec.srcClass.getName());
        assertEquals("org.dnal.fieldcopy.dataclass.Customer", spec.destClass.getName());
        assertEquals(1, spec.fields.size());
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        assertEquals(2, nspec.dfBuilder.getMax());

        chkSegment(nspec, 0, "addr", null);
        chkSegment(nspec, 1, "city", "firstName");
    }

    //============
    private void chkSegment(NormalFieldSpec nspec, int i, String srcExpected, String destExpected) {
        assertEquals(srcExpected, nspec.dfBuilder.getIthSrc(i));
        assertEquals(destExpected, nspec.dfBuilder.getIthDest(i));
//        assertEquals(i > 0, nspec.isSegment);
    }


}
