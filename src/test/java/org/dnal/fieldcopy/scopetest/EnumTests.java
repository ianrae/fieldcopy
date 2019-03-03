package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.ValueTransformer;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.junit.Before;
import org.junit.Test;


public class EnumTests extends BaseScopeTest {
	
	public static class MyTransformer implements ValueTransformer {

		@Override
		public boolean canHandle(String srcFieldName, Class<?>srcClass, Class<?> destClass) {
			if (srcClass.equals(Colour.class) && destClass.equals(Province.class)) {
				return true;
			}
			return false;
		}

		@Override
		public Object transformValue(Object srcBean, Object value, ConverterContext ctx) {
			Colour col = (Colour) value;
			
			Province prov = Province.valueOf(col.name());
			return prov;
		}

		@Override
		public void setCopySvc(FieldCopyService copySvc) {
		}
	}
	
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
	
	@Test
	public void testToEnumTransformer() {
		entity.setColour1(Colour.BLUE);
		copier.copy(entity, dto).withTransformers(new MyTransformer()).field("colour1", "province1").execute();
		assertEquals(Province.BLUE, dto.getProvince1());
		
		reset();
		entity.setColour1(Colour.RED); //there is no Province.RED
		boolean fail = false;
		try {
			copier.copy(entity, dto).withTransformers(new MyTransformer()).field("colour1", "province1").execute();
		} catch (FieldCopyException e) {
			fail = true;
		}
		assertEquals(true, fail);
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
