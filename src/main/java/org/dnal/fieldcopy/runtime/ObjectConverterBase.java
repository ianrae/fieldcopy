package org.dnal.fieldcopy.runtime;

import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;

public abstract class ObjectConverterBase implements ObjectConverter {
    protected FieldTypeInformation srcInfo;
    protected FieldTypeInformation destInfo;

    public ObjectConverterBase(Class<?> srcClass, Class<?> destClass) {
        srcInfo = FieldTypeInformationImpl.create(srcClass);
        destInfo = FieldTypeInformationImpl.create(destClass);
    }

    @Override
    public FieldTypeInformation getSourceFieldTypeInfo() {
        return srcInfo;
    }

    @Override
    public FieldTypeInformation getDestinationFieldTypeInfo() {
        return destInfo;
    }

    public abstract Object convert(Object src, Object dest, ConverterContext ctx);
}
