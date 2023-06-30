package org.dnal.fieldcopy;

import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.converter.FCRegistry;
import org.dnal.fieldcopy.fluent.FCFluent1;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.runtime.RuntimeOptions;
import org.dnal.fieldcopy.types.FieldTypeInformation;

import static java.util.Objects.isNull;

/**
 * The main client class.
 * Use this to configure and create converters.
 */
public class FieldCopy {

    private final ConverterGroup converterGroup;
    private final FCRegistry registry;
    private final RuntimeOptions options;

    public FieldCopy(ConverterGroup converterGroup) {
        this(converterGroup, new RuntimeOptions(), null);
    }

    public FieldCopy(ConverterGroup converterGroup, RuntimeOptions options, FCRegistry namedConverters) {
        this.registry = new FCRegistry();
        this.registry.addAll(converterGroup.getConverters());
        if (namedConverters != null) {
            registry.addNamedFromOtherRegistry(namedConverters);
        }
        this.converterGroup = converterGroup;
        this.options = options;
    }

    public <S, T> Converter getConverter(Class<S> srcClass, Class<T> destClass) {
        ObjectConverter conv = registry.find(srcClass, destClass);
        if (isNull(conv)) {
            //TODO fix msg. won't be good for lists
            String msg = String.format("No converter for %s -> %s", srcClass.getName(), destClass.getName());
            throw new FieldCopyException(msg);
        }
        ConverterContext ctx = new ConverterContext(registry, options);
        Converter converter = new Converter(conv, ctx);

        return converter;
    }
    public <S, T> Converter getConverter(Class<S> srcClass, Class<T> destClass, String converterName) {
        ObjectConverter conv = registry.find(srcClass, destClass, converterName);
        if (isNull(conv)) {
            String msg = String.format("No converter with name '%s'", converterName);
            throw new FieldCopyException(msg);
        }
        ConverterContext ctx = new ConverterContext(registry, options);
        Converter converter = new Converter(conv, ctx);

        return converter;
    }

    public <S, T> Converter getConverter(FieldTypeInformation srcFti, FieldTypeInformation destFti) {
        ObjectConverter conv = registry.find(srcFti, destFti);
        if (isNull(conv)) {
            //TODO fix msg. won't be good for lists
            String msg = String.format("No converter for %s -> %s", srcFti.getEffectiveType().getName(), destFti.getEffectiveType().getName());
            throw new FieldCopyException(msg);
        }
        ConverterContext ctx = new ConverterContext(registry, options);
        Converter converter = new Converter(conv, ctx);

        return converter;
    }

    public static FCFluent1 with(Class<? extends ConverterGroup> groupClass) {
        FCFluent1 fluent1 = new FCFluent1(groupClass, new RuntimeOptions());
        return fluent1;
    }

    public static FCFluent1 with(Class<? extends ConverterGroup> groupClass, RuntimeOptions options) {
        FCFluent1 fluent1 = new FCFluent1(groupClass, options);
        return fluent1;
    } //RuntimeOptions options

    public ConverterGroup getConverterGroup() {
        return converterGroup;
    }

    public FCRegistry getRegistry() {
        return registry;
    }

    public RuntimeOptions getOptions() {
        return options;
    }
}
