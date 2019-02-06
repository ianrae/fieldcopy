package org.dnal.fieldcopy;

import java.util.Date;

public class DateValue extends BaseValue {
	public Date get() {
		Date dt = (Date) rawObject;
		return dt;
	}
	public void set(Date val) {
		rawObject = val;
	}
	
	@Override
	public int getValueType() {
		return ValueTypes.DATE;
	}
}