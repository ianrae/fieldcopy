package org.dnal.fieldcopy.group;

import org.dnal.fieldcopy.runtime.ObjectConverter;

/**
 * Defines a converter that is going to be registered with GroupCodeGenerator
 */
public class ObjectConverterSpec {
    public String converterName; //if null then it's an un-named converter
    public String converterClassName;
    public ObjectConverter<?,?> converter;

    public ObjectConverterSpec(String converterName, String converterClassName) {
        this.converterName = converterName;
        this.converterClassName = converterClassName;
    }

    public ObjectConverterSpec(ObjectConverter<?, ?> converter) {
        if (converter != null) {
            this.converterClassName = converter.getClass().getName();
        }
        this.converter = converter;
    }
}
