package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.FieldCopierTests.Dest;
import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.lambda.ConverterBuilder;
import org.junit.Test;

public class LambdaTests extends BaseTest {
	
	public static class FooService {
		public String doSomething(String input) {
			return input.toUpperCase();
		}
	}
	
	@Test
	public void test() {
		Source src = new Source("bob", 11);
		Dest dest = new Dest(null, 0);
		
		ValueConverter conv = ConverterBuilder.whenSource(Source.class, "name")
				.thenDo(p -> p.getName() + "Suffix")
				.build();
		
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
		
		ValueConverter conv = ConverterBuilder.whenSource(Source.class, "name")
				.andNotNull()
				.thenDo(p -> p.getName() + "Suffix")
				.build();
		
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
		
		ValueConverter conv = ConverterBuilder.whenSource(Source.class)
				.andDestinationField("name")
				.thenDo(p -> p.getName() + "Suffix")
				.build();
		
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
		
		ValueConverter conv = ConverterBuilder.whenSource(Source.class)
				.andDestinationField("name")
				.andNotNull()
				.thenDo(p -> p.getName() + "Suffix")
				.build();
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).withConverters(conv).autoCopy().execute();
		assertEquals("bobSuffix", dest.getName());
		
		//doesn't run lambda
		src = new Source(null, 11);
		copier.copy(src, dest).withConverters(conv).autoCopy().execute();
		assertEquals(null, dest.getName());
	}
	
	@Test
	public void testLambdaScope() {
		Source src = new Source("bob", 11);
		Dest dest = new Dest(null, 0);
		
		final FooService fooSvc = new FooService();
		
		ValueConverter conv = ConverterBuilder.whenSource(Source.class, "name")
				.thenDo(p -> fooSvc.doSomething(p.getName()))
				.build();
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).withConverters(conv).autoCopy().execute();
		assertEquals("BOB", dest.getName());
	}
	
	@Test
	public void testLambdaScope2() {
		Source src = new Source("bob", 11);
		Dest dest = new Dest(null, 0);
		
		ValueConverter conv = buildConverterWithFoo();
		FieldCopier copier = createCopier();
		copier.copy(src, dest).withConverters(conv).autoCopy().execute();
		assertEquals("BOB", dest.getName());
	}
	private ValueConverter buildConverterWithFoo() {
		final FooService fooSvc = new FooService();
		
		ValueConverter conv = ConverterBuilder.whenSource(Source.class, "name")
				.thenDo(p -> fooSvc.doSomething(p.getName()))
				.build();
		return conv;
	}
	
	//--
}
