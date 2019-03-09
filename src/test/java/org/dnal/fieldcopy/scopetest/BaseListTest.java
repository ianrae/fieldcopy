package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BaseListTest extends BaseScopeTest {
	
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
}
