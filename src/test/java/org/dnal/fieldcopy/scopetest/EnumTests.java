package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.scope.MyRunner;
import org.dnal.fieldcopy.scope.Scope;
import org.dnal.fieldcopy.scopetest.data.AllTypesEntity;
import org.dnal.fieldcopy.scopetest.data.Colour;
import org.dnal.fieldcopy.scopetest.data.Province;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(MyRunner.class)
@Scope("enum")
public class EnumTests extends BaseScopeTest {
	
	public static class MyConverter implements ValueConverter {

		@Override
		public boolean canHandle(String srcFieldName, Class<?>srcClass, Class<?> destClass) {
			if (srcClass.equals(Colour.class) && destClass.equals(Province.class)) {
				return true;
			}
			return false;
		}

		@Override
		public Object convertValue(Object srcBean, Object value, ConverterContext ctx) {
			Colour col = (Colour) value;
			
			Province prov = Province.valueOf(col.name());
			return prov;
		}
	}
	
	@Test
	@Scope("values")
	public void test() {
		doCopy("colour1");
		chkValue(Colour.GREEN);
		
		reset();
		entity.setColour1(Colour.RED);
		doCopy("colour1");
		chkValue(Colour.RED);
	}
	
	@Test
	@Scope("null")
	public void testNull() {
		entity.setColour1(null);
		doCopy("colour1");
		assertEquals(null, dto.getColour1());
	}
	
	
	//----------- Enum ------------
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
	}
	@Test
	@Scope("Long")
	public void testToLong() {
		copySrcFieldToFail(mainField, "primitiveLong");
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
		assertEquals("GREEN", dto.getString1());
	}
	@Test
	@Scope("Date")
	public void testToDate() {
		copySrcFieldToFail(mainField, "date1");
	}
	@Test
	@Scope("enum")
	public void testToEnum() {
		copySrcFieldTo(mainField, "colour1");
		assertEquals(Colour.GREEN, dto.getColour1());
		
		copySrcFieldToFail(mainField, "province1");
		
		reset();
		entity.setColour1(Colour.BLUE);
		copySrcFieldToFail(mainField, "province1", false);
	}
	
	@Test
	public void testToEnumConverter() {
		entity.setColour1(Colour.BLUE);
		copier.copy(entity, dto).withConverters(new MyConverter()).field("colour1", "province1").execute();
		assertEquals(Province.BLUE, dto.getProvince1());
		
		reset();
		entity.setColour1(Colour.RED); //there is no Province.RED
		boolean fail = false;
		try {
			copier.copy(entity, dto).withConverters(new MyConverter()).field("colour1", "province1").execute();
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
