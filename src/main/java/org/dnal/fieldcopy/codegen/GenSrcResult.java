package org.dnal.fieldcopy.codegen;

import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.types.FieldTypeInformation;

public class GenSrcResult {
    public String varName;
    public SingleFld srcFld;
    public FieldTypeInformation ftiSrc;
    public  VarExpr varExpr;


    public GenSrcResult(String varName) {
        this.varName = varName;
    }
    public GenSrcResult(SingleFld srcFld, VarExpr varExp) {
        this.varName = varExp.varName;
        this.srcFld = srcFld;
        this.ftiSrc = srcFld.fieldTypeInfo;
        this.varExpr = varExp;
    }

}
