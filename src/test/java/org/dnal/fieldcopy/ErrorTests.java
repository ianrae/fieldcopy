package org.dnal.fieldcopy;

import static org.junit.Assert.*;

import org.dnal.fieldcopy.DefaultValueTests.Dest;
import org.dnal.fieldcopy.DefaultValueTests.Source;
import org.dnal.fieldcopy.ListListTests.Taxi;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.junit.Test;

/**
 * 
 * @author Ian Rae
 *
 */
public class ErrorTests extends BaseTest {
	
	@Test(expected=FieldCopyException.class)
	public void testSrcNull() {
		Dest dest = new Dest();
		FieldCopier copier = createCopier();
		enableLogging();
		copier.copy(null, dest).autoCopy().execute();
	}
	@Test(expected=FieldCopyException.class)
	public void testDestNull() {
		Source src = new Source();
		FieldCopier copier = createCopier();
		enableLogging();
		copier.copy(src, null).autoCopy().execute();
	}
	
	@Test(expected=FieldCopyException.class)
	public void testString() {
		String src = "abc";
		String dest = new String();
		FieldCopier copier = createCopier();
		enableLogging();
		copier.copy(src, dest).autoCopy().execute();
	}

	@Test
	public void testNoMatchAutoCopy() {
		Source src = new Source();
		Taxi dest = new Taxi();
		FieldCopier copier = createCopier();
		enableLogging();
		copier.copy(src, dest).autoCopy().execute();
		
		CopySpec copySpec = copier.getMostRecentCopySpec();
		assertEquals(0, copySpec.fieldPairs.size());
	}
}
