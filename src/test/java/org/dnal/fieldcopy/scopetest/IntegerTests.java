package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.scope.core.MyRunner;
import org.dnal.fieldcopy.scope.core.Scope;
import org.dnal.fieldcopy.scopetest.data.AllTypesEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(MyRunner.class)
@Scope("Integer")
public class IntegerTests extends BaseScopeTest {
	
	@Test
	@Scope("values")
	public void test() {
		doCopy("primitiveInt","int1");
		chkValue(516, 517);
		
		reset();
		entity.setPrimitiveInt(44);
		entity.setInt1(45);
		doCopy("primitiveInt","int1");
		chkValue(44, 45);
		
		reset();
		entity.setPrimitiveInt(Integer.MIN_VALUE);
		entity.setInt1(Integer.MIN_VALUE);
		doCopy("primitiveInt","int1");
		chkValue(Integer.MIN_VALUE, Integer.MIN_VALUE);
		
		reset();
		entity.setPrimitiveInt(Integer.MAX_VALUE);
		entity.setInt1(Integer.MAX_VALUE);
		doCopy("primitiveInt","int1");
		chkValue(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	@Test
	@Scope("null")
	public void testNull() {
		entity.setInt1(null);
		doCopy("int1");
		assertEquals(null, dto.getInt1());
	}
	
	//----------- primitive ------------
	@Test
	@Scope(target="int", value="Boolean")
	public void testPrimitiveToBoolean() {
		copySrcFieldToFail(primitiveField, "primitiveBool");
		assertEquals(false, dto.isPrimitiveBool());
	}
	@Test
	@Scope(target="int", value="Integer")
	public void testPrimitiveToInt() {
		copySrcFieldTo(primitiveField, "int1");
		assertEquals(516, dto.getInt1().intValue());
	}
	@Test
	@Scope(target="int", value="Long")
	public void testPrimitiveToLong() {
		copySrcFieldTo(primitiveField, "primitiveLong");
		assertEquals(516L, dto.getPrimitiveLong());
		
		copySrcFieldTo(primitiveField, "long1");
		assertEquals(516L, dto.getLong1().longValue());
	}
	@Test
	@Scope(target="int", value="Double")
	public void testPrimitiveToDouble() {
		copySrcFieldTo(primitiveField, "primitiveDouble");
		assertEquals(516.0, dto.getPrimitiveDouble(), 0.001);
		
		copySrcFieldTo(primitiveField, "double1");
		assertEquals(516.0, dto.getDouble1(), 0.001);
	}
	@Test
	@Scope(target="int", value="String")
	public void testPrimitiveToString() {
		copySrcFieldTo(primitiveField, "string1");
		assertEquals("516", dto.getString1());
		
		reset();
		entity.setPrimitiveBool(false);
		copySrcFieldTo(primitiveField, "string1", false);
		assertEquals("516", dto.getString1());
	}
	@Test
	@Scope(target="int", value="Date")
	public void testPrimitiveToDate() {
		copySrcFieldToFail(primitiveField, "date1");
		assertEquals(null, dto.getDate1());
	}
	@Test
	@Scope(target="int", value="enum")
	public void testPrimitiveToEnum() {
		//TODO: if enum has int value, perhaps we can copy then
		
		copySrcFieldToFail(primitiveField, "colour1");
		assertEquals(null, dto.getColour1());
	}
	
	//----------- Integer ------------
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
		assertEquals(517, dto.getPrimitiveInt());
	}
	@Test
	@Scope("Long")
	public void testToLong() {
		copySrcFieldTo(mainField, "primitiveLong");
		assertEquals(517L, dto.getPrimitiveLong());
		
		copySrcFieldTo(mainField, "long1");
		assertEquals(517L, dto.getLong1().longValue());
	}
	@Test
	@Scope("Double")
	public void testToDouble() {
		copySrcFieldTo(mainField, "primitiveDouble");
		assertEquals(517.0, dto.getPrimitiveDouble(), 0.001);
		
		copySrcFieldTo(mainField, "double1");
		assertEquals(517.0, dto.getDouble1(), 0.001);
	}
	@Test
	@Scope("String")
	public void testToString() {
		copySrcFieldTo(mainField, "string1");
		assertEquals("517", dto.getString1());
		
		reset();
		entity.setInt1(-200);
		copySrcFieldTo(mainField, "string1", false);
		assertEquals("-200", dto.getString1());
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
	private static final String primitiveField = "primitiveInt";
	private static final String mainField = "int1";
	
	@Before
	public void init() {
		super.init();
	}
	@Override
	protected AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		entity.setPrimitiveInt(516);
		entity.setInt1(517);
		
		return entity;
	}
	
	protected void chkValue(int nPrim, int n2) {
		assertEquals(nPrim, dto.getPrimitiveInt());
		assertEquals(n2, dto.getInt1().intValue());
	}
}
