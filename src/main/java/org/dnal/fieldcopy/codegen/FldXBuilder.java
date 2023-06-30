package org.dnal.fieldcopy.codegen;

import org.apache.commons.lang3.StringUtils;
import org.dnal.fieldcopy.error.NotImplementedException;
import org.dnal.fieldcopy.fieldspec.FldChain;
import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.fieldspec.SingleValue;
import org.dnal.fieldcopy.mlexer.DottedFieldBuilder;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.types.ClassTypeHelper;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.util.ReflectionUtil;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import static java.util.Objects.isNull;

public class FldXBuilder {
    private final FieldCopyOptions options; //not currently needed, but leave in for later
    private ReflectionUtil refHelper = new ReflectionUtil();

    public FldXBuilder(FieldCopyOptions options) {
        this.options = options;
    }

    public FldChain newBuildFldX(Class<?> srcClass, String srcText, DottedFieldBuilder dfBuilder, boolean doSrcSide) {
        if (isNull(dfBuilder)) {
            return buildFldX(srcClass, srcText);
        } else {
            FldChain fldX = new FldChain();
            Class<?> fieldClass = srcClass;
            List<String> fieldList;
            if (doSrcSide) {
                fieldList = dfBuilder.getSrcFieldList();
            } else {
                fieldList = dfBuilder.getDestFieldList();
            }

            for (String fieldName : fieldList) {
                if (isNull(fieldName)) {
                    continue;
                }
                SingleFld fld = new SingleFld(fieldName);
                setFieldType(fld, fieldClass, fieldName);
                fldX.flds.add(fld);

                fieldClass = fld.fieldType;
            }

            return fldX;
        }
    }

    public FldChain buildFldX(Class<?> srcClass, String srcText) {
        FldChain fldX = new FldChain();

        SingleFld fld = new SingleFld(srcText);
        setFieldType(fld, srcClass, srcText);
        fldX.flds.add(fld);
        return fldX;
    }

    public void setFieldType(SingleFld fld, Class<?> srcClass, String srcText) {
        fld.fieldTypeInfo = refHelper.getFieldInformation(srcClass, srcText);
        fld.fieldType = calcFieldType(srcClass, srcText, fld.fieldTypeInfo);
        fld.isField = !refHelper.isGetterMethod(srcClass, srcText); //use getter if available, else use field
        fld.useIsGetter = refHelper.getterStartsWithIs(srcClass, srcText);
    }

    private Class<?> calcFieldType(Class<?> srcClass, String fieldName, FieldTypeInformation info) {
        if (info.isList()) {
            return info.getFieldType();
        } else if (info.isMap()) {
            return info.getFieldType();
        } else if (info.isOptional()) {
            if (info.getTypeTree().getFirstActual() instanceof ParameterizedType) {
                throw new NotImplementedException("Optional of generic value (such as Optional<List<String>> not yet supported");
            }
            return info.getFirstActual();
        } else {
            return info.getFieldType();
        }
    }

    public FldChain buildValueFld(Class<?> srcClass, String srcText, DottedFieldBuilder dfBuilder, FldChain destFldX) {
        FldChain fldX = new FldChain();

        SingleValue fld = new SingleValue(srcText);
        SingleFld destFld = destFldX.getLast();
        //assume value is same as dest
        //TODO later support '35' > intVal
        fld.fieldTypeInfo = destFld.fieldTypeInfo.createNonOptional(); //values are not optional
        fld.fieldType = destFld.fieldType;
        fld.isField = true; //destFld.isField;

        if (isNullValue(fld)) {
            //do nothing.
        } else if (isString(fld) || isDate(fld)) {
            if (fld.fieldName.startsWith("'")) {
                //change ' to "
                String s = fld.fieldName.substring(1);
                s = StringUtils.substringBeforeLast(s, "'");
                fld.fieldName = String.format("\"%s\"", s);
            }
        } else if (isEnum(fld)) {
            String className = fld.fieldType.getSimpleName(); //TODO does this work with Optional<Enum>
            String tmp = removeStrDelim(fld.fieldName);
            String s = String.format("%s.%s", className, tmp);
            fld.fieldName = s;
        }

        fldX.flds.add(fld);
        return fldX;
    }

    private boolean isNullValue(SingleValue fld) {
        String target = "null";
        return target.equals(fld.fieldName);
    }

    private String removeStrDelim(String fieldName) {
        int n = fieldName.length();
        if (fieldName.startsWith("'")) {
            return fieldName.substring(1, n - 1);
        } else if (fieldName.startsWith("\"")) {
            return fieldName.substring(1, n - 1);
        }
        return fieldName;
    }

    private boolean isString(SingleValue fld) {
        if (String.class.isAssignableFrom(fld.fieldTypeInfo.getFieldType())) {
            return true;
        }
        return false;
    }

    private boolean isDate(SingleValue fld) {
        Class<?> clazz = fld.fieldTypeInfo.getFieldType();
        return ClassTypeHelper.isDateType(clazz);
    }

    private boolean isEnum(SingleValue fld) {
        return fld.fieldTypeInfo.getFieldType().isEnum(); //TODO does this work with Optional<Enum>
    }

}

