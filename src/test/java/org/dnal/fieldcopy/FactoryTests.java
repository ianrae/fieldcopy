package org.dnal.fieldcopy;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.dnal.fieldcopy.core.DefaultFieldFilter;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.dnal.fieldcopy.service.beanutils.BUCopyService;
import org.junit.Test;


public class FactoryTests extends BaseTest {
	
	/**
	 * A factory that creates FieldCopier objects.
	 * FieldCopier objects are single-use objects.
	 * Use one to do a single copy.
	 * 
	 * The copier factory caches details of the classes
	 * that it has copied.
	 * 
	 * @author Ian Rae
	 *
	 */
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
	
	public static class BUServiceFactory implements ServiceFactory {
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
		
		/**
		 * Optional method. Use this to set a custom factory.
		 * 
		 * @param factory
		 */
		public static synchronized void setSingleton(FieldCopyFactory factory) {
			theSingleton = factory;
		}
		
		/**
		 * Create a copier factory in a thread-safe way.
		 * @return new instance of a copier factory
		 */
		public static synchronized CopierFactory createFactory() {
			if (theSingleton == null) {
				theSingleton = new FieldCopyFactory(new BUServiceFactory());
			}
			return theSingleton.createF();
		}

		/**
		 * Create a new instance of a copier factory, which contains
		 * a new instance of a field copy service.
		 */
		private CopierFactory createF() {
			FieldCopyService copySvc = svcFactory.createService();
			CopierFactoryImpl dcf = new CopierFactoryImpl(copySvc);
			return dcf;
		}
	}
	
	@Test
	public void test() {
		CopierFactory fact1 = FieldCopyFactory.createFactory();
		FieldCopier fc1a = fact1.createCopier();
		FieldCopier fc1b = fact1.createCopier();
		
		assertNotSame(fc1a, fc1b);
		assertSame(fc1a.getCopyService(), fc1b.getCopyService());
		
		CopierFactory fact2 = FieldCopyFactory.createFactory();
		FieldCopier fc2a = fact2.createCopier();
		
		assertNotSame(fact1, fact2);
		assertNotSame(fc1a.getCopyService(), fc2a.getCopyService());
	}
	
}
