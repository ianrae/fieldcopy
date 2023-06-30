package org.dnal.fieldcopy.newcodegen.javacreator;

import org.dnal.fieldcopy.codegen.ConversionContext;
import org.dnal.fieldcopy.codegen.VarExpr;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.implicitconverter.ImplicitConverter;
import org.dnal.fieldcopy.newcodegen.CodeVar;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.util.StrListCreator;

import java.util.List;

/**
 * indent
 * optional
 * list
 * fail if null
 * defaultVal
 * autoCreateSubList with sharing
 */
public interface JavaCreator {
    StrListCreator getStrCreator();

    List<String> getLines();

    void comment(String str);

    String buildVarType(Class<?> fieldType, FieldTypeInformation fieldTypeInfo);

    String generateIfNullExpr(String varName);

    void generateIfNullBlock(String varName);

    void generateIfNotNullBlock(String varName);

    void text(String str);

    void textNL(String str);

    void nl();

    JavaVar getStatement(String varName, String srcVarName, FieldTypeInformation srcFieldInfo, boolean srcIsOptional, JavaField jfield);

    JavaVar getStatementFromValue(String varName, String srcVarName, JavaField jfield, String varType);

    void setStatement(String destVar, boolean destVarIsOptional, SingleFld destFld, JavaVar codeVar);

    String invokeCustom(String varName, JavaVar srcCodeVar, NormalFieldSpec nspec);

    String buildOptionalGet(SingleFld srcFld, String varName);

    void locateConverter(SingleFld srcFld, SingleFld destFld, VarExpr convExpr, String namedConverter);

    VarExpr forLoopBegin(String varType2, VarExpr srcExpr, VarExpr listExpr, ConversionContext ctx);

    void addElementDefaultVal(NormalFieldSpec nspec, String varName, SingleFld srcFld);

    VarExpr addElementToList(SingleFld srcFld, ImplicitConverter iconv, String varName, VarExpr listExpr, String varType2, ConversionContext ctx);

    VarExpr invokeConverter(SingleFld srcFld, SingleFld destFld, VarExpr convExpr, VarExpr tmpExpr, ConversionContext ctx);

    String buildVarTypeWithoutOptional(Class<?> fieldType, FieldTypeInformation fieldTypeInfo);

    void addNullCheckIfRequired(NormalFieldSpec nspec, String varName, SingleFld srcFld);

    boolean needsAutoCreate(FieldTypeInformation srcFieldInfo, boolean isFinalSubObj);

    CodeVar genAutoCreate(String srcVarName, FieldTypeInformation srcFieldInfo, boolean srcIsOptional, JavaField jfield,
                          boolean isFinalSubObj, String destVarName);
}
