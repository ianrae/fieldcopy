package org.dnal.fieldcopy;

import org.dnal.fieldcopy.core.FieldCopyService;

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
	//List<FieldCopyMapping> mappingList = new ArrayList<>();

	public FieldCopier(FieldCopyService copier) {
		this.copier = copier;
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
}