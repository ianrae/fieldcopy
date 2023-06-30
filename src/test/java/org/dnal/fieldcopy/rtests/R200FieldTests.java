package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.builder.CollectionsBuilder;
import org.dnal.fieldcopy.builder.PrimsBuilder;
import org.dnal.fieldcopy.builder.SpecBuilder2;
import org.dnal.fieldcopy.dataclass.Dest1;
import org.dnal.fieldcopy.dataclass.OptionalSrc1;
import org.dnal.fieldcopy.dataclass.Src1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.types.JavaCollection;
import org.dnal.fieldcopy.types.JavaPrimitive;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

public class R200FieldTests extends RTestBase {

    /**
     * FieldTests
     * R250 field -> field
     * -alltypes Prim of src field
     * -alltypes Scal of dest field
     * <p>
     * R255 optional -> optional
     * some scalar types: opt -> opt
     * some scalar types: opt -> fld
     * some scalar types: fld -> opt
     * optional list -> list
     * list -> optional list
     * optional list -> optional list
     * R260
     * list -> list
     * set -> set
     * map -> map
     * array -> array
     * <p>
     * R270
     * enum -> enum
     */

    @Test
    public void testR210() {
        for (JavaPrimitive prim : JavaPrimitive.values()) {
            testPrimValue(prim);
        }
    }

    @Test
    public void testR210a() {
        for (JavaPrimitive prim : JavaPrimitive.values()) {
            testScalarValue(prim);
        }
    }

    @Test
    public void testR212() {
        for (JavaPrimitive prim : JavaPrimitive.values()) {
            testScalarValue(prim);
        }
    }

    @Test
    public void testR220String() {
        CopySpec spec = specBuilder.buildSpec(2);
        List<String> lines = doGen(spec);
        String[] ar = {
                "int tmp1 = src.getN1();",
                "dest.setN1(tmp1);",
                "String tmp2 = src.getS2();",
                "dest.setS2(tmp2);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void test200Enum() {
        CopySpec spec = buildWithField(Src1.class, Dest1.class, "col1", "col1");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Color tmp1 = src.getCol1();",
                "dest.setCol1(tmp1);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Color");
    }

    //--- dates ---
    @Test
    public void testR100LocalDate() {
        List<String> lines = buildAndGenForDate("date", "date");
        String[] ar = {
                "LocalDate tmp1 = src.getDate();",
                "dest.setDate(tmp1);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.LocalDate");
    }

    @Test
    public void testR100LocalTime() {
        List<String> lines = buildAndGenForDate("time", "time");
        String[] ar = {
                "LocalTime tmp1 = src.getTime();",
                "dest.setTime(tmp1);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.LocalTime");
    }

    @Test
    public void testR100LocalDateTime() {
        List<String> lines = buildAndGenForDate("ldt", "ldt");
        String[] ar = {
                "LocalDateTime tmp1 = src.getLdt();",
                "dest.setLdt(tmp1);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.LocalDateTime");
    }

    @Test
    public void testR100ZonedDateTime() {
        List<String> lines = buildAndGenForDate("zdt", "zdt");
        String[] ar = {
                "ZonedDateTime tmp1 = src.getZdt();",
                "dest.setZdt(tmp1);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.ZonedDateTime");
    }

    @Test
    public void testR100UtilDate() {
        List<String> lines = buildAndGenForDate("utilDate", "utilDate");
        String[] ar = {
                "Date tmp1 = src.getUtilDate();",
                "dest.setUtilDate(tmp1);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.util.Date");
    }

