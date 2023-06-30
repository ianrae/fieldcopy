package org.dnal.fieldcopy.implicitconverter.date;

import org.dnal.fieldcopy.implicitconverter.ImplicitConverter;

import java.time.ZonedDateTime;

public class ZonedDateTimeBuilder extends DateBuilderBase {

    public ZonedDateTimeBuilder() {
        super(ZonedDateTime.class);
    }

    @Override
    protected ImplicitConverter createConverter() {
        return new ZonedDateTimeToStringConverter();
    }

}
