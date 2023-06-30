package org.dnal.fieldcopy.bdd.customconverter;

import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;

import java.util.List;

//used by R1300 tests
public class MyStringListConverter implements ObjectConverter {
    private FieldTypeInformation srcInfo;
    private FieldTypeInformation destInfo;

    public MyStringListConverter() {
        srcInfo = FieldTypeInformationImpl.createForList(String.class);
        destInfo = FieldTypeInformationImpl.create(Integer.class); //TODO support int.class later
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
        int total = 0;
        List<String> list = (List<String>) src;
        for (String s : list) {
            Integer nn = Integer.parseInt(s);
            total += nn.intValue();
        }
        return Integer.valueOf(total);
    }
}
