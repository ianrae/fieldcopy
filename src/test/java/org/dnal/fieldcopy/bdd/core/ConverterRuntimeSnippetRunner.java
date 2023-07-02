package org.dnal.fieldcopy.bdd.core;


import org.apache.commons.collections.CollectionUtils;
import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.converter.FCRegistry;
import org.dnal.fieldcopy.error.FCError;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.group.ObjectConverterFinder;
import org.dnal.fieldcopy.group.ObjectConverterSpec;
import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.parser.fieldcopyjson.ParsedConverterSpec;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.runtime.RuntimeOptions;
import org.dnal.fieldcopy.runtime.RuntimeOptionsHelper;
import org.dnal.fieldcopy.util.ReflectionUtil;
import org.dnal.fieldcopy.util.StringUtil;
import org.dnal.fieldcopy.util.render.ObjectRendererImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import static java.util.Objects.isNull;


public class ConverterRuntimeSnippetRunner implements SnippetRunner {
    private final FieldCopyLog log;
    private final ObjectRendererImpl objParser;
    private final ReflectionUtil helper;

    public ConverterRuntimeSnippetRunner(FieldCopyLog log) {
        this.log = log;
        this.objParser = new ObjectRendererImpl();
        this.helper = new ReflectionUtil();
    }

    @Override
    public BDDSnippetResult execute(BDDSnippet snippet, BDDSnippetResult previousRes, SnippetContext snippetContext) {
        BDDSnippetResult res = new BDDSnippetResult();

        String json = StringUtil.convertToSingleString(snippet.lines);
        Class<?> srcClass = previousRes.specs.get(0).srcClass; //TODO
        Class<?> destClass = previousRes.specs.get(0).destClass;
        Object src = objParser.parse(json, srcClass);
        Object dest = helper.createObj(destClass);

        FCRegistry registry = buildRegistry(res, previousRes, srcClass, destClass, snippetContext);
        ObjectConverter<?, ?> converter = registry.find(srcClass, destClass);
        if (isNull(converter)) {
            String msg = String.format("can't find converter for '%s' -> '%s'", srcClass.getName(), destClass.getName());
            return addError(res, "converter.not.found", msg);
        }

        RuntimeOptions runtimeOptions = new RuntimeOptions();
        //copy from json config
        if (previousRes != null) {
            FieldCopyOptions options = previousRes.parseRes.options;
            RuntimeOptionsHelper runtimeOptionsHelper = new RuntimeOptionsHelper();
            runtimeOptionsHelper.loadFromFieldCopyOptions(options, runtimeOptions);
        }

        ConverterContext ctx = new ConverterContext(registry, runtimeOptions);
        Object dest2 = null;

        Class<?> convClass = converter.getClass();
        try {
            Method meth = convClass.getDeclaredMethod("convert", srcClass, destClass, ctx.getClass());
            dest2 = meth.invoke(converter, src, dest, ctx);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            String actualExceptionMsg = e.getTargetException().getMessage();
            log.log(actualExceptionMsg);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        res.convDestObj = dest2;
        res.ok = true;
        return res;
    }

    private BDDSnippetResult addError(BDDSnippetResult res, String id, String msg) {
        FCError err = new FCError(id, msg);
        this.log.logError(err.toString());
        res.errors.add(err);
        res.ok = false;
        return res;
    }

    private FCRegistry buildRegistry(BDDSnippetResult res, BDDSnippetResult previousRes, Class<?> srcClass, Class<?> destClass, SnippetContext snippetContext) {
        FCRegistry registry = new FCRegistry();
        ReflectionUtil helper = new ReflectionUtil();

        //use copySpecs from previousRes
        for (CopySpec spec : previousRes.specs) {
            //R200T1_Src1ToDest1Converter.java
            String converterClassName = spec.converterName.get(); //buildConverterClassName(suffix, spec.srcClass, spec.destClass);

            String fullName = String.format("%s.%s", ConvLangSnippetRunner.GEN_PACKAGE, converterClassName);
            Class<?> clazz = findConverterClass(registry, fullName, spec.converterNameForUsing, res, snippetContext); //helper.getClassFromName(fullName);
        }

        //addOtherParsedConverters(registry, res, previousRes, suffix);
        addAdditionalConverters(registry, res, previousRes);
        return registry;
    }

    private Class<?> findConverterClass(FCRegistry registry, String fullName, Optional<String> converterNameForUsing, BDDSnippetResult res, SnippetContext snippetContext) {
        Class<?> clazz = helper.getClassFromName(fullName);
        if (isNull(clazz)) {
            String msg = String.format("R%dT%d can't find test's converter '%s'", snippetContext.rNumber, snippetContext.testNum, fullName);
            addError(res, "additional.converter.not.found", msg);
        } else {
            ObjectConverter conv = (ObjectConverter) helper.createObj(clazz);
            if (converterNameForUsing.isPresent()) {
                registry.addNamed(conv, converterNameForUsing.get());
            } else {
                registry.add(conv);
            }
        }
        return clazz;
    }

    private FCRegistry addAdditionalConverters(FCRegistry registry, BDDSnippetResult res, BDDSnippetResult previousRes) {
        ReflectionUtil helper = new ReflectionUtil();

        for (ParsedConverterSpec action : previousRes.parseRes.converters) {
            if (CollectionUtils.isNotEmpty(action.additionalConverters)) {
                for (ObjectConverterSpec converterSpec : action.additionalConverters) {
                    String converterClassName = converterSpec.converterClassName;

                    ObjectConverterFinder finder = new ObjectConverterFinder();
                    String fullName = finder.buildFullName(converterClassName);
                    Class<?> clazz = helper.getClassFromName(fullName);
                    if (isNull(clazz)) {
                        String msg = String.format("can't find additional converter '%s'", converterClassName);
                        addError(res, "additional.converter.not.found", msg);
                    } else {
                        ObjectConverter conv = (ObjectConverter) helper.createObj(clazz);
                        if (isNull(conv)) {
                            throw new FieldCopyException("failed to create: " + clazz.getName());
                        }
                        registry.addNamed(conv, converterSpec.converterName);
                    }
                }
            }
        }
        return registry;
    }

}
