package org.dnal.fc;

public class FieldCopyMapping {
	private Class<?> clazzSrc;
	private Class<?> clazzDest;
	
	public FieldCopyMapping(Class<?> clazzSrc, Class<?> clazzDest) {
		super();
		this.clazzSrc = clazzSrc;
		this.clazzDest = clazzDest;
	}
}
