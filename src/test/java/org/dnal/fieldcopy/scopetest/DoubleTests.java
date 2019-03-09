package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.scope.MyRunner;
import org.dnal.fieldcopy.scope.Scope;
import org.dnal.fieldcopy.scopetest.data.AllTypesEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(MyRunner.class)
@Scope("Double")
public class DoubleTests extends BaseScopeTest {
	
	@Test
	@Scope("values")
	public void test() {
		doCopy("primitiveDouble","double1");
		chkValue(123.456, -123.456);
		
		reset();
		entity.setPrimitiveDouble(44.0);
		entity.setDouble1(45.0);
		doCopy("primitiveDouble","double1");
		chkValue(44.0, 45.0);
		
		reset();
		entity.setPrimitiveDouble(Double.MIN_VALUE);
		entity.setDouble1(Double.MIN_VALUE);
		doCopy("primitiveDouble","double1");
		chkValue(Double.MIN_VALUE, Double.MIN_VALUE);
		
		reset();
		entity.setPrimitiveDouble(Double.MAX_VALUE);
		entity.setDouble1(Double.MAX_VALUE);
		doCopy("primitiveDouble","double1");
		chkValue(Double.MAX_VALUE, Double.MAX_VALUE);
	}
	
	@Test
	@Scope("null")
	public void testNull() {
		entity.setDouble1(null);
		doCopy("double1");
		assertEquals(null, dto.getDouble1());
	}
	
	//----------- primitive ------------
	@Test
	@Scope(target="double", value="Boolean")
	public void testPrimitiveToBoolean() {
		copySrcFieldToFail(primitiveField, "primitiveBool");
		assertEquals(false, dto.isPrimitiveBool());
	}
	@Test
	@Scope(target="double", value="Integer")
	public void testPrimitiveToInt() {
		copySrcFieldTo(primitiveField, "int1");
		//TODO: fix this. it simply truncates. should probably fail
		assertEquals(123, dto.getInt1().intValue());
		
		long n = Integer.MAX_VALUE;
		n += 4;
		System.out.println(n);
		reset();
		entity.setPrimitiveDouble(n);
		copySrcFieldTo(primitiveField, "int1", false);
		
		//TODO: fix this. currently sets int1 to 0, but it should throw a ConverterException
//		assertEquals(n, dto.getInt1().intValue());
		assertEquals(0, dto.getInt1().intValue());
	}
	@Test
	@Scope(target="double", value="Long")
	public void testPrimitiveToLong() {
		copySrcFieldTo(primitiveField, "long1");
		assertEquals(123L, dto.getLong1().longValue());
	}
	@Test
	@Scope(target="double", value="Double")
	public void testPrimitiveToDouble() {
		copySrcFieldTo(primitiveField, "primitiveDouble");
		assertEquals(123.456, dto.getPrimitiveDouble(), 0.001);
		
		copySrcFieldTo(primitiveField, "double1");
		assertEquals(123.456, dto.getDouble1(), 0.001);
	}
	@Test
	@Scope(target="double", value="String")
	public void testPrimitiveToString() {
		copySrcFieldTo(primitiveField, "string1");
		assertEquals("123.456", dto.getString1());
	}
	@Test
	@Scope(target="double", value="Date")
	public void testPrimitiveToDate() {
		copySrcFieldToFail(primitiveField, "date1");
		assertEquals(null, dto.getDate1());
	}
	@Test
	@Scope(target="double", value="enum")
	public void testPrimitiveToEnum() {
		//TODO: if enum has int value, perhaps we can copy then
		
		copySrcFieldToFail(primitiveField, "colour1");
		assertEquals(null, dto.getColour1());
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
		copySrcFieldTo(mainField, "primitiveInt");
		assertEquals(-123, dto.getPrimitiveInt());
		
		copySrcFieldTo(mainField, "int1");
		assertEquals(-123, dto.getInt1().longValue());
	}
	@Test
	@Scope("Long")
	public void testToLong() {
		copySrcFieldTo(mainField, "primitiveLong");
		assertEquals(-123L, dto.getPrimitiveLong());
		
		copySrcFieldTo(mainField, "long1");
		assertEquals(-123L, dto.getLong1().longValue());
	}
	@Test
	@Scope("Double")
	public void testToDouble() {
		copySrcFieldTo(mainField, "primitiveDouble");
		assertEquals(-123.456, dto.getPrimitiveDouble(), 0.001);
		
		copySrcFieldTo(mainField, "double1");
		assertEquals(-123.456, dto.getDouble1(), 0.001);
	}
	@Test
	@Scope("String")
	public void testToString() {
		copySrcFieldTo(mainField, "string1");
		assertEquals("-123.456", dto.getString1());
		
		reset();
		entity.setDouble1(-200.0);
		copySrcFieldTo(mainField, "string1", false);
		assertEquals("-200.0", dto.getString1());
	}
	@Test
	@Scope("Date")
	public void testToDate() {
		copySrcFieldToFail(mainField, "date1");
		assertEquals(null, dto.getDate1());
	}
	@Test
	@Scope("enum")
	public void testToEnum() {
		copySrcFieldToFail(mainField, "colour1");
		assertEquals(null, dto.getColour1());
	}
	
	//---
	private static final String primitiveField = "primitiveDouble";
	private static final String mainField = "double1";
	
	@Before
	public void init() {
		super.init();
	}
	@Override
	protected AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		entity.setPrimitiveDouble(123.456);
		entity.setDouble1(-123.456);
		
		return entity;
	}
	
	protected void chkValue(double nPrim, double n2) {
		assertEquals(nPrim, dto.getPrimitiveDouble(), 0.0001);
		assertEquals(n2, dto.getDouble1().doubleValue(), 0.0001);
	}
}
