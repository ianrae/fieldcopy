package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.dataclass.*;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

/**
 R500 default
 -means we use defaultVal if src value is null
 -defaultVal can be null
 -value -> field
 -field -> field
 -prim
 -scalar
 -list,date,enum
 -NonO->O,O->NonO,O->O
 */
public class R500DefaultTests extends RTestBase {

    @Test
    public void testValuePrim() {
        CopySpec spec = buildSpecValue(Src1.class, Dest1.class,"44", "n1");
        setDefault(spec, "345");
        List<String> lines = doGen(spec);

        //required is ignored for primitives which can't be null
        String[] ar = {
                "int tmp1 = 44;",
                "dest.setN1(tmp1);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }
    @Test
    public void testValueScalar() {
        CopySpec spec = buildSpecValue(Src1.class, Dest1.class,"'abc'", "s2");
        setDefault(spec, "hello");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = \"abc\";",
                "if (tmp1 == null) tmp1 = \"hello\";",
                "dest.setS2(tmp1);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }
    @Test
    public void testValueEnum() {
        CopySpec spec = buildSpecValue(Src1.class, Dest1.class,"'RED'", "col1");
        setDefault(spec, "RED");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Color tmp1 = Color.RED;",
                "if (tmp1 == null) tmp1 = Color.RED;",
                "dest.setCol1(tmp1);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Color");
    }

    @Test
    public void testValueDate() {
        CopySpec spec = buildValueForDate("'2022-02-28'", "date");
        setDefault(spec, "2020-03-31");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = \"2022-02-28\";",
                "if (tmp1 == null) tmp1 = ctx.toLocalDate(\"2020-03-31\");",
                "LocalDate tmp2 = ctx.toLocalDate(tmp1);",
                "dest.setDate(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.LocalDate");
    }
    //let's assume other date types work

    @Test
    public void testOptionalValue() {
        CopySpec spec = buildSpecValue(OptionalSrc1.class, OptionalSrc1.class,"44", "n1");
        setDefault(spec, "55");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Integer tmp1 = 44;",
                "if (tmp1 == null) tmp1 = 55;",
                "dest.n1 = Optional.ofNullable(tmp1);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    // --- fields
    @Test
    public void testField() {
        CopySpec spec = buildOneField("_long", "_int");
        setDefault(spec, "55");
        List<String> lines = doGen(spec);

        //Note if scalar value (src._long) is null, we have no way to represent null in a prim
        //so let it crash
        //TODO add options.defaultNullScalarToDefault
        //Note. user can use default(0) for this, or required

        String[] ar = {
                "Long tmp1 = src._long;",
                "if (tmp1 == null) tmp1 = 55L;",
                "int tmp2 = tmp1.intValue();",
                "dest._int = tmp2;"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }
    @Test
    public void testFieldEnum() {
        CopySpec spec = buildWithField(Src1.class, Dest1.class,"col1", "col1");
        setDefault(spec, "RED");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Color tmp1 = src.getCol1();",
                "if (tmp1 == null) tmp1 = Color.RED;",
                "dest.setCol1(tmp1);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Color");
    }
    @Test
    public void testOptionalField() {
        CopySpec spec = buildWithField(OptionalSrc1.class, OptionalSrc1.class,"n1", "n1");
        setDefault(spec, "55");
        List<String> lines = doGen(spec);

        Optional<Integer> tmp1 = Optional.of(55);
        if (tmp1 == null || !tmp1.isPresent()) tmp1 = Optional.ofNullable(55);

        String[] ar = {
                "Optional<Integer> tmp1 = src.n1;",
                "if (tmp1 == null || !tmp1.isPresent()) tmp1 = Optional.ofNullable(55);",
                "dest.n1 = tmp1;"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testFieldListElement() {
        CopySpec spec = buildOneListField("_double", "_int");
        setElementDefault(spec, "66");
        List<String> lines = doGen(spec);

        String[] ar = {
                "List<Double> tmp1 = src._double == null ? null : ctx.createEmptyList(src._double, Double.class);",
                "List<Integer> list2 = new ArrayList<>();",
                "for(Double el3: tmp1) {",
                "  if (tmp1 == null) tmp1 = 66d;",
                "  Integer tmp4 = el3.intValue();",
                "  list2.add(tmp4);",
                "}",
                "dest._int = list2;"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.util.List");
    }

    @Test
    public void testFieldList() {
        CopySpec spec = buildOneListField("_double", "_int");
        setDefault(spec, "66"); //list field ignores defaultValu
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

    //----------
    private CopySpec buildOneField(String src, String dest) {
        return buildWithField(AllScalars1.class, AllPrims1.class, src, dest);
    }

    private void setDefault(CopySpec spec, String defaultVal) {
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        nspec.defaultVal = defaultVal;
    }
    private void setElementDefault(CopySpec spec, String defaultVal) {
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        nspec.elementDefaultVal = defaultVal;
    }
    private CopySpec buildOneListField(String src, String dest) {
        return buildWithField(AllLists1.class, AllLists1.class, src, dest);
    }

}
