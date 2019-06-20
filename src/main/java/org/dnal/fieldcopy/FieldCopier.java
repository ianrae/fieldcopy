package org.dnal.fieldcopy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldDescriptor;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.core.SourceValueFieldDescriptor;
import org.dnal.fieldcopy.core.TargetPair;

/**
 * The main API for FieldCopy.
 * It is a fluent api in which methods can be used to setup the copy, and ends with
 * a call to execute() which performs the copy.
 * 
 * Example:  
 *   fieldCopy.copy(mySourceObj, myDestObj).autoCopy().execute();
 * 
 * @author Ian Rae
 *
 */
public class FieldCopier {
	FieldCopyService copier;
	Object sourceObj;
	Object destObj;
	CopyOptions options = new CopyOptions();
	CopySpec mostRecentCopySpec; //for testing only
	
	public FieldCopier(FieldCopyService copier) {
		this.copier = copier;
	}
	
	public CopyBuilder1A copy(Object sourceObj) {
		this.sourceObj = sourceObj;
		this.destObj = null;
		return new CopyBuilder1A(this);
	}
	public CopyBuilder1 copy(Object sourceObj, Object destObj) {
		this.sourceObj = sourceObj;
		this.destObj = destObj;
		return new CopyBuilder1(this);
	}
	
	public void addBuiltInConverter(ValueConverter converter) {
		copier.addBuiltInConverter(converter);
	}
	
	public FieldCopyService getCopyService() {
		return copier;
	}

	public CopyOptions getOptions() {
		return options;
	}
	
	public MappingBuilder1 createMapping(Class<?> srcClass, Class<?> destClass) {
		return new MappingBuilder1(this, srcClass, destClass);
	}
	
	List<FieldPair> buildFieldsToCopy(Class<?> destClass, boolean doAutoCopy,  List<String> includeList,
			 List<String> excludeList, List<String> srcList, List<String> destList, 
			 List<Object> defaultValueList, Map<String, Object> additionalSourceValMap) {
		if (sourceObj == null) {
			String error = String.format("NULL passed to sourceObj");
			throw new FieldCopyException(error);
		}
		if (destObj == null && destClass == null) {
			String error = String.format("Either destObj or destClass must be non-NULL");
			throw new FieldCopyException(error);
		}
		
		List<FieldPair> fieldPairs;
		TargetPair targetPair;
		if (destObj == null) {
			targetPair = new TargetPair(sourceObj, destClass);
		} else {
			targetPair = new TargetPair(sourceObj, destObj);
		}
		fieldPairs = copier.buildAutoCopyPairs(targetPair, options);
		
		List<FieldPair> fieldsToCopy;
		if (doAutoCopy) {
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
				
				FieldPair existing = findPairInPairs(srcField, fieldsToCopy);
				
				if (existing == null) {
					FieldPair pair = new FieldPair();
					pair.srcProp = findInPairs(srcField, fieldPairs);
					if (pair.srcProp == null) {
						pair.srcProp = copier.resolveSourceField(srcField, targetPair, options);
					}
					pair.destFieldName = destField;
					pair.defaultValue = defaultValue;
					fieldsToCopy.add(pair);
				} else {
					existing.destFieldName = destField;
					existing.defaultValue = defaultValue;
					existing.destProp = findInPairs(destField, fieldPairs);
				}
			}
		}
		
		//add or merge any additional source vals
		if (additionalSourceValMap != null) {
			for(FieldPair pair : fieldsToCopy) {
				String name = pair.srcProp.getName();
				if (additionalSourceValMap.containsKey(name)) {
					Object value = additionalSourceValMap.get(name);
					if (value == null) {
						String error = String.format("SourceValue value '%s' cannot be null", name);
						throw new FieldCopyException(error);
					}
					
					SourceValueFieldDescriptor svfd = new SourceValueFieldDescriptor(name, value);
					pair.srcProp = svfd;
				}
			}
		}
		
		return fieldsToCopy;
	}
	private FieldDescriptor findInPairs(String srcField, List<FieldPair> fieldPairs) {
		FieldPair pair = findPairInPairs(srcField, fieldPairs);
		if (pair != null) {
			return pair.srcProp;
		}
		return null;
	}
	private FieldPair findPairInPairs(String srcField, List<FieldPair> fieldPairs) {
		for(FieldPair pair: fieldPairs) {
			if (pair.srcProp.getName().equals(srcField)) {
				return pair;
			}
		}
		return null;
	}

	public CopySpec getMostRecentCopySpec() {
		return mostRecentCopySpec;
	}

}