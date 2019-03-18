package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.FieldCopierTests.Dest;
import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.lambda.ConverterBuilder;
import org.dnal.fieldcopy.scopetest.data.AllTypesDTO;
import org.dnal.fieldcopy.scopetest.data.AllTypesEntity;
import org.junit.Test;

public class ExceptionTests extends BaseTest {

	@Test
	public void testGeneratePlan() {
		AllTypesEntity entity = new AllTypesEntity();
		AllTypesDTO dto = new AllTypesDTO();
		
		runAndLogException(entity, dto);
	}
	
	@Test
	public void testConverter() {
		Source src = new Source("bob", 11);
		src.setName(null); //force exception
		
		ValueConverter conv = ConverterBuilder.whenSource(Source.class, "name")
				.thenDo(p -> String.format("%d", p.getName().length()))
				.build();
		
		runWithConverterAndLogException(src, conv);
	}

	
	//--
	private void runAndLogException(AllTypesEntity entity, AllTypesDTO dto) {
		FieldCopier copier = createCopier();
		boolean ok = false;
		try {
			copier.copy(entity, dto).field("listString1", "bool1").execute();
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

	private void runWithConverterAndLogException(Source src, ValueConverter conv) {
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
}
