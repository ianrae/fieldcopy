package org.dnal.fieldcopy;

import static org.junit.Assert.*;

import org.dnal.fieldcopy.core.DefaultFieldFilter;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldFilter;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.dnal.fieldcopy.service.beanutils.BUCopyService;
import org.junit.Test;


public class FactoryTests extends BaseTest {
	
	public interface CopierFactory {
		FieldCopier createCopier();
	}
	
	public static class CopierFactoryImpl implements CopierFactory {
		public FieldCopyService copySvc;
		
		public CopierFactoryImpl(FieldCopyService copySvc) {
			this.copySvc = copySvc;
		}
		
		@Override
		public FieldCopier createCopier() {
			return new FieldCopier(copySvc);
		}
	}
	
	public interface ServiceFactory {
		FieldCopyService createService();
	}
	
	public static class SvcF implements ServiceFactory {
		private SimpleLogger logger;

		@Override
		public FieldCopyService createService() {
			FieldRegistry registry = new FieldRegistry();
			DefaultFieldFilter filter = new DefaultFieldFilter();
			BUCopyService copySvc = new BUCopyService(logger, registry, filter);
			return copySvc;
		}
	}
	
	public static class FieldCopyFactory {
		private static FieldCopyFactory theSingleton;
		
		private ServiceFactory svcFactory;
		
		public FieldCopyFactory(ServiceFactory factory) {
			this.svcFactory = factory;
		}
		
		public static synchronized void setSingleton(FieldCopyFactory factory) {
			theSingleton = factory;
		}
		
		public static synchronized CopierFactory createFactory() {
			if (theSingleton == null) {
				theSingleton = new FieldCopyFactory(new SvcF());
			}
			return theSingleton.createF();
		}

		private CopierFactory createF() {
			FieldCopyService copySvc = svcFactory.createService();
			CopierFactoryImpl dcf = new CopierFactoryImpl(copySvc);
			return dcf;
		}
	}
	
	@Test
	public void test() {
		CopierFactory fact1 = FieldCopyFactory.createFactory();
		FieldCopier fc1 = fact1.createCopier();
		FieldCopier fc2 = fact1.createCopier();
		
		assertNotSame(fc1, fc2);
		assertSame(fc1.getCopyService(), fc2.getCopyService());
		
		CopierFactory fact2 = FieldCopyFactory.createFactory();
		FieldCopier fc3 = fact2.createCopier();
		
		assertNotSame(fact1, fact2);
		assertSame(fc1.getCopyService(), fc3.getCopyService());
	}
	
}
