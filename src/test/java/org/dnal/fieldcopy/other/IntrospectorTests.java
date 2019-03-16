package org.dnal.fieldcopy.other;

import static org.junit.Assert.assertEquals;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;

import org.dnal.fieldcopy.FieldCopierTests;
import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.junit.Test;

public class IntrospectorTests {

	@Test
	public void test() throws IntrospectionException {
		Source src = new Source();
		BeanInfo info = Introspector.getBeanInfo(src.getClass());
		assertEquals(3, info.getPropertyDescriptors().length);
	}
	
	@Test
	public void testInteger() throws IntrospectionException {
		Integer n = 15;
		BeanInfo info = Introspector.getBeanInfo(n.getClass());
		assertEquals(1, info.getPropertyDescriptors().length);
	}
	
}
