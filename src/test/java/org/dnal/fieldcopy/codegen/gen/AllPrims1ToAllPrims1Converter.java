package org.dnal.fieldcopy.codegen.gen;

import org.dnal.fieldcopy.dataclass.AllPrims1;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;

public class AllPrims1ToAllPrims1Converter implements ObjectConverter<AllPrims1, AllPrims1> {

    @Override
    public FieldTypeInformation getSourceFieldTypeInfo() {
        return new FieldTypeInformationImpl(AllPrims1.class);
    }

    @Override
    public FieldTypeInformation getDestinationFieldTypeInfo() {
        return new FieldTypeInformationImpl(AllPrims1.class);
    }


    @Override
    public AllPrims1 convert(AllPrims1 src, AllPrims1 dest, ConverterContext ctx) {
//    int tmp1 = src.get_int();
//    dest.set_int(tmp1);
        return dest;
    }
}
