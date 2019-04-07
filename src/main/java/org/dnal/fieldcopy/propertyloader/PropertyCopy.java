package org.dnal.fieldcopy.propertyloader;

import org.dnal.fieldcopy.CopierFactory;
import org.dnal.fieldcopy.core.CopierFactoryImpl;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.ServiceFactory;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.dnal.fieldcopy.log.SimpleLogger;

public class PropertyCopy {
	private static PropertyCopy theSingleton;
	private static SimpleLogger theLogger = new SimpleConsoleLogger();
	
	private ServiceFactory svcFactory;
	
	public PropertyCopy(ServiceFactory factory) {
		this.svcFactory = factory;
	}
	
	/**
	 * Optional method. Use this to set a custom factory.  setSingleton must
	 * be called before the first call to createFactory.
	 * 
	 * @param factory
	 */
	public static synchronized void setSingleton(PropertyCopy factory) {
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
			theSingleton = new PropertyCopy(new PropertyServiceFactory());
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