package org.dnal.fieldcopy.core;

import java.util.List;

import org.dnal.fieldcopy.CopyOptions;
import org.dnal.fieldcopy.FieldCopyMapping;
import org.dnal.fieldcopy.converter.ValueConverter;

public class CopySpec {
		public Object sourceObj;
		public Object destObj;
		public List<FieldPair> fieldPairs;
		public List<FieldCopyMapping> mappingL;
		public CopyOptions options;
		public List<ValueConverter> converterL;
		public String executionPlanCacheKey;
}
