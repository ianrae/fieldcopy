package org.dnal.fieldcopy.mlexer;

import org.apache.commons.collections.CollectionUtils;
import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.mlexer.ast.*;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.util.ReflectionUtil;

import java.util.List;

import static java.util.Objects.isNull;

public class ASTToSpecBuilder {

    public CopySpec buildSpec(Class<?> srcClass, Class<?> destClass) {
        CopySpec spec = new CopySpec(srcClass, destClass);
        return spec;
    }

    public void addToSpec(CopySpec spec, List<AST> list, String convLangSrc) {
        for (AST ast : list) {
            if (ast instanceof AutoAST) {
                spec.autoFlag = true;
            } else if (ast instanceof ExcludeAST) {
                ExcludeAST excludeAST = (ExcludeAST) ast;
                spec.autoExcludeFields = excludeAST.fieldsToExclude;
            } else if (ast instanceof ValueAST) {
                ValueAST valueAST = (ValueAST) ast;
                NormalFieldSpec nspec = buildFieldSpec(spec, valueAST, convLangSrc);
                nspec.srcTextIsValue = true; //mark it as a value
            } else {
                ConvertAST convertAST = (ConvertAST) ast;
                buildFieldSpec(spec, convertAST, convLangSrc);
            }
        }
    }

    private NormalFieldSpec buildFieldSpec(CopySpec spec, ConvertAST convertAST, String convLangSrc) {
        DottedFieldBuilder dfBuilder = new DottedFieldBuilder(convertAST.srcText, convertAST.srcPieces,
                convertAST.destText, convertAST.destPieces);
        String s1 = dfBuilder.getIthSrc(0);
        String s2 = dfBuilder.getIthDest(0);
        NormalFieldSpec nspec = new NormalFieldSpec(spec.srcClass, spec.destClass, s1, s2);
        boolean isValue = convertAST instanceof ValueAST;

        nspec.isRequired = convertAST.isRequired;
        nspec.skipNull = convertAST.skipNull;
        if (!isValue && isListField(spec.srcClass, s1)) {
            nspec.elementDefaultVal = convertAST.defaultVal; //so default means element default
        } else {
            nspec.defaultVal = convertAST.defaultVal;
        }
        nspec.convLangSrc = convLangSrc;

        nspec.usingConverterName = convertAST.usingConverter;

        if (CollectionUtils.isEmpty(convertAST.srcPieces) && CollectionUtils.isEmpty(convertAST.destPieces)) {
            if (isNull(s2)) {
                throw new FieldCopyException(String.format("syntax error in converter for '%s': %s", spec.srcClass.getSimpleName(), s1));
            }
            spec.fields.add(nspec);
        } else {
            nspec.dfBuilder = dfBuilder;
            spec.fields.add(nspec);
        }
        return nspec;
    }

    private boolean isListField(Class<?> srcClass, String fieldName) {
        //be forgiving here. we will do type checks of srcClass.fieldName later
        ReflectionUtil helper = new ReflectionUtil();
        if (isNull(fieldName) || !helper.isFieldOrGetter(srcClass, fieldName)) {
            return false;
        }
        FieldTypeInformation fti = helper.getFieldInformation(srcClass, fieldName);
        return fti.isList();
    }

}
