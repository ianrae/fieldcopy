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
import org.dnal.fieldcopy.dataclass.TestClass1;
import java.util.List;

public class R1300sub1301T0_CustomerToTestClass1Converter implements ObjectConverter<Customer, TestClass1> {

  @Override
  public FieldTypeInformation getSourceFieldTypeInfo() {
      return new FieldTypeInformationImpl(Customer.class);
  }

  @Override
  public FieldTypeInformation getDestinationFieldTypeInfo() {
    return new FieldTypeInformationImpl(TestClass1.class);
  }

  @Override
  public TestClass1 convert(Customer src, TestClass1 dest, ConverterContext ctx) {
    ctx.throwIfInfiniteLoop(src);
    
    // roles -> nval
    List<String> tmp1 = src.getRoles() == null ? null : ctx.createEmptyList(src.getRoles(), String.class);
    if (tmp1 != null) {
    ObjectConverter<List<String>,Integer> conv2 = ctx.locate(FieldTypeInformationImpl.createForList(String.class), FieldTypeInformationImpl.create(Integer.class));
    Integer tmp3 = conv2.convert(tmp1, null, ctx);
    dest.nval = tmp3;
    }

    return dest;
  }
}
