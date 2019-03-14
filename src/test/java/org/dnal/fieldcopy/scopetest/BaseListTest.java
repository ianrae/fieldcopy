package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class BaseListTest extends BaseScopeTest {
	protected Date refDate1;
	protected Date refDate2;
	
	protected void init() {
		super.init();
		refDate1 = createADate(0);
		refDate2 = createADate(1);
	}

	protected List<String> createStringList() {
		List<String> list = Arrays.asList("abc", "def");
		list = new ArrayList<>(list);
		return list;
	}
	protected List<Integer> createIntList() {
		List<Integer> list = Arrays.asList(44, 45);
		list = new ArrayList<>(list);
		return list;
	}
	protected List<Long> createLongList() {
		List<Long> list = Arrays.asList(44L, 45L);
		list = new ArrayList<>(list);
		return list;
	}
	protected List<Date> createDateList() {
		List<Date> list = new ArrayList<>();
		list.add(createADate(0));
		list.add(createADate(1));
		return list;
	}
	protected Date createADate(int which) {
		int year = 2015 + which;
		return createDate(year,12,25);
	}
	
	protected void chkValue(int expected, String s1, String s2) {
		List<String> list = dto.getListString1();
		assertEquals(expected, list.size());
		
		if (expected > 0) {
			assertEquals(s1, list.get(0));
		}
		if (expected > 1) {
			assertEquals(s2, list.get(1));
		}
	}
	protected void chkIntListValue(int expected, int n1, int n2) {
		List<Integer> list = dto.getListInt1();
		assertEquals(expected, list.size());
		
		if (expected > 0) {
			assertEquals(n1, list.get(0).intValue());
		}
		if (expected > 1) {
			assertEquals(n2, list.get(1).intValue());
		}
	}
	protected void chkLongListValue(int expected, long n1, long n2) {
		List<Long> list = dto.getListLong1();
		assertEquals(expected, list.size());
		
		if (expected > 0) {
			assertEquals(n1, list.get(0).longValue());
		}
		if (expected > 1) {
			assertEquals(n2, list.get(1).longValue());
		}
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
