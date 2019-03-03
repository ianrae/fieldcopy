package org.dnal.fieldcopy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dnal.fieldcopy.converter.ListElementConverter;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldDescriptor;
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
	public <T> T execute(Class<T> destClass) {
		return doExecute(destClass, null, null);
	}
	
	public CopyBuilder1 withMappings(FieldCopyMapping... mappings) {
		if (this.mappingList == null) {
			this.mappingList = new ArrayList<>();
		}
		this.mappingList.addAll(Arrays.asList(mappings));
		return this;
	}
	public CopyBuilder1 withTransformers(ValueConverter... transformers) {
		if (this.converters == null) {
			this.converters = new ArrayList<>();
		}
		this.converters.addAll(Arrays.asList(transformers));
		return this;
	}
	public CopyBuilder1 listHint(String srcField, Class<?> destListElementClass) {
		if (this.converters == null) {
			this.converters = new ArrayList<>();
		}
		ListElementConverter transformer = new ListElementConverter(srcField, destListElementClass);
		this.converters.add(transformer);
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
		List<FieldPair> fieldsToCopy;
		List<FieldPair> fieldPairs;
		if (root.destObj == null) {
			fieldPairs = root.copier.buildAutoCopyPairs(root.sourceObj.getClass(), destClass);
		} else {
			fieldPairs = root.copier.buildAutoCopyPairs(root.sourceObj.getClass(), root.destObj.getClass());
		}
		
		if (this.doAutoCopy) {
			if (includeList == null && excludeList == null) {
				fieldsToCopy = fieldPairs;
			} else {
				fieldsToCopy = new ArrayList<>();
				for(FieldPair pair: fieldPairs) {
					if (includeList != null && !includeList.contains(pair.srcProp.getName())) {
						continue;
					}
					if (excludeList != null && excludeList.contains(pair.srcProp.getName())) {
						continue;
					}
					
					fieldsToCopy.add(pair);
				}
			}
		} else {
			fieldsToCopy = new ArrayList<>();
		}
		
		//now do explicit fields
		if (srcList != null && destList != null) {
			for(int i = 0; i < srcList.size(); i++) {
				String srcField = srcList.get(i);
				String destField = destList.get(i);
				
				FieldPair pair = new FieldPair();
				pair.srcProp = findInPairs(srcField, fieldPairs);
				pair.destFieldName = destField;
				
				fieldsToCopy.add(pair);
			}
		}
			
		CopySpec spec = new CopySpec();
		spec.sourceObj = root.sourceObj;
		spec.destObj = root.destObj;
		spec.fieldPairs = fieldsToCopy;
		spec.mappingL = mappingList;
		spec.options = root.options;
		spec.converterL = this.converters;
		spec.executionPlanCacheKey = generateCacheKey(); //executionPlanCacheKey;
		FieldCopyService copySvc = root.getCopyService();
		if (destClass == null) {
			copySvc.copyFields(spec);
			return null;
		} else {
			return copySvc.copyFields(spec, destClass);
		}
	}
	
	private String generateCacheKey() {
		if (executionPlanCacheKey == null) {
			//NOTE. the following key will not work if you have multiple conversions of the
			//same pair of source,destObj but with different fields, mappings, and transformers.
			//If that is the case, you MUST key cacheKey and provide a unique value.
			
			//if source or destObj are null we will catch it during copy
			String class1Name = root.sourceObj == null ? "" : root.sourceObj.getClass().getName();
			String class2Name = root.destObj == null ? "" : root.destObj.getClass().getName();
			executionPlanCacheKey = String.format("%s--%s", class1Name, class2Name);
		}
		return executionPlanCacheKey;
	}

	private FieldDescriptor findInPairs(String srcField, List<FieldPair> fieldPairs) {
		for(FieldPair pair: fieldPairs) {
			if (pair.srcProp.getName().equals(srcField)) {
				return pair.srcProp;
			}
		}
		return null;
	}

	public CopyBuilder2 field(String srcFieldName) {
		return new CopyBuilder2(this, srcFieldName, srcFieldName);
	}
	public CopyBuilder2 field(String srcFieldName, String destFieldName) {
		return new CopyBuilder2(this, srcFieldName, destFieldName);
	}
}