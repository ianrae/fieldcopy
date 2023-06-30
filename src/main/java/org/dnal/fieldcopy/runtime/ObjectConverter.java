package org.dnal.fieldcopy.runtime;

import org.dnal.fieldcopy.types.FieldTypeInformation;

/**
 * The actual converter that converts an S object to a T object.
 *
 * @param <S> source object class
 * @param <T> destination object class
 */
public interface ObjectConverter<S, T> {
    FieldTypeInformation getSourceFieldTypeInfo();

    FieldTypeInformation getDestinationFieldTypeInfo();

    T convert(S src, T dest, ConverterContext ctx);
}
