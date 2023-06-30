package org.dnal.fieldcopy.codegen;

public class VarExpr {
    public String varName;
    public String varType;

    public VarExpr(ConversionContext ctx) {
        this.varName = ctx.varNameGenerator.nextVarName();
    }
    public VarExpr(ConversionContext ctx, String prefix) {
        this.varName = ctx.varNameGenerator.nextVarName(prefix);
    }
    public VarExpr(String varName) {
        this.varName = varName;
    }

    public String render() {
        String s = String.format("%s %s", varType, varName);
        return s;
    }
}

