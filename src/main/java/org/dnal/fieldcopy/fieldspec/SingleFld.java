package org.dnal.fieldcopy.fieldspec;

import org.dnal.fieldcopy.types.FieldTypeInformation;

import java.util.Optional;

public class SingleFld {
    public String fieldName;
    public Class<?> fieldType;
    public FieldTypeInformation fieldTypeInfo;

    //need methods isPrimitive, isScalar
    public Optional<Object> key; //for list or map
    public Object defaultValue;
    public boolean isField; //if false then use getter
    public boolean useIsGetter; //true if name of getter starts with "is"
    //NOTE if we add a field here, update the copy constructor!!!

    public SingleFld(String fieldName) {
        this.fieldName = fieldName;
    }

    public SingleFld(String fieldName, FieldTypeInformation fieldTypeInfo) {
        this.fieldName = fieldName;
        this.fieldTypeInfo = fieldTypeInfo;
        this.fieldType = fieldTypeInfo.getFieldType();
    }

    public SingleFld(SingleFld fld) {
        this.fieldName = fld.fieldName;
        this.fieldType = fld.fieldType;
        this.fieldTypeInfo = fld.fieldTypeInfo;

        this.key = fld.key;
        this.defaultValue = fld.defaultValue;
        this.isField = fld.isField;
    }
}
