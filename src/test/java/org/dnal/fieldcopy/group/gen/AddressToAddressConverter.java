// ****** This code was generated by the FieldCopy code generation library. *****
package org.dnal.fieldcopy.group.gen;

import org.dnal.fieldcopy.Converter;
import org.dnal.fieldcopy.ConverterGroup;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import org.dnal.fieldcopy.dataclass.Address;
import org.dnal.fieldcopy.dataclass.Address;

public class AddressToAddressConverter implements ObjectConverter<Address, Address> {

  @Override
  public FieldTypeInformation getSourceFieldTypeInfo() {
      return new FieldTypeInformationImpl(Address.class);
  }

  @Override
  public FieldTypeInformation getDestinationFieldTypeInfo() {
    return new FieldTypeInformationImpl(Address.class);
  }

  @Override
  public Address convert(Address src, Address dest, ConverterContext ctx) {
    ctx.throwIfInfiniteLoop(src);
    
    // street1 -> street1
    String tmp1 = src.getStreet1();
    dest.setStreet1(tmp1);
    
    // city -> city
    String tmp2 = src.getCity();
    dest.setCity(tmp2);

    return dest;
  }
}
