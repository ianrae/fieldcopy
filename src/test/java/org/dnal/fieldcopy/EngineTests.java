package org.dnal.fieldcopy;
import static org.junit.Assert.assertEquals;

import org.dnal.fc.CopyOptions;
import org.dnal.fc.DefaultCopyFactory;
import org.dnal.fc.beanutils.ExecuteCopySpec;
import org.dnal.fc.beanutils.FastBeanUtilFieldCopyService;
import org.dnal.fc.core.CopyFactory;
import org.dnal.fc.core.CopySpec;
import org.dnal.fc.core.FieldCopyService;
import org.dnal.fieldcopy.BeanUtilTests.Dest;
import org.dnal.fieldcopy.BeanUtilTests.Source;
import org.junit.Test;

public class EngineTests {

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
		spec.transformerL = null;;
		
		ExecuteCopySpec execSpec = execSvc.generateExecutePlan(spec);
		assertEquals(2, execSpec.fieldL.size());
		
		boolean b = execSvc.executePlan(spec, execSpec);
		assertEquals(true, b);
		assertEquals("bob", dest.getName());
		assertEquals(33, dest.getAge());
	}
	

	
}
