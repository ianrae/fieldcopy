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
import org.dnal.fieldcopy.dataclass.TestClass1;
import org.dnal.fieldcopy.dataclass.TestClass1;
import org.dnal.fieldcopy.dataclass.Color;
import java.util.Date;
import java.time.LocalDateTime;

public class R100sub101T0_TestClass1ToTestClass1Converter implements ObjectConverter<TestClass1, TestClass1> {

  @Override
  public FieldTypeInformation getSourceFieldTypeInfo() {
      return new FieldTypeInformationImpl(TestClass1.class);
  }

  @Override
  public FieldTypeInformation getDestinationFieldTypeInfo() {
    return new FieldTypeInformationImpl(TestClass1.class);
  }

  @Override
  public TestClass1 convert(TestClass1 src, TestClass1 dest, ConverterContext ctx) {
    ctx.throwIfInfiniteLoop(src);
    
    // null -> nval
    Integer tmp1 = null;
    dest.nval = tmp1;
    
    // null -> s2
    String tmp2 = null;
    dest.s2 = tmp2;
    
    // null -> col1
    Color tmp3 = null;
    dest.col1 = tmp3;
    
    // null -> utilDate
    Date tmp4 = null;
    dest.utilDate = tmp4;
    
    // null -> ldt
    LocalDateTime tmp5 = null;
    dest.ldt = tmp5;

    return dest;
  }
}
