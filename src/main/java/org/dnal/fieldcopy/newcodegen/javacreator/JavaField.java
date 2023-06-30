package org.dnal.fieldcopy.newcodegen.javacreator;

import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.types.FieldTypeInformation;

public class JavaField {
    private final boolean isField;
    public String fieldName;
    public SingleFld fld;
    public FieldTypeInformation fti;
    public boolean useIsGetter;

    public JavaField(String fieldName, SingleFld fld) {
        this.fieldName = fieldName;
        this.fti = fld.fieldTypeInfo;
        this.isField = fld.isField;
        this.fld = fld;
    }

    public boolean isField() {
        return isField;
    }
}
