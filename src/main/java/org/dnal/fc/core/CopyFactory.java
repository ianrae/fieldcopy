package org.dnal.fc.core;

import org.dnal.fc.FieldCopier;
import org.dnal.fieldcopy.log.SimpleLogger;

public interface CopyFactory {
	SimpleLogger createLogger();
	FieldCopyService createCopyService();
	FieldCopier createCopier();
}