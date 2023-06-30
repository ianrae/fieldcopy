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

public class R900sub901T0_CustomerToCustomerConverter implements ObjectConverter<Customer, Customer> {

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
    
    // date -> date
    LocalDate tmp1 = src.getDate();
    dest.setDate(tmp1);
    
    // time -> time
    LocalTime tmp2 = src.getTime();
    dest.setTime(tmp2);
    
    // date -> firstName
    LocalDate tmp3 = src.getDate();
    String tmp4 = ctx.dateToString(tmp3);
    dest.setFirstName(tmp4);
    
    // time -> lastName
    LocalTime tmp5 = src.getTime();
    String tmp6 = ctx.timeToString(tmp5);
    dest.setLastName(tmp6);
    
    // utilDate -> dateStr
    Date tmp7 = src.getUtilDate();
    String tmp8 = ctx.dateToString(tmp7);
    dest.setDateStr(tmp8);

    return dest;
  }
}
