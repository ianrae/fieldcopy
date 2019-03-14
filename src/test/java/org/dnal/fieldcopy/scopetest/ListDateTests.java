package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.util.Date;
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
@Scope("List<Date>")
public class ListDateTests extends BaseListTest {
	
	public static class MyIntegerToStringListConverter extends BaseListConverter {
		@Override
		public boolean canHandle(String srcFieldName, Class<?>srcClass, Class<?> destClass) {
			return srcFieldName.equals("listDate1");
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
		chkDateListValue(2, refDate1, refDate2);
		
		reset();
		List<Date> list = createDateList();
		list.add(createADate(2));
		entity.setListDate1(list);
		doCopy(mainField);
		chkDateListValue(3, refDate1, refDate2);

		reset();
		list = createDateList();
		list.clear();
		entity.setListDate1(list);
		doCopy(mainField);
		chkDateListValue(2, null, null);
	}
	
	@Test
	@Scope("null")
	public void testNull() {
		entity.setListDate1(null);
		doCopy(mainField);
		assertEquals(null, dto.getListDate1());
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
		copySrcFieldToFail(mainField, "listInt1");
	}
	
	@Test
	@Scope("List<String>")
	public void testToListString() {
		copier.copy(entity, dto).withConverters(new MyIntegerToStringListConverter()).field("listInt1", "listString1").execute();
		chkValue(2, "44", "45");
	}
	
	
	//---
	private static final String mainField = "listDate1";
	private Date refDate1;
	private Date refDate2;
	
	@Before
	public void init() {
		super.init();
		refDate1 = createADate(0);
		refDate2 = createADate(1);
	}
	@Override
	protected AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		
		List<Date> list = createDateList();
		entity.setListDate1(list);
		
		return entity;
	}
	protected void chkDateListValue(int expected, Date dt1, Date dt2) {
		List<Date> list = dto.getListDate1();
		assertEquals(expected, list.size());
		
		if (expected > 0) {
			assertEquals(dt1, list.get(0));
		}
		if (expected > 1) {
			assertEquals(dt2, list.get(1));
		}
	}
	
}
