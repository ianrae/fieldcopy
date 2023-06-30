package org.dnal.fieldcopy.mlexer;

import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.mlexer.ast.AST;
import org.dnal.fieldcopy.mlexer.ast.ExcludeAST;
import org.dnal.fieldcopy.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExcludeParser {

//        private final DeliaLog log;

//        public MigrationParser(DeliaLog log) {
//            this.log = log;
//        }

    private static final String EXCLUDE = "exclude";


    //TODO: add more error checking
    public List<AST> parseIntoAST(List<Token> tokens) {
        int state = 0;

        ExcludeAST currentAction = null;
        List<String> pieces = new ArrayList<>();
        for (Token tok : tokens) {
            switch (tok.tokType) {
                case MLexer.TOK_SYMBOL:
                    if (state == 0) {
                        if (EXCLUDE.equals(tok.value)) {
                            state = 1;
                        }
                    } else {
                        pieces.add(tok.value);
                    }
                    break;
                case MLexer.TOK_INTEGER:
                case MLexer.TOK_STRING_LITERAL:
                    break;
                case MLexer.TOK_COMMENT:
                case MLexer.TOK_END_LINE:
                    if (currentAction != null) {
                        currentAction = buildAction(pieces);
                        return Collections.singletonList(currentAction);
                    }
                    currentAction = null;
                    pieces = new ArrayList<>();
                    break;

                case MLexer.TOK_LPAREN:
                case MLexer.TOK_RPAREN:
                default:
                    break;
            }

        }

        return Collections.singletonList(buildAction(pieces));
    }

    private ExcludeAST buildAction(List<String> pieces) {
        ExcludeAST excludeAST = new ExcludeAST();
        excludeAST.fieldsToExclude = pieces;
        return excludeAST;
    }

    private void failIfNot(List<String> pieces, int i, String expected) {
        String s = pieces.get(i);
        if (!expected.equals(s)) {
            String msg = String.format("missing %s in: %s", expected, StringUtil.flattenNoComma(pieces));
            throw new FieldCopyException(msg);
        }
    }

}
