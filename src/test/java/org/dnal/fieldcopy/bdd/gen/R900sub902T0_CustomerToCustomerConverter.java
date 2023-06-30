package org.dnal.fieldcopy.bdd.gen;

import org.dnal.fieldcopy.Converter;
import org.dnal.fieldcopy.ConverterGroup;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.dataclass.Customer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class R900sub902T0_CustomerToCustomerConverter implements ObjectConverter<Customer, Customer> {

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
    
    // '28 February 2022' -> date
    String tmp1 = "28 February 2022";
    LocalDate tmp2 = ctx.toLocalDate(tmp1);
    dest.setDate(tmp2);
    
    // '18-30-55' -> time
    String tmp3 = "18-30-55";
    LocalTime tmp4 = ctx.toLocalTime(tmp3);
    dest.setTime(tmp4);
    
    // '31.08.1982 06:20:56' -> utilDate
    String tmp5 = "31.08.1982 06:20:56";
    Date tmp6 = ctx.toDate(tmp5);
    dest.setUtilDate(tmp6);

    return dest;
  }
}
