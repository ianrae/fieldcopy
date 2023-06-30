package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.dataclass.*;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * * -null src value handled correctly
 * * -date
 * * -enum
 * * -NonO->O,O->NonO,O->O --test int,string,list,enum,date
 */
public class R300ConversionOtherTests extends RTestBase {

    @Test
    public void testNullWithoutRequired() {
        CopySpec spec = buildOneField("_long", "_int");
        List<String> lines = doGen(spec);

        //Note if scalar value (src._long) is null, we have no way to represent null in a prim
        //so let it crash

        String[] ar = {
                "Long tmp1 = src._long;",
                "int tmp2 = tmp1.intValue();",
                "dest._int = tmp2;"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testNullWithDefault() {
        CopySpec spec = buildOneField("_long", "_int");
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        nspec.defaultVal = "0";
        List<String> lines = doGen(spec);

        //Note if scalar value (src._long) is null, we have no way to represent null in a prim
        //so let it crash
        //Note. user can use default(0) for this, or required
        //TODO perhaps add options.defaultNullScalarToDefault

        String[] ar = {
                "Long tmp1 = src._long;",
                "if (tmp1 == null) tmp1 = 0L;",
                "int tmp2 = tmp1.intValue();",
                "dest._int = tmp2;"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    //--enum
    @Test
    public void testEnum() {
        CopySpec spec = buildWithField(Src1.class, Dest1.class, "col1", "s2");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Color tmp1 = src.getCol1();",
                "String tmp2 = tmp1.name();",
                "dest.setS2(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Color");
    }

    //--dates
    @Test
    public void testLocalDate() {
        List<String> lines = buildAndGenForDate("date", "firstName");
        String[] ar = {
                "LocalDate tmp1 = src.getDate();",
                "String tmp2 = ctx.dateToString(tmp1);",
                "dest.setFirstName(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.LocalDate");
    }

    @Test
    public void testLocalTime() {
        List<String> lines = buildAndGenForDate("time", "firstName");
        String[] ar = {
                "LocalTime tmp1 = src.getTime();",
                "String tmp2 = ctx.timeToString(tmp1);",
                "dest.setFirstName(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.LocalTime");
    }

    @Test
    public void testLocalDateTime() {
        List<String> lines = buildAndGenForDate("ldt", "firstName");
        String[] ar = {
                "LocalDateTime tmp1 = src.getLdt();",
                "String tmp2 = ctx.dateTimeToString(tmp1);",
                "dest.setFirstName(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.LocalDateTime");
    }

    @Test
    public void testZonedDateTime() {
        List<String> lines = buildAndGenForDate("zdt", "firstName");
        String[] ar = {
                "ZonedDateTime tmp1 = src.getZdt();",
                "String tmp2 = ctx.zonedDateTimeToString(tmp1);",
                "dest.setFirstName(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.ZonedDateTime");
    }

    @Test
    public void testUtilDate() {
        List<String> lines = buildAndGenForDate("utilDate", "firstName");
        String[] ar = {
                "Date tmp1 = src.getUtilDate();",
                "String tmp2 = ctx.dateToString(tmp1);",
                "dest.setFirstName(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.util.Date");
    }

    //---optional
    @Test
    public void testOptToNonOpt() {
        CopySpec spec = buildWithField(OptionalSrc1.class, Dest1.class, "n1", "s2");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Optional<Integer> tmp1 = src.n1;",
                "String tmp2 = tmp1.get().toString();",
                "dest.setS2(tmp2);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testNotOptToOpt() {
        CopySpec spec = buildWithField(Dest1.class, OptionalSrc1.class, "n1", "s2");
        List<String> lines = doGen(spec);

        String[] ar = {
                "int tmp1 = src.getN1();",
                "String tmp2 = Integer.valueOf(tmp1).toString();",
                "dest.s2 = Optional.ofNullable(tmp2);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testOptToOpt() {
        CopySpec spec = buildWithField(OptionalSrc1.class, OptionalSrc1.class, "n1", "s2");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Optional<Integer> tmp1 = src.n1;",
                "String tmp2 = tmp1.get().toString();",
                "dest.s2 = Optional.ofNullable(tmp2);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }


    //--------------------------------
    private CopySpec buildOneField(String src, String dest) {
        return buildWithField(AllScalars1.class, AllPrims1.class, src, dest);
    }
}
