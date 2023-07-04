package org.dnal.fieldcopy.fluent;

import org.dnal.fieldcopy.ConverterGroup;
import org.dnal.fieldcopy.runtime.RuntimeOptions;

/**
 * Fluent builder class
 */
public class FieldCopyBuilder {

    public static FCFluent1 with(Class<? extends ConverterGroup> groupClass) {
        FCFluent1 fluent1 = new FCFluent1(groupClass, new RuntimeOptions());
        return fluent1;
    }

    public static FCFluent1 with(Class<? extends ConverterGroup> groupClass, RuntimeOptions options) {
        FCFluent1 fluent1 = new FCFluent1(groupClass, options);
        return fluent1;
    }
}
