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

public class R200sub202T0_TestClass1ToTestClass1Converter implements ObjectConverter<TestClass1, TestClass1> {

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
    
    // n1 -> n1
    int tmp1 = src.n1;
    dest.n1 = tmp1;
    
    // nval -> nval
    Integer tmp2 = src.nval;
    dest.nval = tmp2;
    
    // s2 -> s2
    String tmp3 = src.s2;
    dest.s2 = tmp3;
    
    // col1 -> col1
    Color tmp4 = src.col1;
    dest.col1 = tmp4;
    
    // utilDate -> utilDate
    Date tmp5 = src.utilDate;
    dest.utilDate = tmp5;
    
    // ldt -> ldt
    LocalDateTime tmp6 = src.ldt;
    dest.ldt = tmp6;

    return dest;
  }
}
