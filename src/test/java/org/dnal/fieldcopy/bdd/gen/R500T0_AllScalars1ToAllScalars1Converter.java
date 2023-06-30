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
import org.dnal.fieldcopy.dataclass.AllScalars1;
import org.dnal.fieldcopy.dataclass.AllScalars1;

public class R500T0_AllScalars1ToAllScalars1Converter implements ObjectConverter<AllScalars1, AllScalars1> {

  @Override
  public FieldTypeInformation getSourceFieldTypeInfo() {
      return new FieldTypeInformationImpl(AllScalars1.class);
  }

  @Override
  public FieldTypeInformation getDestinationFieldTypeInfo() {
    return new FieldTypeInformationImpl(AllScalars1.class);
  }

  @Override
  public AllScalars1 convert(AllScalars1 src, AllScalars1 dest, ConverterContext ctx) {
    ctx.throwIfInfiniteLoop(src);
    
    // _int -> _int default(-45)
    Integer tmp1 = src._int;
    if (tmp1 == null) tmp1 = -45;
    dest._int = tmp1;
    
    // _long -> _long default(9223372036854775807)
    Long tmp2 = src._long;
    if (tmp2 == null) tmp2 = 9223372036854775807L;
    dest._long = tmp2;
    
    // _float -> _float default(-45.76)
    Float tmp3 = src._float;
    if (tmp3 == null) tmp3 = -45.76f;
    dest._float = tmp3;
    
    // _boolean -> _boolean default(true)
    Boolean tmp4 = src._boolean;
    if (tmp4 == null) tmp4 = true;
    dest._boolean = tmp4;
    
    // _char -> _char default('Z')
    Character tmp5 = src._char;
    if (tmp5 == null) tmp5 = 'Z';
    dest._char = tmp5;
    
    // _string -> _string default('abc')
    String tmp6 = src._string;
    if (tmp6 == null) tmp6 = "abc";
    dest._string = tmp6;

    return dest;
  }
}
