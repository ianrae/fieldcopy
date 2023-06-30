package org.dnal.fieldcopy.runtime;

import org.dnal.fieldcopy.Converter;
import org.dnal.fieldcopy.ConverterGroup;
import org.dnal.fieldcopy.FieldCopy;
import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.codegen.gen.CustomerToCustomerConverter;
import org.dnal.fieldcopy.dataclass.Address;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.group.gen.BobCustomerConverter;
import org.dnal.fieldcopy.registry.ICRTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 *
 */
public class FieldCopyTests extends ICRTestBase {

    public static class MyGroup implements ConverterGroup {
        @Override
        public List<ObjectConverter> getConverters() {
            List<ObjectConverter> list = Arrays.asList(new CustomerToCustomerConverter());
            return list;
        }
    }

    @Test
    public void test() {
        Customer src = createSrc();
        Customer dest = new Customer();

        FieldCopy fc = FieldCopy.with(MyGroup.class).build();
        Converter<Customer, Customer> converter = fc.getConverter(Customer.class, Customer.class);

        Customer dest2 = converter.convert(src, dest);

        assertEquals(null, dest.getFirstName()); //not converted
        assertEquals(null, dest.getRoles());
        assertEquals(2, dest.getPoints().size());
        chkInt(35, dest.getPoints(), 0);
        chkInt(37, dest.getPoints(), 1);
        assertSame(dest, dest2);
    }

    @Test
    public void test2() {
        Customer src = createSrc();

        FieldCopy fc = FieldCopy.with(MyGroup.class).build();
        Converter<Customer, Customer> converter = fc.getConverter(Customer.class, Customer.class);

        Customer dest = converter.convert(src, Customer.class);

        assertEquals(null, dest.getFirstName()); //not converted
        assertEquals(null, dest.getRoles());
        assertEquals(2, dest.getPoints().size());
        chkInt(35, dest.getPoints(), 0);
        chkInt(37, dest.getPoints(), 1);
    }

    @Test
    public void testUsing() {
        Customer src = createSrc();
        ObjectConverter namedConverter = new BobCustomerConverter();
        FieldCopy fc = FieldCopy.with(MyGroup.class).usingNamedConverter("MyConv2", namedConverter).build();
        Converter<Customer, Customer> converter = fc.getConverter(Customer.class, Customer.class, "MyConv2");

        Customer dest = converter.convert(src, Customer.class);

        assertEquals("bob", dest.getFirstName());
        assertEquals("0", dest.getLastName()); //not converted
        assertEquals(null, dest.getRoles());
    }

    @Test
    public void testUsingFail() {
        ObjectConverter namedConverter = new BobCustomerConverter();
        FieldCopy fc = FieldCopy.with(MyGroup.class).usingNamedConverter("MyConv2", namedConverter).build();

        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
            Converter<Customer, Customer> converter = fc.getConverter(Customer.class, Customer.class, "UnknownConv");
        });
        chkException(thrown, "No converter with name 'UnknownConv'");
    }

    //============

    private void chkInt(int expected, List<Integer> list, int i) {
        Integer n = list.get(i);
        assertEquals(expected, n.intValue());
    }
    private void chkStr(String expected, List<String> list, int i) {
        String s = list.get(i);
        assertEquals(expected, s);
    }

    private Address createAddress() {
        Address addr = new Address();
        addr.setStreet1("main");
        addr.setCity("kingston");
        return addr;
    }

    private Customer createSrc() {
        Customer cust = new Customer();
        cust.setFirstName("bob");
        cust.setLastName("smith");
        cust.setRoles(Arrays.asList("35", "37"));
        cust.setAddr(createAddress());
        return cust;
    }
}
