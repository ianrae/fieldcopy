package org.dnal.fieldcopy.registry;

import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;

import java.util.Map;

public interface ConverterRegistry {
    <S, T> ObjectConverter<S, T> find(Class<S> srcClass, Class<T> destClass);

    boolean exists(String converterName);

    //note lookup is purely by name. srcClass and destClass only used for type safety
    <S, T> ObjectConverter<S, T> find(Class<S> srcClass, Class<T> destClass, String name); //use when several converters for same src,dest

    <S, T> ObjectConverter<S, T> find(FieldTypeInformation srcInfo, FieldTypeInformation destInfo);

    <S, T> ObjectConverter<S, T> find(Class<S> srcClass, Class<T> destClass, FieldTypeInformation srcInfo, FieldTypeInformation destInfoString, String name); //use when several converters for same src,dest

    int size();

    Map<String, ObjectConverter> getMap();
}
