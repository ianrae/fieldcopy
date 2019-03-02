package org.dnal.fc.core;

import java.util.List;

import org.dnal.fc.CopyOptions;
import org.dnal.fc.FieldCopyMapping;
import org.dnal.fieldcopy.log.SimpleLogger;

public interface FieldCopyService {
	List<FieldPair> buildAutoCopyPairs(Class<?> sourceClass, Class<?> destClass);
	void copyFields(Object sourceObj, Object destObj, List<FieldPair> fieldPairs, List<FieldCopyMapping> mappingL, CopyOptions options);
	void dumpFields(Object sourceObj);
	SimpleLogger getLogger();
	FieldRegistry getRegistry();
}