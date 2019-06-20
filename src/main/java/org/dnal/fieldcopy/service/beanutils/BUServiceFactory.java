package org.dnal.fieldcopy.service.beanutils;

import org.dnal.fieldcopy.core.DefaultFieldFilter;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.core.ServiceFactory;
import org.dnal.fieldcopy.log.SimpleLogger;

public class BUServiceFactory implements ServiceFactory {

	@Override
	public FieldCopyService createService(SimpleLogger logger) {
		FieldRegistry registry = new FieldRegistry();
		DefaultFieldFilter filter = new DefaultFieldFilter();
		BUCopyService copySvc = new BUCopyService(logger, registry, filter);
		return copySvc;
	}
}