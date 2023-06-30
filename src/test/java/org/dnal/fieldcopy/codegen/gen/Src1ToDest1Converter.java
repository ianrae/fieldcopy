package org.dnal.fieldcopy.codegen.gen;

import org.dnal.fieldcopy.dataclass.Dest1;
import org.dnal.fieldcopy.dataclass.Src1;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;

public class Src1ToDest1Converter implements ObjectConverter<Src1, Dest1> {

    @Override
    public FieldTypeInformation getSourceFieldTypeInfo() {
        return new FieldTypeInformationImpl(Src1.class);
    }

    @Override
    public FieldTypeInformation getDestinationFieldTypeInfo() {
        return new FieldTypeInformationImpl(Dest1.class);
    }


    @Override
    public Dest1 convert(Src1 src, Dest1 dest, ConverterContext ctx) {
        ctx.throwIfInfiniteLoop(src);
        int tmp1 = src.getN1();
        dest.setN1(tmp1);
        String tmp2 = src.getS2();
        dest.setS2(tmp2);
        return dest;
    }
}
