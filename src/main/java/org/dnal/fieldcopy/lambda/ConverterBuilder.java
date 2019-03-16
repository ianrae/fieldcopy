package org.dnal.fieldcopy.lambda;

public class ConverterBuilder {
	public static <T> ConvBuilder1<T> whenSource(Class<T> clazz, String fieldName) {
		return new ConvBuilder1<T>(clazz, fieldName);
	}
	public static <T> ConvBuilder2<T> whenSource(Class<T> clazz) {
		return new ConvBuilder2<T>(clazz);
	}
}