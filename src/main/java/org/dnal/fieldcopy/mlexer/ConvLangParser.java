package org.dnal.fieldcopy.mlexer;

import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.mlexer.ast.AST;
import org.dnal.fieldcopy.mlexer.ast.AutoAST;
import org.dnal.fieldcopy.mlexer.ast.ConvertAST;
import org.dnal.fieldcopy.mlexer.ast.ValueAST;
import org.dnal.fieldcopy.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConvLangParser {

//        private final DeliaLog log;

//        public MigrationParser(DeliaLog log) {
//            this.log = log;
//        }

    private static final String EXCLUDE = "exclude";

    public List<Token> parseIntoTokens(String migrationSrc) {
        Reader r = new Reader(migrationSrc);
        MLexer lexer = new MLexer(r);
        List<Token> tokens = new ArrayList<>();
        Token prev = null;
        while (true) {
            Token tok = lexer.getNextToken();
            if (tok == null) {
                return null;
            }
            if (tok.tokType == 99) {
                break;
            }
            tokens.add(tok);

            if (tok.tokType == MLexer.TOK_COLON) {
                if (prev != null && prev.tokType == MLexer.TOK_SYMBOL && prev.value.equals("ADDITIONS")) {
                    return tokens;
                }
            }
            prev = tok;
        }
        return tokens;
    }

    //TODO: add more error checking
    public List<AST> parseIntoAST(List<Token> tokens) {
        List<AST> list = new ArrayList<>();

        int state = 1; //1=before ->, 2=after

//        Token prev = null;
        String currentSrc = null;
        String currentDest = null;
        List<String> srcPieces = new ArrayList<>();
        List<String> destPieces = new ArrayList<>();
        String strDelim = null;

        for (Token tok : tokens) {
            switch (tok.tokType) {
                case MLexer.TOK_SYMBOL:
                    if (state == 1) {
                        if (EXCLUDE.equals(tok.value)) {
                            ExcludeParser excludeParser = new ExcludeParser();
                            return excludeParser.parseIntoAST(tokens);
                        }

                        if (currentSrc == null) {
                            currentSrc = tok.value;
                        } else {
                            srcPieces.add(tok.value);
                        }
                    } else {
                        if (currentDest == null) {
                            currentDest = tok.value;
                        } else {
                            destPieces.add(tok.value);
                        }
                    }
                    break;
                case MLexer.TOK_STRING_LITERAL:
                    if (state == 1) {
                        strDelim = tok.stringDelim;
                        srcPieces.add(tok.value);
                    } else {
                        destPieces.add(tok.value);
                    }
                    break;
                case MLexer.TOK_HYPHEN:
                case MLexer.TOK_INTEGER:
                case MLexer.TOK_LPAREN:
                case MLexer.TOK_RPAREN:
                case MLexer.TOK_PERIOD:
                    addPieces(tok, state, srcPieces, destPieces);
                    break;
//                    case MLexer.TOK_COLON:
//                        if (isStr(currentAction, "ALTERATIONS")) {
//                            state = 1;
//                        } else if (isStr(currentAction, "ADDITIONS")) {
//                            break;
//                        }
//                        break;
                case MLexer.TOK_COMMENT:
                case MLexer.TOK_END_LINE:
                    if (currentSrc != null) {
                        buildAction(list, currentSrc, srcPieces, currentDest, destPieces);
                    }
//                    prev = null;
                    currentSrc = null;
                    srcPieces = new ArrayList<>();
                    currentDest = null;
                    destPieces = new ArrayList<>();
                    break;
                case MLexer.TOK_ARROW:
                    state = 2;
                    break;
                default:
                    break;
            }

//            prev = tok;
        }

        //null is a special symbol that represents a value
        String target = "null";
        if (target.equals(currentSrc)) {
            srcPieces.add(target);
            currentSrc = null;
        }

        if (currentSrc != null) {
            buildAction(list, currentSrc, srcPieces, currentDest, destPieces);
        } else {
            List<String> tmpL = srcPieces.isEmpty() ? Collections.emptyList() : srcPieces.subList(1, srcPieces.size());
            buildValueAction(list, srcPieces.get(0), tmpL, currentDest, destPieces, strDelim);
        }

        return list;
    }

    private void addPieces(Token tok, int state, List<String> srcPieces, List<String> destPieces) {
        if (state == 1) {
            srcPieces.add(tok.value);
        } else {
            destPieces.add(tok.value);
        }
    }

    private void buildAction(List<AST> list, String currentSrc, List<String> srcPieces,
                             String currentDest, List<String> destPieces) {
        switch (currentSrc) {
            case "auto": {
                AutoAST ast = new AutoAST();
                list.add(ast);
            }
            break;
            default: {
                String destText = currentDest;
                ConvertAST ast = new ConvertAST(currentSrc, destText);
                if (destPieces.contains("required")) {
                    ast.isRequired = true;
                    destPieces.remove("required");
                }
                int pos = destPieces.indexOf("default");
                if (pos >= 0) {
                    ast.defaultVal = buildDefaultVal(destPieces, false);
                    destPieces = destPieces.subList(0, pos);
                }

                pos = destPieces.indexOf("using");
                if (pos >= 0) {
                    ast.usingConverter = buildDefaultVal(destPieces, true); //TODO fail is param is not string
                    destPieces = destPieces.subList(0, pos);
                }

                ast.srcPieces = srcPieces;
                ast.destPieces = destPieces;

                list.add(ast);
//                    throw new FieldCopyException("sdfsdf");
//                    DeliaExceptionHelper.throwError("bad.migration.source", "bad migration source!");
            }
            break;
        }
    }

    private String buildDefaultVal(List<String> destPieces, boolean doTrim) {
        failIfNot(destPieces, 1, "(");
        //failIfNot(destPieces, Arrays.asList(3,4), ")");
        boolean endParenSeen = false;

        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < destPieces.size(); i++) {
            String s3 = destPieces.get(i);
            if (doTrim) {
                s3 = s3.trim();
            }
            if (s3.equals(")")) {
                endParenSeen = true;
                break;
            }
            sb.append(s3);
        }
        String defaultVal = sb.toString();

        if (! endParenSeen) {
            failIfNot(destPieces, 1, ")");
        }
        return defaultVal;
    }

    private void buildValueAction(List<AST> list, String currentSrc, List<String> srcPieces, String currentDest, List<String> destPieces, String strDelim) {
        String destText = currentDest;

        //handle negative values
        if ("-".equals(currentSrc)) {
            List<String> tmp = new ArrayList<>();
            boolean isFirst = true;
            for (String s : srcPieces) {
                if (isFirst) {
                    currentSrc = "-" + s;
                } else {
                    tmp.add(s);
                }
            }
            srcPieces = tmp;
        }

        if (strDelim != null) {
            //convert ' delim to "
            if (strDelim.equals("'")) {
                strDelim = "\"";
            }
            currentSrc = strDelim + currentSrc + strDelim;
        }

        ValueAST ast = new ValueAST(currentSrc, destText);
        if (destPieces.contains("required")) {
            ast.isRequired = true;
            destPieces.remove("required");
        }
        int pos = destPieces.indexOf("default");
        if (pos >= 0) {
            ast.defaultVal = buildDefaultVal(destPieces, false);
            destPieces = destPieces.subList(0, pos);
        }

        ast.srcPieces = srcPieces;
        ast.destPieces = destPieces;

        list.add(ast);
    }


    private void failIfNot(List<String> pieces, int i, String expected) {
        String s = pieces.get(i);
        if (!expected.equals(s)) {
            String msg = String.format("missing %s in: %s", expected, StringUtil.flattenNoComma(pieces));
            throw new FieldCopyException(msg);
        }
    }

}
