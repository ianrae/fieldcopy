package org.dnal.fieldcopy.newcodegen;

import org.dnal.fieldcopy.codegen.*;
import org.dnal.fieldcopy.fieldspec.*;
import org.dnal.fieldcopy.implicitconverter.ImplicitConvRegistry;
import org.dnal.fieldcopy.newcodegen.javacreator.JavaCreatorImpl;
import org.dnal.fieldcopy.newcodegen.javacreator.JavaField;
import org.dnal.fieldcopy.newcodegen.javacreator.JavaVar;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.registry.ConverterRegistry;
import org.dnal.fieldcopy.types.ClassTypeHelper;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.util.ReflectionUtil;
import org.dnal.fieldcopy.util.StrListCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

public class CopySpecToJavaCodeGenerator extends ExtractGenBase {
    private FieldSpecBuilder fieldSpecBuilder;
    private EnumHandler enumConverterHandler;
    private ValueConverter valueConverter;

    public CopySpecToJavaCodeGenerator(ImplicitConvRegistry implicitConvRegistry, ConverterRegistry registry, FieldCopyOptions options) {
        super(implicitConvRegistry, registry, new VarNameGenerator(), options);
        this.enumConverterHandler = new EnumHandler(implicitConvRegistry);
        //Some tests pass null for implicitConvRegistry. Normally it will never be null
        this.valueConverter = (implicitConvRegistry == null) ? null : new ValueConverter(implicitConvRegistry.getStringFieldTypeInfo());
        this.fieldSpecBuilder = new FieldSpecBuilder(options);
    }

    public JavaSrcSpec generate(CopySpec spec) {
        String converterName = buildName(spec);
        JavaSrcSpec srcSpec = new JavaSrcSpec(converterName);

        List<String> lines = new ArrayList<>();
        varNameGenerator = new VarNameGenerator();

        if (spec.autoFlag) {
            AutoFieldSpecCreator autoFieldSpecCreator = new AutoFieldSpecCreator();
            autoFieldSpecCreator.createAutoFields(spec, spec.autoExcludeFields);
        }

        for (FieldSpec fspec : spec.fields) {
            fieldSpecBuilder.buildFieldSpec(fspec, srcSpec);
            List<String> tmp = genSingleFieldCopy(fspec, srcSpec);
            lines.addAll(tmp);
        }
        srcSpec.lines = lines;
        return srcSpec;
    }

    private String buildName(CopySpec spec) {
        String s = String.format("%sTo%sConverter", spec.srcClass.getSimpleName(), spec.destClass.getSimpleName());
        return s;
    }

    public List<String> genSingleFieldCopy(FieldSpec fspec, JavaSrcSpec srcSpec) {
        this.javaCreator = new JavaCreatorImpl(srcSpec, options);

        if (fspec instanceof NormalFieldSpec) {
            NormalFieldSpec nspec = (NormalFieldSpec) fspec;

            //now generate source code into sc
            doSingleField(nspec, srcSpec);
            StrListCreator sc = javaCreator.getStrCreator();
            return sc.getLines();
        }

        return null;
    }

    private void doSingleField(NormalFieldSpec nspec, JavaSrcSpec srcSpec) {
        if (options.outputFieldCommentFlag) {
//            sc.nl();
//            sc.o("// %s ", nspec.convLangSrc);
            javaCreator.nl();
            javaCreator.comment(nspec.convLangSrc);
        }

        //STEP 1. generate lines to get src value from outer to innermost
        List<CodeVar> codeVarL = generateSrcValue(nspec, srcSpec);

        //STEP 2. do conversion if needed..
        codeVarL = doConversion(nspec, codeVarL, srcSpec);

        //STEP 3. generate lines to set dest value from outer to innermost, creating sub-objs as needed
        generateDestValue(nspec, srcSpec, codeVarL);

        if (!codeVarL.isEmpty()) {
            StrListCreator sc = javaCreator.getStrCreator();
            CodeVar lastVar = codeVarL.get(codeVarL.size() - 1);
            if (lastVar.needToAddClosingBrace) {
                sc.addStr("}");
            }
        }
    }

