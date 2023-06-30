package org.dnal.fieldcopy.bdd.customconverter;

import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;

import java.util.ArrayList;
import java.util.List;

public class MyStringListToIntegerListConverter implements ObjectConverter {
    private FieldTypeInformation srcInfo;
    private FieldTypeInformation destInfo;

    public MyStringListToIntegerListConverter() {
        srcInfo = FieldTypeInformationImpl.createForList(String.class);
        destInfo = FieldTypeInformationImpl.createForList(Integer.class);
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
        List<Integer> resultL = new ArrayList<>();
        List<String> list = (List<String>) src;
        for (String s : list) {
            Integer nn = Integer.parseInt(s);
            resultL.add(nn);
        }
        return resultL;
    }

}
