package org.dnal.fieldcopy;

import org.dnal.fieldcopy.FieldCopier;
import org.dnal.fieldcopy.core.CopyFactory;
import org.dnal.fieldcopy.core.DefaultFieldFilter;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldFilter;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.dnal.fieldcopy.service.beanutils.BUCopyService;

/**
 * Creates a field copier that uses Apache BeanUtils (reflection-based),
 * and a console logger.
 * 
 * @author Ian Rae
 *
 */
public class DefaultCopyFactory implements CopyFactory {
	private static DefaultCopyFactory theSingleton;
	private static SimpleLogger theLogger;
	private static FieldCopyService theSvc;
	
	public static CopyFactory Factory() {
		if (theSingleton == null) {
			theSingleton = new DefaultCopyFactory();
		}
		return theSingleton;
	}
	
	public static void clearCopyService() {
		theSvc = null;
	}

	@Override
	public SimpleLogger createLogger() {
		if (theLogger != null) {
			return theLogger;
		}
		return new SimpleConsoleLogger();
	}

	@Override
	public FieldCopyService createCopyService() {
		//TODO: not threadsafe!!
		if (theSvc != null) {
			return theSvc;
		}
		SimpleLogger logger = createLogger();
		FieldRegistry registry = new FieldRegistry();
		FieldFilter fieldFilter = createFieldFilter();
		BUCopyService copySvc = new BUCopyService(logger, registry, fieldFilter);
		theSvc = copySvc;
		return copySvc;
	}

	/**
	 * Uses a single copy service singleton.
	 */
	@Override
	public FieldCopier createCopier() {
		FieldCopyService copySvc = createCopyService();
		FieldCopier builder = new FieldCopier(copySvc);
		return builder;
	}
	
	/**
	 * Use this when you have multiple instances of the copySvc.
	 */
	@Override
	public FieldCopier createCopier(FieldCopyService copySvc) {
		FieldCopier builder = new FieldCopier(copySvc);
		return builder;
	}
	
	public static void setLogger(SimpleLogger logger) {
		theLogger = logger;
	}
	
	@Override
	public FieldFilter createFieldFilter() {
		return new DefaultFieldFilter();
	}
}