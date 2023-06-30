//package org.dnal.fieldcopy.fluent;
//
//import org.dnal.fieldcopy.ConverterGroup;
//import org.dnal.fieldcopy.FieldCopy;
//import org.dnal.fieldcopy.converter.FCRegistry;
//import org.dnal.fieldcopy.runtime.ObjectConverter;
//import org.dnal.fieldcopy.util.ReflectionUtil;
//
//import static java.util.Objects.isNull;
//
///**
// * Fluent builder class
// */
//public class FieldCopyBuilder {
//
//    private final Class<? extends ConverterGroup> groupClass;
//    private final ConverterGroup converterGroup;
//    private final ReflectionUtil helper;
//
//    public FieldCopyBuilder(Class<? extends ConverterGroup> groupClass) {
//        this.groupClass = groupClass;
//        this.helper = new ReflectionUtil();
//        this.converterGroup = createObj(groupClass);
//    }
//
//    private ConverterGroup createObj(Class<? extends ConverterGroup> groupClass) {
//        ConverterGroup obj = (ConverterGroup) helper.createObj(groupClass);
//        return obj;
//    }
//
//    public FieldCopy build() {
//        return new FieldCopy(converterGroup);
//    }
//
//}
