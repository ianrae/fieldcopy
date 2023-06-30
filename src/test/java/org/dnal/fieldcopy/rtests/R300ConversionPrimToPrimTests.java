package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.dataclass.AllPrims1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.types.JavaPrimitive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * R300 field->field //implicit-conversion
 * -we have lower-level tests of all the combinations
 * -number,char,bool,string,date
 * -prim -> prim
 * -prim -> scalar
 * -scalar -> prim
 * -scalar -> scalar
 * -list,etc
 * -null src value handled correctly
 * -date
 * -enum
 * -NonO->O,O->NonO,O->O
 * -test int,string,list,enum,date
 */
public class R300ConversionPrimToPrimTests extends RTestBase {

    @Test
    public void test1() {
        CopySpec spec = buildOneField("_long", "_int");
        List<String> lines = doGen(spec);

        String[] ar = {
                "long tmp1 = src._long;",
                "int tmp2 = (int)tmp1;",
                "dest._int = tmp2;"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void test2() {
        CopySpec spec = buildOneField( "_int", "_long");
        List<String> lines = doGen(spec);

        String[] ar = {
                "int tmp1 = src._int;",
                "dest._long = tmp1;"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testAll() {
        List<JavaPrimitive> skipThese = Arrays.asList(JavaPrimitive.BYTE, JavaPrimitive.BOOLEAN);
        for (JavaPrimitive primSrc : JavaPrimitive.values()) {
            if (skipThese.contains(primSrc)) {
                continue;
            }

            String srcTypeStr = JavaPrimitive.lowify(primSrc);
            String srcField = String.format("_%s", srcTypeStr);
            for (JavaPrimitive primDest : JavaPrimitive.values()) {
                if (skipThese.contains(primDest)) {
                    continue;
                }
                String destTypeStr = JavaPrimitive.lowify(primDest);
                String destField = String.format("_%s", destTypeStr);
                CopySpec spec = buildOneField(srcField, destField);
                List<String> lines = doGen(spec);

                log(String.format("  %s -> %s", srcTypeStr, destTypeStr));
                String[] ar = {};
                if (lines.size() == 2) {
                    String line1 = String.format("%s tmp1 = src.%s;", srcTypeStr, srcField);
                    String line2 = String.format("dest.%s = tmp1;", destField);
                    String[] ar2 = {line1, line2};
                    ar = ar2;
                } else {
                    String line1 = String.format("%s tmp1 = src.%s;", srcTypeStr, srcField);
                    String line2 = String.format("%s tmp2 = (%s)tmp1;", destTypeStr, destTypeStr);
                    String line3 = String.format("dest.%s = tmp2;", destField);
                    String[] ar2 = {line1, line2, line3};
                    ar = ar2;
                }
                chkLines(lines, ar);
                chkNoImports(currentSrcSpec);
            }
        }
    }

    @Test
    public void testByte() {
        String destField = "_byte";
        String[] ar = {
                "int tmp1 = src._int;",
                "byte tmp2 = (byte)tmp1;",
                "dest._byte = tmp2;"};
        runAndChk("_int", destField, ar);

        String[] ar2 = {
                "short tmp1 = src._short;",
                "byte tmp2 = (byte)tmp1;",
                "dest._byte = tmp2;"};
        runAndChk("_short", destField, ar2);

        String[] ar3 = {
                "long tmp1 = src._long;",
                "byte tmp2 = (byte)tmp1;",
                "dest._byte = tmp2;"};
        runAndChk("_long", destField, ar3);

        String[] ar4 = {
                "double tmp1 = src._double;",
                "byte tmp2 = (byte)tmp1;",
                "dest._byte = tmp2;"};
        runAndChk("_double", destField, ar4);

        runAndChkNoSupported("_boolean", destField);

        String[] ar5 = {
                "char tmp1 = src._char;",
                "dest._byte = tmp1;"};
        runAndChk("_char", destField, ar5);
    }

    @Test
    public void testBoolean() {
        String destField = "_boolean";

        runAndChkNoSupported("_byte", destField);
        runAndChkNoSupported("_short", destField);
        runAndChkNoSupported("_int", destField);
        runAndChkNoSupported("_long", destField);
        runAndChkNoSupported("_float", destField);
        runAndChkNoSupported("_char", destField);
    }

    //--for debugging only
    @Test
    public void testDebug() {
    }

    //============

    private void runAndChk(String src, String dest, String[] ar) {
        log(String.format("  %s -> %s", src, src));
        CopySpec spec = buildOneField(src, dest);
        List<String> lines = doGen(spec);
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    private CopySpec buildOneField(String src, String dest) {
        return buildWithField(AllPrims1.class, AllPrims1.class, src, dest);
    }

    private void runAndChkNoSupported(String src, String dest) {
        log(String.format("  %s -> %s", src, src));
        CopySpec spec = buildOneField(src, dest);
        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
            List<String> lines = doGen(spec);
        });
        chkException(thrown, "Cannot convert '");
    }
}
