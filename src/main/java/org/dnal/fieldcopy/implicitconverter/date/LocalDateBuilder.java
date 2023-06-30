package org.dnal.fieldcopy.implicitconverter.date;

import org.dnal.fieldcopy.implicitconverter.ImplicitConverter;

import java.time.LocalDate;

public class LocalDateBuilder extends DateBuilderBase {

    public LocalDateBuilder() {
        super(LocalDate.class);
    }

    @Override
    protected ImplicitConverter createConverter() {
        return new LocalDateToStringConverter();
    }
}
