package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.dnal.fc.DefaultCopyFactory;
import org.dnal.fc.FieldCopier;
import org.dnal.fieldcopy.BeanUtilTests.Dest;
import org.dnal.fieldcopy.BeanUtilTests.Source;
import org.junit.Test;


public class CacheTests {
	
	@Test
	public void test() {
		List<Source> list1 = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			Source src = new Source(String.format("bob%d", i), 33);
			list1.add(src);
		}
		
		FieldCopier copier = createCopier();
		copier.getOptions().logEachCopy = true;
		int index = 0;
		for(Source src: list1) {
			Dest dest = new Dest(null, 1);
			copier.copy(src, dest).autoCopy().execute();
			
			String s = String.format("bob%d", index);
			assertEquals(s, dest.getName());
			assertEquals(33, dest.getAge());
			index++;
		}
	}
	
	//--
	private FieldCopier createCopier() {
		return DefaultCopyFactory.Factory().createCopier();
	}

}
