package org.dnal.fieldcopy.codegen;

import org.dnal.fieldcopy.fieldspec.FldChain;
import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.fieldspec.SingleValue;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.runtime.DateFormatter;
import org.dnal.fieldcopy.runtime.RuntimeOptions;
import org.dnal.fieldcopy.runtime.RuntimeOptionsHelper;
import org.dnal.fieldcopy.types.ClassTypeHelper;
import org.dnal.fieldcopy.util.ReflectionUtil;
import org.dnal.fieldcopy.util.StringUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class FldXValidator {
    private final FieldCopyOptions options;
    private ReflectionUtil refHelper = new ReflectionUtil();

    public FldXValidator(FieldCopyOptions options) {
        this.options = options;
    }

    public boolean validate(FldChain fldChain) {
        for(SingleFld fld: fldChain.flds) {
            if (fld instanceof SingleValue) {
                validateValueFld((SingleValue) fld);
            }
        }
        return true;
    }


    //validation. During codegen we can validate values somewhat to give early error detection.
    //eg. '2022-08-22' -> someDate may be invalid for given date format
    private void validateValueFld(SingleValue fld) {
        Class<?> clazz = fld.fieldTypeInfo.getEffectiveType();
        if (ClassTypeHelper.isDateType(clazz)) {
            validateDateValue(clazz, fld);
        }

        //we dont' validate numbers,bools, enums, etc because if the values are not syntactically correct
        //The code generation will produce Java code with compile erorrs that the compiler and IDE will notice.
    }

    private void validateDateValue(Class<?> clazz, SingleValue fld) {
        if (! options.validateDateAndTimeValues) return;

        //TODO validate value against new DateFormatter(options);
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        RuntimeOptionsHelper runtimeOptionsHelper = new RuntimeOptionsHelper();
        runtimeOptionsHelper.loadFromFieldCopyOptions(options, runtimeOptions);

        DateFormatter dateFormatter = new DateFormatter(runtimeOptions);

        String dateStr = StringUtil.removeDoubleQuotes(fld.fieldName);
        //this will throw java.time.format.DateTimeParseException is parsing fails
        //For now let's not wrap this in FieldCopy

        try {
            if (Date.class.isAssignableFrom(clazz)) {
                Date dt = dateFormatter.parseDate(dateStr);
            } else if (LocalDate.class.isAssignableFrom(clazz)) {
                LocalDate dt = dateFormatter.parseLocalDate(dateStr);
            } else if (LocalTime.class.isAssignableFrom(clazz)) {
                LocalTime dt = dateFormatter.parseLocalTime(dateStr);
            } else if (LocalDateTime.class.isAssignableFrom(clazz)) {
                LocalDateTime dt = dateFormatter.parseLocalDateTime(dateStr);
            } else if (ZonedDateTime.class.isAssignableFrom(clazz)) {
                ZonedDateTime dt = dateFormatter.parseZonedDateTime(dateStr);
            }
        } catch (DateTimeParseException e) {
            //TODO later include convert lang src such as "'202222-02-28' -> myDate"
            String msg = String.format("%s: %s", clazz.getSimpleName(), e.getMessage());
            throw new FieldCopyException(msg);
        }
    }
}