    private List<CodeVar> generateSrcValue(NormalFieldSpec nspec, JavaSrcSpec srcSpec) {
        List<CodeVar> codeVarL = new ArrayList<>();

        String srcVarName = "src";
        boolean prevIsOptional = false;
        FieldTypeInformation prevFieldInfo = null;
        for (int i = 0; i < nspec.srcFldX.size(); i++) {
            SingleFld fld = nspec.srcFldX.flds.get(i);
            String varType = buildVarType(fld.fieldType, srcSpec, fld.fieldTypeInfo);
            if (nspec.srcTextIsValue) {
                varType = valueConverter.buildValueType(fld, varType);
            }
            String varName = varNameGenerator.nextVarName();

//TODO replace with skipNull
//            if (i > 0) {
//                CodeVar previous = codeVarL.get(codeVarL.size() - 1);
//                if (! ClassTypeHelper.isPrimitive(previous.fld.fieldTypeInfo.getFieldType())) {
//                    sc.o("if (%s != null) {", previous.varName);
//                    previous.needToAddClosingBrace = true;
//                }
//            }

            adjustForOptionalValue(fld);
            JavaField jfield = new JavaField(fld.fieldName, fld);
            jfield.useIsGetter = fld.useIsGetter;
            JavaVar javaVar;
            if (nspec.srcTextIsValue) {
                varType = valueConverter.buildValueType(fld, varType);
                javaVar = javaCreator.getStatementFromValue(varName, srcVarName, jfield, varType);
            } else {
                if (isNull(prevFieldInfo)) {
                    prevFieldInfo = fld.fieldTypeInfo;
                }

                javaVar = javaCreator.getStatement(varName, srcVarName, prevFieldInfo, prevIsOptional, jfield);
            }

            addNullCheckIfRequired(nspec, varName, fld);
            addSkipNullIfNeeded(nspec, javaVar, fld);
            addToCodeVarL(codeVarL, javaVar, fld);

            srcVarName = varName;
            prevIsOptional = fld.fieldTypeInfo.isOptional();
            prevFieldInfo = fld.fieldTypeInfo;
        }

        return codeVarL;
    }

    private void addSkipNullIfNeeded(NormalFieldSpec nspec, JavaVar javaVar, SingleFld fld) {
        if (nspec.skipNull) {
            if (ClassTypeHelper.isPrimitive(fld.fieldTypeInfo.getFieldType())) {
                //do nothing
            } else {
                javaCreator.generateIfNotNullBlock(javaVar.varName);
                javaVar.needToAddClosingBrace = true;
            }
        }

    }

    //TODO remove later
    private void addToCodeVarL(List<CodeVar> codeVarL, JavaVar javaVar, SingleFld fld) {
        CodeVar codeVar = new CodeVar(javaVar.varName, javaVar.varType, fld);
        codeVar.needToAddClosingBrace = javaVar.needToAddClosingBrace;
        codeVarL.add(codeVar);
    }

    private void adjustForOptionalValue(SingleFld fld) {
        if (fld instanceof SingleValue) {
            valueConverter.ensureSingleQuoteDelimNotUsedUnlessChar(fld);
        }
    }

    private List<CodeVar> doConversion(NormalFieldSpec nspec, List<CodeVar> codeVarL, JavaSrcSpec srcSpec) {
        enumConverterHandler.generateEnumImplicitConverters(nspec);

        ConverterHandler gen = new ConverterHandler(implicitConvRegistry, registry, varNameGenerator, options, javaCreator);
        List<CodeVar> newL = new ArrayList<>();

        int numToDo = codeVarL.size() - nspec.destFldX.size();
        int index = 0;
        for (CodeVar srcCodeVar : codeVarL) {
            SingleFld srcFld = srcCodeVar.fld; //fldX.flds.get(index);

            //for now we only do conversion on leaf elements. eg on city in addr.city
            //TODO improve later if need more than leaf elements!
            if (index != codeVarL.size() - 1) {
                newL.add(srcCodeVar);
            } else {
                GenResult result = gen.genConversion(nspec, index - numToDo, srcSpec, srcCodeVar);
                if (result.varName != null) {
                    SingleFld fld = srcFld;
                    if (result.suppressOptional) {
                        fld = new SingleFld(fld);
                        fld.fieldTypeInfo = fld.fieldTypeInfo.createNonOptional();
                    }
                    CodeVar codeVar = new CodeVar(result.varName, result.varType, fld);
                    codeVar.needToAddClosingBrace = result.needToAddClosingBrace;
                    newL.add(codeVar);
                } else {
                    newL.add(srcCodeVar);
                }
            }

            index++;
        }
        return newL;
    }

