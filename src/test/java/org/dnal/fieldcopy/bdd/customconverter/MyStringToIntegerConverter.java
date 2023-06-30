package org.dnal.fieldcopy.bdd.customconverter;

import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;
import org.dnal.fieldcopy.types.TypeTree;

import java.util.Optional;

//used by R1300 tests
public class MyStringToIntegerConverter implements ObjectConverter {
    private FieldTypeInformation srcInfo;
    private FieldTypeInformation destInfo;

    public MyStringToIntegerConverter() {
        srcInfo = FieldTypeInformationImpl.create(String.class);
        destInfo = FieldTypeInformationImpl.create(Integer.class);
    }

    @Override
    public FieldTypeInformation getSourceFieldTypeInfo() {
        return srcInfo;
    }

    @Override
    public FieldTypeInformation getDestinationFieldTypeInfo() {
        return destInfo;
    }

    @Override
    public Object convert(Object src, Object dest, ConverterContext ctx) {
        String s = (String) src;
//            Integer nval = (Integer) dest;
        Integer nn = Integer.parseInt(s);
        return nn;
//            return Optional.of(nn);
    }
}
