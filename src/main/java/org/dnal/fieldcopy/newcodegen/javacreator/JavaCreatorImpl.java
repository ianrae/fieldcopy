package org.dnal.fieldcopy.newcodegen.javacreator;

import org.dnal.fieldcopy.codegen.ConversionContext;
import org.dnal.fieldcopy.codegen.JavaSrcSpec;
import org.dnal.fieldcopy.codegen.VarExpr;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.implicitconverter.ImplicitConverter;
import org.dnal.fieldcopy.implicitconverter.date.StringToDateOrTimeConverter;
import org.dnal.fieldcopy.newcodegen.CodeVar;
import org.dnal.fieldcopy.newcodegen.vardef.JavaGetSetGenerator;
import org.dnal.fieldcopy.newcodegen.vardef.QField;
import org.dnal.fieldcopy.newcodegen.vardef.QVar;
import org.dnal.fieldcopy.newcodegen.vardef.QVarDefine;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.types.ClassTypeHelper;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.util.ClassNameUtil;
import org.dnal.fieldcopy.util.StrListCreator;
import org.dnal.fieldcopy.util.StringUtil;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

public class JavaCreatorImpl implements JavaCreator {
    private final FieldCopyOptions options;
    private final JavaTypeHelper javaTypeHelper;
    private StrListCreator sc = new StrListCreator();
    public int indent = 0; //#chars
    private JavaSrcSpec srcSpec;
    private JavaGetSetGenerator getSetGenerator;

    public JavaCreatorImpl(JavaSrcSpec srcSpec, FieldCopyOptions options) {
        this.srcSpec = srcSpec;
        this.options = options;
        this.javaTypeHelper = new JavaTypeHelper(srcSpec);
        this.getSetGenerator = new JavaGetSetGenerator(options);
    }

    @Override
    public StrListCreator getStrCreator() {
        return sc;
    }

    @Override
    public void text(String str) {
        if (indent > 0) {
            String s = StringUtil.createSpace(indent);
            sc.o("%s%s", s, str);
        } else {
            sc.addStr(str);
        }
    }

    @Override
    public void textNL(String str) {
        text(str);
        nl();
    }

    @Override
    public void nl() {
        sc.nl();
    }

    @Override
    public List<String> getLines() {
        return sc.getLines();
    }

    @Override
    public String generateIfNullExpr(String varName) {
        return String.format("if (%s == null)", varName);
    }

    @Override
    public void generateIfNullBlock(String varName) {
        String s = generateIfNullExpr(varName);
        sc.o("%s {", s);
    }

    @Override
    public void generateIfNotNullBlock(String varName) {
        String s = String.format("if (%s != null)", varName);
        sc.o("%s {", s);
    }

    @Override
    public JavaVar getStatement(String destVarName, String srcVarName, FieldTypeInformation srcFieldInfo, boolean srcIsOptional, JavaField jfield) {
        QVar qvar = new QVar(srcVarName, srcFieldInfo);
        QField destField = new QField(jfield.fieldName, jfield.fti, jfield.useIsGetter, jfield.isField());

        boolean destIsOptional = destField.fti.isOptional();
        QVarDefine varDef;
        if (srcIsOptional) {
            varDef = getSetGenerator.safeGenVar(qvar, destField, srcIsOptional, destIsOptional, destVarName, false, true);
        } else {
            varDef = getSetGenerator.genVar(qvar, destField, srcIsOptional, destIsOptional, destVarName);
        }

        String varType = buildVarType(jfield.fti.getFieldType(), jfield.fti);
        sc.o("%s %s = %s;", varType, destVarName, varDef.srcText);

        JavaVar javaVar = new JavaVar(destVarName, varType, Optional.of(jfield));
        return javaVar;
    }

