package org.dnal.fieldcopy;
import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.CopyOptions;
import org.dnal.fieldcopy.DefaultCopyFactory;
import org.dnal.fieldcopy.FieldCopierTests.Dest;
import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.dnal.fieldcopy.core.CopyFactory;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.service.beanutils.ExecuteCopyPlan;
import org.dnal.fieldcopy.service.beanutils.FastBeanUtilFieldCopyService;
import org.junit.Test;

public class StructTests {

	@Test
	public void test() {
		CopyFactory factory = DefaultCopyFactory.Factory();
		FieldCopyService copySvc = factory.createCopyService();
		
		FastBeanUtilFieldCopyService execSvc = new FastBeanUtilFieldCopyService(factory.createLogger(),factory.createFieldFilter());
		
		Source src = new Source("bob", 33);
		Dest dest = new Dest(null, -1);

		CopySpec spec = new CopySpec();
		spec.sourceObj = src;
		spec.destObj = dest;
		spec.fieldPairs = copySvc.buildAutoCopyPairs(src.getClass(), dest.getClass());
		spec.mappingL = null;
		spec.options = new CopyOptions();
		spec.converterL = null;;
		
		ExecuteCopyPlan execSpec = execSvc.generateExecutePlan(spec);
		assertEquals(2, execSpec.fieldL.size());
		
		boolean b = execSvc.executePlan(spec, execSpec, null, 1);
		assertEquals(true, b);
		assertEquals("bob", dest.getName());
		assertEquals(33, dest.getAge());
	}
	
}
