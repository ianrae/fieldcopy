package org.dnal.fieldcopy.runtime;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 * Options used by converters at runtime
 */
public class RuntimeOptions {
    /**
     * if false then you MUST set all the format field fields (ie. ending ing "Fmt" to a non-null value.
     * if true then format fields can be null or be a non-null value to override ISO formatting
     */
    public boolean useIsoDateAndTimeFormats = true;

    public SimpleDateFormat utilDateFormatter;
    public DateTimeFormatter localDateFormatter;
    public DateTimeFormatter localTimeFormatter;
    public DateTimeFormatter localDateTimeFormatter;
    public DateTimeFormatter zonedDateTimeFormatter;

    public void setLocalDateFormat(String fmtStr) {
        this.localDateFormatter = DateTimeFormatter.ofPattern(fmtStr);
    }
    public void setLocalTimeFormat(String fmtStr) {
        this.localTimeFormatter = DateTimeFormatter.ofPattern(fmtStr);
    }
    public void setLocalDateTimeFormat(String fmtStr) {
        this.localDateTimeFormatter = DateTimeFormatter.ofPattern(fmtStr);
    }
    public void setZonedDateTimeFormat(String fmtStr) {
        this.localDateTimeFormatter = DateTimeFormatter.ofPattern(fmtStr);
    }
    public void setUtilDateFormat(String fmtStr) {
        this.utilDateFormatter = new SimpleDateFormat(fmtStr);
    }
}
