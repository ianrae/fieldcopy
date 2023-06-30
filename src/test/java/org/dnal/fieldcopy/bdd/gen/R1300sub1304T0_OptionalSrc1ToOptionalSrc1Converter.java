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
import org.dnal.fieldcopy.dataclass.OptionalSrc1;
import org.dnal.fieldcopy.dataclass.OptionalSrc1;
import org.dnal.fieldcopy.dataclass.Color;

public class R1300sub1304T0_OptionalSrc1ToOptionalSrc1Converter implements ObjectConverter<OptionalSrc1, OptionalSrc1> {

  @Override
  public FieldTypeInformation getSourceFieldTypeInfo() {
      return new FieldTypeInformationImpl(OptionalSrc1.class);
  }

  @Override
  public FieldTypeInformation getDestinationFieldTypeInfo() {
    return new FieldTypeInformationImpl(OptionalSrc1.class);
  }

  @Override
  public OptionalSrc1 convert(OptionalSrc1 src, OptionalSrc1 dest, ConverterContext ctx) {
    ctx.throwIfInfiniteLoop(src);
    
    // s2 -> n1 using(conv1)
    Optional<String> tmp1 = src.s2;
    if (tmp1 != null) {
    ObjectConverter<String,Integer> conv2 = ctx.locate(String.class, Integer.class, "conv1");
    Integer tmp3 = conv2.convert(tmp1.get(), null, ctx);
    dest.n1 = Optional.ofNullable(tmp3);
    }
    
    // col1 -> col1
    Optional<Color> tmp4 = src.col1;
    dest.col1 = tmp4;

    return dest;
  }
}
