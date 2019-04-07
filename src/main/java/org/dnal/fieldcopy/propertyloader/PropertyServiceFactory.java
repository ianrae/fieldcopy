package org.dnal.fieldcopy.propertyloader;

import org.dnal.fieldcopy.core.DefaultFieldFilter;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.core.ServiceFactory;
import org.dnal.fieldcopy.log.SimpleLogger;

public class PropertyServiceFactory implements ServiceFactory {
	@Override
	public FieldCopyService createService(SimpleLogger logger) {
		FieldRegistry registry = new FieldRegistry();
		DefaultFieldFilter filter = new DefaultFieldFilter();
		PropertyLoaderService copySvc = new PropertyLoaderService(logger, registry, filter);
		return copySvc;
	}
}