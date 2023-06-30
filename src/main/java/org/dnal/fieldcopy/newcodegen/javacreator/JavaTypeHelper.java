package org.dnal.fieldcopy.newcodegen.javacreator;

import org.apache.commons.lang3.StringUtils;
import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.codegen.JavaSrcSpec;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;
import org.dnal.fieldcopy.types.TypeTree;

import java.lang.reflect.Type;

public class JavaTypeHelper {
    private JavaSrcSpec srcSpec;

    public JavaTypeHelper(JavaSrcSpec srcSpec) {
        this.srcSpec = srcSpec;
    }

    protected String buildVarType(Class<?> fieldType, FieldTypeInformation fieldTypeInfo) {
        String s = fieldType.getName();

        FieldTypeInformation shim = new FieldTypeInformationImpl(null, null, new TypeTree());

        if (fieldTypeInfo.isList()) {
            String elType = buildVarType(fieldTypeInfo.getFirstActual(), shim); //*** recursion ***
            String collType = buildVarType(fieldTypeInfo.getFieldType(), shim); //*** recursion ***
            return String.format("%s<%s>", collType, elType); //TODO we shouldn't hard-code List. later support Set,etc
        }
        if (fieldTypeInfo.isMap()) {
            String keyType = buildVarType(fieldTypeInfo.getFirstActual(), shim); //*** recursion ***
            Type mapValueType = fieldTypeInfo.getTypeTree().getMapValueType(0);
            String valueType = buildVarType((Class<?>) mapValueType, shim);
            srcSpec.addImportIfNotAlreadyPresent(fieldTypeInfo.getFieldType().getName());
            return String.format("Map<%s,%s>", keyType, valueType); //TODO we shouldn't hard-code Map. later support others...
        }
        if (fieldTypeInfo.isOptional()) {
            String elType = buildVarType(fieldTypeInfo.getFirstActual(), shim); //*** recursion ***
            //don't add import java.util.Optional we do that by default in codegen anyway
            //srcSpec.addImportIfNotAlreadyPresent(fieldTypeInfo.getFieldType().getName());
            return String.format("Optional<%s>", elType);
        }

        String target = "java.lang.";
        if (s.startsWith(target)) {
            return StringUtils.substringAfter(s, target);
        }

        //array
        target = "[L";
        if (s.startsWith(target)) {
            String elType = StringUtils.substringAfter(s, target);
            elType = StringUtils.substringBefore(elType, ";");
            try {
                Class<?> arrayClass = Class.forName(elType);
                String varType = buildVarType(arrayClass, shim); //*** recursion ***
                return String.format("%s[]", varType);
            } catch (ClassNotFoundException e) {
                throw new FieldCopyException(e.getMessage());
            }
        }

        if (s.contains(".")) {
//            String simple = fieldType.getSimpleName();
//            String ss = StringUtils.substringBefore(s, "." + simple);
            srcSpec.addImportIfNotAlreadyPresent(fieldType.getName());
        }

        return fieldType.getSimpleName();
    }

}
