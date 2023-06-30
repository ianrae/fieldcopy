package org.dnal.fieldcopy.mlexer.ast;

public class ValueAST extends ConvertAST {
    //src is a value such as 'abc'
    public ValueAST(String srcText, String destText) {
        super(srcText, destText);
    }
}
