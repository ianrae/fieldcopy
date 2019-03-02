package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;


public class EnumTests extends BaseScopeTest {
	
	@Test
	public void test() {
		doCopy("colour1");
		chkValue(Colour.GREEN);
		
		reset();
		entity.setColour1(Colour.RED);
		doCopy("colour1");
		chkValue(Colour.RED);
	}
	
	@Test
	public void testNull() {
		entity.setColour1(null);
		doCopy("colour1");
		assertEquals(null, dto.getColour1());
	}
	
	
	//----------- Enum ------------
	@Test
	public void testToBoolean() {
		copySrcFieldToFail(mainField, "primitiveBool");
		assertEquals(false, dto.isPrimitiveBool());
	}
	@Test
	public void testToInt() {
		copySrcFieldToFail(mainField, "primitiveInt");
	}
	@Test
	public void testToLong() {
		copySrcFieldToFail(mainField, "primitiveLong");
	}
	@Test
	public void testToDouble() {
		copySrcFieldToFail(mainField, "primitiveDouble");
	}
	@Test
	public void testToString() {
		copySrcFieldTo(mainField, "string1");
		assertEquals("GREEN", dto.getString1());
	}
	@Test
	public void testToDate() {
		copySrcFieldToFail(mainField, "date1");
	}
	@Test
	public void testToEnum() {
		copySrcFieldTo(mainField, "colour1");
		assertEquals(Colour.GREEN, dto.getColour1());
		
		copySrcFieldToFail(mainField, "province1");
		
		reset();
		entity.setColour1(Colour.BLUE);
		copySrcFieldToFail(mainField, "province1", false);
	}
	
	//---
	private static final String mainField = "colour1";
	
	@Before
	public void init() {
		super.init();
	}
	@Override
	protected AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		entity.setColour1(Colour.GREEN);
		
		return entity;
	}
	
	protected void chkValue(Colour col) {
		assertEquals(col, dto.getColour1());
	}
}
