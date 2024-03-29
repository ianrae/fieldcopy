// ****** This code was generated by the FieldCopy code generation library. *****
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

public class R1100sub603T0_CustomerToCustomerConverter implements ObjectConverter<Customer, Customer> {

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
    
    // firstName -> addr.street1
    String tmp1 = src.getFirstName();
    Address tmp2 = (dest.getAddr() == null) ? new Address() : dest.getAddr();
    dest.setAddr(tmp2);
    tmp2.setStreet1(tmp1);
    
    // lastName -> addr.city
    String tmp3 = src.getLastName();
    Address tmp4 = (dest.getAddr() == null) ? new Address() : dest.getAddr();
    dest.setAddr(tmp4);
    tmp4.setCity(tmp3);

    return dest;
  }
}
