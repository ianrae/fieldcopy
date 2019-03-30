package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.FieldInfo;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.junit.Test;

/**
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
	
	public static class OuterWithList {
		private String name;
		private String title;
		private List<Source> sourceL;

		public OuterWithList() {
		}
		public OuterWithList(String name, String title) {
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
		public List<Source> getSourceL() {
			return sourceL;
		}
		public void setSourceL(List<Source> sourceL) {
			this.sourceL = sourceL;
		}
	}
	public static class OuterWithListDTO {
		private String name;
		private String title;
		private List<Source> sourceL;

		public OuterWithListDTO() {
		}
		public OuterWithListDTO(String name, String title) {
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
		public List<Source> getSourceL() {
			return sourceL;
		}
		public void setSourceL(List<Source> sourceL) {
			this.sourceL = sourceL;
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
		
		CopySpec spec = copier.getMostRecentCopySpec();
		assertEquals(1, spec.converterL.size());
		assertEquals(1, spec.mappingL.size());

		log("again..");
		copier.copy(src, dest).withConverters(conv).autoCopy().execute();
		assertEquals("BOB", dest.getName());
		assertEquals("TITLE1", dest.getTitle());
		
		//transitive! converter applied to fields of sub-object
		assertEquals("SUE", dest.getSource().getName()); 
		
		spec = copier.getMostRecentCopySpec();
		assertEquals(1, spec.converterL.size());
		assertEquals(1, spec.mappingL.size());
	}

	@Test
	public void testWithList() {
		OuterWithList src = new OuterWithList("bob", "title1");
		Source source = new Source("sue", 28);
		List<Source> srcL = new ArrayList<>();
		srcL.add(source);
		src.setSourceL(srcL);
		OuterWithListDTO dest = new OuterWithListDTO(null, null);
		
		MyConverter1 conv = new MyConverter1();
		
		FieldCopier copier = createCopier();
		enableLogging();
		copier.copy(src, dest).withConverters(conv).autoCopy().execute();
		assertEquals("BOB", dest.getName());
		assertEquals("TITLE1", dest.getTitle());
		
		//transitive does apply to elements of lists or arrays
		assertEquals(1, dest.getSourceL().size());
		assertEquals("SUE", dest.getSourceL().get(0).getName()); 
	}
}
