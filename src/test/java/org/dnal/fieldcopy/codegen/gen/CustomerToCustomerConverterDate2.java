package org.dnal.fieldcopy.codegen.gen;

import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public class CustomerToCustomerConverterDate2 implements ObjectConverter<Customer, Customer> {

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
        String tmp1 = src.getDateStr();
        LocalDate tmp2 = ctx.toLocalDate(tmp1);
        dest.setDate(tmp2);
        String tmp4 = src.getTimeStr();
        LocalTime tmp5 = ctx.toLocalTime(tmp4);
        dest.setTime(tmp5);
        String tmp7 = src.getDateTimeStr();
        LocalDateTime tmp8 = ctx.toLocalDateTime(tmp7);
        dest.setLdt(tmp8);
        String tmp10 = src.getZonedDateTimeStr();
        ZonedDateTime tmp11 = ctx.toZonedDateTime(tmp10);
        dest.setZdt(tmp11);
        return dest;
    }
}
