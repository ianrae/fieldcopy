package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.FieldCopierTests.Dest;
import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.FieldInfo;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.junit.Test;

public class LambdaTests extends BaseTest {
	
	public static class ZConverter<T> implements ValueConverter {
		private Class<T> clazz;
		private ZZ<T> zz;
		private String srcFieldName;
		private boolean notNullFlag;
		private String destFieldName;

		public ZConverter(Class<T> clazz, String srcFieldName, String destFieldName, boolean notNullFlag, ZZ<T> zz) {
			this.clazz = clazz;
			this.srcFieldName = srcFieldName;
			this.destFieldName = destFieldName;
			this.notNullFlag = notNullFlag;
			this.zz = zz;
		}

		@Override
		public boolean canConvert(FieldInfo source, FieldInfo dest) {
			if (source.beanClass.equals(clazz)) {
				if (srcFieldName != null && source.matches(srcFieldName)) {
					return true;
				} else if (destFieldName != null && dest.matches(destFieldName)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public Object convertValue(Object srcBean, Object value, ConverterContext ctx) {
			@SuppressWarnings("unchecked")
			T bean = (T) srcBean;
			if (value == null && notNullFlag) {
				return null;
			}
			Object result = zz.zzz(bean);
			return result;
		}
		
	}
	
	public interface ZZ<T> {
		Object zzz(T t);
	}
	
	public static class ConvBuilder1<T> {
		private Class<T> clazz;
		private ZZ<T> zz;
		private String srcFieldName;
		private boolean notNullFlag = false;
		
		public ConvBuilder1(Class<T> clazz, String srcFieldName) {
			this.clazz = clazz;
			this.srcFieldName = srcFieldName;
		}
		
		public ConvBuilder1<T> andNotNull() {
			this.notNullFlag = true;
			return this;
		}
		
		public ConvBuilder1<T> thenDo(ZZ<T> zz) {
			this.zz = zz;
			return this;
		}
		
		public ZConverter<T> build() {
			return new ZConverter<T>(clazz, srcFieldName, null, notNullFlag, zz);
		}
	}
	public static class ConvBuilder2<T> {
		Class<T> clazz;
		
		public ConvBuilder2(Class<T> clazz) {
			this.clazz = clazz;
		}
		
		public ConvBuilder2A<T> andDestinationField(String destFieldName) {
			return new ConvBuilder2A<T>(this, destFieldName);
		}
	}
	public static class ConvBuilder2A<T> {
		private Class<T> clazz;
		private ZZ<T> zz;
		private String destFieldName;
		private boolean notNullFlag = false;
		
		public ConvBuilder2A(ConvBuilder2<T> parent, String destFieldName) {
			this.clazz = parent.clazz;
			this.destFieldName = destFieldName;
		}
		
		public ConvBuilder2A<T> andDestinationField(String destFieldName) {
			this.destFieldName = destFieldName;
			return this;
		}
		
		public ConvBuilder2A<T> andNotNull() {
			this.notNullFlag = true;
			return this;
		}
		
		public ConvBuilder2A<T> thenDo(ZZ<T> zz) {
			this.zz = zz;
			return this;
		}
		
		public ZConverter<T> build() {
			return new ZConverter<T>(clazz, null, destFieldName, notNullFlag, zz);
		}
	}
	
	public static class ConverterBuilder {
		public static <T> ConvBuilder1<T> whenSource(Class<T> clazz, String fieldName) {
			return new ConvBuilder1<T>(clazz, fieldName);
		}
		public static <T> ConvBuilder2<T> whenSource(Class<T> clazz) {
			return new ConvBuilder2<T>(clazz);
		}
	}
	
	
	@Test
	public void test() {
		Source src = new Source("bob", 11);
		Dest dest = new Dest(null, 0);
		
		ValueConverter conv = ConverterBuilder.whenSource(src.getClass(), "name").thenDo(p -> p.getName() + "Suffix").build();
		FieldCopier copier = createCopier();
		copier.copy(src, dest).withConverters(conv).autoCopy().execute();
		assertEquals("bobSuffix", dest.getName());

		//still run lambda if src.name is null
		src.setName(null);
		copier.copy(src, dest).withConverters(conv).autoCopy().execute();
		assertEquals("nullSuffix", dest.getName());
	}
	@Test
	public void testNotNull() {
		Source src = new Source("bob", 11);
		Dest dest = new Dest(null, 0);
		
		ValueConverter conv = ConverterBuilder.whenSource(src.getClass(), "name").andNotNull().thenDo(p -> p.getName() + "Suffix").build();
		FieldCopier copier = createCopier();
		copier.copy(src, dest).withConverters(conv).autoCopy().execute();
		assertEquals("bobSuffix", dest.getName());
		
		//doesn't run lambda
		src = new Source(null, 11);
		copier.copy(src, dest).withConverters(conv).autoCopy().execute();
		assertEquals(null, dest.getName());
	}
	
	@Test
	public void testDest() {
		Source src = new Source("bob", 11);
		Dest dest = new Dest(null, 0);
		
		ValueConverter conv = ConverterBuilder.whenSource(src.getClass()).andDestinationField("name").thenDo(p -> p.getName() + "Suffix").build();
		FieldCopier copier = createCopier();
		copier.copy(src, dest).withConverters(conv).autoCopy().execute();
		assertEquals("bobSuffix", dest.getName());

		//still run lambda if src.name is null
		src.setName(null);
		copier.copy(src, dest).withConverters(conv).autoCopy().execute();
		assertEquals("nullSuffix", dest.getName());
	}
	@Test
	public void testNotNullDest() {
		Source src = new Source("bob", 11);
		Dest dest = new Dest(null, 0);
		
		ValueConverter conv = ConverterBuilder.whenSource(src.getClass()).andDestinationField("name").andNotNull().thenDo(p -> p.getName() + "Suffix").build();
		FieldCopier copier = createCopier();
		copier.copy(src, dest).withConverters(conv).autoCopy().execute();
		assertEquals("bobSuffix", dest.getName());
		
		//doesn't run lambda
		src = new Source(null, 11);
		copier.copy(src, dest).withConverters(conv).autoCopy().execute();
		assertEquals(null, dest.getName());
	}
	
	
	//--
}
