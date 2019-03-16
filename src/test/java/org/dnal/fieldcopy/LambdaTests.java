package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.FieldCopierTests.Dest;
import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.lambda.ConverterBuilder;
import org.junit.Test;

public class LambdaTests extends BaseTest {
	
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
