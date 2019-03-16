package org.dnal.fieldcopy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldPair;

/**
 * First-level Fluent API for FieldCopy
 * 
 * @author Ian Rae
 *
 */
public class CopyBuilder1 {
	private FieldCopier root;
	private List<String> includeList;
	private List<String> excludeList;
	private boolean doAutoCopy;
	private List<FieldCopyMapping> mappingList;
	private List<ValueConverter> converters;
	private String executionPlanCacheKey;
	

	public CopyBuilder1(FieldCopier fieldCopierBuilder) {
		this.root = fieldCopierBuilder;
	}
	
	public CopyBuilder1 cacheKey(String key) {
		executionPlanCacheKey = key;
		return this;
	}
	
	public CopyBuilder1 include(String...fieldNames) {
		this.includeList = Arrays.asList(fieldNames);
		return this;
	}
	public CopyBuilder1 exclude(String...fieldNames) {
		this.excludeList = Arrays.asList(fieldNames);
		return this;
	}
	
	public CopyBuilder1 autoCopy() {
		this.doAutoCopy = true;
		return this;
	}
	
	public void execute() {
		doExecute(null, null, null);
	}
	
	public CopyBuilder1 withMappings(FieldCopyMapping... mappings) {
		if (this.mappingList == null) {
			this.mappingList = new ArrayList<>();
		}
		this.mappingList.addAll(Arrays.asList(mappings));
		return this;
	}
	public CopyBuilder1 withConverters(ValueConverter... converters) {
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
	
	<T> T doExecute(Class<T> destClass, List<String> srcList, List<String> destList) {
		List<FieldPair> fieldsToCopy = root.buildFieldsToCopy(destClass, doAutoCopy, includeList, 
				excludeList, srcList, destList);
			
		CopySpec spec = new CopySpec();
		spec.sourceObj = root.sourceObj;
		spec.destObj = root.destObj;
		spec.fieldPairs = fieldsToCopy;
		spec.mappingL = mappingList;
		spec.options = root.options;
		spec.converterL = this.converters;
		spec.executionPlanCacheKey = this.executionPlanCacheKey;
		FieldCopyService copySvc = root.getCopyService();
		copySvc.copyFields(spec);
		return null;
	}
	
	public CopyBuilder2 field(String srcFieldName) {
		return new CopyBuilder2(this, srcFieldName, srcFieldName);
	}
	public CopyBuilder2 field(String srcFieldName, String destFieldName) {
		return new CopyBuilder2(this, srcFieldName, destFieldName);
	}
}