package org.dnal.fieldcopy.runtime;

import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.converter.FCRegistry;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;

import static java.util.Objects.isNull;

public class ConverterContext {
    private FCRegistry fcRegistry;
    private Map<Object, String> runawayMap = new HashMap<>();
    private DateFormatter dateFormatter;
    private RuntimeOptions options;

    public ConverterContext(FCRegistry fcRegistry, RuntimeOptions options) {
        this.fcRegistry = fcRegistry;
        this.options = options;
        this.dateFormatter = new DateFormatter(options);
    }

    public <S, T> ObjectConverter<S, T> locate(Class<?> srcClass, Class<T> destClass, String converterName) {
        ObjectConverter cc = fcRegistry.find(srcClass, destClass, converterName);
        if (isNull(cc)) {
            //TODO fix msg. won't be good for lists
            String msg = String.format("No converter for %s -> %s", srcClass.getName(), destClass.getName());
            throw new FieldCopyException(msg);
        }
        return cc;
    }
    public <S, T> ObjectConverter<S, T> locate(Class<?> srcClass, Class<T> destClass) {
        FieldTypeInformation srcInfo = FieldTypeInformationImpl.create(srcClass);
        FieldTypeInformation destInfo = FieldTypeInformationImpl.create(destClass);
        ObjectConverter cc = fcRegistry.find(srcInfo, destInfo);
        if (isNull(cc)) {
            //TODO fix msg. won't be good for lists
            String msg = String.format("No converter for %s -> %s", srcClass.getName(), destClass.getName());
            throw new FieldCopyException(msg);
        }
        return cc;
    }
    public <S, T> ObjectConverter<S, T> locate(FieldTypeInformation srcInfo, FieldTypeInformation destInfo) {
        ObjectConverter cc = fcRegistry.find(srcInfo, destInfo);
        if (isNull(cc)) {
            //TODO fix msg. won't be good for lists
            Class<?> srcClass = srcInfo.getEffectiveType();
            Class<?> destClass = destInfo.getEffectiveType();
            String msg = String.format("No converter for %s -> %s", srcClass.getName(), destClass.getName());
            throw new FieldCopyException(msg);
        }
        return cc;
    }

    public FieldTypeInformation buildFldInfo(Class<?> clazz) {
        return new FieldTypeInformationImpl(clazz);
    }

    public void throwIfInfiniteLoop(Object src) {
        if (isNull(src)) return;
        if (runawayMap.containsKey(src)) {
            String msg = String.format("Infinite loop detected with src object '%s'", src.getClass().getName());
            throw new FieldCopyException(msg);
        }
        runawayMap.put(src, "");
    }

    public void throwUnexpectedNullError(FieldTypeInformation srcFieldInfo, String fieldName) {
        String msg = String.format("Encountered a NULL value for required field %s.%s", srcFieldInfo.getFieldType().getName(), fieldName);
        throw new FieldCopyException(msg);

    }

    public DateFormatter getDateFormatter() {
        return dateFormatter;
    }

    public void setDateFormatOptions(DateFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    public String dateToString(LocalDate dt) {
        return dateFormatter.localDateToString(dt);
    }

    public String timeToString(LocalTime dt) {
        return dateFormatter.localTimeToString(dt);
    }

    public String dateTimeToString(LocalDateTime ldt) {
        return dateFormatter.localDateTimeToString(ldt);
    }

    public String zonedDateTimeToString(ZonedDateTime zdt) {
        return dateFormatter.zonedDateTimeToString(zdt);
    }

    public LocalDate toLocalDate(String str) {
        return dateFormatter.parseLocalDate(str);
    }

    public LocalTime toLocalTime(String str) {
        return dateFormatter.parseLocalTime(str);
    }

    public LocalDateTime toLocalDateTime(String str) {
        return dateFormatter.parseLocalDateTime(str);
    }

    public ZonedDateTime toZonedDateTime(String str) {
        return dateFormatter.parseZonedDateTime(str);
    }

    public String dateToString(Date dt) {
        return dateFormatter.dateToString(dt);
    }
    public Date toDate(String str) {
        return dateFormatter.parseDate(str);
    }

    //null-safe way of creating list that is a copy of srcList
    public <T> List<T> createEmptyList(List<T> srcList, Class<?> elementClass) {
        if (isNull(srcList)) {
            return null;
        }
        return new ArrayList<>(srcList);
    }

    public boolean isNullOrEmpty(Optional<?> opt) {
        if (isNull(opt)) return true;
        return !opt.isPresent();
    }
}
