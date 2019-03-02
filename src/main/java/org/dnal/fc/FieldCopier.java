package org.dnal.fc;

import java.util.ArrayList;
import java.util.List;

import org.dnal.fc.core.FieldCopyService;

public class FieldCopier {
	FieldCopyService copier;
	Object sourceObj;
	Object destObj;
	CopyOptions options = new CopyOptions();
	//List<FieldCopyMapping> mappingList = new ArrayList<>();

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
	
	public FCM0 createMapping(Class<?> srcClass, Class<?> destClass) {
		return new FCM0(this, srcClass, destClass);
	}
}