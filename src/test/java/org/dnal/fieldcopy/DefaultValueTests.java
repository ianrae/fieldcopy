package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.FieldCopierTests.Dest;
import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.junit.Test;

public class DefaultValueTests extends BaseTest {
	
	
	@Test
	public void testCopyByName() {
		Source src = new Source("bob", 33);
		Dest dest = new Dest(null, -1);
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).cacheKey("key1").field("name", "name").execute();
		assertEquals("bob", dest.getName());
		assertEquals(-1, dest.getAge());
		
		dest = new Dest(null, -1);
		copier.copy(src, dest).cacheKey("key2").field("age", "age").execute();
		assertEquals(null, dest.getName());
		assertEquals(33, dest.getAge());
		
		dest = new Dest(null, -1);
		copier.copy(src, dest).cacheKey("key3").field("age", "age").field("name").execute();
		assertEquals("bob", dest.getName());
		assertEquals(33, dest.getAge());
	}
	
	//--
}
