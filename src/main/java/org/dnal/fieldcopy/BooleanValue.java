package org.dnal.fieldcopy;

public class BooleanValue extends BaseValue {
	public Boolean get() {
		Boolean b = (Boolean) rawObject;
		return b;
	}
	public void set(Boolean val) {
		rawObject = val;
	}
	@Override
	public int getValueType() {
		return ValueTypes.BOOLEAN;
	}
}