package org.dnal.fieldcopy.codegen.gen;

import org.dnal.fieldcopy.dataclass.Address;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;

public class CustomerToCustomerConverter3 implements ObjectConverter<Customer, Customer> {

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
        if (tmp3 != null) {
            ObjectConverter<Address, Address> conv4 = ctx.locate(FieldTypeInformationImpl.create(Address.class), FieldTypeInformationImpl.create(Address.class));
            Address tmp5 = conv4.convert(tmp3, new Address(), ctx);
            dest.setAddr(tmp5);
        }
        return dest;
    }
}
