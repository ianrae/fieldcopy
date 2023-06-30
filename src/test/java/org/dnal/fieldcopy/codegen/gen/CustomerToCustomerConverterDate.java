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

public class CustomerToCustomerConverterDate implements ObjectConverter<Customer, Customer> {

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
        LocalDate tmp1 = src.getDate();
        String tmp2 = ctx.dateToString(tmp1);
        dest.setDateStr(tmp2);
        LocalTime tmp4 = src.getTime();
        String tmp5 = ctx.timeToString(tmp4);
        dest.setTimeStr(tmp5);
        LocalDateTime tmp7 = src.getLdt();
        String tmp8 = ctx.dateTimeToString(tmp7);
        dest.setDateTimeStr(tmp8);
        ZonedDateTime tmp10 = src.getZdt();
        String tmp11 = ctx.zonedDateTimeToString(tmp10);
        dest.setZonedDateTimeStr(tmp11);
        return dest;
    }
}
