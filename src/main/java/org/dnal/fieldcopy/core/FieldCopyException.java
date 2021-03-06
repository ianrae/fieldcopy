package org.dnal.fieldcopy.core;

public class FieldCopyException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FieldCopyException(String msg) {
		super(msg);
	}
	public FieldCopyException(String msg, Exception inner) {
		super(msg, inner);
	}
}