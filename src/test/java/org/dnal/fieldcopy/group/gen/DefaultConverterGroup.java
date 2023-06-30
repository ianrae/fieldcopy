package org.dnal.fieldcopy.group.gen;

import org.dnal.fieldcopy.Converter;
import org.dnal.fieldcopy.ConverterGroup;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import org.dnal.fieldcopy.group.gen.CustomerToCustomerConverter;
import org.dnal.fieldcopy.group.gen.AddressToAddressConverter;

public class DefaultConverterGroup implements ConverterGroup {

  @Override
  public List<ObjectConverter> getConverters() {
    List<ObjectConverter> list = new ArrayList<>();
    list.add(new CustomerToCustomerConverter());
    list.add(new AddressToAddressConverter());
    return list;
  }
}
