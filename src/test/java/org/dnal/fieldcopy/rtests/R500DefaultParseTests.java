package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.codegen.CodeGenerator;
import org.dnal.fieldcopy.dataclass.AllScalars1;
import org.dnal.fieldcopy.dataclass.Child1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.mlexer.ASTToSpecBuilder;
import org.dnal.fieldcopy.mlexer.ConvLangParser;
import org.dnal.fieldcopy.mlexer.Token;
import org.dnal.fieldcopy.mlexer.ast.AST;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * test parsing of default val
 */
public class R500DefaultParseTests extends RTestBase {

    @Test
    public void testBoolean() {
        genAndCheckBool("_boolean -> _boolean default(false)", "false");
        genAndCheckBool("_boolean -> _boolean default(true)", "true");
    }

    @Test
    public void testInteger() {
        genAndCheckInt("_int -> _int default(-2)", "-2");
        genAndCheckInt("_int -> _int default(0)", "0");
        genAndCheckInt("_int -> _int default(34)", "34");
        genAndCheckInt("_int -> _int default(2147483647)", "2147483647");
        genAndCheckInt("_int -> _int default(-2147483648)", "-2147483648");
    }

    @Test
    public void testByte() {
        genAndCheckByte("_byte -> _byte default(-2)", "-2");
        Byte bb = 6;
        Short ss = 34;
        Integer x = 55;
        Long y = 34L;
    }

    @Test
    public void testShort() {
        genAndCheckShort("_short -> _short default(22)", "22");
    }

    @Test
    public void testLong() {
        genAndCheckLong("_long -> _long default(2147483648)", "2147483648L");
    }

    @Test
    public void testFloat() {
        genAndCheckFloat("_float -> _float default(214.78)", "214.78f");
        Float ff = 324.4f;
        Double dd = 456.43d;
    }

    @Test
    public void testDouble() {
        genAndCheckDouble("_double -> _double default(214.78)", "214.78d");
    }

    @Test
    public void testChar() {
        genAndCheckChar("_char -> _char default('k')", "'k'");
        Character ch = 'k';
    }

    @Test
    public void testString() {
        genAndCheckString("_string -> _string default('abc')", "\"abc\"");
        genAndCheckString("_string -> _string default('')", "\"\"");
        genAndCheckString("_string -> _string default(' ')", "\" \"");
        genAndCheckString("_string -> _string default('ab)c')", "\"ab)c\"");
        genAndCheckString("_string -> _string default('ab(c')", "\"ab(c\"");
    }

    @Test
    public void testLocalDate() {
        genAndCheckLocalDate("moveDate -> moveDate default('2022-09-22')", "\"abc\"");
    }


    /*
    public class AllScalars1 {
    public Character _char;

    //and
    public String _string;
}

     */

    //----------
    private CopySpec buildSpecFromAST(String convLangSrc, Class<?> srcClass, Class<?> destClass) {
        ConvLangParser parser = new ConvLangParser();
        List<Token> toks = parser.parseIntoTokens(convLangSrc);

        List<AST> list = parser.parseIntoAST(toks);
        ASTToSpecBuilder builder = new ASTToSpecBuilder();
        CopySpec spec = builder.buildSpec(srcClass, destClass);
        builder.addToSpec(spec, list, convLangSrc);
        return spec;
    }

    private List<String> buildAndGenDefault(String text, Class<?> srcClass, Class<?> destClas) {
        CodeGenerator codegen = new CodeGenerator();
        codegen.getOptions().outputFieldCommentFlag = false;
        CopySpec spec = buildSpecFromAST(text, srcClass, destClas);
        List<String> lines = doGen(spec);
        return lines;
    }

    private void genAndCheckInt(String srcText, String expected) {
        String line = String.format("if (tmp1 == null) tmp1 = %s;", expected);
        String[] ar = {
                "Integer tmp1 = src._int;",
                line,
                "dest._int = tmp1;"};
        doBuildGenAndCheck(srcText, ar);
    }

    private void genAndCheckBool(String srcText, String expected) {
        String line = String.format("if (tmp1 == null) tmp1 = %s;", expected);
        String[] ar = {
                "Boolean tmp1 = src._boolean;",
                line,
                "dest._boolean = tmp1;"};
        doBuildGenAndCheck(srcText, ar);
    }

    private void genAndCheckByte(String srcText, String expected) {
        String line = String.format("if (tmp1 == null) tmp1 = %s;", expected);
        String[] ar = {
                "Byte tmp1 = src._byte;",
                line,
                "dest._byte = tmp1;"};
        doBuildGenAndCheck(srcText, ar);
    }

    private void genAndCheckShort(String srcText, String expected) {
        String line = String.format("if (tmp1 == null) tmp1 = %s;", expected);
        String[] ar = {
                "Short tmp1 = src._short;",
                line,
                "dest._short = tmp1;"};
        doBuildGenAndCheck(srcText, ar);
    }

    private void genAndCheckLong(String srcText, String expected) {
        String line = String.format("if (tmp1 == null) tmp1 = %s;", expected);
        String[] ar = {
                "Long tmp1 = src._long;",
                line,
                "dest._long = tmp1;"};
        doBuildGenAndCheck(srcText, ar);
    }

    private void genAndCheckFloat(String srcText, String expected) {
        String line = String.format("if (tmp1 == null) tmp1 = %s;", expected);
        String[] ar = {
                "Float tmp1 = src._float;",
                line,
                "dest._float = tmp1;"};
        doBuildGenAndCheck(srcText, ar);
    }

    private void genAndCheckDouble(String srcText, String expected) {
        String line = String.format("if (tmp1 == null) tmp1 = %s;", expected);
        String[] ar = {
                "Double tmp1 = src._double;",
                line,
                "dest._double = tmp1;"};
        doBuildGenAndCheck(srcText, ar);
    }

    private void genAndCheckChar(String srcText, String expected) {
        String line = String.format("if (tmp1 == null) tmp1 = %s;", expected);
        String[] ar = {
                "Character tmp1 = src._char;",
                line,
                "dest._char = tmp1;"};
        doBuildGenAndCheck(srcText, ar);
    }

    private void genAndCheckString(String srcText, String expected) {
        String line = String.format("if (tmp1 == null) tmp1 = %s;", expected);
        String[] ar = {
                "String tmp1 = src._string;",
                line,
                "dest._string = tmp1;"};
        doBuildGenAndCheck(srcText, ar);
    }
    private void genAndCheckLocalDate(String srcText, String expected) {
        String line = String.format("if (tmp1 == null) tmp1 = ctx.toLocalDate(\"2022-09-22\");", expected);
        String[] ar = {
                "LocalDate tmp1 = src.getMoveDate();",
                line,
                "dest.setMoveDate(tmp1);"};
        List<String> lines = buildAndGenDefault(srcText, Child1.class, Child1.class);
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.LocalDate");
    }

    private void doBuildGenAndCheck(String srcText, String[] ar) {
        List<String> lines = buildAndGenDefault(srcText, AllScalars1.class, AllScalars1.class);
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }
}
