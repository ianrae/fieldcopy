package org.dnal.fieldcopy.codegen.gen;

import org.dnal.fieldcopy.dataclass.Address;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;

public class AddressToAddressConverter implements ObjectConverter<Address, Address> {

    @Override
    public FieldTypeInformation getSourceFieldTypeInfo() {
        return new FieldTypeInformationImpl(Address.class);
    }

    @Override
    public FieldTypeInformation getDestinationFieldTypeInfo() {
        return new FieldTypeInformationImpl(Address.class);
    }

    @Override
    public Address convert(Address src, Address dest, ConverterContext ctx) {
        ctx.throwIfInfiniteLoop(src);
        String tmp1 = src.getStreet1();
        dest.setStreet1(tmp1);
        String tmp2 = src.getCity();
        dest.setCity(tmp2);
        return dest;
    }
}
