package org.dnal.fieldcopy.parser.fieldcopyjson;


import org.dnal.fieldcopy.group.ObjectConverterSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParsedConverterSpec {
    public String typesStr;
    public String nameStr; //has test and classname
    public String nameForUsingStr;
    public List<String> fieldStrings = new ArrayList<>();
    public String srcClass;
    public String destClass;
    public String packageStr;
    public List<ObjectConverterSpec> additionalConverters;

    public ParsedConverterSpec(String typesStr, String nameForUsingStr) {
        this.typesStr = typesStr;
        this.nameForUsingStr = nameForUsingStr;
    }
}
