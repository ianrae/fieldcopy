package org.dnal.fieldcopy.factory;

import org.dnal.fieldcopy.Value;

public interface ValueFactory {
	Value createEmptyValue(Class<?>clazz);
}