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
import org.dnal.fieldcopy.dataclass.OptionalTestClass1;
import org.dnal.fieldcopy.dataclass.Color;
import java.util.Date;
import java.time.LocalDateTime;

public class R200sub204T0_TestClass1ToOptionalTestClass1Converter implements ObjectConverter<TestClass1, OptionalTestClass1> {

  @Override
  public FieldTypeInformation getSourceFieldTypeInfo() {
      return new FieldTypeInformationImpl(TestClass1.class);
  }

  @Override
  public FieldTypeInformation getDestinationFieldTypeInfo() {
    return new FieldTypeInformationImpl(OptionalTestClass1.class);
  }

  @Override
  public OptionalTestClass1 convert(TestClass1 src, OptionalTestClass1 dest, ConverterContext ctx) {
    ctx.throwIfInfiniteLoop(src);
    
    // nval -> nval
    Integer tmp1 = src.nval;
    dest.nval = Optional.ofNullable(tmp1);
    
    // s2 -> s2
    String tmp2 = src.s2;
    dest.s2 = Optional.ofNullable(tmp2);
    
    // col1 -> col1
    Color tmp3 = src.col1;
    dest.col1 = Optional.ofNullable(tmp3);
    
    // utilDate -> utilDate
    Date tmp4 = src.utilDate;
    dest.utilDate = Optional.ofNullable(tmp4);
    
    // ldt -> ldt
    LocalDateTime tmp5 = src.ldt;
    dest.ldt = Optional.ofNullable(tmp5);

    return dest;
  }
}
