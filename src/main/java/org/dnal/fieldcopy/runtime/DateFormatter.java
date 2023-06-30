package org.dnal.fieldcopy.runtime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateFormatter {

    private final RuntimeOptions options;

    private DateAndTimeFieldFormatter dateFormatter;
    private DateAndTimeFieldFormatter timeFormatter;
    private DateAndTimeFieldFormatter dateTimeFormatter;
    private DateAndTimeFieldFormatter zonedDateTimeFormatter;
    private UtilDateFieldFormatter utilDateFormatter;

    public DateFormatter(RuntimeOptions options) {
        this.options = options;
        dateFormatter = new DateAndTimeFieldFormatter(DateTimeFormatter.ISO_DATE);
        timeFormatter = new DateAndTimeFieldFormatter(DateTimeFormatter.ISO_TIME);
        dateTimeFormatter = new DateAndTimeFieldFormatter(DateTimeFormatter.ISO_DATE_TIME);
        zonedDateTimeFormatter = new DateAndTimeFieldFormatter(DateTimeFormatter.ISO_ZONED_DATE_TIME);

        if (options.localDateFormatter != null) {
            dateFormatter = new DateAndTimeFieldFormatter(options.localDateFormatter);
        }
        if (options.localTimeFormatter != null) {
            timeFormatter = new DateAndTimeFieldFormatter(options.localTimeFormatter);
        }
        if (options.localDateTimeFormatter != null) {
            dateTimeFormatter = new DateAndTimeFieldFormatter(options.localDateTimeFormatter);
        }
        if (options.zonedDateTimeFormatter != null) {
            zonedDateTimeFormatter = new DateAndTimeFieldFormatter(options.zonedDateTimeFormatter);
        }
        if (options.utilDateFormatter != null) {
            utilDateFormatter = new UtilDateFieldFormatter(options.utilDateFormatter);
        }
    }


    public DateAndTimeFieldFormatter getDateFormatter() {
        return dateFormatter;
    }

    public DateAndTimeFieldFormatter getTimeFormatter() {
        return timeFormatter;
    }

    public DateAndTimeFieldFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public DateAndTimeFieldFormatter getZonedDateTimeFormatter() {
        return zonedDateTimeFormatter;
    }

    public String localDateToString(LocalDate dt) {
        return dateFormatter.formatDate(dt);
    }

    public LocalDate parseLocalDate(String str) {
        return dateFormatter.parseLocalDate(str);
    }

    public String localTimeToString(LocalTime dt) {
        return timeFormatter.formatTime(dt);
    }

    public LocalTime parseLocalTime(String str) {
        return timeFormatter.parseLocalTime(str);
    }

    public String localDateTimeToString(LocalDateTime ldt) {
        return dateTimeFormatter.formatDateTime(ldt);
    }

    public LocalDateTime parseLocalDateTime(String str) {
        return dateTimeFormatter.parseLocalDateTime(str);
    }

    public String zonedDateTimeToString(ZonedDateTime zdt) {
        return zonedDateTimeFormatter.formatZonedDateTime(zdt);
    }

    public ZonedDateTime parseZonedDateTime(String str) {
        return zonedDateTimeFormatter.parseZonedDateTime(str);
    }

    public String dateToString(Date dt) {
        if (utilDateFormatter != null) {
            return utilDateFormatter.format(dt);
        }
        LocalDateTime ldt = dt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return dateTimeFormatter.formatDateTime(ldt);
    }

    public Date parseDate(String s) {
        if (utilDateFormatter != null) {
            return utilDateFormatter.parse(s);
        }
        LocalDateTime ldt = null;
        try {
            ldt = dateTimeFormatter.parseLocalDateTime(s);
            ZoneId zoneId = ZoneId.systemDefault();
            Date dt = Date.from(ldt.atZone(zoneId).toInstant());
            return dt;
        } catch (DateTimeParseException e) {
            return parseDateOnly(s);
        }
    }

    //will throw exception if not in date-only format
    private Date parseDateOnly(String s) {
        LocalDate ldt = dateFormatter.parseLocalDate(s);
        ZoneId zoneId = ZoneId.systemDefault();
        Date dt = Date.from(ldt.atStartOfDay().atZone(zoneId).toInstant());
        return dt;
    }
}