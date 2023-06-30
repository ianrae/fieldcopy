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
import org.dnal.fieldcopy.dataclass.OptionalTestClass1;
import org.dnal.fieldcopy.dataclass.TestClass1;
import org.dnal.fieldcopy.dataclass.Color;
import java.util.Date;
import java.time.LocalDateTime;

public class R200sub203T0_OptionalTestClass1ToTestClass1Converter implements ObjectConverter<OptionalTestClass1, TestClass1> {

  @Override
  public FieldTypeInformation getSourceFieldTypeInfo() {
      return new FieldTypeInformationImpl(OptionalTestClass1.class);
  }

  @Override
  public FieldTypeInformation getDestinationFieldTypeInfo() {
    return new FieldTypeInformationImpl(TestClass1.class);
  }

  @Override
  public TestClass1 convert(OptionalTestClass1 src, TestClass1 dest, ConverterContext ctx) {
    ctx.throwIfInfiniteLoop(src);
    
    // nval -> nval
    Optional<Integer> tmp1 = src.nval;
    dest.nval = (ctx.isNullOrEmpty(tmp1)) ? null : tmp1.orElse(null);
    
    // s2 -> s2
    Optional<String> tmp2 = src.s2;
    dest.s2 = (ctx.isNullOrEmpty(tmp2)) ? null : tmp2.orElse(null);
    
    // col1 -> col1
    Optional<Color> tmp3 = src.col1;
    dest.col1 = (ctx.isNullOrEmpty(tmp3)) ? null : tmp3.orElse(null);
    
    // utilDate -> utilDate
    Optional<Date> tmp4 = src.utilDate;
    dest.utilDate = (ctx.isNullOrEmpty(tmp4)) ? null : tmp4.orElse(null);
    
    // ldt -> ldt
    Optional<LocalDateTime> tmp5 = src.ldt;
    dest.ldt = (ctx.isNullOrEmpty(tmp5)) ? null : tmp5.orElse(null);

    return dest;
  }
}
