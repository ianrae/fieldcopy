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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destClass == null) ? 0 : destClass.hashCode());
		result = prime * result + ((srcClass == null) ? 0 : srcClass.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TargetPair other = (TargetPair) obj;
		if (destClass == null) {
			if (other.destClass != null)
				return false;
		} else if (!destClass.equals(other.destClass))
			return false;
		if (srcClass == null) {
			if (other.srcClass != null)
				return false;
		} else if (!srcClass.equals(other.srcClass))
			return false;
		return true;
	}
}