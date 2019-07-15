package org.dnal.fieldcopy.core;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds internal information about copy operations.
 * Is used by FieldCopy to cache class and field information, in order to speed up
 * subsequent copy operations. 
 * 
 * @author Ian Rae
 *
 */
public class FieldRegistry {
	private ConcurrentHashMap<TargetPair,List<FieldPair>> autocopyCache = new ConcurrentHashMap<>();
	
	public FieldRegistry() {
	}
	public List<FieldPair> findAutoCopyInfo(TargetPair pair) {
//		String key = buildClassPairKey(pair);
		return autocopyCache.get(pair);
	}
	public void registerAutoCopyInfo(TargetPair pair, List<FieldPair> fieldPairs) {
//		String key = buildClassPairKey(pair);
		autocopyCache.put(pair, fieldPairs);
	}
//	private String buildClassPairKey(TargetPair pair) {
////		return String.format("%s--%s", class1.getName(), class2.getName());
//		return pair.getSrcClass().getName() + pair.getDestClass().getName();
//	}
}