package org.dnal.fieldcopy.newcodegen.javacreator;

import org.dnal.fieldcopy.newcodegen.CodeVar;

import java.util.Optional;

public class JavaVar {
    public String varName;
    public String varType;
    public Optional<JavaField> jfield;

    public JavaVar(String varName, String varType, Optional<JavaField> jfield) {
        this.varName = varName;
        this.varType = varType;
        this.jfield = jfield;
    }

    public JavaVar(CodeVar codeVar) {
        this.varName = codeVar.varName;
        this.varType = codeVar.varType;
        //TODO     public boolean needToAddClosingBrace;
        JavaField jfield = new JavaField(codeVar.fld.fieldName, codeVar.fld);
        this.jfield = Optional.of(jfield);
    }
}
