package org.dnal.fieldcopy.codegen;

public class GenResult {
    public String varName;
    public String varType;
    public boolean needToAddClosingBrace;
    public boolean suppressOptional; //we've already handled optional-ness of src

    public GenResult(String varName) {
        this.varName = varName;
    }
    public GenResult(VarExpr varExpr) {
        this.varName = varExpr.varName;
        this.varType = varExpr.varType;
    }
}
