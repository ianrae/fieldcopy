package org.dnal.fc.core;

import java.util.List;

import org.dnal.fc.CopyOptions;
import org.dnal.fieldcopy.log.SimpleLogger;

public interface FieldCopyService {
	List<FieldPair> buildAutoCopyPairs(Object sourceObj, Object destObj);
	void copyFields(Object sourceObj, Object destObj, List<FieldPair> fieldPairs, CopyOptions options);
	void dumpFields(Object sourceObj);
	SimpleLogger getLogger();
	FieldRegistry getRegistry();
}