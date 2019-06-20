package org.dnal.fieldcopy.core;

import java.util.List;
import java.util.Map;

import org.dnal.fieldcopy.CopyOptions;
import org.dnal.fieldcopy.FieldCopyMapping;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.util.ThreadSafeList;

public class CopySpec {
	public Object sourceObj;
	public Object destObj;
	public List<FieldPair> fieldPairs;
	public List<FieldCopyMapping> mappingL;
	public CopyOptions options;
	public ThreadSafeList<ValueConverter> converterL;
	public String executionPlanCacheKey;
	public int runawayCounter = 1;
	public Map<String,Object> additionalSourceValMap;

	public boolean hasSourceValueMap() {
		return (additionalSourceValMap != null) && additionalSourceValMap.size() > 0;
	}
}
