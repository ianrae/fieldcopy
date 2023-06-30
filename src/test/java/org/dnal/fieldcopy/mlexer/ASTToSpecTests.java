package org.dnal.fieldcopy.mlexer;

import org.dnal.fieldcopy.TestBase;
import org.dnal.fieldcopy.codegen.CodeGenerator;
import org.dnal.fieldcopy.codegen.JavaSrcSpec;
import org.dnal.fieldcopy.dataclass.AllScalars1;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.dataclass.Dest1;
import org.dnal.fieldcopy.dataclass.Src1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.mlexer.ast.AST;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TODO
 * - add required
 */
public class ASTToSpecTests extends TestBase {

    @Test
    public void test() {
        CodeGenerator codegen = new CodeGenerator();
        CopySpec spec = buildSpecFromAST("n1 -> n1");
        JavaSrcSpec srcSpec = codegen.generate(spec);

        assertEquals("Src1ToDest1Converter", srcSpec.className);
        List<String> lines = srcSpec.lines;
        dumpLines(lines);
        assertEquals(4, lines.size());
        assertEquals("", lines.get(0));
        assertEquals("// n1 -> n1", lines.get(1));
        assertEquals("int tmp1 = src.getN1();", lines.get(2));
        assertEquals("dest.setN1(tmp1);", lines.get(3));
    }

    @Test
    public void testRequired() {
        CodeGenerator codegen = new CodeGenerator();
        codegen.getOptions().outputFieldCommentFlag = false;
        CopySpec spec = buildSpecFromAST("s2 -> s2 required");
        JavaSrcSpec srcSpec = codegen.generate(spec);

        assertEquals("Src1ToDest1Converter", srcSpec.className);
        List<String> lines = srcSpec.lines;
        dumpLines(lines);
        assertEquals(3, lines.size());
        assertEquals("String tmp1 = src.getS2();", lines.get(0));
        assertEquals("if (tmp1 == null) ctx.throwUnexpectedNullError(getSourceFieldTypeInfo(), \"s2\");", lines.get(1));
        assertEquals("dest.setS2(tmp1);", lines.get(2));
    }

    @Test
    public void testDefault() {
        CodeGenerator codegen = new CodeGenerator();
        codegen.getOptions().outputFieldCommentFlag = false;
        CopySpec spec = buildSpecFromAST("s2 -> s2 default('def')");
        JavaSrcSpec srcSpec = codegen.generate(spec);

        assertEquals("Src1ToDest1Converter", srcSpec.className);
        List<String> lines = srcSpec.lines;

        String[] ar = {
                "String tmp1 = src.getS2();",
                "if (tmp1 == null) tmp1 = \"def\";",
                "dest.setS2(tmp1);"};
        chkLines(lines, ar);
    }

    @Test
    public void testElementDefault() {
        CodeGenerator codegen = new CodeGenerator();
        codegen.getOptions().outputFieldCommentFlag = false;
        CopySpec spec = buildSpecFromAST("roles -> roles default('def')", Customer.class, Customer.class);
        JavaSrcSpec srcSpec = codegen.generate(spec);

        assertEquals("CustomerToCustomerConverter", srcSpec.className);
        List<String> lines = srcSpec.lines;
        dumpLines(lines);
        //Note registry is null in these tests so no implicit conversions. They are tested in R500
        assertEquals(2, lines.size());
        assertEquals("List<String> tmp1 = src.getRoles() == null ? null : ctx.createEmptyList(src.getRoles(), String.class);", lines.get(0));
        assertEquals("dest.setRoles(tmp1);", lines.get(1));
    }

    @Test
    public void testNegativeDefault() {
        CodeGenerator codegen = new CodeGenerator();
        codegen.getOptions().outputFieldCommentFlag = false;
        CopySpec spec = buildSpecFromAST("n2 -> n2 default(-2)", Src1.class, Src1.class);
        JavaSrcSpec srcSpec = codegen.generate(spec);

        assertEquals("Src1ToSrc1Converter", srcSpec.className);
        List<String> lines = srcSpec.lines;
        dumpLines(lines);
        //Note registry is null in these tests so no implicit conversions. They are tested in R500
        assertEquals(3, lines.size());
        assertEquals("Integer tmp1 = src.getN2();", lines.get(0));
        assertEquals("if (tmp1 == null) tmp1 = -2;", lines.get(1));
        assertEquals("dest.setN2(tmp1);", lines.get(2));
    }

    @Test
    public void testFloatDefault() {
        CodeGenerator codegen = new CodeGenerator();
        codegen.getOptions().outputFieldCommentFlag = false;
        CopySpec spec = buildSpecFromAST("_double -> _double default(14.56)", AllScalars1.class, AllScalars1.class);
        JavaSrcSpec srcSpec = codegen.generate(spec);

        assertEquals("AllScalars1ToAllScalars1Converter", srcSpec.className);
        List<String> lines = srcSpec.lines;
        dumpLines(lines);
        //Note registry is null in these tests so no implicit conversions. They are tested in R500
        assertEquals(3, lines.size());
        assertEquals("Double tmp1 = src._double;", lines.get(0));
        assertEquals("if (tmp1 == null) tmp1 = 14.56d;", lines.get(1));
        assertEquals("dest._double = tmp1;", lines.get(2));
    }

//    //TODO
//    @Test
//    public void testCustomMethod() {
//        CodeGenerator codegen = new CodeGenerator();
//        codegen.getOptions().outputFieldCommentFlag = false;
//        CopySpec spec = buildSpecFromAST("roles -> roles custom", Customer.class, Customer.class);
//        JavaSrcSpec srcSpec = codegen.generate(spec);
//
//        assertEquals("CustomerToCustomerConverter", srcSpec.className);
//        List<String> lines = srcSpec.lines;
//        dumpLines(lines);
//        //Note registry is null in these tests so no implicit conversions. They are tested in R500
//        assertEquals(2, lines.size());
//        assertEquals("List<String> tmp1 = src.getRoles() == null ? null : ctx.createEmptyList(src.getRoles(), String.class);", lines.get(0));
//        assertEquals("dest.setRoles(tmp1);", lines.get(1));
//    }

//    @Test
//    public void test2() {
//        CodeGenerator codegen = new CodeGenerator();
//        CopySpec spec = buildSpec(2);
//        JavaSrcSpec srcSpec = codegen.generate(spec);
//
//        assertEquals("Src1ToDest1Converter", srcSpec.className);
//        List<String> lines = srcSpec.lines;
//        chkLines(lines, "int tmp1 = src.getN1();",
//                "dest.setN1(tmp1);",
//                "String tmp2 = src.getS2();",
//                "dest.setS2(tmp2);");
//    }
//

    //============
    private CopySpec buildSpecFromAST(String convLangSrc) {
        return buildSpecFromAST(convLangSrc, Src1.class, Dest1.class);
    }

    private CopySpec buildSpecFromAST(String convLangSrc, Class<?> srcClass, Class<?> destClass) {
        ConvLangParser parser = new ConvLangParser();
        List<Token> toks = parser.parseIntoTokens(convLangSrc);

        List<AST> list = parser.parseIntoAST(toks);
        ASTToSpecBuilder builder = new ASTToSpecBuilder();
        CopySpec spec = builder.buildSpec(srcClass, destClass);
        builder.addToSpec(spec, list, convLangSrc);
        return spec;
    }
}
