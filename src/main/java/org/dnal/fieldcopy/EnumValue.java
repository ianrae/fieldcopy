package org.dnal.fieldcopy;

public class EnumValue<T extends Enum<?>> extends BaseValue {
	private Class<T> enumClass;

	public EnumValue(Class<T> enumClass) {
		this.enumClass = enumClass;
		if (enumClass == null) {
			throw new FieldCopyException("NULL passed to EnumValue constructor");
		}
		if (! enumClass.isEnum()) {
			String error = String.format("'%s' is not an enum", enumClass.getName());
			throw new FieldCopyException(error);
		}
	}
	public T get() {
		@SuppressWarnings("unchecked")
		T t = (T) rawObject;
		return t;
	}
	public void set(T val) {
		rawObject = val;
	}
	public Class<T> getEnumClass() {
		return enumClass;
	}
	
	@Override
	public int getValueType() {
		return ValueTypes.ENUM;
	}
}