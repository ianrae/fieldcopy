package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.dataclass.*;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.junit.jupiter.api.Test;

import java.util.List;

/*
 R600  custom
 -codegen creates abstract base class
 -and creates method for the field
*/
public class R600CustomTests extends RTestBase {

    @Test
    public void testValuePrim() {
        CopySpec spec = buildSpecValue(Src1.class, Dest1.class,"44", "n1");
        setCustom(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "int tmp1 = 44;",
                "dest.setN1(tmp1);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }
    @Test
    public void testValueScalar() {
        CopySpec spec = buildSpecValue(Src1.class, Dest1.class,"'abc'", "s2");
        setCustom(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = \"abc\";",
                "dest.setS2(tmp1);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }
    @Test
    public void testValueEnum() {
        CopySpec spec = buildSpecValue(Src1.class, Dest1.class,"'RED'", "col1");
        setCustom(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "Color tmp1 = Color.RED;",
                "dest.setCol1(tmp1);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Color");
    }

    @Test
    public void testValueDate() {
        CopySpec spec = buildValueForDate("'2022-02-28'", "date");
        setCustom(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = \"2022-02-28\";",
                "LocalDate tmp2 = ctx.toLocalDate(tmp1);",
                "dest.setDate(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.LocalDate");
    }

    //--optional
    @Test
    public void testOptionalValue() {
        CopySpec spec = buildSpecValue(OptionalSrc1.class, OptionalSrc1.class,"44", "n1");
        setCustom(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "Integer tmp1 = 44;",
                "dest.n1 = Optional.ofNullable(tmp1);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }


    // --- fields
    @Test
    public void testField() {
        CopySpec spec = buildWithField(AllScalars1.class, AllPrims1.class, "_long", "_int");
        setCustom(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "Long tmp1 = src._long;",
                "Long tmp2 = convert_long(tmp1, src, dest, ctx);",
                "dest._int = tmp2;"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }
    @Test
    public void testFieldEnum() {
        CopySpec spec = buildWithField(Src1.class, Dest1.class,"col1", "col1");
        setCustom(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "Color tmp1 = src.getCol1();",
                "Color tmp2 = convertCol1(tmp1, src, dest, ctx);",
                "dest.setCol1(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Color");
    }
    @Test
    public void testOptionalField() {
        CopySpec spec = buildWithField(OptionalSrc1.class, OptionalSrc1.class,"n1", "n1");
        setCustom(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "Optional<Integer> tmp1 = src.n1;",
                "Optional<Integer> tmp2 = convertN1(tmp1, src, dest, ctx);",
                "dest.n1 = tmp2;"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testFieldList() {
        CopySpec spec = buildWithField(AllLists1.class, AllLists1.class,"_double", "_int");
        setCustom(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "List<Double> tmp1 = src._double == null ? null : ctx.createEmptyList(src._double, Double.class);",
                "List<Double> tmp2 = convert_double(tmp1, src, dest, ctx);",
                "dest._int = tmp2;"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.util.List");
    }



    //------------------
    private void setCustom(CopySpec spec) {
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        nspec.isCustom = true;
    }
}
