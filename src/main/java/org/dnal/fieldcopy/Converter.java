package org.dnal.fieldcopy;

import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.util.ReflectionUtil;

/**
 * The public converter interface used by client code.
 * It is a thin wrapper over ObjectConverter.
 *
 * @param <S> srcClass
 * @param <T> destClass
 */
public class Converter<S, T> {
    private final ConverterContext ctx;
    private ObjectConverter<S, T> innerConverter;

    public Converter(ObjectConverter<S, T> innerConverter, ConverterContext ctx) {
        this.innerConverter = innerConverter;
        this.ctx = ctx;
    }

    public T convert(S src, T dest) {
        return innerConverter.convert(src, dest, ctx);
    }

    public T convert(S src, Class<T> destClass) {
        ReflectionUtil util = new ReflectionUtil();
        T destObj = (T) util.createObj(destClass);
        return innerConverter.convert(src, destObj, ctx);
    }
}
