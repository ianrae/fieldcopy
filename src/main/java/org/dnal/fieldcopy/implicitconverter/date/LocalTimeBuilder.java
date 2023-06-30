package org.dnal.fieldcopy.implicitconverter.date;

import org.dnal.fieldcopy.implicitconverter.ImplicitConverter;

import java.time.LocalTime;

public class LocalTimeBuilder extends DateBuilderBase {

    public LocalTimeBuilder() {
        super(LocalTime.class);
    }

    @Override
    protected ImplicitConverter createConverter() {
        return new LocalTimeToStringConverter();
    }
}
