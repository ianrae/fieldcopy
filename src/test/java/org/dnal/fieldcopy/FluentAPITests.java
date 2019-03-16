package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.FieldCopierTests.Dest;
import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.junit.Test;


public class FluentAPITests extends BaseTest {
	
	@Test
	public void test() {
		Source src = new Source("bob", 33);
		Dest dest = new Dest();
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).autoCopy().execute();
		assertEquals("bob", dest.getName());
		assertEquals(33, dest.getAge());
	}
	
	@Test
	public void testA() {
		Source src = new Source("bob", 33);
		
		FieldCopier copier = createCopier();
		Dest dest = copier.copy(src).autoCopy().execute(Dest.class);
		assertEquals("bob", dest.getName());
		assertEquals(33, dest.getAge());
	}
	
}
