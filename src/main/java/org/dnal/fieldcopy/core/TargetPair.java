package org.dnal.fieldcopy.core;

/**
 * Specify the copy source and destination
 * either as objects or classes, or a mixture.
 * 
 * @author Ian Rae
 *
 */
public class TargetPair {
	private Object srcObj;
	private Class<?> srcClass;
	private Object destObj;
	private Class<?> destClass;
	
	public TargetPair(Object srcObj, Object destObj) {
		this.srcObj = srcObj;
		this.destObj = destObj;
		this.srcClass = srcObj.getClass();
		this.destClass = destObj.getClass();
	}
	public TargetPair(Object srcObj, Class<?> destClass) {
		this.srcObj = srcObj;
		this.destObj = null;
		this.srcClass = srcObj.getClass();
		this.destClass = destClass;
	}
	public TargetPair(Class<?> srcClass, Object destObj) {
		this.srcObj = null;
		this.destObj = destObj;
		this.srcClass = srcClass;
		this.destClass = destObj.getClass();
	}
	public TargetPair(Class<?> srcClass, Class<?> destClass) {
		this.srcObj = null;
		this.destObj = null;
		this.srcClass = srcClass;
		this.destClass = destClass;
	}
	public Object getSrcObj() {
		return srcObj;
	}
	public Class<?> getSrcClass() {
		return srcClass;
	}
	public Object getDestObj() {
		return destObj;
	}
	public Class<?> getDestClass() {
		return destClass;
	}
}