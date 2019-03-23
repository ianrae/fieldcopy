package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.FieldInfo;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.junit.Test;

/**
 * TODO
 * -mutli-dim arrays
 * -failIfNull on field and global
 * -builtInConveters
 * -bean detector service
 *   -everthing that isn't gets a mapping automatically
 * -only transitive stuff if needed (don't create sub-obj mapping for example)
 * 
 * 
 * Transitive features
 * -features such as built-in converters apply 'globally'.
 * -they are applied at the field level to the top-level object
 *  and all its sub-objects
 * -they are not applied to list elements, map keys/values
 * 
 * @author Ian Rae
 *
 */
public class TransitiveTests extends BaseTest {
	
	public static class MyConverter1 implements ValueConverter {
		@Override
		public boolean canConvert(FieldInfo source, FieldInfo dest) {
			return source.fieldClass.equals(String.class) && dest.fieldClass.equals(String.class);
		}
		@Override
		public Object convertValue(Object srcBean, Object value, ConverterContext ctx) {
			if (value == null) {
				return null;
			}
			String s = (String) value;
			return s.toUpperCase();
		}
	}
	
	public static class Outer {
		private String name;
		private String title;
		private Source source;

		public Outer() {
		}
		public Outer(String name, String title) {
			this.name = name;
			this.title = title;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public Source getSource() {
			return source;
		}
		public void setSource(Source source) {
			this.source = source;
		}
	}
	public static class OuterDTO {
		private String name;
		private String title;
		private Source source;

		public OuterDTO() {
		}
		public OuterDTO(String name, String title) {
			this.name = name;
			this.title = title;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public Source getSource() {
			return source;
		}
		public void setSource(Source source) {
			this.source = source;
		}
	}
	
	
	@Test
	public void test() {
		Outer src = new Outer("bob", "title1");
		Source source = new Source("sue", 28);
		src.setSource(source);
		OuterDTO dest = new OuterDTO(null, null);
		
		MyConverter1 conv = new MyConverter1();
		
		FieldCopier copier = createCopier();
		enableLogging();
		copier.copy(src, dest).withConverters(conv).autoCopy().execute();
		assertEquals("BOB", dest.getName());
		assertEquals("TITLE1", dest.getTitle());
		
		//transitive! converter applied to fields of sub-object
		assertEquals("SUE", dest.getSource().getName()); 
	}

}
