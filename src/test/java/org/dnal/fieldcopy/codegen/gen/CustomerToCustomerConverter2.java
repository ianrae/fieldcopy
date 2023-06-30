package org.dnal.fieldcopy.codegen.gen;

import org.dnal.fieldcopy.dataclass.Address;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;

public class CustomerToCustomerConverter2 implements ObjectConverter<Customer, Customer> {

    @Override
    public FieldTypeInformation getSourceFieldTypeInfo() {
        return new FieldTypeInformationImpl(Customer.class);
    }

    @Override
    public FieldTypeInformation getDestinationFieldTypeInfo() {
        return new FieldTypeInformationImpl(Customer.class);
    }


    @Override
    public Customer convert(Customer src, Customer dest, ConverterContext ctx) {
        ctx.throwIfInfiniteLoop(src);
        String tmp1 = src.getFirstName();
        dest.setFirstName(tmp1);
        String tmp2 = src.getLastName();
        dest.setLastName(tmp2);
        Address tmp3 = src.getAddr();
        dest.setAddr(tmp3);
        return dest;
    }
}
