package org.dnal.fieldcopy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dnal.fieldcopy.core.FieldDescriptor;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.core.TargetPair;

/**
 * First-level fluent API for creating a mapping.
 * 
 * @author Ian Rae
 *
 */
public class MappingBuilder1 {
	private FieldCopier root;
	private List<String> includeList;
	private List<String> excludeList;
	private boolean doAutoCopy;
	private Class<?> srcClass;
	private Class<?> destClass;

	public MappingBuilder1(FieldCopier fieldCopierBuilder, Class<?> srcClass, Class<?> destClass) {
		this.root = fieldCopierBuilder;
		this.srcClass = srcClass;
		this.destClass = destClass;
	}

	public MappingBuilder1 include(String...fieldNames) {
		this.includeList = Arrays.asList(fieldNames);
		return this;
	}
	public MappingBuilder1 exclude(String...fieldNames) {
		this.excludeList = Arrays.asList(fieldNames);
		return this;
	}
	
	public MappingBuilder1 autoCopy() {
		this.doAutoCopy = true;
		return this;
	}
	
	public FieldCopyMapping build() {
		return doBuild(null, null, null);
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
	 * @param defaultValueList 
	 */
	
	FieldCopyMapping doBuild(List<String> srcList, List<String> destList, List<Object> defaultValueList) {
		List<FieldPair> fieldsToCopy;
		List<FieldPair> fieldPairs = root.copier.buildAutoCopyPairs(new TargetPair(srcClass, destClass));
		
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
				Object defaultValue = (defaultValueList == null) ? null : defaultValueList.get(i);
				
				FieldPair pair = new FieldPair();
				pair.srcProp = findInPairs(srcField, fieldPairs);
				pair.destFieldName = destField;
				pair.defaultValue = defaultValue;
				
				fieldsToCopy.add(pair);
			}
		}
			
		FieldCopyMapping mapping = new FieldCopyMapping(srcClass, destClass, fieldsToCopy);
		return mapping;
	}
	
	private FieldDescriptor findInPairs(String srcField, List<FieldPair> fieldPairs) {
		for(FieldPair pair: fieldPairs) {
			if (pair.srcProp.getName().equals(srcField)) {
				return pair.srcProp;
			}
		}
		return null;
	}

	public MappingBuilder2 field(String srcFieldName) {
		return new MappingBuilder2(this, srcFieldName, srcFieldName, null);
	}
	public MappingBuilder2 field(String srcFieldName, String destFieldName) {
		return new MappingBuilder2(this, srcFieldName, destFieldName, null);
	}
	public MappingBuilder2 field(String srcFieldName, String destFieldName, Object defaultValue) {
		return new MappingBuilder2(this, srcFieldName, destFieldName, defaultValue);
	}
	
}