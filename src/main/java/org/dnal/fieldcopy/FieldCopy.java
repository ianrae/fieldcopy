package org.dnal.fieldcopy;

import org.dnal.fieldcopy.core.CopierFactoryImpl;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.ServiceFactory;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.dnal.fieldcopy.service.beanutils.BUServiceFactory;

public class FieldCopy {
	private static FieldCopy theSingleton;
	private static SimpleLogger theLogger = new SimpleConsoleLogger();
	
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
	public static synchronized void setLogger(SimpleLogger logger) {
		theLogger = logger;
	}
	public static SimpleLogger getLogger() {
		return theLogger;
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
		FieldCopyService copySvc = svcFactory.createService(theLogger);
		CopierFactoryImpl dcf = new CopierFactoryImpl(copySvc);
		return dcf;
	}
}