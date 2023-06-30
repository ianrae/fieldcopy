package org.dnal.fieldcopy.implicitconverter.date;

import org.dnal.fieldcopy.implicitconverter.ImplicitConverter;

import java.time.LocalDateTime;

public class LocalDateTimeBuilder extends DateBuilderBase {

    public LocalDateTimeBuilder() {
        super(LocalDateTime.class);
    }

    @Override
    protected ImplicitConverter createConverter() {
        return new LocalDateTimeToStringConverter();
    }

}
