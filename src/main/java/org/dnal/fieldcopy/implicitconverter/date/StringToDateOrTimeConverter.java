package org.dnal.fieldcopy.implicitconverter.date;

import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.implicitconverter.ImplicitConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

public class StringToDateOrTimeConverter implements ImplicitConverter {
    private Class<?> clazz; //eg LocalDate

    public StringToDateOrTimeConverter(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String gen(String varName) {
        if (isMatch(LocalDate.class)) {
            return String.format("ctx.toLocalDate(%s)", varName);
        } else if (isMatch(LocalTime.class)) {
            return String.format("ctx.toLocalTime(%s)", varName);
        } else if (isMatch(LocalDateTime.class)) {
            return String.format("ctx.toLocalDateTime(%s)", varName);
        } else if (isMatch(ZonedDateTime.class)) {
            return String.format("ctx.toZonedDateTime(%s)", varName);
        } else if (isMatch(Date.class)) {
            return String.format("ctx.toDate(%s)", varName);
        } else {
            throw new FieldCopyException(String.format("unknown class %s", clazz.getName()));
        }
    }

    private boolean isMatch(Class<?> target) {
        return (target.equals(clazz));
    }
}
