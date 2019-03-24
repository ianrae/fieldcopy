package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.dnal.fieldcopy.TransitiveTests.MyConverter1;
import org.dnal.fieldcopy.TransitiveTests.Outer;
import org.dnal.fieldcopy.TransitiveTests.OuterDTO;
import org.dnal.fieldcopy.core.CopySpec;
import org.junit.Test;

/**
 * 
 * @author Ian Rae
 *
 */
public class BuiltInConverterTests extends BaseTest {
	
	
	@Test
	public void test() {
		Outer src = new Outer("bob", "title1");
		Source source = new Source("sue", 28);
		src.setSource(source);
		OuterDTO dest = new OuterDTO(null, null);
		
		MyConverter1 conv = new MyConverter1();
		
		FieldCopier copier = createCopier();
		copier.addBuiltInConverter(conv);
		enableLogging();
		copier.copy(src, dest).autoCopy().execute();
		assertEquals("BOB", dest.getName());
		assertEquals("TITLE1", dest.getTitle());
		
		//transitive! converter applied to fields of sub-object
		assertEquals("SUE", dest.getSource().getName()); 
		
		CopySpec spec = copier.getMostRecentCopySpec();
		assertEquals(null, spec.converterL);
		assertEquals(1, spec.mappingL.size());

		log("again..");
		copier.copy(src, dest).autoCopy().execute();
		assertEquals("BOB", dest.getName());
		assertEquals("TITLE1", dest.getTitle());
		
		//transitive! converter applied to fields of sub-object
		assertEquals("SUE", dest.getSource().getName()); 
		
		spec = copier.getMostRecentCopySpec();
		assertEquals(1, spec.converterL.size());
		assertEquals(1, spec.mappingL.size());
	}
}
