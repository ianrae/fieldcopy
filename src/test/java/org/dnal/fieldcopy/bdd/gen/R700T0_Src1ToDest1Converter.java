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
import org.dnal.fieldcopy.dataclass.Src1;
import org.dnal.fieldcopy.dataclass.Dest1;
import org.dnal.fieldcopy.dataclass.Color;
import org.dnal.fieldcopy.dataclass.Inner1;

public class R700T0_Src1ToDest1Converter implements ObjectConverter<Src1, Dest1> {

  @Override
  public FieldTypeInformation getSourceFieldTypeInfo() {
      return new FieldTypeInformationImpl(Src1.class);
  }

  @Override
  public FieldTypeInformation getDestinationFieldTypeInfo() {
    return new FieldTypeInformationImpl(Dest1.class);
  }

  @Override
  public Dest1 convert(Src1 src, Dest1 dest, ConverterContext ctx) {
    ctx.throwIfInfiniteLoop(src);
    
    // col1 -> col1
    Color tmp1 = src.getCol1();
    dest.setCol1(tmp1);
    
    // inner1 -> inner1
    Inner1 tmp2 = src.getInner1();
    dest.setInner1(tmp2);
    
    // n1 -> n1
    int tmp3 = src.getN1();
    dest.setN1(tmp3);
    
    // s2 -> s2
    String tmp4 = src.getS2();
    dest.setS2(tmp4);

    return dest;
  }
}
