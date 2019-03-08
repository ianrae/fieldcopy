package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.scope.MyRunner;
import org.dnal.fieldcopy.scope.Scope;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;;

@RunWith(MyRunner.class)
@Scope("Boolean")
public class BooleanTests extends BaseScopeTest {
	
	@Test
	@Scope("values")
	public void testValues() {
		doCopy("primitiveBool","bool1");
		chkValue(true, true);
		
		reset();
		entity.setPrimitiveBool(false);
		entity.setBool1(false);
		doCopy("primitiveBool","bool1");
		chkValue(false, false);
	}
	
	@Test
	@Scope("null")
	public void testNull() {
		entity.setBool1(null);
		doCopy("bool1");
		assertEquals(null, dto.getBool1());
	}
	
	//----------- primitive ------------
	@Test
	@Scope(target="boolean", value="boolean")
	public void testPrimitiveToBoolean() {
		copySrcFieldTo(primitiveField, "bool1");
		assertEquals(true, dto.getBool1().booleanValue());
	}
	@Test
	@Scope(target="boolean", value="int")
	public void testPrimitiveToInt() {
		copySrcFieldTo(primitiveField, "primitiveInt");
		assertEquals(1, dto.getPrimitiveInt());
		
		copySrcFieldTo(primitiveField, "int1");
		assertEquals(1, dto.getInt1().intValue());
	}
	@Test
	@Scope(target="boolean", value="long")
	public void testPrimitiveToLong() {
		copySrcFieldTo(primitiveField, "primitiveLong");
		assertEquals(1L, dto.getPrimitiveLong());
		
		copySrcFieldTo(primitiveField, "long1");
		assertEquals(1L, dto.getLong1().longValue());
	}
	@Test
	@Scope(target="boolean", value="double")
	public void testPrimitiveToDouble() {
		copySrcFieldTo(primitiveField, "primitiveDouble");
		assertEquals(1.0, dto.getPrimitiveDouble(), 0.001);
		
		copySrcFieldTo(primitiveField, "double1");
		assertEquals(1.0, dto.getDouble1(), 0.001);
	}
	@Test
	@Scope(target="boolean", value="String")
	public void testPrimitiveToString() {
		copySrcFieldTo(primitiveField, "string1");
		assertEquals("true", dto.getString1());
		
		reset();
		entity.setPrimitiveBool(false);
		copySrcFieldTo(primitiveField, "string1", false);
		assertEquals("false", dto.getString1());
	}
	@Test
	@Scope(target="boolean", value="Date")
	public void testPrimitiveToDate() {
		copySrcFieldToFail(primitiveField, "date1");
		assertEquals(null, dto.getDate1());
	}
	@Test
	@Scope(target="boolean", value="enum")
	public void testPrimitiveToEnum() {
		copySrcFieldToFail(primitiveField, "colour1");
		assertEquals(null, dto.getColour1());
	}
	
	//----------- Boolean ------------
	@Test
	@Scope("Boolean")
	public void testToBoolean() {
		copySrcFieldTo(mainField, "primitiveBool");
		assertEquals(true, dto.isPrimitiveBool());
	}
	@Test
	@Scope("Integer")
	public void testToInt() {
		copySrcFieldTo(mainField, "primitiveInt");
		assertEquals(1, dto.getPrimitiveInt());
		
		copySrcFieldTo(mainField, "int1");
		assertEquals(1, dto.getInt1().intValue());
	}
	@Test
	@Scope("Long")
	public void testToLong() {
		copySrcFieldTo(mainField, "primitiveLong");
		assertEquals(1L, dto.getPrimitiveLong());
		
		copySrcFieldTo(mainField, "long1");
		assertEquals(1L, dto.getLong1().longValue());
	}
	@Test
	@Scope("Double")
	public void testToDouble() {
		copySrcFieldTo(mainField, "primitiveDouble");
		assertEquals(1.0, dto.getPrimitiveDouble(), 0.001);
		
		copySrcFieldTo(mainField, "double1");
		assertEquals(1.0, dto.getDouble1(), 0.001);
	}
	@Test
	@Scope("String")
	public void testToString() {
		copySrcFieldTo(mainField, "string1");
		assertEquals("true", dto.getString1());
		
		reset();
		entity.setBool1(false);
		copySrcFieldTo(mainField, "string1", false);
		assertEquals("false", dto.getString1());
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
	private static final String primitiveField = "primitiveBool";
	private static final String mainField = "bool1";
	
	@Before
	public void init() {
//		System.out.println("iiiiiiiiiiiiiiiii");
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
