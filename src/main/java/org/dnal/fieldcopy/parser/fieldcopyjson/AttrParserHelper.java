package org.dnal.fieldcopy.parser.fieldcopyjson;


import java.util.Map;

public class AttrParserHelper {
    private final Map<String, Object> optionsMap;
    public AttrParser attrParser = new AttrParser();
    
    public AttrParserHelper(Map<String,Object> optionsMap) {
        this.optionsMap = optionsMap;
    }

    public boolean getBool(String attrName, boolean existingVal) {
        if (optionsMap.containsKey(attrName)) {
            return attrParser.getBooleanAttr(optionsMap, attrName);
        } else {
            return existingVal;
        }
    }
    public boolean getBool(String attrName, String attr2Name, boolean existingVal) {
        if (optionsMap.containsKey(attrName)) {
            return attrParser.getBooleanAttr(optionsMap, attrName, attr2Name);
        } else {
            return existingVal;
        }
    }
    public int getInt(String attrName, int existingVal) {
        if (optionsMap.containsKey(attrName)) {
            return attrParser.getIntAttr(optionsMap, attrName, existingVal);
        } else {
            return existingVal;
        }
    }

    public String getString(String attrName, String existingVal) {
        if (optionsMap.containsKey(attrName)) {
            return attrParser.getStringAttr(optionsMap, attrName);
        } else {
            return existingVal;
        }
    }
}
