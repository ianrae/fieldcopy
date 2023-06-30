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
public class ValueParserTests extends ParserTestBase {

    //types of values
    // 44
    //TODO these:
    // false,true
    // 'str' or "sdfsd"
    // 'b'
    // 44.56

    @Test
    public void testNumber() {
        ParserResults res = loadFile("parser/convlang-value1.json");
        dumpObj("actions:", res.converters);
        chkNoErrors(res);

        FieldCopyJsonParser parser = createParser();
        ParsedConverterSpec action = res.converters.get(0);
        CopySpec spec = parser.buildSpecFromAction(action, res.options);
        assertEquals("org.dnal.fieldcopy.dataclass.AllPrims1", spec.srcClass.getName());
        assertEquals("org.dnal.fieldcopy.dataclass.AllPrims1", spec.destClass.getName());
        assertEquals(1, spec.fields.size());
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        assertEquals(null, nspec.dfBuilder);
        assertEquals(true, nspec.srcTextIsValue);

        assertEquals("44", nspec.srcText);
        assertEquals("_int", nspec.destText);
    }


    //============
    private void chkSegment(NormalFieldSpec nspec, int i, String srcExpected, String destExpected) {
        assertEquals(srcExpected, nspec.dfBuilder.getIthSrc(i));
        assertEquals(destExpected, nspec.dfBuilder.getIthDest(i));
//        assertEquals(i > 0, nspec.isSegment);
    }


}
