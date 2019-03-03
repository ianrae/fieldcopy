package org.dnal.fieldcopy.factory;

import java.util.Date;

import org.dnal.fieldcopy.BooleanValue;
import org.dnal.fieldcopy.DateValue;
import org.dnal.fieldcopy.DoubleValue;
import org.dnal.fieldcopy.EnumValue;
import org.dnal.fieldcopy.FieldCopyException;
import org.dnal.fieldcopy.IntegerValue;
import org.dnal.fieldcopy.LongValue;
import org.dnal.fieldcopy.StringValue;
import org.dnal.fieldcopy.Value;

public class DefaultValueFactory implements ValueFactory {
	@Override
	public Value createEmptyValue(Class<?>clazz) {
		if (clazz.isEnum()) {
			@SuppressWarnings("unchecked")
			Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) clazz;
			return new EnumValue<>(enumClass); 
		} else if (clazz.equals(Boolean.class)) {
			return new BooleanValue();
		} else if (clazz.equals(Integer.class)) {
			return new IntegerValue();
		} else if (clazz.equals(Long.class)) {
			return new LongValue();
		} else if (clazz.equals(Double.class)) {
			return new DoubleValue();
		} else if (clazz.equals(Date.class)) {
			return new DateValue();
		} else if (clazz.equals(String.class)) {
			return new StringValue();
		} else {
			throw new FieldCopyException(String.format("unknown class: %s", clazz.getName()));
		}
	}
}