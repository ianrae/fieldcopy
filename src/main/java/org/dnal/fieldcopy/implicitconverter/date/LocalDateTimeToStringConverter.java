package org.dnal.fieldcopy.implicitconverter.date;

import org.dnal.fieldcopy.implicitconverter.ImplicitConverter;

public class LocalDateTimeToStringConverter implements ImplicitConverter {

    @Override
    public String gen(String varName) {
        //java defaults to ISO_8601 ISO_DATE
        return String.format("ctx.dateTimeToString(%s)", varName);
    }
}