    private void generateDestValue(NormalFieldSpec nspec, JavaSrcSpec srcSpec, List<CodeVar> srcCodeVarL) {
        int numToDo = srcCodeVarL.size() - nspec.destFldX.size();
        int adjustment = 0;
        List<CodeVar> destCodeVarL = new ArrayList<>();
        if (numToDo > 0) {
            // addr.city -> lastName
        } else {
            // lastName -> addr.city
            int n = -numToDo;  //n will be zero for most cases (firstName->firstName)
            boolean needsAdjustment = false;
            if (n == 0) {
                n = nspec.destFldX.size() - 1;
                needsAdjustment = true;
            }
            String destVar = "dest";

            int lastIndex = nspec.destFldX.size() - 1;
            boolean prevIsOptional = false;
            for (int k = 0; k < n; k++) {
                SingleFld fld = nspec.destFldX.flds.get(k);
                boolean isFinalSubObj = (k == lastIndex);
                CodeVar cv = doAutoCreateIfNeeded(nspec, fld, k, destVar, isFinalSubObj, srcSpec, prevIsOptional);
                if (cv != null) {
                    destCodeVarL.add(cv);
                }
                destVar = cv.varName;
                prevIsOptional = fld.fieldTypeInfo.isOptional();
            }

            if (needsAdjustment) {
                adjustment = destCodeVarL.size();
            } else {
                numToDo = 0;
            }
            destCodeVarL.addAll(srcCodeVarL);
            srcCodeVarL = destCodeVarL;
        }

        String destVar = "dest";
        boolean prevIsOptional = false;
        int lastIndex = nspec.destFldX.size() - 1;
        for (int i = 0; i < nspec.destFldX.size(); i++) {
            SingleFld fld = nspec.destFldX.flds.get(i);
            int index = i + numToDo;
            if (i > 0) {
                index += adjustment;
            }

            //auto-created vars will be in destCodeVarL. use those if they exist, else lookup using index
            CodeVar codeVar = srcCodeVarL.get(index);
            Optional<CodeVar> optVar = findFldInList(destCodeVarL, fld);
            if (optVar.isPresent()) {
                codeVar = optVar.get();
            }

            JavaField jfield = new JavaField(fld.fieldName, codeVar.fld);
            JavaVar javaVar = new JavaVar(codeVar.varName, codeVar.varType, Optional.of(jfield));
            javaCreator.setStatement(destVar, prevIsOptional, fld, javaVar);

            if (i != lastIndex) {
                destVar = findFldVarNameInList(destCodeVarL, fld);
            }

            prevIsOptional = fld.fieldTypeInfo.isOptional();
        }

//TODO replace with skipNull
//        for(CodeVar codeVar: srcCodeVarL) {
//            if (codeVar.needToAddClosingBrace) {
//                sc.addStr("}");
//            }
//        }
    }

    private String findFldVarNameInList(List<CodeVar> destCodeVarL, SingleFld fld) {
        Optional<CodeVar> codeVar = destCodeVarL.stream().filter(x -> x.fld == fld).findAny();
        return codeVar.get().varName; //should always be there
    }

    private Optional<CodeVar> findFldInList(List<CodeVar> destCodeVarL, SingleFld fld) {
        Optional<CodeVar> codeVar = destCodeVarL.stream().filter(x -> x.fld == fld).findAny();
        return codeVar;
    }

    private CodeVar doAutoCreateIfNeeded(NormalFieldSpec nspec, SingleFld fld, int index, String destVar,
                                         boolean isFinalSubObj, JavaSrcSpec srcSpec, boolean srcIsOptional) {

        if (!javaCreator.needsAutoCreate(fld.fieldTypeInfo, isFinalSubObj)) {
            return null;
        }
        JavaField jfield = new JavaField(fld.fieldName, fld);
        String destVarName = varNameGenerator.nextVarName();

        return javaCreator.genAutoCreate(destVar, fld.fieldTypeInfo, srcIsOptional, jfield, isFinalSubObj, destVarName);
    }
}
