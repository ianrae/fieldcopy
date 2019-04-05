package org.dnal.fieldcopy;

import java.util.ArrayList;
import java.util.List;

/**
 * Second-level Fluent API for FieldCopy
 * 
 * @author Ian Rae
 *
 */
public class CopyBuilder2A {
	private CopyBuilder1A fcb1;
	private List<String> srcList = new ArrayList<>();
	private List<String> destList = new ArrayList<>();
	private List<Object> defaultValueList = new ArrayList<>();

	public CopyBuilder2A(CopyBuilder1A fcb1, String srcField, String destField, Object defaultValue) {
		this.fcb1 = fcb1;
		srcList.add(srcField);
		destList.add(destField);
		defaultValueList.add(defaultValue);
	}
	
	public CopyBuilder2A field(String srcFieldName) {
		srcList.add(srcFieldName);
		destList.add(srcFieldName);
		defaultValueList.add(null);
		return this;
	}
	public CopyBuilder2A field(String srcFieldName, String destFieldName) {
		srcList.add(srcFieldName);
		destList.add(destFieldName);
		defaultValueList.add(null);
		return this;
	}

	void replaceDefaultValue(Object defaultValue) {
		int n = defaultValueList.size();
		defaultValueList.remove(n - 1);
		defaultValueList.add(defaultValue);
	}
	
	public <T> T execute(Class<T> destClass) {
		return fcb1.doExecute(destClass, srcList, destList, defaultValueList);
	}
}