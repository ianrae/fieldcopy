package org.dnal.fieldcopy.service.beanutils;

import java.lang.reflect.Field;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.log.SimpleLogger;

public class BUFieldSetterService {
	protected SimpleLogger logger;

	public BUFieldSetterService(SimpleLogger logger) {
		this.logger = logger;
	}

	/**
	 * @param destObj
	 * @param destFieldName
	 * @param value is not null. 
	 */
	public void setField(Object destObj, String destFieldName, Object value) {
		try {
			Field field = FieldUtils.getField(destObj.getClass(), destFieldName, true);
			Class<?> clazz = field.getType();
			Object objValue = ConvertUtils.convert(value, clazz);
			FieldUtils.writeField(destObj, destFieldName, objValue, true);
		} catch (Exception ex) {
			String err = String.format("setting field '%s' failed. %s", destFieldName, ex.getMessage());
			throw new FieldCopyException(err, ex);
		}
	}
	
	/**
	 * @param destObj
	 * @param destFieldName
	 * @param value  is not null. 
	 */
	public void setFieldFromString(Object destObj, String destFieldName, String value) {
		try {
			Field field = FieldUtils.getField(destObj.getClass(), destFieldName, true);
			Object objValue = convertForField(field, value);
			FieldUtils.writeField(destObj, destFieldName, objValue, true);
		} catch (Exception ex) {
			String err = String.format("setting field '%s' failed. %s", destFieldName, ex.getMessage());
			throw new FieldCopyException(err, ex);
		}
	}

	private Object convertForField(Field field, String value) {
		Class<?> clazz = field.getType();
		return ConvertUtils.convert(value, clazz);
	}
}
