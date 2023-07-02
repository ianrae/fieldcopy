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
import org.dnal.fieldcopy.dataclass.OptionalTestClass1;
import org.dnal.fieldcopy.dataclass.Color;
import java.util.Date;
import java.time.LocalDateTime;

public class R100sub102T0_OptionalTestClass1ToOptionalTestClass1Converter implements ObjectConverter<OptionalTestClass1, OptionalTestClass1> {

  @Override
  public FieldTypeInformation getSourceFieldTypeInfo() {
      return new FieldTypeInformationImpl(OptionalTestClass1.class);
  }

  @Override
  public FieldTypeInformation getDestinationFieldTypeInfo() {
    return new FieldTypeInformationImpl(OptionalTestClass1.class);
  }

  @Override
  public OptionalTestClass1 convert(OptionalTestClass1 src, OptionalTestClass1 dest, ConverterContext ctx) {
    ctx.throwIfInfiniteLoop(src);
    
    // -57 -> nval
    Integer tmp1 = -57;
    dest.nval = Optional.ofNullable(tmp1);
    
    // 'abc' -> s2
    String tmp2 = "abc";
    dest.s2 = Optional.ofNullable(tmp2);
    
    // 'RED' -> col1
    Color tmp3 = Color.RED;
    dest.col1 = Optional.ofNullable(tmp3);
    
    // '2022-02-28' -> utilDate
    String tmp4 = "2022-02-28";
    Date tmp5 = ctx.toDate(tmp4);
    dest.utilDate = Optional.ofNullable(tmp5);
    
    // '2023-02-28T18:30:55' -> ldt
    String tmp6 = "2023-02-28T18:30:55";
    LocalDateTime tmp7 = ctx.toLocalDateTime(tmp6);
    dest.ldt = Optional.ofNullable(tmp7);

    return dest;
  }
}