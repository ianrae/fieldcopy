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

public class R500T0_Src1ToDest1Converter implements ObjectConverter<Src1, Dest1> {

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
    
    // n1 -> n1
    int tmp1 = src.getN1();
    dest.setN1(tmp1);
    
    // s2 -> s2
    String tmp2 = src.getS2();
    dest.setS2(tmp2);
    
    // col1 -> col1 default(BLUE)
    Color tmp3 = src.getCol1();
    if (tmp3 == null) tmp3 = Color.BLUE;
    dest.setCol1(tmp3);

    return dest;
  }
}
