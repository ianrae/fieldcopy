package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.FieldCopierTests.Dest;
import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.dnal.fieldcopy.service.beanutils.BUBeanDetectorService;
import org.junit.Test;

public class BeanDetectorServiceTests extends BaseTest {
	
	@Test
	public void test() {
		this.svc = new BUBeanDetectorService();
		chkFalse(String.class);
		chkFalse(Integer.class);
		chkFalse(Boolean.class);
		
		chkTrue(Source.class);
		chkTrue(Dest.class);
	}
	
	//--
	private BUBeanDetectorService svc;
	
	private void chkFalse(Class<?> clazz) {
		assertEquals(false, svc.isBeanClass(clazz));
	}
	private void chkTrue(Class<?> clazz) {
		assertEquals(true, svc.isBeanClass(clazz));
	}
}
