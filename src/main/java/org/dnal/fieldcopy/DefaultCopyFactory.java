package org.dnal.fieldcopy;

import org.dnal.fieldcopy.core.CopyFactory;
import org.dnal.fieldcopy.core.DefaultFieldFilter;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldFilter;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.dnal.fieldcopy.service.beanutils.BeanUtilsFieldCopyService;
import org.dnal.fieldcopy.service.beanutils.OldBeanUtilFieldCopyService;

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
	
	public static CopyFactory Factory() {
		if (theSingleton == null) {
			theSingleton = new DefaultCopyFactory();
		}
		return theSingleton;
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
		SimpleLogger logger = createLogger();
		FieldRegistry registry = new FieldRegistry();
		FieldFilter fieldFilter = createFieldFilter();
//		FieldCopyService copySvc = new BeanUtilFieldCopyService(logger, registry, fieldFilter);
		FieldCopyService copySvc = new BeanUtilsFieldCopyService(logger, registry, fieldFilter);
		return copySvc;
	}

	@Override
	public FieldCopier createCopier() {
		FieldCopyService copySvc = createCopyService();
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