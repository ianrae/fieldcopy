package org.dnal.fc.core;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FieldRegistry {
	private ConcurrentHashMap<String,List<FieldPair>> autocopyCache = new ConcurrentHashMap<>();
	
	public FieldRegistry() {
	}
	public List<FieldPair> findAutoCopyInfo(Class<?> clazz1, Class<?> clazz2) {
		String key = buildClassPairKey(clazz1, clazz2);
		return autocopyCache.get(key);
	}
	public void registerAutoCopyInfo(Class<?> clazz1, Class<?> clazz2, List<FieldPair> fieldPairs) {
		String key = buildClassPairKey(clazz1, clazz2);
		autocopyCache.put(key, fieldPairs);
	}
	private String buildClassPairKey(Class<?> class1, Class<?> class2) {
		return String.format("%s--%s", class1.getName(), class2.getName());
	}
}