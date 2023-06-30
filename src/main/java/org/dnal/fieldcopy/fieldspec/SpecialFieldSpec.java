package org.dnal.fieldcopy.fieldspec;

public class SpecialFieldSpec implements FieldSpec {
    public Class<?> srcClass;
    public Class<?> destClass;
    public String srcText;
    public String destText;

    //set during codegen
    public FldChain srcFldX;
    public FldChain destFldX;
}