    @Override
    public JavaVar getStatementFromValue(String destVarName, String srcVarName, JavaField jfield, String varType) {
        String getterStr = buildGetterValue(null, jfield);
//TODO replace with skipNull
//            if (i > 0) {
//                CodeVar previous = codeVarL.get(codeVarL.size() - 1);
//                if (! ClassTypeHelper.isPrimitive(previous.fld.fieldTypeInfo.getFieldType())) {
//                    sc.o("if (%s != null) {", previous.varName);
//                    previous.needToAddClosingBrace = true;
//                }
//            }

        sc.o("%s %s = %s;", varType, destVarName, getterStr);

        JavaVar javaVar = new JavaVar(destVarName, varType, Optional.of(jfield));
        return javaVar;
    }

    @Override
    public void comment(String str) {
        sc.o("// %s", str);
    }

    @Override
    public void addNullCheckIfRequired(NormalFieldSpec nspec, String varName, SingleFld srcFld) {
        if (nspec.defaultVal != null) {
            if (ClassTypeHelper.isPrimitive(srcFld.fieldTypeInfo.getFieldType())) {
                //do nothing
            } else if (srcFld.fieldTypeInfo.isOptional()) {
                //TODO do type check here that defaultVar matches the fieldType
                String s = renderJavaValue(srcFld, nspec.defaultVal);
                sc.o("if (%s == null || !%s.isPresent()) %s = Optional.ofNullable(%s);", varName, varName, varName, s);
            } else if (srcFld.fieldTypeInfo.isList()) {
                //do nothing. the defaultVal is applied to elements
                //TODO should we also support a defaultVal for the list itself?
            } else {
                //TODO do type check here that defaultVar matches the fieldType
                String s = renderJavaValue(srcFld, nspec.defaultVal);
                sc.o("if (%s == null) %s = %s;", varName, varName, s);
            }
        }

        if (nspec.isRequired) {
            if (ClassTypeHelper.isPrimitive(srcFld.fieldTypeInfo.getFieldType())) {
                //do nothing
            } else {
                sc.o("if (%s == null) ctx.throwUnexpectedNullError(getSourceFieldTypeInfo(), \"%s\");", varName, nspec.srcText);
            }
        }
    }

    @Override
    public void setStatement(String destVar, boolean destVarIsOptional, SingleFld destFld, JavaVar codeVar) {
        QVar qvar = new QVar(codeVar.varName, codeVar.jfield.get().fti);
        QField qfield = new QField(destFld.fieldName, destFld.fieldTypeInfo, false, destFld.isField);

        QVarDefine varDef = getSetGenerator.genSet(qvar, destVar, qfield, destVarIsOptional);
        sc.addStr(varDef.srcText);
        return;
    }

    @Override
    public String invokeCustom(String varName, JavaVar srcCodeVar, NormalFieldSpec nspec) {
        String srcVarName = srcCodeVar.varName;
        String name = StringUtil.uppify(nspec.srcText);
        name = name.replace('.', '_'); //prod.region.code to prod_region_code

        String methodName = String.format("convert%s", name);
        sc.o("%s %s = %s(%s, src, dest, ctx);", srcCodeVar.varType, varName, methodName, srcVarName);
        nspec.customReturnType = srcCodeVar.varType;
        nspec.customMethodName = methodName;
        return varName;
    }

    @Override
    public String buildOptionalGet(SingleFld srcFld, String varName) {
        String tmp = srcFld.fieldTypeInfo.isOptional() ? String.format("%s.get()", varName) : varName;
        return tmp;
    }

    @Override
    public void locateConverter(SingleFld srcFld, SingleFld destFld, VarExpr convExpr, String namedConverter) {
        String srcClassName = buildGenericTypeName(srcFld);
        String destClassName = buildGenericTypeName(destFld);

        if (namedConverter != null) {
            String locParam1 = buildSimpleClassName(srcFld);
            String locParam2 = buildSimpleClassName(destFld);
            sc.o("ObjectConverter<%s,%s> %s = ctx.locate(%s.class, %s.class, \"%s\");",
                    srcClassName, destClassName, convExpr.varName, locParam1, locParam2, namedConverter);
        } else if (srcFld.fieldTypeInfo.isList() || destFld.fieldTypeInfo.isList()) {
            String locParam1 = buildLocParam(srcFld);
            String locParam2 = buildLocParam(destFld);

            sc.o("ObjectConverter<%s,%s> %s = ctx.locate(%s, %s);",
                    srcClassName, destClassName, convExpr.varName, locParam1, locParam2);
        } else {
            String locParam1 = buildSimpleClassName(srcFld);
            String locParam2 = buildSimpleClassName(destFld);
            sc.o("ObjectConverter<%s,%s> %s = ctx.locate(%s.class, %s.class);",
                    srcClassName, destClassName, convExpr.varName, locParam1, locParam2);
        }
    }

