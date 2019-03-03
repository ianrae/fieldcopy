package org.dnal.fieldcopy;

public class DoubleValue extends BaseValue {
	public Double get() {
		Double d = (Double) rawObject;
		return d;
	}
	public void set(Double val) {
		rawObject = val;
	}
	
	public double getDouble() {
		Double d = (Double) rawObject;
		return d;
	}
	@Override
	public int getValueType() {
		return ValueTypes.DOUBLE;
	}
}