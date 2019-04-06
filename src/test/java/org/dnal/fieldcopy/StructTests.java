package org.dnal.fieldcopy;
import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.CopyOptions;
import org.dnal.fieldcopy.FieldCopierTests.Dest;
import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.DefaultFieldFilter;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.TargetPair;
import org.dnal.fieldcopy.service.beanutils.old.ExecuteCopyPlan;
import org.dnal.fieldcopy.service.beanutils.old.FastBeanUtilFieldCopyService;
import org.junit.Test;

public class StructTests extends BaseTest {

	@Test
	public void test() {
		FieldCopyService copySvc = this.createCopyService();
		
		FastBeanUtilFieldCopyService execSvc = new FastBeanUtilFieldCopyService(FieldCopy.getLogger(), new DefaultFieldFilter());
		
		Source src = new Source("bob", 33);
		Dest dest = new Dest(null, -1);

		CopySpec spec = new CopySpec();
		spec.sourceObj = src;
		spec.destObj = dest;
		spec.fieldPairs = copySvc.buildAutoCopyPairs(new TargetPair(src.getClass(), dest.getClass()));
		spec.mappingL = null;
		spec.options = new CopyOptions();
		spec.converterL = null;;
		
		ExecuteCopyPlan execSpec = execSvc.generateExecutePlan(spec, null); //TODO: fix null later
		assertEquals(2, execSpec.fieldL.size());
		
		boolean b = execSvc.executePlan(spec, execSpec, null, 1);
		assertEquals(true, b);
		assertEquals("bob", dest.getName());
		assertEquals(33, dest.getAge());
	}
	
}
