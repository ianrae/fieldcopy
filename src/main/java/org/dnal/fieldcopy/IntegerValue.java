package org.dnal.fieldcopy;

public class IntegerValue extends BaseValue {
	public Integer get() {
		Integer n = (Integer) rawObject;
		return n;
	}
	public void set(Integer val) {
		rawObject = val;
	}
	
	public int getInt() {
		Integer n = (Integer) rawObject;
		return n;
	}
	@Override
	public int getValueType() {
		return ValueTypes.INTEGER;
	}
}