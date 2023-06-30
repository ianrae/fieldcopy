package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.dataclass.*;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * R210 value -> field
 * -0, 'asdfsd',etc
 * <p>
 * public int _int;
 * public byte _byte;
 * public short _short;
 * public long _long;
 * public float _float;
 * public double _double;
 * public boolean _boolean;
 * public char _char;
 * <p>
 * and
 * string
 * enum
 * date,localDate,localTime,localDateTime,zonedDateTime
 */

public class R100ValueTests extends RTestBase {

    @Test
    public void testR100() {
        CopySpec spec = buildSpec(0);
        List<String> lines = doGen(spec);
        String[] arEmpty = {};
        chkLines(lines, arEmpty);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testR100a() {
        CopySpec spec = buildSpec(0);
        specBuilder.addValue(spec, "44", "n1");
        List<String> lines = doGen(spec);

        String[] ar = {
                "int tmp1 = 44;",
                "dest.setN1(tmp1);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testR100a1() {
        CopySpec spec = buildSpec(0);
        specBuilder.addValue(spec, "-44", "n1");
        List<String> lines = doGen(spec);

        String[] ar = {
                "int tmp1 = -44;",
                "dest.setN1(tmp1);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testR100b() {
        List<String> lines = buildAndGen("44", "_short");
        String[] ar = {
                "short tmp1 = 44;",
                "dest._short = tmp1;"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testR100Double() {
        List<String> lines = buildAndGen("44.5", "_double");
        String[] ar = {
                "double tmp1 = 44.5;",
                "dest._double = tmp1;"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testR100Boolean() {
        List<String> lines = buildAndGen("false", "_boolean");
        String[] ar = {
                "boolean tmp1 = false;",
                "dest._boolean = tmp1;"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testR100Boolean2() {
        List<String> lines = buildAndGen("true", "_boolean");
        String[] ar = {
                "boolean tmp1 = true;",
                "dest._boolean = tmp1;"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testR100Char() {
        List<String> lines = buildAndGen("'c'", "_char");

        String[] ar = {
                "char tmp1 = 'c';",
                "char tmp2 = Character.parseChar(tmp1);", //TODO is this necessary
                "dest._char = tmp2;"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testR100Enum() {
        CopySpec spec = new CopySpec(Src1.class, Dest1.class);
//        specBuilder.addValue(spec, "Color.RED", "col1");
        //TODO: later support RED or "RED" or Color.RED
        specBuilder.addValue(spec, "'RED'", "col1");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Color tmp1 = Color.RED;",
                "dest.setCol1(tmp1);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Color");
    }

    @Test
    public void testR100String() {
        CopySpec spec = new CopySpec(Src1.class, Dest1.class);
        specBuilder.addValue(spec, "'abc'", "s2");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = \"abc\";",
                "dest.setS2(tmp1);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testR100Null() {
        CopySpec spec = new CopySpec(Src1.class, Dest1.class);
        specBuilder.addValue(spec, "null", "s2");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = null;",
                "dest.setS2(tmp1);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }


    //-- date --
    @Test
    public void testLocalDate() {
        List<String> lines = buildValueAndGenForDate("'2022-02-28'", "date");
        String[] ar = {
                "String tmp1 = \"2022-02-28\";",
                "LocalDate tmp2 = ctx.toLocalDate(tmp1);",
                "dest.setDate(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.LocalDate");
    }
    @Test
    public void testLocalDate2() {
        //any value string is just propogated
        List<String> lines = buildValueAndGenForDate("'2022/02/28'", "date");
        String[] ar = {
                "String tmp1 = \"2022/02/28\";",
                "LocalDate tmp2 = ctx.toLocalDate(tmp1);",
                "dest.setDate(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.LocalDate");
    }

    @Test
    public void testLocalTime() {
        List<String> lines = buildValueAndGenForDate("'18:30:55'", "time");
        String[] ar = {
                "String tmp1 = \"18:30:55\";",
                "LocalTime tmp2 = ctx.toLocalTime(tmp1);",
                "dest.setTime(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.LocalTime");
    }

    @Test
    public void testLocalDateTime() {
        List<String> lines = buildValueAndGenForDate("'2023-02-28T18:30:55'", "ldt");
        String[] ar = {
                "String tmp1 = \"2023-02-28T18:30:55\";",
                "LocalDateTime tmp2 = ctx.toLocalDateTime(tmp1);",
                "dest.setLdt(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.LocalDateTime");
    }

    @Test
    public void testZonedDateTime() {
        List<String> lines = buildValueAndGenForDate("'2023-02-28T18:30:55-05:00[America/New_York]'", "zdt");
        String[] ar = {
                "String tmp1 = \"2023-02-28T18:30:55-05:00[America/New_York]\";",
                "ZonedDateTime tmp2 = ctx.toZonedDateTime(tmp1);",
                "dest.setZdt(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.ZonedDateTime");
    }

    @Test
    public void testUtilDate() {
        List<String> lines = buildValueAndGenForDate("'2022-02-28'", "utilDate");
        String[] ar = {
                "String tmp1 = \"2022-02-28\";",
                "Date tmp2 = ctx.toDate(tmp1);",
                "dest.setUtilDate(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.util.Date");
    }

    //---optional
    @Test
    public void testOptionalInt() {
        CopySpec spec = buildSpecValue(Src1.class, OptionalSrc1.class, "44", "n1");
        List<String> lines = doGen(spec);

        Optional<Integer> x = Optional.of(44);
        assertEquals(44, x.get());

        String[] ar = {
                "Integer tmp1 = 44;",
                "dest.n1 = Optional.ofNullable(tmp1);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testOptionalInt2() {
        //again with optional src field
        CopySpec spec = buildSpecValue(OptionalSrc1.class, OptionalSrc1.class, "44", "n1");
        List<String> lines = doGen(spec);

        Optional<Integer> x = Optional.of(44);
        assertEquals(44, x.get());

        String[] ar = {
                "Integer tmp1 = 44;",
                "dest.n1 = Optional.ofNullable(tmp1);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testOptionalString() {
        CopySpec spec = buildSpecValue(Src1.class, OptionalSrc1.class, "\"abc\"", "s2");
        List<String> lines = doGen(spec);

        Optional<String> x = Optional.of("abc");
        assertEquals("abc", x.get());

        String[] ar = {
                "String tmp1 = \"abc\";",
                "dest.s2 = Optional.ofNullable(tmp1);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    //    @Test
//    public void testOptionalStringFail() {
//        CopySpec spec = buildSpecValue(Src1.class, OptionalSrc1.class, "'abc'", "s2");
//
//        List<String> lines = null;
//        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
//            doGen(spec);
//        });
//        log(thrown.getMessage());
//        chkExecption(thrown, "String value must use \" delimiter, such as \"dog\". Single quote delimiter ' is not allowed.");
//    }
    @Test
    public void testOptionalEnum() {
        CopySpec spec = buildSpecValue(Src1.class, OptionalSrc1.class, "\"RED\"", "col1");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Color tmp1 = Color.RED;",
                "dest.col1 = Optional.ofNullable(tmp1);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Color");
    }

    @Test
    public void testOptionalLocalDateTime() {
        CopySpec spec = buildSpecValue(Src1.class, OptionalTestClass1.class, "'2023-02-28T18:30:55'", "ldt");
        List<String> lines = doGen(spec);
        String[] ar = {
                "String tmp1 = \"2023-02-28T18:30:55\";",
                "LocalDateTime tmp2 = ctx.toLocalDateTime(tmp1);",
                "dest.ldt = Optional.ofNullable(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.LocalDateTime");
    }


    //--for debugging only
    @Test
    public void testDebug() {
    }


    //============

}
