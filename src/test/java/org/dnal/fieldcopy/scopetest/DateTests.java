package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.dnal.fieldcopy.scope.core.MyRunner;
import org.dnal.fieldcopy.scope.core.Scope;
import org.dnal.fieldcopy.scopetest.data.AllTypesEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(MyRunner.class)
@Scope("Date")
public class DateTests extends BaseScopeTest {
	
	@Test
	@Scope("values")
	public void test() {
		doCopy("date1");
		chkValue(testDate());
		
		reset();
		entity.setDate1(this.createDate(2021, 1, 31));
		doCopy("date1");
		chkValue(createDate(2021, 1, 31));
	}
	
	@Test
	@Scope("null")
	public void testNull() {
		entity.setDouble1(null);
		doCopy("double1");
		assertEquals(null, dto.getDouble1());
	}
	
	
	//----------- Double ------------
	@Test
	@Scope("Boolean")
	public void testToBoolean() {
		copySrcFieldToFail(mainField, "primitiveBool");
		assertEquals(false, dto.isPrimitiveBool());
	}
	@Test
	@Scope("Integer")
	public void testToInt() {
		copySrcFieldToFail(mainField, "primitiveInt");
		assertEquals(0, dto.getPrimitiveInt());
	}
	@Test
	@Scope("Long")
	public void testToLong() {
		copySrcFieldTo(mainField, "primitiveLong");
		Date dt = testDate();
		assertEquals(dt.getTime(), dto.getPrimitiveLong());
	}
	@Test
	@Scope("Double")
	public void testToDouble() {
		copySrcFieldToFail(mainField, "primitiveDouble");
	}
	@Test
	@Scope("String")
	public void testToString() {
		copySrcFieldTo(mainField, "string1");
		//TODO: need a date-to-string string converter
		assertEquals("Fri Dec 25 07:30:41 EST 2015", dto.getString1());
	}
	@Test
	@Scope("Date")
	public void testToDate() {
		copySrcFieldTo(mainField, "date1");
		Date dt = testDate();
		assertEquals(dt, dto.getDate1());
	}
	@Test
	@Scope("enum")
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
	
	
	protected void chkValue(Date dt) {
		assertEquals(dt, dto.getDate1());
	}
}
