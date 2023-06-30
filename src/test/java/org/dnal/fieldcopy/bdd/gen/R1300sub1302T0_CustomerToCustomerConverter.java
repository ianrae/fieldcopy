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
import java.util.List;

public class R1300sub1302T0_CustomerToCustomerConverter implements ObjectConverter<Customer, Customer> {

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
    
    // roles -> points
    List<String> tmp1 = src.getRoles() == null ? null : ctx.createEmptyList(src.getRoles(), String.class);
    if (tmp1 != null) {
    ObjectConverter<List<String>,List<Integer>> conv2 = ctx.locate(FieldTypeInformationImpl.createForList(String.class), FieldTypeInformationImpl.createForList(Integer.class));
    List<Integer> tmp3 = conv2.convert(tmp1, new ArrayList(), ctx);
    dest.setPoints(tmp3);
    }

    return dest;
  }
}
