package org.dnal.fieldcopy.codegen.gen;

import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;

import java.util.ArrayList;
import java.util.List;

public class CustomerToCustomerConverter implements ObjectConverter<Customer, Customer> {

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
        List<String> tmp1 = src.getRoles();
        List<Integer> list2 = new ArrayList<>();
        for (String el3 : tmp1) {
            Integer tmp4 = Integer.parseInt(el3);
            list2.add(tmp4);
        }
        dest.setPoints(list2);
        return dest;
    }
}
