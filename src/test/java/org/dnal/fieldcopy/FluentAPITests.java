package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.BeanUtilTests.Dest;
import org.dnal.fieldcopy.BeanUtilTests.Source;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.junit.Test;


public class FluentAPITests {
	
	@Test
	public void test() {
		Source src = new Source("bob", 33);
		
		FieldCopier copier = createCopier();
		Dest dest = copier.copy(src).autoCopy().execute(Dest.class);
		assertEquals("bob", dest.getName());
		assertEquals(33, dest.getAge());
	}
	
	
	//--
	private FieldCopier createCopier() {
		DefaultCopyFactory.setLogger(new SimpleConsoleLogger());
		return DefaultCopyFactory.Factory().createCopier();
	}


}
