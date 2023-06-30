package org.dnal.fieldcopy.bdd.customconverter;

import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;

import java.util.ArrayList;
import java.util.List;

public class MyStringToStringListConverter implements ObjectConverter {
    private FieldTypeInformation srcInfo;
    private FieldTypeInformation destInfo;

    public MyStringToStringListConverter() {
        srcInfo = FieldTypeInformationImpl.create(String.class);
        destInfo = FieldTypeInformationImpl.createForList(String.class);
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
        List<String> resultL = new ArrayList<>();
        String s = (String) src;
        resultL.add(s + "x");
        return resultL;
    }

}
