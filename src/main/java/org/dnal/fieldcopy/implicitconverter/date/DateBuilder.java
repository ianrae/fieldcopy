package org.dnal.fieldcopy.implicitconverter.date;

import org.dnal.fieldcopy.implicitconverter.ImplicitConverter;

import java.util.Date;

public class DateBuilder extends DateBuilderBase {

    public DateBuilder() {
        super(Date.class);
    }

    @Override
    protected ImplicitConverter createConverter() {
        return new DateToStringConverter();
    }

}
