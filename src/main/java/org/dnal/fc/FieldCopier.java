package org.dnal.fc;

import org.dnal.fc.core.FieldCopyService;

public class FieldCopier {
	FieldCopyService copier;
	Object sourceObj;
	Object destObj;
	CopyOptions options = new CopyOptions();

	public FieldCopier(FieldCopyService copier) {
		this.copier = copier;
	}
	
	public FCB1 copy(Object sourceObj, Object destObj) {
		this.sourceObj = sourceObj;
		this.destObj = destObj;
		return new FCB1(this);
	}
	
	FieldCopyService getCopyService() {
		return copier;
	}

	public CopyOptions getOptions() {
		return options;
	}
}