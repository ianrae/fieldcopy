package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.dataclass.AllLists1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * R300 field->field //implicit-conversion
 * -list,etc
 */
public class R300ConversionListTests extends RTestBase {

    @Test
    public void test1() {
        CopySpec spec = buildOneListField("_int", "_byte");
        List<String> lines = doGen(spec);

        //is this ok?
        AllLists1 src = new AllLists1();
        AllLists1 dest = new AllLists1();
        List<Integer> tmp1 = src._int == null ? null : new ArrayList<>(src._int);
        if (tmp1 == null) {
            dest._byte = null;
        } else {
            List<Byte> list2 = new ArrayList<>();
            for (Integer el3 : tmp1) {
                Byte tmp4 = el3.byteValue();
                list2.add(tmp4);
            }
            dest._byte = list2;
        }

        String[] ar = {
                "List<Integer> tmp1 = src._int == null ? null : ctx.createEmptyList(src._int, Integer.class);",
                "List<Byte> list2 = new ArrayList<>();",
                "for(Integer el3: tmp1) {",
                "  Byte tmp4 = el3.byteValue();",
                "  list2.add(tmp4);",
                "}",
                "dest._byte = list2;"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.util.List");
    }

    @Test
    public void test2() {
        CopySpec spec = buildOneListField("_double", "_int");
        List<String> lines = doGen(spec);

        String[] ar = {
                "List<Double> tmp1 = src._double == null ? null : ctx.createEmptyList(src._double, Double.class);",
                "List<Integer> list2 = new ArrayList<>();",
                "for(Double el3: tmp1) {",
                "  Integer tmp4 = el3.intValue();",
                "  list2.add(tmp4);",
                "}",
                "dest._int = list2;"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.util.List");
    }

    @Test
    public void test3() {
        runAndChkNoSupported("_int", "_boolean");
    }

    //TODO perhaps add a testAll, but we already have tests for all scalar->scalar types


    //--for debugging only
    @Test
    public void testDebug() {
    }


    //============
    private CopySpec buildOneListField(String src, String dest) {
        return buildWithField(AllLists1.class, AllLists1.class, src, dest);
    }

    private void runAndChk(String src, String dest, String[] ar) {
        log(String.format("  %s -> %s", src, src));
        CopySpec spec = buildOneListField(src, dest);
        List<String> lines = doGen(spec);
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    private void runAndChkNoSupported(String src, String dest) {
        log(String.format("  %s -> %s", src, src));
        CopySpec spec = buildOneListField(src, dest);
        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
            List<String> lines = doGen(spec);
        });
        chkException(thrown, "Cannot convert '");
    }
}
