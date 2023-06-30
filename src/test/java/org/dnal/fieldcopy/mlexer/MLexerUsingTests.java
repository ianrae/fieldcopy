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
public class MLexerUsingTests extends ICRTestBase {

    @Test
    public void testUsing() {
        chkParse("x -> y using('abc')", 2, 25, 2, 2, 23, 11, 24);
    }

    @Test
    public void testAST() {
        List<Token> toks = chkParse("x -> y using('abc')", 2, 25, 2, 2, 23, 11, 24);
        ConvLangParser parser = new ConvLangParser();
        List<AST> list = parser.parseIntoAST(toks);

        assertEquals(1, list.size());
        chkConvert(list, 0, "x", "y", "abc");
    }

    @Test
    public void testDebug() {
        chkParse("x -> y default(-45.56)", 2, 25, 2, 2, 23, 28, 3, 26, 3, 24);
    }

    //------------
    private List<Token> chkParse(String src, Integer... tokTypes) {
        ConvLangParser parser = new ConvLangParser();
        List<Token> toks = parser.parseIntoTokens(src);
        int i = 0;
        for (Integer tokType : tokTypes) {
            Token tok = toks.get(i++);
//            log(tok.toString());
            assertEquals(tokType, tok.tokType);
        }
        assertEquals(tokTypes.length, toks.size());
        return toks;
    }

    private void chkConvert(List<AST> list, int i, String expectedSrc, String expectedDest, String usingVal) {
        ConvertAST ast = (ConvertAST) list.get(i);
        assertEquals(expectedSrc, ast.srcText);
        assertEquals(expectedDest, ast.destText);
        assertEquals(usingVal, ast.usingConverter);
    }
}
