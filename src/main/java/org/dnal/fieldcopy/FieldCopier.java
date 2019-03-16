package org.dnal.fieldcopy;

import java.util.ArrayList;
import java.util.List;

import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldDescriptor;
import org.dnal.fieldcopy.core.FieldPair;

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
	
	FieldCopyService getCopyService() {
		return copier;
	}

	public CopyOptions getOptions() {
		return options;
	}
	
	public MapBuilder1 createMapping(Class<?> srcClass, Class<?> destClass) {
		return new MapBuilder1(this, srcClass, destClass);
	}
	
	List<FieldPair> buildFieldsToCopy(Class<?> destClass, boolean doAutoCopy,  List<String> includeList,
			 List<String> excludeList, List<String> srcList, List<String> destList) {
		List<FieldPair> fieldsToCopy;
		List<FieldPair> fieldPairs;
		if (destObj == null) {
			fieldPairs = copier.buildAutoCopyPairs(sourceObj.getClass(), destClass);
		} else {
			fieldPairs = copier.buildAutoCopyPairs(sourceObj.getClass(), destObj.getClass());
		}
		
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
				
				FieldPair pair = new FieldPair();
				pair.srcProp = findInPairs(srcField, fieldPairs);
				pair.destFieldName = destField;
				
				fieldsToCopy.add(pair);
			}
		}
		return fieldsToCopy;
	}
	private FieldDescriptor findInPairs(String srcField, List<FieldPair> fieldPairs) {
		for(FieldPair pair: fieldPairs) {
			if (pair.srcProp.getName().equals(srcField)) {
				return pair.srcProp;
			}
		}
		return null;
	}

}