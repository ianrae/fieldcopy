package org.dnal.fieldcopy;

import org.dnal.fieldcopy.builder.SpecBuilder1;
import org.dnal.fieldcopy.codegen.CodeGenerator;
import org.dnal.fieldcopy.codegen.JavaSrcSpec;
import org.dnal.fieldcopy.codegen.VarNameGenerator;
import org.dnal.fieldcopy.dataclass.Inner1;
import org.dnal.fieldcopy.dataclass.Src1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FieldCopy2Tests extends TestBase {

    @Test
    public void test0() {
        CodeGenerator codegen = buildCodeGenerator();
        CopySpec spec = buildSpec(1);
        JavaSrcSpec srcSpec = codegen.generate(spec);

        assertEquals("Src1ToDest1Converter", srcSpec.className);
        List<String> lines = srcSpec.lines;
        dumpLines(lines);
        assertEquals(2, lines.size());
        assertEquals("int tmp1 = src.getN1();", lines.get(0));
        assertEquals("dest.setN1(tmp1);", lines.get(1));
    }

    @Test
    public void test1() {
        CodeGenerator codegen = buildCodeGenerator();
        CopySpec spec = buildSpec(1);

        JavaSrcSpec srcSpec = codegen.generate(spec);
        List<String> lines = srcSpec.lines;
        dumpLines(lines);
        assertEquals(2, lines.size());
        assertEquals("int tmp1 = src.getN1();", lines.get(0));
        assertEquals("dest.setN1(tmp1);", lines.get(1));
    }

    @Test
    public void testInner1() {
        CodeGenerator codegen = buildCodeGenerator();
        CopySpec spec = buildSpecWithInner();

        JavaSrcSpec srcSpec = codegen.generate(spec);
        List<String> lines = srcSpec.lines;
        dumpLines(lines);
        //old
//        assertEquals(2, lines.size());
//        assertEquals("String tmp1 = Optional.ofNullable(src.getInner1()).map(x -> s3).orElse(null);", lines.get(0));
//        assertEquals("dest.setS2(tmp1);", lines.get(1));
        assertEquals(3, lines.size());
        assertEquals("Inner1 tmp1 = src.getInner1();", lines.get(0));
        assertEquals("String tmp2 = tmp1.s3;", lines.get(1));
        assertEquals("dest.setS2(tmp2);", lines.get(2));
    }

    @Test
    public void test2() {
        Field[] allFields = Src1.class.getFields();
        assertEquals(6, allFields.length);

        Field ff = allFields[0];
        Class<?> zz = ff.getType();
        log(zz.getName());
    }

    @Test
    public void test3() {
        Src1 src1 = new Src1();
        Optional<Inner1> opt = Optional.ofNullable(src1).map(x -> x.inner1);
        assertEquals(false, opt.isPresent());

        String s1 = Optional.ofNullable(src1).map(x -> x.inner1).map(x -> x.s3).orElse(null);
        assertEquals(null, s1);

        src1.inner1 = new Inner1();
        src1.inner1.s3 = "abc";
        s1 = Optional.ofNullable(src1).map(x -> x.inner1).map(x -> x.s3).orElse(null);
        assertEquals("abc", s1);
    }

    @Test
    public void test4() {
        CodeGenerator codegen = buildCodeGenerator();
        CopySpec spec = buildSpec(2);
        JavaSrcSpec srcSpec = codegen.generate(spec);

        assertEquals("Src1ToDest1Converter", srcSpec.className);
        List<String> lines = srcSpec.lines;
        chkLines(lines, "int tmp1 = src.getN1();",
                "dest.setN1(tmp1);",
                "String tmp2 = src.getS2();",
                "dest.setS2(tmp2);");
    }

    @Test
    public void test4a() {
        CodeGenerator codegen = buildCodeGenerator();
        CopySpec spec = buildSpec(3);
        JavaSrcSpec srcSpec = codegen.generate(spec);

        assertEquals("Src1ToDest1Converter", srcSpec.className);
        List<String> lines = srcSpec.lines;
        chkLines(lines, "int tmp1 = src.getN1();",
                "dest.setN1(tmp1);",
                "String tmp2 = src.getS2();",
                "dest.setS2(tmp2);",
                "Inner1 tmp3 = src.getInner1();",
                "String tmp4 = tmp3.s3;",
                "dest.setS2(tmp4);");
    }

    //============
    private SpecBuilder1 specBuilder = new SpecBuilder1();

    private CodeGenerator buildCodeGenerator() {
        CodeGenerator codegen = new CodeGenerator();
        codegen.getOptions().outputFieldCommentFlag = false;
        return codegen;
    }

    private CopySpec buildSpec(int n) {
        return specBuilder.buildSpec(n);
    }

    private CopySpec buildSpecWithInner() {
        return specBuilder.buildSpecWithInner();
    }
}
