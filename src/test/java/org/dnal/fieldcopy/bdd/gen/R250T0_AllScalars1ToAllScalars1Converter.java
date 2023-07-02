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

public class R250T0_AllScalars1ToAllScalars1Converter implements ObjectConverter<AllScalars1, AllScalars1> {

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
    
    // _boolean -> _boolean
    Boolean tmp1 = src._boolean;
    dest._boolean = tmp1;
    
    // _byte -> _byte
    Byte tmp2 = src._byte;
    dest._byte = tmp2;
    
    // _char -> _char
    Character tmp3 = src._char;
    dest._char = tmp3;
    
    // _double -> _double
    Double tmp4 = src._double;
    dest._double = tmp4;
    
    // _float -> _float
    Float tmp5 = src._float;
    dest._float = tmp5;
    
    // _int -> _int
    Integer tmp6 = src._int;
    dest._int = tmp6;
    
    // _long -> _long
    Long tmp7 = src._long;
    dest._long = tmp7;
    
    // _short -> _short
    Short tmp8 = src._short;
    dest._short = tmp8;
    
    // _string -> _string
    String tmp9 = src._string;
    dest._string = tmp9;

    return dest;
  }
}