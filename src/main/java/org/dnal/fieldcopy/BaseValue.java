package org.dnal.fieldcopy;

/**
 * Base class for Value classes
 * @author Ian Rae
 *
 */
public abstract class BaseValue implements Value {
	protected String name;
	protected Object rawObject;

	@Override
	public String name() {
		return name;
	}
	@Override
	public void setNameInternal(String name) {
		this.name = name;
	}

	@Override
	public Object getRawObject() {
		return rawObject;
	}

	@Override
	public void setRawObject(Object val) {
		rawObject = val;
	}

	@Override
	public boolean isNull() {
		return rawObject == null;
	}
	@Override
	public String toString() {
		return rawObject.toString();
	}
}