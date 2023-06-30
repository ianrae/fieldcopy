package org.dnal.fieldcopy.parser.fieldcopyjson;

import java.util.*;
import java.util.stream.Collectors;

public class AttrParser {
    private Set<String> usedSet = new HashSet<>();

    public String getStringAttr(Map<String, Object> map, String attrName) {
        if (map.containsKey(attrName)) {
            usedSet.add(attrName);
            return map.get(attrName).toString();
        } else {
            return null;
        }
    }

    public int getIntAttr(Map<String, Object> map, String attrName, int defaultValue) {
        if (map.containsKey(attrName)) {
            usedSet.add(attrName);
            return Integer.parseInt(map.get(attrName).toString());
        } else {
            return defaultValue;
        }
    }
    public boolean getBooleanAttr(Map<String, Object> map, String attrName) {
        if (map.containsKey(attrName)) {
            usedSet.add(attrName);
            return Boolean.parseBoolean(map.get(attrName).toString());
        } else {
            return false;
        }
    }

    public boolean getBooleanAttr(Map<String, Object> map, String attrName1, String attrName2) {
        if (map.containsKey(attrName1)) {
            usedSet.add(attrName1);
            return Boolean.parseBoolean(map.get(attrName1).toString());
        } else if (map.containsKey(attrName2)) {
            usedSet.add(attrName2);
            return Boolean.parseBoolean(map.get(attrName2).toString());
        } else {
            return false;
        }
    }
    public Object getData(Map<String, Object> map) {
        if (map.containsKey("data")) {
            usedSet.add("data");
        }
        Object data = (List<Map<String, Object>>) map.get("data");
        return data;
    }
    public List<String> getJunctionFields(Map<String, Object> map) {
        if (map.containsKey("junctionFields")) {
            usedSet.add("junctionFields");
        }
        Object obj = map.get("junctionFields");
        if (obj instanceof List) {
            return (List<String>)obj;
        } else if (obj != null) {
            String s = (String) obj;
            return Collections.singletonList(s);
        } else {
            return null;
        }
    }

    public boolean areUnParsedAttrs(Map<String, Object> inner) {
        return inner.size() != usedSet.size();
    }

    public List<String> getUnParsedAttrs(Map<String, Object> inner) {
        return inner.keySet().stream().filter(attr -> !usedSet.contains(attr)).collect(Collectors.toList());
    }
}
