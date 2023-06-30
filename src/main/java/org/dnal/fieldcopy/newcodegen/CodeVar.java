package org.dnal.fieldcopy.newcodegen;

import org.dnal.fieldcopy.codegen.VarExpr;
import org.dnal.fieldcopy.fieldspec.SingleFld;

public class CodeVar extends VarExpr {
    public SingleFld fld;
    public boolean needToAddClosingBrace;

    public CodeVar(String varName, String varType, SingleFld fld) {
        super(varName);
        this.varType = varType;
        this.fld = fld;
    }

    @Override
    public String toString() {
        String s = String.format("%s", varName);
        return s;
    }
}
