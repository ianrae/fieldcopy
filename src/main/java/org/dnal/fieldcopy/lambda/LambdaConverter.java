package org.dnal.fieldcopy.lambda;

import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.FieldInfo;
import org.dnal.fieldcopy.converter.ValueConverter;

public class LambdaConverter<T> implements ValueConverter {
	private Class<T> clazz;
	private LambdaCallback<T> zz;
	private String srcFieldName;
	private boolean notNullFlag;
	private String destFieldName;

	public LambdaConverter(Class<T> clazz, String srcFieldName, String destFieldName, boolean notNullFlag, LambdaCallback<T> zz) {
		this.clazz = clazz;
		this.srcFieldName = srcFieldName;
		this.destFieldName = destFieldName;
		this.notNullFlag = notNullFlag;
		this.zz = zz;
	}

	@Override
	public boolean canConvert(FieldInfo source, FieldInfo dest) {
		if (source.beanClass.equals(clazz)) {
			if (srcFieldName != null && source.matches(srcFieldName)) {
				return true;
			} else if (destFieldName != null && dest.matches(destFieldName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object convertValue(Object srcBean, Object value, ConverterContext ctx) {
		@SuppressWarnings("unchecked")
		T bean = (T) srcBean;
		if (value == null && notNullFlag) {
			return null;
		}
		Object result = zz.exec(bean);
		return result;
	}
	
}