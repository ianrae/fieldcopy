package org.dnal.fieldcopy;

public class StructValue<T> extends BaseValue {
	private Class<T> structClass;

	public StructValue(Class<T> structClass) {
		this.structClass = structClass;
		if (structClass == null) {
			throw new FieldCopyException("NULL passed to StructValue constructor");
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
	public Class<T> getStructClass() {
		return structClass;
	}
	
	@Override
	public int getValueType() {
		return ValueTypes.STRUCT;
	}
}