    //--- optional
    @Test
    public void testR240OptToNonOpt() {
        CopySpec spec = buildWithField(OptionalSrc1.class, Dest1.class, "s2", "s2");
        List<String> lines = doGen(spec);

        Optional<String> opt = Optional.of("sdf");
        String ss = opt.orElse(null);

//        String[] ar = {
//                "Optional<String> tmp1 = src.s2;",
//                "dest.setS2((tmp1 == null) ? null : tmp1.orElse(null));"};
        String[] ar = {
                "Optional<String> tmp1 = src.s2;",
                "dest.setS2((ctx.isNullOrEmpty(tmp1)) ? null : tmp1.orElse(null));"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testR240NotOptToOpt() {
        CopySpec spec = buildWithField(Dest1.class, OptionalSrc1.class, "s2", "s2");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.getS2();",
                "dest.s2 = Optional.ofNullable(tmp1);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testR240OptToOpt() {
        CopySpec spec = buildWithField(OptionalSrc1.class, OptionalSrc1.class, "s2", "s2");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Optional<String> tmp1 = src.s2;",
                "dest.s2 = tmp1;"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    //TOOD
//    @Test
//    public void testR240OptOfList() {
//        CopySpec spec = buildWithField(OptionalSrc1.class, OptionalSrc1.class, "profiles", "profiles");
//        List<String> lines = doGen(spec);
//
//        String[] ar = {
//                "Optional<String> tmp1 = src.s2;",
//                "dest.s2 = tmp1.get();"};
//        chkLines(lines, ar);
//        chkImports(currentSrcSpec, "java.util.Optional");
//    }

    //--- lists
    @Test
    public void testR230() {
        options.createNewListWhenCopying = false;
        for (JavaCollection prim : JavaCollection.values()) {
            testCollectionValue(prim);
        }
    }

    @Test
    public void testListWithCopy() {
        CopySpec spec = collectionsBuilder.buildSpec(JavaCollection.LIST);
        List<String> lines = doGen(spec);
        String importStr = JavaCollection.getImport(JavaCollection.LIST);

        String[] ar = {"List<String> tmp1 = src._list == null ? null : ctx.createEmptyList(src._list, String.class);",
                "dest._list = tmp1;"
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, importStr);
    }


    //TODO:
    //R231 list<list>, list<set>, list<map>
    //R232 set<list>, set<set>, set<map>
    //R233 map<list>, map<set>, map<map>


    //--for debugging only
    @Test
    public void testDebug() {
    }

    //============
    private PrimsBuilder primsBuilder = new PrimsBuilder();
    private SpecBuilder2 specBuilder2 = new SpecBuilder2();
    private CollectionsBuilder collectionsBuilder = new CollectionsBuilder();

    private void testPrimValue(JavaPrimitive prim) {
        CopySpec spec = primsBuilder.buildSpec(prim);
        List<String> lines = doGen(spec);

        String javaTypeStr = JavaPrimitive.lowify(prim);

        String[] ar = {String.format("%s tmp1 = src._%s;", javaTypeStr, javaTypeStr),
                String.format("dest._%s = tmp1;", javaTypeStr)};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    private void testScalarValue(JavaPrimitive prim) {
        CopySpec spec = primsBuilder.buildSpecScalars(prim);
        List<String> lines = doGen(spec);

//        String str = StringUtil.uppify(prim.name().toLowerCase(Locale.ROOT));
        String str = JavaPrimitive.lowify(prim);
        String javaTypeStr = JavaPrimitive.getScalarType(prim);

        String[] ar = {String.format("%s tmp1 = src._%s;", javaTypeStr, str),
                String.format("dest._%s = tmp1;", str)};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    private void testCollectionValue(JavaCollection prim) {
        CopySpec spec = collectionsBuilder.buildSpec(prim);
        List<String> lines = doGen(spec);

        String str = JavaCollection.lowify(prim);
        String javaTypeStr = JavaCollection.getVarType(prim, "String", "Integer"); //Integer only used by Map
        String importStr = JavaCollection.getImport(prim);

        String[] ar = {String.format("%s tmp1 = src._%s;", javaTypeStr, str),
                String.format("dest._%s = tmp1;", str)};
        chkLines(lines, ar);
        if (isNull(importStr)) {
            chkNoImports(currentSrcSpec);
        } else {
            chkImports(currentSrcSpec, importStr);
        }
    }

}
