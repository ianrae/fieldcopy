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

public class R700T1_Src1ToDest1Converter implements ObjectConverter<Src1, Dest1> {

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
    
    // s2 -> col1
    String tmp1 = src.getS2();
    Color tmp2 = Enum.valueOf(org.dnal.fieldcopy.dataclass.Color.class, tmp1);
    dest.setCol1(tmp2);
    
    // inner1 -> inner1
    Inner1 tmp3 = src.getInner1();
    dest.setInner1(tmp3);
    
    // n1 -> n1
    int tmp4 = src.getN1();
    dest.setN1(tmp4);

    return dest;
  }
}
