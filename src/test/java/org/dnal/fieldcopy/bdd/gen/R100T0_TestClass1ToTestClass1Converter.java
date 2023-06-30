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

public class R100T0_TestClass1ToTestClass1Converter implements ObjectConverter<TestClass1, TestClass1> {

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
    
    // 56 -> n1
    int tmp1 = 56;
    dest.n1 = tmp1;
    
    // -57 -> nval
    Integer tmp2 = -57;
    dest.nval = tmp2;
    
    // 'abc' -> s2
    String tmp3 = "abc";
    dest.s2 = tmp3;
    
    // 'RED' -> col1
    Color tmp4 = Color.RED;
    dest.col1 = tmp4;
    
    // '2022-02-28' -> utilDate
    String tmp5 = "2022-02-28";
    Date tmp6 = ctx.toDate(tmp5);
    dest.utilDate = tmp6;
    
    // '2023-02-28T18:30:55' -> ldt
    String tmp7 = "2023-02-28T18:30:55";
    LocalDateTime tmp8 = ctx.toLocalDateTime(tmp7);
    dest.ldt = tmp8;

    return dest;
  }
}
