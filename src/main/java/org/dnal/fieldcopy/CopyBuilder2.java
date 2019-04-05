package org.dnal.fieldcopy;

import java.util.ArrayList;
import java.util.List;

/**
 * Second-level Fluent API for FieldCopy
 * 
 * @author Ian Rae
 *
 */
public class CopyBuilder2 {
	private CopyBuilder1 fcb1;
	private List<String> srcList = new ArrayList<>();
	private List<String> destList = new ArrayList<>();
	private List<Object> defaultValueList = new ArrayList<>();

	public CopyBuilder2(CopyBuilder1 fcb1, String srcField, String destField, Object defaultValue) {
		this.fcb1 = fcb1;
		srcList.add(srcField);
		destList.add(destField);
		defaultValueList.add(defaultValue);
	}
	
	/**
	 * Specifies that a source field whose name equals srcFieldName will be copied to a field of the same
	 * name in the destination object.
	 * @param srcFieldName - name of a field in the source object.
	 * @return
	 */
	public CopyBuilder3 field(String srcFieldName) {
		srcList.add(srcFieldName);
		destList.add(srcFieldName);
		defaultValueList.add(null);
		return new CopyBuilder3(this);
	}
	/**
	 * Specifies that a source field whose name equals srcFieldName will be copied to a field whose
	 * name equals destFieldName in the destination object.
	 * @param srcFieldName - name of a field in the source object.
	 * @param destFieldName - name of a field in the destination object
	 * @return
	 */
	public CopyBuilder3 field(String srcFieldName, String destFieldName) {
		srcList.add(srcFieldName);
		destList.add(destFieldName);
		defaultValueList.add(null);
		return new CopyBuilder3(this);
	}
	/**
	 * Specifies that a source field whose name equals srcFieldName will be copied to a field whose
	 * name equals destFieldName in the destination object.
	 * @param srcFieldName - name of a field in the source object.
	 * @param destFieldName - name of a field in the destination object
	 * @param defaultValue - value to use if source field is null
	 * @return
	 */
	public CopyBuilder3 field(String srcFieldName, String destFieldName, Object defaultValue) {
		srcList.add(srcFieldName);
		destList.add(destFieldName);
		defaultValueList.add(defaultValue);
		return new CopyBuilder3(this);
	}
	
	
	/**
	 * Perform the copy.
	 */
	public void execute() {
		fcb1.doExecute(null, srcList, destList, defaultValueList);
	}
	/**
	 * Perform the copy.
	 */
	public <T> T execute(Class<T> destClass) {
		return fcb1.doExecute(destClass, null, null, defaultValueList);
	}

	void replaceDefaultValue(Object defaultValue) {
		int n = defaultValueList.size();
		defaultValueList.remove(n - 1);
		defaultValueList.add(defaultValue);
	}
}