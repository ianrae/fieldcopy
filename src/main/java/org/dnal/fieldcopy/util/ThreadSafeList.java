package org.dnal.fieldcopy.util;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Use CopyOnWriteArrayList to make a thread-safe list.
 * 
 * @author Ian Rae
 *
 * @param <T>
 */
public class ThreadSafeList<T> {
	private CopyOnWriteArrayList<T> list = new CopyOnWriteArrayList<>();
	
	public void add(T el) {
		list.add(el);
	}
	public void addAll(List<T> anotherList) {
		list.addAll(anotherList);
	}
	public void addAll(ThreadSafeList<T> anotherList) {
		list.addAll(anotherList.list);
	}
	
	public Iterator<T> iterator() {
		return list.iterator();
	}
	
	public int size() {
		return list.size();
	}
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public void addIntoOtherList(List<T> otherList) {
		Iterator<T> iter = iterator();
		while(iter.hasNext()) {
			T val = iter.next();
			otherList.add(val);
		}
	}
	public boolean contains(T val) {
		return list.contains(val);
	}
	
	public static boolean isNotEmpty(ThreadSafeList<?> list) {
		if (list != null && ! list.isEmpty()) {
			return true;
		}
		return false;
	}
	
}