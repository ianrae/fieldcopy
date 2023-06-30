package org.dnal.fieldcopy.newcodegen;

import org.dnal.fieldcopy.codegen.*;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.implicitconverter.ICRow;
import org.dnal.fieldcopy.implicitconverter.ImplicitConvRegistry;
import org.dnal.fieldcopy.implicitconverter.ImplicitConvertService;
import org.dnal.fieldcopy.implicitconverter.ImplicitConverter;
import org.dnal.fieldcopy.newcodegen.javacreator.JavaCreatorImpl;
import org.dnal.fieldcopy.newcodegen.javacreator.JavaVar;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.registry.ConverterRegistry;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.ClassTypeHelper;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

public class ConverterHandler extends ExtractGenBase {
    public ConverterHandler(ImplicitConvRegistry implicitConvRegistry, ConverterRegistry registry, VarNameGenerator varNameGenerator,
                            FieldCopyOptions options, JavaCreatorImpl javaCreator) {
        super(implicitConvRegistry, registry, varNameGenerator, options);
        this.javaCreator = javaCreator;
    }

    public GenResult genConversion(NormalFieldSpec nspec, int index, JavaSrcSpec srcSpec, CodeVar srcCodeVar) {
        SingleFld srcFld = srcCodeVar.fld;
        SingleFld destFld = nspec.destFldX.flds.get(index);

        Optional<GenResult> convVarName = Optional.empty();
        if (!nspec.isCustomField(srcFld)) {
            ConversionContext ctx = new ConversionContext(varNameGenerator);
            convVarName = doConversionIfNeeded(srcFld, destFld, ctx, srcSpec, nspec, srcCodeVar);
        }

        String varName = null;
        GenResult result = new GenResult(varName);
        if (!convVarName.isPresent()) {
            if (nspec.isCustomField(srcFld)) {
                varName = varNameGenerator.nextVarName();

                JavaVar javaVar = new JavaVar(srcCodeVar);
//                javaVar.srcOptz = srcFld.fieldTypeInfo.isOptional(); //TODO fix. should be optionalness for srcCodeVar
//                javaVar.destOptz = destFld.fieldTypeInfo.isOptional();
                result.varName = javaCreator.invokeCustom(varName, javaVar, nspec);
//                String srcVarName = srcCodeVar.varName;
//                String name = StringUtil.uppify(nspec.srcText);
//                String methodName = String.format("convert%s", name);
//                sc.o("%s %s = %s(%s, src, dest, ctx);", srcCodeVar.varType, varName, methodName, srcVarName);
//                nspec.customReturnType = srcCodeVar.varType;
//                nspec.customMethodName = methodName;
//                result.varName = varName;
            }
        } else {
            result = convVarName.get();
            result.suppressOptional = true; //we've handled src optional-ness
        }
        return result;
    }

