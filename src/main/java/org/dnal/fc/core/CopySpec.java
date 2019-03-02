package org.dnal.fc.core;

import java.util.List;

import org.dnal.fc.CopyOptions;
import org.dnal.fc.FieldCopyMapping;

public class CopySpec {
		public Object sourceObj;
		public Object destObj;
		public List<FieldPair> fieldPairs;
		public List<FieldCopyMapping> mappingL;
		public CopyOptions options;
}
