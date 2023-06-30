//package org.dnal.fieldcopy.codegen.gen;
//
//import org.dnal.fieldcopy.Converter;
//import org.dnal.fieldcopy.runtime.ObjectConverter;
//import org.dnal.fieldcopy.runtime.ConverterContext;
//import org.dnal.fieldcopy.codegen.FieldTypeInfo;
//import java.util.Optional;
//import java.util.ArrayList;
//import org.dnal.fieldcopy.dataclass.Customer;
//import org.dnal.fieldcopy.dataclass.Customer;
//import org.dnal.fieldcopy.types.FieldTypeInformation;
//import org.dnal.fieldcopy.types.FieldTypeInformationImpl;
//
//public abstract class CustomerToCustomerConverterCustomBase implements ObjectConverter<Customer, Customer> {
//
//  @Override
//  public FieldTypeInformation getSourceFieldTypeInfo() {
//    return new FieldTypeInformationImpl(Customer.class);
//  }
//
//  @Override
//  public FieldTypeInformation getDestinationFieldTypeInfo() {
//    return new FieldTypeInformationImpl(Customer.class);
//  }
//
//
//  @Override
//  public Customer convert(Customer src, Customer dest, ConverterContext ctx) {
//    ctx.throwIfInfiniteLoop(src);
//    String tmp1 = src.getFirstName();
//    String tmp2 = convertFirstName(tmp1, src, dest, ctx);
//    dest.setFirstName(tmp2);
//
//    return dest;
//  }
//  protected abstract String convertFirstName(String srcValue, Customer src, Customer dest, ConverterContext ctx);
//}
