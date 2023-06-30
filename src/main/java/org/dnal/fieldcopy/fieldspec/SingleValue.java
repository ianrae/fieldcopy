package org.dnal.fieldcopy.fieldspec;

import org.dnal.fieldcopy.types.FieldTypeInformation;

public class SingleValue extends SingleFld {
    //fieldName is a value such as "44"

    public SingleValue(String fieldName) {
        super(fieldName);
    }

    public SingleValue(String fieldName, FieldTypeInformation fieldTypeInfo) {
        super(fieldName, fieldTypeInfo);
    }

    public SingleValue(SingleValue fld) {
        super(fld);
    }
}
