package org.dnal.fieldcopy;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;


public class FactoryTests extends BaseTest {
	
	@Test
	public void test() {
		CopierFactory fact1 = FieldCopy.createFactory();
		FieldCopier fc1a = fact1.createCopier();
		FieldCopier fc1b = fact1.createCopier();
		
		assertNotSame(fc1a, fc1b);
		assertSame(fc1a.getCopyService(), fc1b.getCopyService());
		
		CopierFactory fact2 = FieldCopy.createFactory();
		FieldCopier fc2a = fact2.createCopier();
		
		assertNotSame(fact1, fact2);
		assertNotSame(fc1a.getCopyService(), fc2a.getCopyService());
	}
	
}
