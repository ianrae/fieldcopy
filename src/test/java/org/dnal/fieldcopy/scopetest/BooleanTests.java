package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;


public class BooleanTests extends BaseScopeTest {
	
	@Test
	public void test() {
		doCopy("primitiveBool","bool1");
		chkValue(true, true);
		
		reset();
		entity.setPrimitiveBool(false);
		entity.setBool1(false);
		doCopy("primitiveBool","bool1");
		chkValue(false, false);
	}
	
	@Test
	public void testNull() {
		entity.setBool1(null);
		doCopy("bool1");
		assertEquals(null, dto.getBool1());
	}
	
	//----------- primitive ------------
	@Test
	public void testPrimitiveToBoolean() {
		copySrcFieldTo(primitiveField, "bool1");
		assertEquals(true, dto.getBool1().booleanValue());
	}
	@Test
	public void testPrimitiveToInt() {
		copySrcFieldTo(primitiveField, "primitiveInt");
		assertEquals(1, dto.getPrimitiveInt());
		
		copySrcFieldTo(primitiveField, "int1");
		assertEquals(1, dto.getInt1().intValue());
	}
	@Test
	public void testPrimitiveToLong() {
		copySrcFieldTo(primitiveField, "primitiveLong");
		assertEquals(1L, dto.getPrimitiveLong());
		
		copySrcFieldTo(primitiveField, "long1");
		assertEquals(1L, dto.getLong1().longValue());
	}
	@Test
	public void testPrimitiveToDouble() {
		copySrcFieldTo(primitiveField, "primitiveDouble");
		assertEquals(1.0, dto.getPrimitiveDouble(), 0.001);
		
		copySrcFieldTo(primitiveField, "double1");
		assertEquals(1.0, dto.getDouble1(), 0.001);
	}
	@Test
	public void testPrimitiveToString() {
		copySrcFieldTo(primitiveField, "string1");
		assertEquals("true", dto.getString1());
		
		reset();
		entity.setPrimitiveBool(false);
		copySrcFieldTo(primitiveField, "string1", false);
		assertEquals("false", dto.getString1());
	}
	@Test
	public void testPrimitiveToDate() {
		copySrcFieldToFail(primitiveField, "date1");
		assertEquals(null, dto.getDate1());
	}
	@Test
	public void testPrimitiveToEnum() {
		copySrcFieldToFail(primitiveField, "colour1");
		assertEquals(null, dto.getColour1());
	}
	
	//----------- Boolean ------------
	@Test
	public void testToBoolean() {
		copySrcFieldTo(mainField, "primitiveBool");
		assertEquals(true, dto.isPrimitiveBool());
	}
	@Test
	public void testToInt() {
		copySrcFieldTo(mainField, "primitiveInt");
		assertEquals(1, dto.getPrimitiveInt());
		
		copySrcFieldTo(mainField, "int1");
		assertEquals(1, dto.getInt1().intValue());
	}
	@Test
	public void testToLong() {
		copySrcFieldTo(mainField, "primitiveLong");
		assertEquals(1L, dto.getPrimitiveLong());
		
		copySrcFieldTo(mainField, "long1");
		assertEquals(1L, dto.getLong1().longValue());
	}
	@Test
	public void testToDouble() {
		copySrcFieldTo(mainField, "primitiveDouble");
		assertEquals(1.0, dto.getPrimitiveDouble(), 0.001);
		
		copySrcFieldTo(mainField, "double1");
		assertEquals(1.0, dto.getDouble1(), 0.001);
	}
	@Test
	public void testToString() {
		copySrcFieldTo(mainField, "string1");
		assertEquals("true", dto.getString1());
		
		reset();
		entity.setBool1(false);
		copySrcFieldTo(mainField, "string1", false);
		assertEquals("false", dto.getString1());
	}
	@Test
	public void testToDate() {
		copySrcFieldToFail(mainField, "date1");
		assertEquals(null, dto.getDate1());
	}
	@Test
	public void testToEnum() {
		copySrcFieldToFail(mainField, "colour1");
		assertEquals(null, dto.getColour1());
	}
	
	//---
	private static final String primitiveField = "primitiveBool";
	private static final String mainField = "bool1";
	
	@Before
	public void init() {
		super.init();
	}
	@Override
	protected AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		entity.primitiveBool = true;
		entity.bool1 = true;
		
		return entity;
	}
	
	protected void chkValue(boolean bPrim, boolean b2) {
		assertEquals(bPrim, dto.isPrimitiveBool());
		assertEquals(b2, dto.getBool1().booleanValue());
		assertEquals(null, dto.getString1());
	}
}
