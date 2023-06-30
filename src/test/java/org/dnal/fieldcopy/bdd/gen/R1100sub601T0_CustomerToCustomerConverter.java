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
import org.dnal.fieldcopy.dataclass.Address;

public class R1100sub601T0_CustomerToCustomerConverter implements ObjectConverter<Customer, Customer> {

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
    
    // firstName -> firstName
    String tmp1 = src.getFirstName();
    dest.setFirstName(tmp1);
    
    // addr.city -> lastName
    Address tmp2 = src.getAddr();
    String tmp3 = tmp2.getCity();
    dest.setLastName(tmp3);

    return dest;
  }
}
