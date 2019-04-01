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
	 * Creates FieldCopier objects.
	 * 
	 * FieldCopier objects are not thread-safe. Therefore they should be
	 * created as local variables and used to do a single copy.
	 * 
	 * All FieldCopier objects created by an instance of CopierFactory
	 * share the same FieldCopyService object.  This is important
	 * for performance, because FieldCopyService caches details of 
	 * the data classes that it copies, to minimize the use of Java
	 * reflection.
	 * 
	 * Your application should arrange that each data class being copied
	 * is being copied by the same copier factory.  This could be by
	 * having a single copier factory in the application, or by having
	 * a copier member variable in each service or controller that is doing
	 * copying.
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
	
	public static class FieldCopy {
		private static FieldCopy theSingleton;
		
		private ServiceFactory svcFactory;
		
		public FieldCopy(ServiceFactory factory) {
			this.svcFactory = factory;
		}
		
		/**
		 * Optional method. Use this to set a custom factory.  setSingleton must
		 * be called before the first call to createFactory.
		 * 
		 * @param factory
		 */
		public static synchronized void setSingleton(FieldCopy factory) {
			theSingleton = factory;
		}
		
		/**
		 * Create a copier factory.
		 * This method is thread-safe.
		 * 
		 * @return new instance of a copier factory
		 */
		public static synchronized CopierFactory createFactory() {
			if (theSingleton == null) {
				theSingleton = new FieldCopy(new BUServiceFactory());
			}
			return theSingleton.createCopierFactory();
		}

		/**
		 * Create a new instance of a copier factory, which contains
		 * a new instance of a field copy service.
		 */
		private CopierFactory createCopierFactory() {
			FieldCopyService copySvc = svcFactory.createService();
			CopierFactoryImpl dcf = new CopierFactoryImpl(copySvc);
			return dcf;
		}
	}
	
	@Test
	public void test() {
		CopierFactory fact1 = FieldCopy.createFactory();
		FieldCopier fc1a = fact1.createCopier();
		FieldCopier fc1b = fact1.createCopier();
		
		assertNotSame(fc1a, fc1b);
		assertSame(fc1a.getCopyService(), fc1b.getCopyService());
		
		CopierFactory fact2 = FieldCopy.createFactory();
		FieldCopier fc2a = fact2.createCopier();
		
		assertNotSame(fact1, fact2);
		assertNotSame(fc1a.getCopyService(), fc2a.getCopyService());
	}
	
}
