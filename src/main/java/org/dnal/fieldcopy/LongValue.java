package org.dnal.fieldcopy;

public class LongValue extends BaseValue {
	public Long get() {
		Long n = (Long) rawObject;
		return n;
	}
	public void set(Long val) {
		rawObject = val;
	}
	
	public long getLong() {
		Long n = (Long) rawObject;
		return n;
	}
	@Override
	public int getValueType() {
		return ValueTypes.LONG;
	}
}