    private String buildLocParam(SingleFld srcFld) {
        if (srcFld.fieldTypeInfo.isList()) {
            return String.format("FieldTypeInformationImpl.createForList(%s.class)", buildSimpleClassName(srcFld));
        } else {
            return String.format("FieldTypeInformationImpl.create(%s.class)", buildSimpleClassName(srcFld));
        }
    }

    private String buildGenericTypeName(SingleFld fld) {
        String className = buildSimpleClassName(fld);
        if (fld.fieldTypeInfo.isList()) {
            className = String.format("List<%s>", className);
        }
        return className;
    }

    private String buildSimpleClassName(SingleFld fld) {
        String className = fld.fieldTypeInfo.getEffectiveType().getSimpleName();
        return className;
    }

    @Override
    public VarExpr forLoopBegin(String varType2, VarExpr srcExpr, VarExpr listExpr, ConversionContext ctx) {
        sc.o("List<%s> %s = new ArrayList<>();", varType2, listExpr.varName); //TODO later support more than ArrayList

        //for(String tmp3: tmp1) {
        VarExpr elExpr = new VarExpr(ctx, "el");
        sc.o("for(%s %s: %s) {", srcExpr.varType, elExpr.varName, srcExpr.varName);
        indent = 2;
        return elExpr;
    }

    @Override
    public VarExpr addElementToList(SingleFld srcFld, ImplicitConverter iconv, String varName,
                                    VarExpr listExpr, String varType2, ConversionContext ctx) {
        VarExpr expr2 = new VarExpr(ctx);
        expr2.varType = varType2;
        String s1 = buildGetStr(srcFld, iconv, varName);
        sc.oIndented(indent, "%s %s = %s;", varType2, expr2.varName, s1);

        if (ctx.doListCopy) {
            sc.oIndented(indent, "%s.add(%s);", listExpr.varName, expr2.varName);
            sc.addStr("}");
        }

        return expr2;
    }

    private String buildGetStr(SingleFld srcFld, ImplicitConverter iconv, String varName) {
        String tmp = buildOptionalGet(srcFld, varName);
        String s1 = iconv.gen(tmp);
        return s1;
    }

    @Override
    public void addElementDefaultVal(NormalFieldSpec nspec, String varName, SingleFld srcFld) {
        if (nspec.elementDefaultVal != null) {
            //TODO. enhance this when we support optional<list> or list<list>
            //TODO do type check here that defaultVar matches the fieldType
            String s = renderJavaValue(srcFld, nspec.elementDefaultVal);
            sc.oIndented(indent, "if (%s == null) %s = %s;", varName, varName, s);
        }
    }

    @Override
    public VarExpr invokeConverter(SingleFld srcFld, SingleFld destFld, VarExpr convExpr, VarExpr tmpExpr, ConversionContext ctx) {
        FieldTypeInformation ftiDest = destFld.fieldTypeInfo;
        String destClassName = destFld.fieldType.getSimpleName(); //import already done

        VarExpr varExpr3 = new VarExpr(ctx);
        varExpr3.varType = buildVarTypeWithoutOptional(destFld.fieldType, ftiDest);

        //new Integer() is not supported by Java. pass null in these cases

        String newObjStr;
        if (ClassTypeHelper.isListType(ftiDest.getFieldType())) {
            newObjStr = String.format("new ArrayList()"); //TODO later add a factory
        } else if (ClassTypeHelper.isStructType(ftiDest)) {
            newObjStr = String.format("new %s()", destClassName); //TODO later add a factory
        } else {
            newObjStr = String.format("null");
        }

        String srcVarStr = String.format("%s", tmpExpr.varName);
        if (srcFld.fieldTypeInfo.isOptional()) {
            srcVarStr += ".get()";
        }

        sc.o("%s = %s.convert(%s, %s, ctx);", varExpr3.render(), convExpr.varName, srcVarStr, newObjStr);
        return varExpr3;
    }

