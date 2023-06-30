package org.dnal.fieldcopy.newcodegen;

import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.util.ClassNameUtil;

public class ValueConverter {
    private FieldTypeInformation stringFieldTypeInfo;

    public ValueConverter(FieldTypeInformation stringFieldTypeInfo) {
        this.stringFieldTypeInfo = stringFieldTypeInfo;
    }

    public SingleFld convertValueFld(SingleFld srcFld) {
        if (isStringValue(srcFld.fieldName)) {
            SingleFld fld = new SingleFld(srcFld.fieldName);
            fld.fieldType = stringFieldTypeInfo.getFieldType();
            fld.fieldTypeInfo = stringFieldTypeInfo;
            return fld;
        }
        return srcFld;
    }

    public String buildValueType(SingleFld srcFld, String varType) {
        if (isCharType(srcFld)) {
            return varType;
        }
        if (srcFld.fieldType.isEnum()) {
//            String enumClass = srcFld.fieldType.getSimpleName();
//            String val = srcFld.fieldName.replace("\"", "");
//            String enumStr = String.format("%s.%s", enumClass, val);
//            srcFld.fieldName = enumStr;
//            //TODO is this safe to change src.fieldname ??
            return varType;
        }

        if (isStringValue(srcFld.fieldName)) {
            Class<?> clazz = stringFieldTypeInfo.getFieldType();
            String newVarType = ClassNameUtil.renderClassName(clazz.getName());
            if (srcFld.fieldTypeInfo.isOptional()) {
                return String.format("Optional<%s>", newVarType);
            }
            return newVarType;
        }
        return varType;
    }

    private boolean isCharType(SingleFld srcFld) {
        if (char.class.isAssignableFrom(srcFld.fieldTypeInfo.getFieldType())) { //TODO does this work with optional?
            return true;
        }
        if (Character.class.isAssignableFrom(srcFld.fieldTypeInfo.getFieldType())) { //TODO does this work with optional?
            return true;
        }
        return false;
    }

    private boolean isStringValue(String fieldName) {
        if (fieldName.startsWith("'")) {
            return true;
        }
        if (fieldName.startsWith("\"")) {
            return true;
        }
        return false;
    }

    public String buildOptionalOf(SingleFld fld, String getterStr) {
        if (fld.fieldTypeInfo.isOptional()) {
            String s = String.format("Optional.of(%s)", getterStr);
            return s;
        }
        return getterStr;
    }

    public void ensureSingleQuoteDelimNotUsedUnlessChar(SingleFld fld) {
        if (isCharType(fld)) {
            return; //we allow 'x' -> mychar
        }
        //ensure that a string value uses " for delim, not '
        if (fld.fieldName.startsWith("'")) {
            String msg = String.format("String value must use \" delimiter, such as \"dog\". Single quote delimiter ' is not allowed.");
            throw new FieldCopyException(msg);
        }
    }
}
