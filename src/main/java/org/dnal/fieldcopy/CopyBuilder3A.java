package org.dnal.fieldcopy;

/**
 * Third-level Fluent API for FieldCopy
 * 
 * @author Ian Rae
 *
 */
public class CopyBuilder3A {
	private CopyBuilder2A fcb2;

	public CopyBuilder3A(CopyBuilder2A fcb2) {
		this.fcb2 = fcb2;
	}
	
	/**
	 * Specifies that a source field whose name equals srcFieldName will be copied to a field of the same
	 * name in the destination object.
	 * @param srcFieldName - name of a field in the source object.
	 * @return fluent API object
	 */
	public CopyBuilder3A field(String srcFieldName) {
		fcb2.field(srcFieldName);
		return this;
	}
	/**
	 * Specifies that a source field whose name equals srcFieldName will be copied to a field whose
	 * name equals destFieldName in the destination object.
	 * @param srcFieldName - name of a field in the source object.
	 * @param destFieldName - name of a field in the destination object
	 * @return fluent API object
	*/ 
	public CopyBuilder3A field(String srcFieldName, String destFieldName) {
		fcb2.field(srcFieldName, destFieldName);
		return this;
	}
	
	public CopyBuilder3A defaultValue(Object defaultValue) {
		fcb2.replaceDefaultValue(defaultValue);
		return this;
	}
	

	/***
	 * Perform the copy
	 * @param destClass type of destination object to create.
	 * @return destination object.
	 */
	public <T> T execute(Class<T> destClass) {
		return fcb2.execute(destClass);
	}
}