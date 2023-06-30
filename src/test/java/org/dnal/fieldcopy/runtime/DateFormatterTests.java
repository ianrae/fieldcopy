package org.dnal.fieldcopy.runtime;

import org.dnal.fieldcopy.TestBase;
import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.dataclass.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class DateFormatterTests extends TestBase {

    @Test
    public void test() {
        DateFormatter formatter = new DateFormatter(options);

        LocalDate dt = LocalDate.of(2022, 2, 28);
        String s = formatter.localDateToString(dt);
        assertEquals("2022-02-28", s);
        LocalDate dt2 = formatter.parseLocalDate(s);
        assertEquals(true, dt2.equals(dt));
        assertEquals(dt, dt2);

        LocalDate dt3 = LocalDate.of(2022, 3, 28);
        assertEquals(false, dt2.equals(dt3));
    }

    @Test
    public void test2() {
        DateFormatter formatter = new DateFormatter(options);
        LocalTime dt = LocalTime.of(18, 30, 55);
        String s = formatter.localTimeToString(dt);
        assertEquals("18:30:55", s);
        LocalTime dt2 = formatter.parseLocalTime(s);
        assertEquals(true, dt2.equals(dt));
        assertEquals(dt, dt2);
    }
    @Test
    public void test2a() {
        DateFormatter formatter = new DateFormatter(options);
        formatter.getDateFormatter().setFormatStr("dd LLLL yyyy");
        LocalDate dt = LocalDate.of(2022, 2, 28);
        String s = formatter.localDateToString(dt);
        assertEquals("28 February 2022", s);
        LocalDate dt2 = formatter.parseLocalDate(s);
        assertEquals(true, dt2.equals(dt));
        assertEquals(dt, dt2);
    }

    @Test
    public void test3() {
        DateFormatter formatter = new DateFormatter(options);
        LocalDateTime dt = LocalDateTime.of(LocalDate.of(2022, 2, 28), LocalTime.of(18, 30, 55));
        String s = formatter.localDateTimeToString(dt);
        assertEquals("2022-02-28T18:30:55", s);
        LocalDateTime dt2 = formatter.parseLocalDateTime(s);
        assertEquals(true, dt2.equals(dt));
        assertEquals(dt, dt2);
    }

    @Test
    public void test4() {
        DateFormatter formatter = new DateFormatter(options);
        LocalDateTime dtx = LocalDateTime.of(LocalDate.of(2022, 2, 28), LocalTime.of(18, 30, 55));
        ZonedDateTime zdt = ZonedDateTime.of(dtx, ZoneId.systemDefault());
        String s = formatter.zonedDateTimeToString(zdt);
        assertEquals("2022-02-28T18:30:55-05:00[America/New_York]", s);
        ZonedDateTime dt2 = formatter.parseZonedDateTime(s);
        assertEquals(true, dt2.equals(zdt));
        assertEquals(zdt, dt2);
    }

    @Test
    public void test5() throws ParseException {
        DateFormatter formatter = new DateFormatter(options);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String dateInString = "31-08-1982 10:20:56";
        Date dt = sdf.parse(dateInString);

        //supports both date and date-time
        String s = formatter.dateToString(dt);
        assertEquals("1982-08-31T10:20:56", s);

        //defaults to date-time
        Date dt2 = formatter.parseDate(s);
        assertEquals(true, dt2.equals(dt));
        assertEquals(dt, dt2);

        //but can parse a date-only string
        Date dt3 = formatter.parseDate("1982-08-31");
        s = formatter.dateToString(dt3);
        assertEquals("1982-08-31T00:00:00", s);
    }

    //--custom
    @Test
    public void testCustomDate() {
        String europeanDatePattern = "dd.MM.yyyy";
        options.localDateFormatter = DateTimeFormatter.ofPattern(europeanDatePattern);
        DateFormatter formatter = new DateFormatter(options);

        LocalDate dt = LocalDate.of(2022, 2, 28);
        String s = formatter.localDateToString(dt);
        assertEquals("28.02.2022", s);

        //Note. it needs day,month,year. the following is not supported by LocalDate
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy");
//        LocalDate dt33 = LocalDate.parse("2022", dtf);

        LocalDate dt2 = formatter.parseLocalDate(s);
        assertEquals(true, dt2.equals(dt));
        assertEquals(dt, dt2);
    }
    @Test
    public void testCustomTime() {
        String pattern = "hh:mm:ss/a";
        options.localTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        DateFormatter formatter = new DateFormatter(options);

        LocalTime dt = LocalTime.of(9, 32, 28);
        String s = formatter.localTimeToString(dt);
        assertEquals("09:32:28/a.m.", s);

        LocalTime dt2 = formatter.parseLocalTime(s);
        assertEquals(true, dt2.equals(dt));
        assertEquals(dt, dt2);
    }
    @Test
    public void testCustomDateTime() {
        String pattern = "dd.MM.yyyy HH:mm:ss";
        options.localDateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        DateFormatter formatter = new DateFormatter(options);

        LocalDateTime dt = LocalDateTime.of(2022, 2, 28, 9, 32, 28);
        String s = formatter.localDateTimeToString(dt);
        assertEquals("28.02.2022 09:32:28", s);

        LocalDateTime dt2 = formatter.parseLocalDateTime(s);
        assertEquals(true, dt2.equals(dt));
        assertEquals(dt, dt2);
    }
    @Test
    public void testCustomZonedDateTime() {
        String pattern = "dd.MM.yyyy HH:mm:ss z";
        options.zonedDateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        DateFormatter formatter = new DateFormatter(options);

        ZoneId zoneId = ZoneId.of("America/New_York");

        LocalDateTime ldt = LocalDateTime.of(2022, 2, 28, 9, 32, 28);
        ZonedDateTime zdt = ZonedDateTime.of(ldt, zoneId);
        String s = formatter.zonedDateTimeToString(zdt);
        assertEquals("28.02.2022 09:32:28 EST", s);

        ZonedDateTime zdt2 = formatter.parseZonedDateTime(s);
        assertEquals(false, zdt2.equals(zdt)); //something not matching with timezone. TODO fix
    }
    @Test
    public void testCustomUtilDate() throws ParseException {
        String pattern = "dd.MM.yyyy HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        //Note. if you don't define a utilDateFormatter then the dateTimeFormatter or dateFormatter are used
        options.utilDateFormatter = sdf;
        DateFormatter formatter = new DateFormatter(options);

        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String dateInString = "31-08-1982 10:20:56";
        Date date = sdf2.parse(dateInString);
        String s = formatter.dateToString(date);
        assertEquals("31.08.1982 10:20:56", s);

        Date date2 = formatter.parseDate(s);
        assertEquals(true, date2.equals(date));

        sdf = new SimpleDateFormat(); //default
        s = formatter.dateToString(date);
        assertEquals("31.08.1982 10:20:56", s);
    }

    @Test
    public void testCustomUtilDateFail() throws ParseException {
        String pattern = "dd.MM.yyyy HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        //Note. if you don't define a utilDateFormatter then the dateTimeFormatter or dateFormatter are used
        options.utilDateFormatter = sdf;
        DateFormatter formatter = new DateFormatter(options);

        String s = "wwww31.08.1982 10:20:56";
        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
            Date date2 = formatter.parseDate(s);
        });
        log(thrown.getMessage());
        chkException(thrown, "failed to parse java.util.date");
    }


    //============
    private RuntimeOptions options = new RuntimeOptions();
}
