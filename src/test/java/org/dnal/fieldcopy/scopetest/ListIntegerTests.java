package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.scope.MyRunner;
import org.dnal.fieldcopy.scope.Scope;
import org.dnal.fieldcopy.scopetest.data.AllTypesEntity;
import org.dnal.fieldcopy.scopetest.data.BaseListConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(MyRunner.class)
@Scope("List<Integer>")
public class ListIntegerTests extends BaseListTest {
	
	public static class MyIntegerToStringListConverter extends BaseListConverter {
		@Override
		public boolean canHandle(String srcFieldName, Class<?>srcClass, Class<?> destClass) {
			return srcFieldName.equals("listInt1");
		}

		@Override
		protected Object copyElement(Object el) {
			Integer n = (Integer) el;
			return n.toString();
		}

		@Override
		public void setCopySvc(FieldCopyService copySvc) {
		}
	}
	
	@Test
	@Scope("values")
	public void test() {
		doCopy(mainField);
		chkIntListValue(2, 44, 45);
		
		reset();
		List<Integer> list = createIntList();
		list.add(66);
		entity.setListInt1(list);;
		doCopy(mainField);
		chkIntListValue(3, 44, 45);

		reset();
		list = createIntList();
		list.clear();
		entity.setListInt1(list);;
		doCopy(mainField);
		chkIntListValue(0, 0, 0);
	}
	
	@Test
	@Scope("null")
	public void testNull() {
		entity.setListString1(null);
		doCopy(mainField);
		assertEquals(null, dto.getListString1());
	}
	
	@Test
	@Scope("Boolean")
	public void testToBoolean() {
		copySrcFieldToFail(mainField, "primitiveBool");
		copySrcFieldToFail(mainField, "bool1");
	}
	@Test
	@Scope("Integer")
	public void testToInt() {
		copySrcFieldToFail(mainField, "primitiveInt");
		copySrcFieldToFail(mainField, "int1");
	}
	@Test
	@Scope("Long")
	public void testToLong() {
		copySrcFieldToFail(mainField, "primitiveLong");
		copySrcFieldToFail(mainField, "long1");
	}
	@Test
	@Scope("Double")
	public void testToDouble() {
		copySrcFieldToFail(mainField, "primitiveDouble");
		copySrcFieldToFail(mainField, "double1");
	}
	@Test
	@Scope("String")
	public void testToString() {
		copySrcFieldToFail(mainField, "string1");
	}
	@Test
	@Scope("Date")
	public void testToDate() {
		copySrcFieldToFail(mainField, "date1");
	}
	@Test
	@Scope("enum")
	public void testToEnum() {
		copySrcFieldToFail(mainField, "colour1");
	}
	
	@Test
	@Scope("List<Integer>")
	public void testToListInt() {
		copySrcFieldTo(mainField, "listInt1");
		chkIntListValue(2, 44, 45);
	}
	@Test
	@Scope("List<String>")
	public void testToListString() {
		copier.copy(entity, dto).withConverters(new MyIntegerToStringListConverter()).field("listInt1", "listString1").execute();
		chkValue(2, "44", "45");
	}
	
	
	//---
	private static final String mainField = "listInt1";
	
	@Before
	public void init() {
		super.init();
	}
	@Override
	protected AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		
		List<Integer> list = createIntList();
		entity.setListInt1(list);
		
		return entity;
	}
}
