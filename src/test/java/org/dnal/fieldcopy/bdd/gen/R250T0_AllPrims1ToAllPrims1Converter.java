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
import org.dnal.fieldcopy.dataclass.AllPrims1;
import org.dnal.fieldcopy.dataclass.AllPrims1;

public class R250T0_AllPrims1ToAllPrims1Converter implements ObjectConverter<AllPrims1, AllPrims1> {

  @Override
  public FieldTypeInformation getSourceFieldTypeInfo() {
      return new FieldTypeInformationImpl(AllPrims1.class);
  }

  @Override
  public FieldTypeInformation getDestinationFieldTypeInfo() {
    return new FieldTypeInformationImpl(AllPrims1.class);
  }

  @Override
  public AllPrims1 convert(AllPrims1 src, AllPrims1 dest, ConverterContext ctx) {
    ctx.throwIfInfiniteLoop(src);
    
    // _boolean -> _boolean
    boolean tmp1 = src._boolean;
    dest._boolean = tmp1;
    
    // _byte -> _byte
    byte tmp2 = src._byte;
    dest._byte = tmp2;
    
    // _char -> _char
    char tmp3 = src._char;
    dest._char = tmp3;
    
    // _double -> _double
    double tmp4 = src._double;
    dest._double = tmp4;
    
    // _float -> _float
    float tmp5 = src._float;
    dest._float = tmp5;
    
    // _int -> _int
    int tmp6 = src._int;
    dest._int = tmp6;
    
    // _long -> _long
    long tmp7 = src._long;
    dest._long = tmp7;
    
    // _short -> _short
    short tmp8 = src._short;
    dest._short = tmp8;

    return dest;
  }
}
