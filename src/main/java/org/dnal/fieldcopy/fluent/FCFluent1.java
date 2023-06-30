package org.dnal.fieldcopy.fluent;

import org.dnal.fieldcopy.ConverterGroup;
import org.dnal.fieldcopy.FieldCopy;
import org.dnal.fieldcopy.converter.FCRegistry;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.runtime.RuntimeOptions;
import org.dnal.fieldcopy.runtime.RuntimeOptionsHelper;
import org.dnal.fieldcopy.util.ReflectionUtil;

import static java.util.Objects.isNull;

public class FCFluent1 {
    private final Class<? extends ConverterGroup> groupClass;
    private RuntimeOptions options;
    private final ConverterGroup converterGroup;
    private FCRegistry namedConverters;
    private final ReflectionUtil helper;

    public FCFluent1(Class<? extends ConverterGroup> groupClass, RuntimeOptions options) {
        this.groupClass = groupClass;
        this.options = options;
        this.helper = new ReflectionUtil();
        this.converterGroup = createObj(groupClass);
    }
    private ConverterGroup createObj(Class<? extends ConverterGroup> groupClass) {
        ConverterGroup obj = (ConverterGroup) helper.createObj(groupClass);
        return obj;
    }

    public FCFluent1 usingNamedConverter(String name, ObjectConverter converterName) {
        if (isNull(namedConverters)) {
            namedConverters = new FCRegistry();
        }
        namedConverters.addNamed(converterName, name);
        return this;
    }

    public FCFluent1 options(RuntimeOptions options) {
        this.options = options;
        return this;
    }
    public FCFluent1 loadOptionsFromConfig(FieldCopyOptions config) {
        RuntimeOptionsHelper helper = new RuntimeOptionsHelper();
        helper.loadFromFieldCopyOptions(config, options);
        return this;
    }

    public FieldCopy build() {
        return new FieldCopy(converterGroup, options, namedConverters);
    }

}
