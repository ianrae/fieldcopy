package org.dnal.fieldcopy.core;

import org.dnal.fieldcopy.log.SimpleLogger;

public interface ServiceFactory {
	FieldCopyService createService(SimpleLogger logger);
}