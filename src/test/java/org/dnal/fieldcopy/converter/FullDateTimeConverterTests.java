package org.dnal.fieldcopy.converter;

import org.dnal.fieldcopy.builder.PrimsBuilder;
import org.dnal.fieldcopy.codegen.gen.CustomerToCustomerConverterDate;
import org.dnal.fieldcopy.codegen.gen.CustomerToCustomerConverterDate2;
import org.dnal.fieldcopy.dataclass.Address;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.registry.ICRTestBase;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.RuntimeOptions;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 *
 */
public class FullDateTimeConverterTests extends ICRTestBase {

    @Test
    public void testToString() {
        Customer src = createSrc();
        Customer dest = new Customer();

        CustomerToCustomerConverterDate converter = new CustomerToCustomerConverterDate();
        ConverterContext ctx = new ConverterContext(new FCRegistry(), options);
        Customer dest2 = converter.convert(src, dest, ctx);

        assertEquals(expectedDate, dest.getDateStr());
        assertEquals(expectedTime, dest.getTimeStr());
        assertEquals(expectedDateTime, dest.getDateTimeStr());
        assertEquals(expectedZonedDateTime, dest.getZonedDateTimeStr()); //TODO fix timezone
        assertSame(dest, dest2);
    }

    @Test
    public void testFromString() {
        Customer src = createSrcReverse();
        Customer dest = new Customer();

        CustomerToCustomerConverterDate2 converter = new CustomerToCustomerConverterDate2();
        ConverterContext ctx = new ConverterContext(new FCRegistry(), options);
        Customer dest2 = converter.convert(src, dest, ctx);

        LocalDate dt1 = LocalDate.parse(expectedDate, DateTimeFormatter.ISO_DATE);
        LocalTime dt2 = LocalTime.parse(expectedTime, DateTimeFormatter.ISO_TIME);
        LocalDateTime dt3 = LocalDateTime.parse(expectedDateTime, DateTimeFormatter.ISO_DATE_TIME);
        ZonedDateTime dt4 = ZonedDateTime.parse(expectedZonedDateTime, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        assertEquals(dt1, dest.getDate());
        assertEquals(dt2, dest.getTime());
        assertEquals(dt3, dest.getLdt());
        assertEquals(dt4, dest.getZdt()); //TODO fix timezone
        assertSame(dest, dest2);
    }

    //============
    private PrimsBuilder primsBuilder = new PrimsBuilder();
    private String expectedDate = "2022-02-28";
    private String expectedTime = "18:30:55";
    private String expectedDateTime = "2023-02-28T18:30:55";
    private String expectedZonedDateTime = "2023-02-28T18:30:55-05:00[America/New_York]";
    private RuntimeOptions options = new RuntimeOptions();


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

        cust.setDate(LocalDate.of(2022, 2, 28));
        cust.setTime(LocalTime.of(18, 30, 55));
        LocalDateTime ldt = LocalDateTime.of(2023, 02, 28, 18, 30, 55);
        cust.setLdt(ldt);
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
        cust.setZdt(zdt);

        return cust;
    }

    private Customer createSrcReverse() {
        Customer cust = new Customer();
        cust.setFirstName("bob");
        cust.setLastName("smith");
        cust.setRoles(Arrays.asList("35", "37"));
        cust.setAddr(createAddress());

        cust.setDateStr(expectedDate);
        cust.setTimeStr(expectedTime);
        cust.setDateTimeStr(expectedDateTime);
        cust.setZonedDateTimeStr(expectedZonedDateTime);

        return cust;
    }

}
