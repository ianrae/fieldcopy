package org.dnal.fieldcopy.fieldspec;

import org.dnal.fieldcopy.mlexer.DottedFieldBuilder;

public class NormalFieldSpec implements FieldSpec {
    public Class<?> srcClass;
    public Class<?> destClass;
    public String srcText;
    public String destText;
    public boolean isRequired;
    public String defaultVal;
    public String elementDefaultVal; //for list
//    public boolean isSegment; //is part of MultiFieldSpec and not the first field
    public DottedFieldBuilder dfBuilder; //only if multi
    public boolean srcTextIsValue; //for 34 -> points
    public String usingConverterName; //if not null, then use this converter

    //set during codegen
    public FldChain srcFldX;
    public FldChain destFldX;
    public boolean isCustom;
    public String customReturnType; //set during gen
    public String customMethodName; //set during gen
    public String convLangSrc;

    public NormalFieldSpec(Class<?> srcClass, Class<?> destClass, String srcText, String destText) {
        this.srcClass = srcClass;
        this.destClass = destClass;
        this.srcText = srcText;
        this.destText = destText;
    }

    //ignore custom for value->field
    public boolean isCustomField(SingleFld srcFld) {
        if (isCustom && !(srcFld instanceof SingleValue)) {
            return true;
        }
        return false;
    }
}
