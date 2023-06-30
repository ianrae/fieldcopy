package org.dnal.fieldcopy.codegen;

import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.implicitconverter.ImplicitConvRegistry;
import org.dnal.fieldcopy.newcodegen.javacreator.JavaCreatorImpl;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.registry.ConverterRegistry;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.util.ReflectionUtil;

public class ExtractGenBase {
    protected ReflectionUtil refHelper = new ReflectionUtil();
    protected ConverterRegistry registry;
    protected ImplicitConvRegistry implicitConvRegistry;
    protected VarNameGenerator varNameGenerator;
    protected FieldCopyOptions options;
    protected JavaCreatorImpl javaCreator;


    public ExtractGenBase(ImplicitConvRegistry implicitConvRegistry, ConverterRegistry registry, VarNameGenerator varNameGenerator,
                          FieldCopyOptions options) {
        this.implicitConvRegistry = implicitConvRegistry;
        this.registry = registry;
        this.varNameGenerator = varNameGenerator;
        this.options = options;
    }

//    protected String buildGetterOrField(String fieldName, SingleFld srcFld) {
//        if (srcFld.isField) {
//            return fieldName;
//        } else if (srcFld.useIsGetter) {
//            return String.format("is%s()", StringUtil.uppify(fieldName));
//        } else {
//            return buildGetter(fieldName);
//        }
//    }
//
//    protected String buildGetter(String fieldName) {
//        return String.format("get%s()", StringUtil.uppify(fieldName));
//    }

    protected String buildVarTypeWithoutOptional(Class<?> fieldType, JavaSrcSpec srcSpec, FieldTypeInformation fieldTypeInfo) {
        FieldTypeInformation fti = fieldTypeInfo.createNonOptional();
        return buildVarType(fieldType, srcSpec, fti);
    }

    protected String buildVarType(Class<?> fieldType, JavaSrcSpec srcSpec, FieldTypeInformation fieldTypeInfo) {
        return javaCreator.buildVarType(fieldType, fieldTypeInfo);
    }

    protected FieldTypeInformation getFieldTypeInfo(Class<?> clazz, String fieldName) {
        return refHelper.getFieldInformation(clazz, fieldName);
    }

//    protected GetFragment buildGetFragment(String varNameParam, SingleFld fld) {
//        String varName = (fld instanceof SingleValue) ? "" : varNameParam;
//        GetFragment getFrag = new GetFragment(varName);
//        getFrag.getter = buildGetterOrField(fld.fieldName, fld);
//        return getFrag;
//    }

    protected void addNullCheckIfRequired(NormalFieldSpec nspec, String varName, SingleFld srcFld) {
        javaCreator.addNullCheckIfRequired(nspec, varName, srcFld);
    }

//    private String renderJavaValue(SingleFld srcFld, String str) {
//        Class<?> clazz = srcFld.fieldTypeInfo.getFieldType();
//        if (ClassTypeHelper.isStringType(clazz)) {
//            return String.format("\"%s\"", str);
//        } else if (ClassTypeHelper.isEnumType(clazz)) {
//            String enumClassName = clazz.getSimpleName();
//            return String.format("%s.%s", enumClassName, str);
//        } else if (ClassTypeHelper.isDateType(clazz)) {
//            String s = String.format("\"%s\"", str);
//            StringToDateOrTimeConverter conv = new StringToDateOrTimeConverter(clazz);
//            return conv.gen(s);
//        }
//        return str;
//    }
}
