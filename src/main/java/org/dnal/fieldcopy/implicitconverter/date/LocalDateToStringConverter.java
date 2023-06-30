package org.dnal.fieldcopy.implicitconverter.date;

import org.dnal.fieldcopy.implicitconverter.ImplicitConverter;

public class LocalDateToStringConverter implements ImplicitConverter {


    public LocalDateToStringConverter() {
//        this.destPrim = destPrim;
    }

    @Override
    public String gen(String varName) {
        //java defaults to ISO_8601 ISO_DATE
        return String.format("ctx.dateToString(%s)", varName);
    }
}
