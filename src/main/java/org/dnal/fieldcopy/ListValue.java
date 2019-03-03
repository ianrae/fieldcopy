package org.dnal.fieldcopy;

import java.util.ArrayList;
import java.util.List;

import org.dnal.fieldcopy.factory.DefaultValueFactory;
import org.dnal.fieldcopy.factory.ValueFactory;

public class ListValue<T> extends BaseValue {
	private Class<T> elementClass;
	private ValueFactory factory;
	
	public ListValue(Class<T> elementClass) {
		this(elementClass, null);
	}
	public ListValue(Class<T> elementClass, ValueFactory factory) {
		this.elementClass = elementClass;
		this.rawObject = new ArrayList<>();
		this.factory = factory;
	}
	public void setValueFactory(ValueFactory factory) {
		this.factory = factory;
	}
	protected void initValueFactoryIfNeeded() {
		if (factory == null) {
			factory = new DefaultValueFactory();
		}
	}
	
	public Value createEmptyElement() {
		initValueFactoryIfNeeded();
		return factory.createEmptyValue(elementClass);
	}
	
	public T get(int index) {
		initValueFactoryIfNeeded();
		List<Value> list = getValueList();
		Value val = list.get(index);
		@SuppressWarnings("unchecked")
		T t = (T) val.getRawObject();
		return t;
	}
	public void add(T obj) {
		initValueFactoryIfNeeded();
		List<Value> list = getValueList();
		Value val = createEmptyElement();
		val.setRawObject(obj);
		list.add(val);
	}
	@SuppressWarnings("unchecked")
	public List<T> getList() {
		initValueFactoryIfNeeded();
		List<Value> list = getValueList();
		List<T> outlist = new ArrayList<>();
		for(Value val: list) {
			Object obj = val.getRawObject();
			outlist.add((T) obj);
		}
		return outlist;
	}
	public void setList(List<T> list) {
		List<Value> vlist = getValueList();
		for(T obj: list) {
			Value val = createEmptyElement();
			val.setRawObject(obj);
			vlist.add(val);
		}
	}
	public List<Value> getValueList() {
		@SuppressWarnings("unchecked")
		List<Value> list = (List<Value>) rawObject;
		return list;
	}
	public Class<T> getElementClass() {
		return elementClass;
	}
	@Override
	public int getValueType() {
		return ValueTypes.LIST;
	}
}