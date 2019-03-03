package org.dnal.fieldcopy;

public class StringValue extends BaseValue {
	public String get() {
		return rawObject.toString();
	}
	public void set(String val) {
		rawObject = val;
	}
	@Override
	public int getValueType() {
		return ValueTypes.STRING;
	}
}