package org.dnal.fieldcopy.bdd.gen;

import org.dnal.fieldcopy.Converter;
import org.dnal.fieldcopy.ConverterGroup;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import org.dnal.fieldcopy.bdd.gen.R1300sub1306T0_OptionalSrc1ToOptionalSrc1Converter;

public class DefaultConverterGroup implements ConverterGroup {

  @Override
  public List<ObjectConverter> getConverters() {
    List<ObjectConverter> list = new ArrayList<>();
    list.add(new R1300sub1306T0_OptionalSrc1ToOptionalSrc1Converter());
    return list;
  }
}