    protected Optional<GenResult> doConversionIfNeeded(SingleFld srcFld, SingleFld destFld,
                                                       ConversionContext ctx, JavaSrcSpec srcSpec, NormalFieldSpec nspec,
                                                       VarExpr srcCodeVar) {
        if (isNull(registry)) {
            return Optional.empty();
        }

        if (nspec.srcTextIsValue) {
            ValueConverter valueConverter = new ValueConverter(implicitConvRegistry.getStringFieldTypeInfo());
            srcFld = valueConverter.convertValueFld(srcFld);
        }

        FieldTypeInformation ftiSrc = srcFld.fieldTypeInfo;
        FieldTypeInformation ftiDest = destFld.fieldTypeInfo;

        //our approach is:
        //a) check for an explicit converter. there may be one even for basic types such as int -> String
        //b) check for an implicit converter. we support most common ones (eg. int -> Integer)
        //c) check if src and dest are same type. If so then return w/o doing anything
        //d) when there is no implicit or explicit converter and the types are different, fail!
        //Note. We need to support a converter where src and dest are same type. There may be business logic
        //that the developer wants

        //we use ImplicitConvertService to determine if implicit conversion is possible given possibly
        //Optional src or dest
        ImplicitConvertService implicitConvertService = new ImplicitConvertService();
        List<ImplicitConverter> convL = new ArrayList<>();

        //look for an explicit converter
        ObjectConverter<?, ?> converter = findConverter(srcFld, destFld, Optional.ofNullable(nspec.usingConverterName));
        if (converter != null) {
            //ftiSrc.fieldType, ftiDest.fieldType lookup in Registry
            VarExpr tmpExpr;
            tmpExpr = new VarExpr(srcCodeVar.varName);

            boolean needToAddClosingBrace;
            if (ClassTypeHelper.isPrimitive(srcFld.fieldTypeInfo.getFieldType())) {
                //do nothing
                needToAddClosingBrace = false;
            } else {
                javaCreator.generateIfNotNullBlock(tmpExpr.varName);
                needToAddClosingBrace = true;
            }
            VarExpr convExpr = new VarExpr(ctx, "conv");
            javaCreator.locateConverter(srcFld, destFld, convExpr, nspec.usingConverterName);
//            String srcClassName = srcFld.fieldType.getSimpleName(); //import already done
//            String destClassName = destFld.fieldType.getSimpleName(); //import already done
//            if (srcFld.fieldTypeInfo.isList()) {
//                //    <S,T> Converter<S,T> locateForList(Class<?> srcElClass, Class<T> destElClass) {
//                String srcEl = srcFld.fieldTypeInfo.getFirstActual().getName();
//                String destEl = destFld.fieldTypeInfo.getFirstActual().getName();
//                srcEl = ClassNameUtil.renderClassName(srcEl);
//                destEl = ClassNameUtil.renderClassName(destEl);
//                sc.o("Converter<%s,%s> %s = ctx.locateForList(%s.class, %s.class);",
//                        srcClassName, destClassName, convExpr.varName, srcEl, destEl);
//            } else {
//                // Converter<Address,Address> conv3 = ctx.locate(Address.class, Address.class, ctx.buildFldInfo(Address.class), ctx.buildFldInfo(Address.class));
//                sc.o("Converter<%s,%s> %s = ctx.locate(%s.class, %s.class, ctx.buildFldInfo(%s.class), ctx.buildFldInfo(%s.class));",
//                        srcClassName, destClassName, convExpr.varName, srcClassName, destClassName, srcClassName, destClassName);
//            }


            VarExpr varExpr3 = javaCreator.invokeConverter(srcFld, destFld, convExpr, tmpExpr, ctx);
//            String destClassName = destFld.fieldType.getSimpleName(); //import already done
//            VarExpr varExpr3 = new VarExpr(ctx);
//            varExpr3.varType = buildVarTypeWithoutOptional(destFld.fieldType, srcSpec, ftiDest);
//            String newObj = String.format("new %s()", destClassName); //TODO later add a factory
//            sc.o("%s = %s.convert(%s, %s, ctx);", varExpr3.render(), convExpr.varName, tmpExpr.varName, newObj);
            GenResult result = new GenResult(varExpr3.varName);
            result.needToAddClosingBrace = needToAddClosingBrace;
            return Optional.of(result);
        } else if (implicitConvertService.isConversionSupported(implicitConvRegistry, srcFld, destFld, convL)) {
            VarExpr srcExpr;
            if (ctx.doListCopy) {
                srcExpr = new VarExpr(ctx.listVar);
                srcExpr.varType = buildVarTypeWithoutOptional(srcFld.fieldType, srcSpec, ftiSrc);
            } else {
                srcExpr = srcCodeVar;
            }

            String varType2 = buildVarTypeWithoutOptional(destFld.fieldType, srcSpec, ftiDest);
//            int indent = 0;
            VarExpr listExpr = null;
            VarExpr elExpr = null;
            if (ctx.doListCopy) {
                listExpr = new VarExpr(ctx, "list");
                elExpr = javaCreator.forLoopBegin(varType2, srcExpr, listExpr, ctx);
//                //List<String> list2 = new ArrayList<>();
//                sc.o("List<%s> %s = new ArrayList<>();", varType2, listExpr.varName); //TODO later support more than ArrayList
//
//                //for(String tmp3: tmp1) {
//                elExpr = new VarExpr(ctx, "el");
//                sc.o("for(%s %s: %s) {", srcExpr.varType, elExpr.varName, srcExpr.varName);
//                indent = 2;
            }

            ICRow row = implicitConvertService.getRowForDestType(implicitConvRegistry, destFld);
            ImplicitConverter iconv = implicitConvertService.getConverterForSrc(srcFld, row);

//            VarExpr expr2 = new VarExpr(ctx);
//            expr2.varType = varType2;
//            addElementDefaultVal(sc, indent, nspec, srcExpr.varName, srcFld);
            javaCreator.addElementDefaultVal(nspec, srcExpr.varName, srcFld);

            VarExpr expr2 = javaCreator.addElementToList(srcFld, iconv, ctx.doListCopy ? elExpr.varName : srcExpr.varName,
                    listExpr, varType2, ctx);
//            String s1 = buildGetStr(srcFld, iconv, ctx.doListCopy ? elExpr.varName : srcExpr.varName);
//            sc.oIndented(indent, "%s %s = %s;", varType2, expr2.varName, s1);
//            if (ctx.doListCopy) {
//                sc.oIndented(indent, "%s.add(%s);", listExpr.varName, expr2.varName);
//                sc.addStr("}");
//            }

            if (ctx.doListCopy) {
                return Optional.of(new GenResult(listExpr.varName));
            } else {
                return Optional.of(new GenResult(expr2));
            }
        }

        //do nothing if src and dest are same type
        if (implicitConvertService.areSameTypes(ftiSrc, ftiDest, convL)) {
            return Optional.empty();
        }

        //if src and dest are lists (of different elements), needs special handling
        if (ftiSrc.isList() && ftiDest.isList()) {
            Optional<GenResult> opt = doListConversion(srcFld, destFld, ctx, srcSpec, nspec, srcCodeVar);
            if (opt.isPresent()) {
                return opt;
            }
        }

        //there is no implicit or explicit converter. Fail!
        String msg = String.format("Are you specifying the correct fields? Or you need to add a converter for %s -> %s", srcFld.fieldType.getSimpleName(), destFld.fieldType.getSimpleName());
        msg = String.format("Cannot convert '%s %s' to '%s %s'. %s", srcFld.fieldType.getSimpleName(), srcFld.fieldName,
                destFld.fieldType.getSimpleName(), destFld.fieldName, msg);
        throw new FieldCopyException(msg);
    }

