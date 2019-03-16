package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.DefaultCopyFactory;
import org.dnal.fieldcopy.FieldCopier;
import org.dnal.fieldcopy.FieldCopyMapping;
import org.dnal.fieldcopy.BeanUtilTests.Source;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.junit.Test;


public class MappingTests {
	
	public static class Combo1 {
		private int size;
		private Source source;
		
		public Combo1() {
		}
		
		public int getSize() {
			return size;
		}
		public void setSize(int size) {
			this.size = size;
		}
		public Source getSource() {
			return source;
		}
		public void setSource(Source source) {
			this.source = source;
		}
	}
	
	
	@Test
	public void testCopyStruct() {
		Combo1 combo = new Combo1();
		combo.size = 15;
		combo.source = new Source("bob", 33);
		Combo1 combo2 = new Combo1();
		
		FieldCopier copier = createCopier();
		copier.getOptions().logEachCopy = true;
		copier.copy(combo, combo2).autoCopy().execute();
		assertEquals("bob", combo2.source.getName());
		assertEquals(33, combo2.source.getAge());
		assertEquals(15, combo2.getSize());
	}
	@Test
	public void testCopyStructMapping() {
		Combo1 combo = new Combo1();
		combo.size = 15;
		combo.source = new Source("bob", 33);
		Combo1 combo2 = new Combo1();
		
		FieldCopier copier = createCopier();
		FieldCopyMapping mapping = copier.createMapping(Source.class, Source.class).autoCopy().build();
		copier.getOptions().logEachCopy = true;
		copier.copy(combo, combo2).autoCopy().execute();
		assertEquals("bob", combo2.source.getName());
		assertEquals(33, combo2.source.getAge());
		assertEquals(15, combo2.getSize());
	}
	
	@Test
	public void testCopyStructMapping2() {
		Combo1 combo = new Combo1();
		combo.size = 15;
		combo.source = new Source("bob", 33);
		Combo1 combo2 = new Combo1();
		
		FieldCopier copier = createCopier();
		FieldCopyMapping mapping = copier.createMapping(Source.class, Source.class).field("age").build();
		copier.getOptions().logEachCopy = true;
		copier.copy(combo, combo2).withMappings(mapping).autoCopy().execute();
		assertEquals(null, combo2.source.getName());
		assertEquals(33, combo2.source.getAge());
		assertEquals(15, combo2.getSize());
	}
	
	//--
	private FieldCopier createCopier() {
		DefaultCopyFactory.setLogger(new SimpleConsoleLogger());
		return DefaultCopyFactory.Factory().createCopier();
	}
}
