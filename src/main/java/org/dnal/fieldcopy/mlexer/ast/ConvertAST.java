package org.dnal.fieldcopy.mlexer.ast;

import java.util.List;

public class ConvertAST implements AST {
    public String srcText;
    public String destText;
    public List<String> srcPieces; //remaining segments
    public List<String> destPieces;
    public boolean isRequired;
    public boolean skipNull;
    public String defaultVal;
    public String usingConverter;

    public ConvertAST(String srcText, String destText) {
        this.srcText = srcText;
        this.destText = destText;
    }

//    public String getIthSrcPiece(int i) {
//        if (i == 0) {
//            return srcText;
//        } else {
//            return srcPieces.get(i - 1); //1 means index 0
//    public String getIthDestPiece(int i) {
//        }
//    }
//        if (i == 0) {
//            return destText;
//        } else {
//            return destPieces.get(i - 1); //1 means index 0
//        }
//    }
}
