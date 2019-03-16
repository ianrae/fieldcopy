package org.dnal.fieldcopy.service.beanutils;

public class ListSpec {
	public int depth; //0 means list<elementclass>, 1 means list<list<elementclass>>, etc
	public Class<?> elementClass;
}
