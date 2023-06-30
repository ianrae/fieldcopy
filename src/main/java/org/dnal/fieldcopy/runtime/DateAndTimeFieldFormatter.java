package org.dnal.fieldcopy.runtime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.isNull;

public class DateAndTimeFieldFormatter {

    private String formatStr; //if null then we use an ISO default such as ISO_DATE
    private DateTimeFormatter dtf = null;
    private DateTimeFormatter dtfDefault;

    public DateAndTimeFieldFormatter(DateTimeFormatter dtfDefault) {
        this.dtfDefault = dtfDefault;
        this.dtf = dtfDefault;
    }

    public String getFormatStr() {
        return formatStr;
    }

    public void setFormatStr(String formatStr) {
        this.formatStr = formatStr;
        if (isNull(formatStr)) {
            this.dtf = dtfDefault;
        }
        this.dtf = DateTimeFormatter.ofPattern(formatStr);
    }

    public String formatDate(LocalDate dt) {
        String s = dtf.format(dt);
        return s;
    }
    public String formatTime(LocalTime dt) {
        String s = dtf.format(dt);
        return s;
    }
    public String formatDateTime(LocalDateTime dt) {
        String s = dtf.format(dt);
        return s;
    }
    public String formatZonedDateTime(ZonedDateTime dt) {
        String s = dtf.format(dt);
        return s;
    }

    public LocalDate parseLocalDate(String str) {
        LocalDate dt = LocalDate.parse(str, dtf);
        return dt;
    }
    public LocalTime parseLocalTime(String str) {
        LocalTime dt = LocalTime.parse(str, dtf);
        return dt;
    }
    public LocalDateTime parseLocalDateTime(String str) {
        LocalDateTime dt = LocalDateTime.parse(str, dtf);
        return dt;
    }
    public ZonedDateTime parseZonedDateTime(String str) {
        ZonedDateTime dt = ZonedDateTime.parse(str, dtf);
        return dt;
    }
}
