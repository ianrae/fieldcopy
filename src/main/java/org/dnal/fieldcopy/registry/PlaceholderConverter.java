package org.dnal.fieldcopy.registry;

import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;

//we never inovke this converter. we just use it to track the presence of a converter defined in the convlang file
public class PlaceholderConverter implements ObjectConverter<DummyClass, DummyClass> {
    private Class<?> srcClass;
    private Class<?> destClass;
    private FieldTypeInformation srcInfo;
    private FieldTypeInformation destInfo;

    public PlaceholderConverter(Class<?> srcClass, Class<?> destClass) {
        this.srcClass = srcClass;
        this.destClass = destClass;
        this.srcInfo = new FieldTypeInformationImpl(srcClass);
        this.destInfo = new FieldTypeInformationImpl(destClass);
    }

    //use these
    public Class<?> getSourceClassEx() {
        return srcClass;
    }

    public Class<?> getDestinationClassEx() {
        return destClass;
    }

    //don't use these
//    @Override
//    public Class<DummyClass> getSourceClass() {
//        return (Class<DummyClass>) srcClass;
//    }
//
//    @Override
//    public Class<DummyClass> getDestinationClass() {
//        return (Class<DummyClass>) destClass;
//    }

    @Override
    public FieldTypeInformation getSourceFieldTypeInfo() {
        return srcInfo;
    }

    @Override
    public FieldTypeInformation getDestinationFieldTypeInfo() {
        return destInfo;
    }

    @Override
    public DummyClass convert(DummyClass src, DummyClass dest, ConverterContext ctx) {
        return dest;
    }
}
