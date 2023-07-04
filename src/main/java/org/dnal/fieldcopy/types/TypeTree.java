package org.dnal.fieldcopy.types;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The full type of a field or getter.
 * If size is 0 then type is simple type (no generics)
 * Otherwise size is divisible by 2 where first of each pair is raw type (java.util.list)
 * and second of pair is actual type (java.lang.String)
 * If size greater than 2 then is something like {@code Optional<List<String>/>}
 */
public class TypeTree {
    private List<Type> list = new ArrayList<>();
    private Map<Integer,Type> mapValueMap = new HashMap<>(); //index of list,value type

    public void addPair(Type rawType, Type actualType) {
        list.add(rawType);
        list.add(actualType);
    }

    public int size() {
        return list.size();
    }

    public String getIthName(int i) {
        Type type = list.get(i);
        return type.getTypeName();
    }

    public Type getFirstRaw() {
        return list.get(0);
    }

    public Type getFirstActual() {
        return list.get(1);
    }

    public Type getIthRaw(int iPair) {
        return list.get(iPair / 2);
    }

    public Type getIthActual(int iPair) {
        return list.get(1 + iPair / 2);
    }

    public void setMapValueType(Type mapValueType) {
        int pairIndex = -1 + list.size() / 2;
        mapValueMap.put(pairIndex, mapValueType);
    }
    public Type getMapValueType(int pairIndex) {
        return mapValueMap.get(pairIndex);
    }
}
