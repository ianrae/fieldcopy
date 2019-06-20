package org.dnal.fieldcopy;

/**
 * Third-level Fluent API for FieldCopy
 * 
 * @author Ian Rae
 *
 */
public class CopyBuilder3 {
	private CopyBuilder2 fcb2;

	public CopyBuilder3(CopyBuilder2 fcb2) {
		this.fcb2 = fcb2;
	}
	
	/**
	 * Specifies that a source field whose name equals srcFieldName will be copied to a field of the same
	 * name in the destination object.
	 * @param srcFieldName - name of a field in the source object.
	 * @return
	 */
	public CopyBuilder3 field(String srcFieldName) {
		fcb2.field(srcFieldName);
		return this;
	}
	/**
	 * Specifies that a source field whose name equals srcFieldName will be copied to a field whose
	 * name equals destFieldName in the destination object.
	 * @param srcFieldName - name of a field in the source object.
	 * @param destFieldName - name of a field in the destination object
	 * @return
	 */
	public CopyBuilder3 field(String srcFieldName, String destFieldName) {
		fcb2.field(srcFieldName, destFieldName);
		return this;
	}
	
	/**
	 * Specifies the default value to use if the source value is null
	 * @param defaultValue
	 * @return
	 */
	public CopyBuilder3 defaultValue(Object defaultValue) {
		fcb2.replaceDefaultValue(defaultValue);
		return this;
	}
	
	
	/**
	 * Perform the copy.
	 */
	public void execute() {
		fcb2.execute();
	}
	/**
	 * Perform the copy.
	 */
	public <T> T execute(Class<T> destClass) {
		return fcb2.execute(destClass);
	}
}