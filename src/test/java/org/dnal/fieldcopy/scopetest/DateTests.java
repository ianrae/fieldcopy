package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;


public class DateTests extends BaseScopeTest {
	
	@Test
	public void test() {
		doCopy("date1");
		chkValue(testDate());
		
		reset();
		entity.setDate1(this.createDate(2021, 1, 31));
		doCopy("date1");
		chkValue(createDate(2021, 1, 31));
	}
	
	@Test
	public void testNull() {
		entity.setDouble1(null);
		doCopy("double1");
		assertEquals(null, dto.getDouble1());
	}
	
	
	//----------- Double ------------
	@Test
	public void testToBoolean() {
		copySrcFieldToFail(mainField, "primitiveBool");
		assertEquals(false, dto.isPrimitiveBool());
	}
	@Test
	public void testToInt() {
		copySrcFieldToFail(mainField, "primitiveInt");
		assertEquals(0, dto.getPrimitiveInt());
	}
	@Test
	public void testToLong() {
		copySrcFieldTo(mainField, "primitiveLong");
		Date dt = testDate();
		assertEquals(dt.getTime(), dto.getPrimitiveLong());
	}
	@Test
	public void testToDouble() {
		copySrcFieldToFail(mainField, "primitiveDouble");
	}
	@Test
	public void testToString() {
		copySrcFieldTo(mainField, "string1");
		//TODO: need a date-to-string string converter
		assertEquals("Fri Dec 25 07:30:41 EST 2015", dto.getString1());
	}
	@Test
	public void testToDate() {
		copySrcFieldTo(mainField, "date1");
		Date dt = testDate();
		assertEquals(dt, dto.getDate1());
	}
	@Test
	public void testToEnum() {
		copySrcFieldToFail(mainField, "colour1");
		assertEquals(null, dto.getColour1());
	}
	
	//---
	private static final String mainField = "date1";
	
	@Before
	public void init() {
		super.init();
	}
	@Override
	protected AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		entity.setDate1(testDate());
		
		return entity;
	}
	
	private Date testDate() {
		return createDate(2015,12,25);
	}
	
	
	private Date createDate(int year, int mon, int day) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, mon - 1);
    cal.set(Calendar.DATE, day);
    cal.set(Calendar.HOUR_OF_DAY, 7);
    cal.set(Calendar.MINUTE, 30);
    cal.set(Calendar.SECOND, 41);
    cal.set(Calendar.MILLISECOND, 0);
    Date dt = cal.getTime();
    return dt;
}
	
	protected void chkValue(Date dt) {
		assertEquals(dt, dto.getDate1());
	}
}
