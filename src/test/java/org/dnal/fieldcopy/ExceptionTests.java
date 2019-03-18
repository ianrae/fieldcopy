package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.FieldCopierTests.Dest;
import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.lambda.ConverterBuilder;
import org.junit.Test;

public class ExceptionTests extends BaseTest {
	
	@Test
	public void testConverter() {
		Source src = new Source("bob", 11);
		src.setName(null); //force exception
		
		ValueConverter conv = ConverterBuilder.whenSource(Source.class, "name")
				.thenDo(p -> String.format("%d", p.getName().length()))
				.build();
		
		runAndLogException(src, conv);
	}

	private void runAndLogException(Source src, ValueConverter conv) {
		Dest dest = new Dest(null, 0);
		
		FieldCopier copier = createCopier();
		boolean ok = false;
		try {
			copier.copy(src, dest).withConverters(conv).autoCopy().execute();
			ok = true;
		} catch (FieldCopyException e) {
			log(e.getMessage());
			log("");
			e.getCause().printStackTrace();
		} catch (Exception e) {
			log(e.getMessage());
		}
		assertEquals(false, ok);
	}
	
	//--
}
