package org.dnal.fc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dnal.fc.core.CopySpec;
import org.dnal.fc.core.FieldCopyService;
import org.dnal.fc.core.FieldDescriptor;
import org.dnal.fc.core.FieldPair;
import org.dnal.fc.core.ValueTransformer;

/**
 * First-level Fluent API for FieldCopy
 * 
 * @author Ian Rae
 *
 */
public class FCB1 {
	private FieldCopier root;
	private List<String> includeList;
	private List<String> excludeList;
	private boolean doAutoCopy;
	private List<FieldCopyMapping> mappingList;
	public List<ValueTransformer> transformers;

	public FCB1(FieldCopier fieldCopierBuilder) {
		this.root = fieldCopierBuilder;
	}
	
	public FCB1 include(String...fieldNames) {
		this.includeList = Arrays.asList(fieldNames);
		return this;
	}
	public FCB1 exclude(String...fieldNames) {
		this.excludeList = Arrays.asList(fieldNames);
		return this;
	}
	
	public FCB1 autoCopy() {
		this.doAutoCopy = true;
		return this;
	}
	
	public void execute() {
		doExecute(null, null);
	}
	
	public FCB1 withMappings(FieldCopyMapping... mappings) {
		if (this.mappingList == null) {
			this.mappingList = new ArrayList<>();
		}
		this.mappingList.addAll(Arrays.asList(mappings));
		return this;
	}
	public FCB1 withTransformers(ValueTransformer... transformers) {
		if (this.transformers == null) {
			this.transformers = new ArrayList<>();
		}
		this.transformers.addAll(Arrays.asList(transformers));
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
	
	void doExecute(List<String> srcList, List<String> destList) {
		List<FieldPair> fieldsToCopy;
		List<FieldPair> fieldPairs = root.copier.buildAutoCopyPairs(root.sourceObj.getClass(), root.destObj.getClass());
		
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
			
		FieldCopyService fieldCopier = root.getCopyService();
		
		
		CopySpec spec = new CopySpec();
		spec.sourceObj = root.sourceObj;
		spec.destObj = root.destObj;
		spec.fieldPairs = fieldsToCopy;
		spec.mappingL = mappingList;
		spec.options = root.options;
		spec.transformerL = this.transformers;
		fieldCopier.copyFields(spec);
	}
	
	private FieldDescriptor findInPairs(String srcField, List<FieldPair> fieldPairs) {
		for(FieldPair pair: fieldPairs) {
			if (pair.srcProp.getName().equals(srcField)) {
				return pair.srcProp;
			}
		}
		return null;
	}

	public FCB2 field(String srcFieldName) {
		return new FCB2(this, srcFieldName, srcFieldName);
	}
	public FCB2 field(String srcFieldName, String destFieldName) {
		return new FCB2(this, srcFieldName, destFieldName);
	}
}