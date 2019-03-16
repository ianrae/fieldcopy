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

	public CopyBuilder2(CopyBuilder1 fcb1, String srcField, String destField) {
		this.fcb1 = fcb1;
		srcList.add(srcField);
		destList.add(destField);
	}
	
	/**
	 * Specifies that a source field whose name equals srcFieldName will be copied to a field of the same
	 * name in the destination object.
	 * @param srcFieldName - name of a field in the source object.
	 * @return
	 */
	public CopyBuilder2 field(String srcFieldName) {
		srcList.add(srcFieldName);
		destList.add(srcFieldName);
		return this;
	}
	/**
	 * Specifies that a source field whose name equals srcFieldName will be copied to a field whose
	 * name equals destFieldName in the destination object.
	 * @param srcFieldName - name of a field in the source object.
	 * @param destFieldName - name of a field in the destination object
	 * @return
	 */
	public CopyBuilder2 field(String srcFieldName, String destFieldName) {
		srcList.add(srcFieldName);
		destList.add(destFieldName);
		return this;
	}
	
	/**
	 * Perform the copy.
	 */
	public void execute() {
		fcb1.doExecute(null, srcList, destList);
	}
	/**
	 * Perform the copy.
	 */
	public <T> T execute(Class<T> destClass) {
		return fcb1.doExecute(destClass, null, null);
	}
}