package org.dnal.fieldcopy;

import org.dnal.fieldcopy.runtime.ObjectConverter;

import java.util.List;

/**
 * A set of related converters.
 * The codegen will generate a converter group class for
 * each ConvLang file.
 */
public interface ConverterGroup {
    List<ObjectConverter> getConverters();
}
