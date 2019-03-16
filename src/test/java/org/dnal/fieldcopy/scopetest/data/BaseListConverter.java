package org.dnal.fieldcopy.scopetest.data;

import java.util.ArrayList;
import java.util.List;

import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.ValueConverter;

public abstract class BaseListConverter implements ValueConverter {
	@Override
	public Object convertValue(Object srcBean, Object value, ConverterContext ctx) {
		@SuppressWarnings("unchecked")
		List<?> list = (List<?>) value;
		
		List<Object> list2 = new ArrayList<>();
		for(Object el: list) {
			Object copy = copyElement(el);
			list2.add(copy);
		}
		return list2;
	}

	protected abstract Object copyElement(Object el);
}