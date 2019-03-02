package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.ConvertUtils;
import org.junit.Test;

public class OtherTests {

	@Test
	public void test() {
		assertEquals(1,1);
	}
	
	@Test
	public void test2() throws IllegalAccessException, InvocationTargetException {
		Integer n1 = 44;
		Integer n2 = (Integer) ConvertUtils.convert(n1, Integer.class);
		assertEquals(44, n2.intValue());
	}
}
