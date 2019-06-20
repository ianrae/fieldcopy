package org.dnal.fieldcopy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.util.ThreadSafeList;

/**
 * First-level Fluent API for FieldCopy
 * 
 * @author Ian Rae
 *
 */
public class CopyBuilder1A {
	private FieldCopier root;
	private List<String> includeList;
	private List<String> excludeList;
	private boolean doAutoCopy;
	private List<FieldCopyMapping> mappingList;
	private List<ValueConverter> converters;
	private String executionPlanCacheKey;
	private Map<String,Object> additionalSourceValMap;


	public CopyBuilder1A(FieldCopier fieldCopierBuilder) {
		this.root = fieldCopierBuilder;
	}

	public CopyBuilder1A cacheKey(String key) {
		executionPlanCacheKey = key;
		return this;
	}

	public CopyBuilder1A include(String...fieldNames) {
		this.includeList = Arrays.asList(fieldNames);
		return this;
	}
	public CopyBuilder1A exclude(String...fieldNames) {
		this.excludeList = Arrays.asList(fieldNames);
		return this;
	}
	public CopyBuilder1A includeSourceValue(String name, Object value) {
		if (additionalSourceValMap == null) {
			additionalSourceValMap = new HashMap<>();
		}
		additionalSourceValMap.put(name, value);
		return this;
	}

	public CopyBuilder1A autoCopy() {
		this.doAutoCopy = true;
		return this;
	}

	public <T> T execute(Class<T> destClass) {
		return doExecute(destClass, null, null, null);
	}

	public CopyBuilder1A withMappings(FieldCopyMapping... mappings) {
		if (this.mappingList == null) {
			this.mappingList = new ArrayList<>();
		}
		this.mappingList.addAll(Arrays.asList(mappings));
		return this;
	}
	public CopyBuilder1A withConverters(ValueConverter... converters) {
		if (this.converters == null) {
			this.converters = new ArrayList<>();
		}
		this.converters.addAll(Arrays.asList(converters));
		return this;
	}

	/**
	 * if autocopy then copies matching fields
	 *   -if excludeList non-empty then fields in it are not copied
	 *   -if includeList non-empty then fields not in it are not copied
	 *   -excludeList has priority over includeList
	 *   
	 * -if srList non-empty then those fields are copied
	 * 
	 * @param srcList
	 * @param destList
	 */

	<T> T doExecute(Class<T> destClass, List<String> srcList, List<String> destList, List<Object> defaultValueList) {
		List<FieldPair> fieldsToCopy = root.buildFieldsToCopy(destClass, doAutoCopy, includeList, 
				excludeList, srcList, destList, defaultValueList, additionalSourceValMap);

		CopySpec spec = new CopySpec();
		spec.sourceObj = root.sourceObj;
		spec.destObj = root.destObj;
		spec.fieldPairs = fieldsToCopy;
		spec.mappingL = mappingList;
		spec.options = root.options;
		spec.additionalSourceValMap = additionalSourceValMap;
		if (this.converters != null) {
			spec.converterL = new ThreadSafeList<ValueConverter>();
			spec.converterL.addAll(this.converters);
		}
		spec.executionPlanCacheKey = executionPlanCacheKey;
		root.mostRecentCopySpec = spec;
		FieldCopyService copySvc = root.getCopyService();
		return copySvc.copyFields(spec, destClass);
	}

	public CopyBuilder2A field(String srcFieldName) {
		return new CopyBuilder2A(this, srcFieldName, srcFieldName, null);
	}
	public CopyBuilder2A field(String srcFieldName, String destFieldName) {
		return new CopyBuilder2A(this, srcFieldName, destFieldName, null);
	}
}