    //    private String buildGetStr(SingleFld srcFld, ImplicitConverter iconv, String varName) {
//        String tmp = javaCreator.buildOptionalGet(srcFld, varName);
//        String s1 = iconv.gen(tmp);
//        return s1;
//    }
//
    //look for converter, ignoring whether src or dst are optional, in the same way as
    //ImplicitConvertService.isConversionSupported
    private ObjectConverter<?, ?> findConverter(SingleFld srcFld, SingleFld destFld, Optional<String> converterName) {
        if (converterName.isPresent()) {
            ObjectConverter<?, ?> conv = registry.find(srcFld.fieldTypeInfo.getFieldType(), destFld.fieldTypeInfo.getFieldType(), converterName.get());
            if (conv != null) {
                return conv;
            }
        }


        ObjectConverter<?, ?> conv = registry.find(srcFld.fieldTypeInfo, destFld.fieldTypeInfo);
        if (conv != null) {
            return conv;
        }

        //try non-optional versions. if fieldTypeInfo is not optional then createNonOptional() returns identical copy
        FieldTypeInformation srcWithoutOptional = srcFld.fieldTypeInfo.createNonOptional();
        FieldTypeInformation destWithoutOptional = destFld.fieldTypeInfo.createNonOptional();
        conv = registry.find(srcWithoutOptional, destWithoutOptional);
        if (conv != null) {
            return conv;
        }
        return null;
    }

    private Optional<GenResult> doListConversion(SingleFld srcFld1, SingleFld destFld1, ConversionContext ctx1,
                                                 JavaSrcSpec srcSpec, NormalFieldSpec nspec, VarExpr srcCodeVar) {

        Type type = srcFld1.fieldTypeInfo.getFirstActual();
        Class<?> srcFieldClass = (Class<?>) type; //TODO can this fail? fix for nested Optional<list,...
        Class<?> destFieldClass = (Class<?>) destFld1.fieldTypeInfo.getFirstActual(); //TODO can this fail? fix for nested Optional<list,...

        FieldTypeInformation ftiSrc = new FieldTypeInformationImpl(srcFieldClass);
        FieldTypeInformation ftiDest = new FieldTypeInformationImpl(destFieldClass);

        SingleFld srcFld = new SingleFld(srcFld1.fieldName, ftiSrc); //TODO fix ftiSrc.getFirstActual().get());
//        //need methods isPrimitive, isScalar
//        public Optional<Object> key; //for list or map
//        public boolean isRequired;
//        public Object defaultValue;

        SingleFld destFld = new SingleFld(destFld1.fieldName, ftiDest); //TODO fix ftiDest.elementType.get());
//        //need methods isPrimitive, isScalar
//        public Optional<Object> key; //for list or map
//        public boolean isRequired;
//        public Object defaultValue;

        ConversionContext ctx = new ConversionContext(ctx1.varNameGenerator);
        ctx.doListCopy = true;
        ctx.listVar = srcCodeVar.varName;

        Optional<GenResult> result = doConversionIfNeeded(srcFld, destFld, ctx, srcSpec, nspec, srcCodeVar);
        return result;
    }

}
