package org.dnal.fieldcopy.converter;

import org.dnal.fieldcopy.builder.PrimsBuilder;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.codegen.gen.*;
import org.dnal.fieldcopy.dataclass.Address;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.registry.ICRTestBase;
import org.dnal.fieldcopy.runtime.RuntimeOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 *
 */
public class FullConverterTests extends ICRTestBase {


    @Test
    public void test() {
        Customer src = createSrc();
        Customer dest = new Customer();

        CustomerToCustomerConverter converter = new CustomerToCustomerConverter();
        ConverterContext ctx = new ConverterContext(new FCRegistry(), new RuntimeOptions());
        Customer dest2 = converter.convert(src, dest, ctx);

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
        Customer dest = new Customer();

        CustomerToCustomerConverter2 converter = new CustomerToCustomerConverter2();
        ConverterContext ctx = new ConverterContext(new FCRegistry(), new RuntimeOptions());
        Customer dest2 = converter.convert(src, dest, ctx);

        assertEquals("bob", dest.getFirstName());
        assertEquals("smith", dest.getLastName());
        assertEquals(null, dest.getRoles());
        assertEquals(null, dest.getPoints());
        assertSame(dest, dest2);

        assertEquals("kingston", dest.getAddr().getCity());
    }

    @Test
    public void testSubObjFailNoConv() {
        Customer src = createSrc();
        Customer dest = new Customer();

        CustomerToCustomerConverter3 converter = new CustomerToCustomerConverter3();
        ConverterContext ctx = new ConverterContext(new FCRegistry(), new RuntimeOptions());

        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
            Customer dest2 = converter.convert(src, dest, ctx);
        });
        log(thrown.getMessage());
        chkException(thrown, "No converter for org.dnal.fieldcopy.dataclass.Address -> org.dnal.fieldcopy.dataclass.Address");
    }

    @Test
    public void testSubObj() {
        Customer src = createSrc();
        Customer dest = new Customer();

        FCRegistry fcRegistry = new FCRegistry();
        fcRegistry.add(new AddressToAddressConverter());

        CustomerToCustomerConverter3 converter = new CustomerToCustomerConverter3();
        ConverterContext ctx = new ConverterContext(fcRegistry, new RuntimeOptions());

        Customer dest2 = converter.convert(src, dest, ctx);

        assertEquals("bob", dest.getFirstName());
        assertEquals(null, dest.getRoles());
        assertEquals("kingston", dest.getAddr().getCity());
        assertSame(dest, dest2);
    }
    @Test
    public void testBackRef() {
        Customer src = createSrc();
        src.getAddr().setBackRef(src); //not a good idea!
        Customer dest = new Customer();

        CustomerToCustomerConverter3 converter = new CustomerToCustomerConverter3();

        FCRegistry fcRegistry = new FCRegistry();
        fcRegistry.add(converter); //needed for backref
        fcRegistry.add(new AddressToAddressConverterBackRef());
        ConverterContext ctx = new ConverterContext(fcRegistry, new RuntimeOptions());

        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
            Customer dest2 = converter.convert(src, dest, ctx);
        });
        log(thrown.getMessage());
        chkException(thrown, "Infinite loop detected with src object 'org.dnal.fieldcopy.dataclass.Customer'");
    }

    //============
    private PrimsBuilder primsBuilder = new PrimsBuilder();
    private String outDir = "C:/tmp/fieldcopy2/gen";

    private void chkInt(int expected, List<Integer> list, int i) {
        Integer n = list.get(i);
        assertEquals(expected, n.intValue());
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
