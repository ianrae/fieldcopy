package org.dnal.fieldcopy.mlexer;

import org.dnal.fieldcopy.mlexer.ast.AST;
import org.dnal.fieldcopy.mlexer.ast.ConvertAST;
import org.dnal.fieldcopy.mlexer.ast.ValueAST;
import org.dnal.fieldcopy.registry.ICRTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class MLexerTests extends ICRTestBase {

    @Test
    public void testLexer() {
        chkParseEmpty("", 0);
        chkParseEmpty(" ", 0);
        chkParseEmpty("  \t", 0);

        chkParse("x -> y", 2, 25, 2);
        chkParse("x -> y required", 2, 25, 2, 2);
        chkParse("x -> y default(7)", 2, 25, 2, 2, 23, 3, 24);
        chkParse("x -> y default('abc')", 2, 25, 2, 2, 23, 11, 24);
    }

    @Test
    public void testAST() {
        List<Token> toks = chkParse("x -> y", 2, 25, 2);
        ConvLangParser parser = new ConvLangParser();
        List<AST> list = parser.parseIntoAST(toks);

        assertEquals(1, list.size());
        chkConvert(list, 0, "x", "y");
    }

    @Test
    public void testSubObj1() {
        List<Token> toks = chkParse("x.z -> y", 2, 26, 2, 25, 2);
        ConvLangParser parser = new ConvLangParser();
        List<AST> list = parser.parseIntoAST(toks);

        assertEquals(1, list.size());
        chkConvert(list, 0, "x", "y");
    }
    @Test
    public void testIntValue() {
        List<Token> toks = chkParse("35 -> y", 3, 25, 2);
        ConvLangParser parser = new ConvLangParser();
        List<AST> list = parser.parseIntoAST(toks);

        assertEquals(1, list.size());
        chkConvertValue(list, 0, "35", "y");
    }
    @Test
    public void testNegativeIntValue() {
        List<Token> toks = chkParse("-35 -> y", 28, 3, 25, 2);
        ConvLangParser parser = new ConvLangParser();
        List<AST> list = parser.parseIntoAST(toks);

        assertEquals(1, list.size());
        chkConvertValue(list, 0, "-35", "y");
    }
    @Test
    public void testStringValue() {
        List<Token> toks = chkParse("'def' -> y", 11, 25, 2);
        ConvLangParser parser = new ConvLangParser();
        List<AST> list = parser.parseIntoAST(toks);
        //Note. parser converters ' delim to " for string values

        assertEquals(1, list.size());
        chkConvertValue(list, 0, "\"def\"", "y");
    }
    @Test
    public void testStringValue2() {
        List<Token> toks = chkParse("\"d'ef\" -> y", 11, 25, 2);
        ConvLangParser parser = new ConvLangParser();
        List<AST> list = parser.parseIntoAST(toks);

        assertEquals(1, list.size());
        chkConvertValue(list, 0, "\"d'ef\"", "y");
    }
    @Test
    public void testNullValue() {
        List<Token> toks = chkParse("null -> y", 2, 25, 2);
        ConvLangParser parser = new ConvLangParser();
        List<AST> list = parser.parseIntoAST(toks);

        assertEquals(1, list.size());
        chkConvertValue(list, 0, "null", "y");
    }
    @Test
    public void testDefault() {
        chkParse("x -> y default('abc')", 2, 25, 2, 2, 23, 11, 24);
        chkParse("x -> y default(-45)", 2, 25, 2, 2, 23, 28, 3, 24);
        chkParse("x -> y default(-45.56)", 2, 25, 2, 2, 23, 28, 3, 26, 3, 24);
    }

    @Test
    public void testSkipNull() {
        chkParse("x -> y skipNull", 2, 25, 2, 2);
    }


    @Test
    public void testDebug() {
//        chkParse("x -> y", 2, 25, 2);
//        chkParse("x -> y required", 2, 25, 2, 2);
        chkParse("x -> y default(-45.56)", 2, 25, 2, 2, 23, 28, 3, 26, 3, 24);
    }

    //------------
    private void chkParseEmpty(String src, int size) {
        ConvLangParser parser = new ConvLangParser();
        List<Token> toks = parser.parseIntoTokens(src);
        assertEquals(size, toks.size());
    }
    private List<Token> chkParse(String src, Integer... tokTypes) {
        ConvLangParser parser = new ConvLangParser();
        List<Token> toks = parser.parseIntoTokens(src);
        int i = 0;
        for(Integer tokType : tokTypes) {
            Token tok = toks.get(i++);
//            log(tok.toString());
            if (tokType.intValue() != tok.tokType) {
                dumpTokens(toks);
            }
            assertEquals(tokType, tok.tokType);
        }
        assertEquals(tokTypes.length, toks.size());
        return toks;
    }

    private void dumpTokens(List<Token> toks) {
        for(Token tok: toks) {
            this.log(String.format("%d", tok.tokType));
        }
    }

    private void chkConvert(List<AST> list, int i, String expectedSrc, String expectedDest) {
        ConvertAST ast = (ConvertAST) list.get(i);
        assertEquals(expectedSrc, ast.srcText);
        assertEquals(expectedDest, ast.destText);
    }

    private void chkConvertValue(List<AST> list, int i, String expectedSrc, String expectedDest) {
        ValueAST ast = (ValueAST) list.get(i);
        assertEquals(expectedSrc, ast.srcText);
        assertEquals(expectedDest, ast.destText);
    }



}