    @Override
    public String buildVarTypeWithoutOptional(Class<?> fieldType, FieldTypeInformation fieldTypeInfo) {
        FieldTypeInformation fti = fieldTypeInfo.createNonOptional();
        return buildVarType(fieldType, fti);
    }

    //--helpers--
    private String renderJavaValue(SingleFld srcFld, String str) {
        Class<?> clazz = srcFld.fieldTypeInfo.getFieldType();
        if (ClassTypeHelper.isStringType(clazz)) {
            return String.format("\"%s\"", str);
        } else if (ClassTypeHelper.isEnumType(clazz)) {
            String enumClassName = clazz.getSimpleName();
            return String.format("%s.%s", enumClassName, str);
        } else if (ClassTypeHelper.isDateType(clazz)) {
            String s = String.format("\"%s\"", str);
            StringToDateOrTimeConverter conv = new StringToDateOrTimeConverter(clazz);
            return conv.gen(s);
        } else if (ClassTypeHelper.isLongType(clazz)) {
            String s = String.format("%sL", str);
            return s;
        } else if (ClassTypeHelper.isFloatType(clazz)) {
            String s = String.format("%sf", str);
            return s;
        } else if (ClassTypeHelper.isDoubleType(clazz)) {
            String s = String.format("%sd", str);
            return s;
        } else if (ClassTypeHelper.isCharType(clazz)) {
            String s = String.format("'%s'", str);
            return s;
        }
        return str;
    }

    private String buildGetterValue(String srcVarName, JavaField jfield) {
        String s = jfield.fieldName;
        if (isNull(srcVarName)) {
            s = String.format("%s", s);
        } else {
            s = String.format("%s.%s", srcVarName, s);
        }

        FieldTypeInformation fti = jfield.fti;
        if (fti.isList() && options.createNewListWhenCopying) {
            String className = ClassNameUtil.renderClassName(fti.getFirstActual());
            String tmp = String.format("ctx.createEmptyList(%s, %s.class)", s, className);
            s = String.format("%s == null ? null : %s", s, tmp);

            //we already do this by default srcSpec.addImportIfNotAlreadyPresent("java.util.ArrayList");
        }

        return s;
    }

    @Override
    public String buildVarType(Class<?> fieldType, FieldTypeInformation fieldTypeInfo) {
        return javaTypeHelper.buildVarType(fieldType, fieldTypeInfo);
    }

    @Override
    public boolean needsAutoCreate(FieldTypeInformation srcFieldInfo, boolean isFinalSubObj) {
        return getSetGenerator.needsAutoCreate(srcFieldInfo, isFinalSubObj);
    }

    @Override
    public CodeVar genAutoCreate(String srcVarName, FieldTypeInformation srcFieldInfo, boolean srcIsOptional, JavaField jfield,
                                 boolean isFinalSubObj, String destVarName) {
        QVar qvar = new QVar(srcVarName, srcFieldInfo);
        QField destField = new QField(jfield.fieldName, jfield.fti, jfield.useIsGetter, jfield.isField());
        boolean destIsOptional = destField.fti.isOptional();

        QVarDefine varDef = getSetGenerator.getVarAutoCreate(qvar, destField, srcIsOptional, destIsOptional, destVarName, isFinalSubObj);
        if (varDef.srcText.contains("?")) {
            String varType = buildVarType(jfield.fti.getFieldType(), jfield.fti);
            sc.o("%s %s = %s;", varType, destVarName, varDef.srcText);

            CodeVar javaVar = new CodeVar(destVarName, varType, jfield.fld);
            return javaVar;

        }
        return null;
    }
}
